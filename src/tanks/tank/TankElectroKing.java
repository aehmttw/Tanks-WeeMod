package tanks.tank;

import tanks.Drawing;
import tanks.Effect;
import tanks.Game;
import tanks.Panel;
import tanks.bullet.Bullet;
import tanks.bullet.BulletElectro;
import tanks.bullet.BulletElectroRocket;
import tanks.event.*;
import tanks.gui.screen.ScreenGame;
import tanks.obstacle.Obstacle;
import tanks.obstacle.ObstacleElectroPadOff;

public class TankElectroKing extends TankAIControlled
{
	public int phase = 0;
	public double timer = 1000;

	public double phaseTimerBase = 200;
	public double phaseTimerRandom = 1000;
	public double phaseTimerCharge = 0;
	public boolean canTeleport = true;

	public double teleportTimerMax = 50;
	public double teleportTimer = 0;
	public double teleX = 0;
	public double teleY = 0;
	public double destX = 0;
	public double destY = 0;

	public boolean charged = false;
	public boolean shot = false;

	public double glowOverride = -1;

	public double phaseTimer = phaseTimerBase + phaseTimerRandom * Math.random();

	public TankElectroKing(String name, double x, double y, double angle)
	{
		super(name, x, y, Game.tile_size * 1.5, 0, 0, 200, angle, ShootAI.straight);

		this.turret.length *= 1.5;

		this.turret.colorR = 0;
		this.turret.colorG = 25;
		this.turret.colorB = 25;
		Game.eventsOut.add(new EventTankUpdateColor(this));

		this.enableMovement = true;
		this.maxSpeed = 0.5;
		this.enableMineLaying = false;
		this.enablePredictiveFiring = false;
		this.enableDefensiveFiring = false;
		this.liveBulletMax = 1;
		this.cooldownRandom = 0;
		this.cooldownBase = 100;
		this.aimTurretSpeed = 0.02;
		this.bulletBounces = 0;
		this.bulletEffect = Bullet.BulletEffect.ice;
		this.bulletSpeed = 25.0 / 8;
		this.enableLookingAtTargetEnemy = false;
		this.motionChangeChance = 0.001;
		this.avoidSensitivity = 1;
		this.baseHealth = 12;
		this.health = 30;

		this.coinValue = 40;

		this.description = "A boss tank which shoots electro rockets---and charges up electric pads";
	}

	public void launchBullet(double offset)
	{
		Drawing.drawing.playGlobalSound("shoot.ogg", (float) (Bullet.bullet_size / this.bulletSize));

		Bullet b = new BulletElectroRocket(this.posX, this.posY, this.bulletBounces, this);

		b.setPolarMotion(angle + offset, this.bulletSpeed);

		b.moveOut(50 / this.bulletSpeed * this.size / Game.tile_size);
		b.effect = this.bulletEffect;
		b.size = this.bulletSize;
		b.damage = this.bulletDamage;
		b.heavy = this.bulletHeavy;

		Game.movables.add(b);
		Game.eventsOut.add(new EventShootBullet(b));

		this.cooldown = Math.random() * this.cooldownRandom + this.cooldownBase;

		if (this.shootAIType.equals(ShootAI.alternate))
			this.straightShoot = !this.straightShoot;
	}

	public void update()
	{
		if (this.teleportTimer > 0)
		{
			this.teleportTimer -= Panel.frameFrequency;

			double frac = this.teleportTimer / this.teleportTimerMax;
			this.posX = this.teleX * frac + this.destX * (1 - frac);
			this.posY = this.teleY * frac + this.destY * (1 - frac);

			this.addEffect();

			if (this.teleportTimer <= 0)
			{
				this.invulnerable = false;
				this.targetable = true;
				this.inControlOfMotion = true;
				this.positionLock = false;
				this.canTeleport = false;
				this.size = Game.tile_size * 1.5;
				this.turret.size = 8 * 1.5;
				this.turret.length = Game.tile_size * 1.5;

				this.cooldown = 75;
				this.phaseTimer = this.phaseTimerCharge;

				for (int i = 0; i < 50 * Game.effectMultiplier; i++)
				{
					this.addEffect();
				}
			}
		}
		else
		{
			super.update();

			if (ScreenGame.finishedQuick)
				return;

			this.phaseTimer -= Panel.frameFrequency;

			if (this.phaseTimer <= this.phaseTimerCharge)
			{
				if (!this.charged)
				{
					Drawing.drawing.playGlobalSound("charge.ogg", 1f, 0.5f);
					this.charged = true;
					Game.eventsOut.add(new EventTankUpdateColor(this));
				}

				this.addEffect();
				Game.eventsOut.add(new EventElectroGlow(this));

				if (this.phaseTimer <= 0 && Math.random() < Panel.frameFrequency * 0.006 && !this.canTeleport)
				{
					this.phaseTimer = phaseTimerBase + phaseTimerRandom * Math.random();
					this.canTeleport = true;
				}

				if (this.phaseTimer <= 0 && Math.random() < Panel.frameFrequency * 0.003 && this.canTeleport)
				{
					for (int i = 0; i < 5; i++)
					{
						int x = (int) (Math.random() * Game.currentSizeX);
						int y = (int) (Math.random() * Game.currentSizeY);

						if (!Game.game.solidGrid[x][y])
						{
							this.teleX = this.posX;
							this.teleY = this.posY;
							this.destX = (x + 0.5) * Game.tile_size;
							this.destY = (y + 0.5) * Game.tile_size;

							this.invulnerable = true;
							this.targetable = false;
							this.inControlOfMotion = false;
							this.positionLock = true;
							this.teleportTimer = this.teleportTimerMax;
							this.size = 0;
							this.turret.size = 0;
							this.turret.length = 0;

							Game.eventsOut.add(new EventTankElectroTeleport(this.posX, this.posY, this.destX, this.destY, this));

							Drawing.drawing.playGlobalSound("electro_teleport.ogg");

							for (int j = 0; j < 50 * Game.effectMultiplier; j++)
							{
								this.addEffect();
							}

							break;
						}
					}
				}
			}
			else
			{
				if (this.charged)
				{
					this.charged = false;
					Game.eventsOut.add(new EventTankUpdateColor(this));
				}
			}
		}
	}

	public void postUpdate()
	{
		this.timer -= Panel.frameFrequency;

		for (Obstacle o: Game.obstacles)
		{
			if (o instanceof ObstacleElectroPadOff && Math.pow(this.posX - o.posX, 2) + Math.pow(this.posY - o.posY, 2) < 10000)
			{
				((ObstacleElectroPadOff) o).onTimer = 300;
				Game.eventsOut.add(new EventElectroPadActivate(o.posX, o.posY, false));
			}
		}

		if (timer < 300)
		{
			if (this.phase == 0)
			{
				Drawing.drawing.playGlobalSound("charge.ogg", 0.5f, 0.5f);
				for (Obstacle o: Game.obstacles)
				{
					if (o instanceof ObstacleElectroPadOff && Math.random() < 0.08)
					{
						((ObstacleElectroPadOff) o).warmingUp = true;
						Game.eventsOut.add(new EventElectroPadActivate(o.posX, o.posY, true));
					}
				}
			}

			this.phase = 1;
			this.turret.colorG = 25 + (1 - timer / 300) * 230;
			this.turret.colorB = 25 + (1 - timer / 300) * 230;

			Game.eventsOut.add(new EventTankUpdateColor(this));

			Game.eventsOut.add(new EventTankElectroKingCharge(this.networkID, (300 - this.timer) / 300));
			if (Math.random() * 300 * Game.effectMultiplier > timer && Game.effectsEnabled)
			{
				Effect e = Effect.createNewEffect(this.posX, this.posY, this.size / 4, Effect.EffectType.charge);
				e.maxAge *= 2;
				e.posX -= this.posX - e.posX;
				e.posY -= this.posY - e.posY;
				e.posZ -= this.posZ - e.posZ;

				double var = 50;
				e.colR = Math.min(255, Math.max(0, this.turret.colorR + Math.random() * var - var / 2));
				e.colG = Math.min(255, Math.max(0, this.turret.colorG + Math.random() * var - var / 2));
				e.colB = Math.min(255, Math.max(0, this.turret.colorB + Math.random() * var - var / 2));

				Game.effects.add(e);
			}
		}

		if (timer < 0)
		{
			this.shot = true;
			Drawing.drawing.playGlobalSound("laser.ogg", 0.5f);

			timer = 1000;
			this.phase = 0;

			Game.eventsOut.add(new EventTankElectroKingElectrify(this.networkID));
			for (int i = 0; i < this.size * 4 * Game.effectMultiplier; i++)
			{
				Effect e = Effect.createNewEffect(this.posX, this.posY, this.size / 4, Effect.EffectType.piece);
				double var = 50;

				e.colR = Math.min(255, Math.max(0, this.turret.colorR + Math.random() * var - var / 2));
				e.colG = Math.min(255, Math.max(0, this.turret.colorG + Math.random() * var - var / 2));
				e.colB = Math.min(255, Math.max(0, this.turret.colorB + Math.random() * var - var / 2));

				if (Game.enable3d)
					e.set3dPolarMotion(Math.random() * 2 * Math.PI, Math.random() * Math.PI, Math.random() * this.size / 50.0 * 4);
				else
					e.setPolarMotion(Math.random() * 2 * Math.PI, Math.random() * this.size / 50.0 * 4);

				Game.effects.add(e);
			}

			this.turret.colorR = 0;
			this.turret.colorG = 25;
			this.turret.colorB = 25;
			Game.eventsOut.add(new EventTankUpdateColor(this));
		}
	}

	public void draw()
	{
		if (this.charged)
		{
			this.turret.colorG += 50;
			this.turret.colorB += 100;
			Game.eventsOut.add(new EventTankUpdateColor(this));
		}

		if (this.teleportTimer > 0)
		{
			for (int i = 0; i < Game.tile_size; i++)
			{
				Drawing.drawing.setColor(i * 2.5, i * 5, 255, 20);

				if (Game.enable3d)
					Drawing.drawing.fillOval(this.posX, this.posY, Game.tile_size / 2, i, i, false, true);
				else
					Drawing.drawing.fillOval(this.posX, this.posY, i, i);
			}

			Drawing.drawing.setColor(50, 150, 255, 255);

			if (Game.enable3d)
				Drawing.drawing.fillGlow(this.posX, this.posY, this.posZ, Game.tile_size * 2, Game.tile_size * 2, false, true);
			else
				Drawing.drawing.fillGlow(this.posX, this.posY, Game.tile_size * 2, Game.tile_size * 2);
		}
		else
			super.draw();

		if (this.charged)
		{
			this.turret.colorG -= 50;
			this.turret.colorB -= 100;
			Game.eventsOut.add(new EventTankUpdateColor(this));
		}

		double intensity = 0;
		if (this.phase == 1)
			intensity = (300 - this.timer) / 300 * 1.4;
		else if (shot)
			intensity = Math.max(0, (this.timer - 900) / 50);

		if (this.size < 50)
			intensity += 1;

		if (this.glowOverride >= 0)
			intensity = this.glowOverride;

		Drawing.drawing.setColor(127 * intensity, 230 * intensity, 255 * intensity);
		Drawing.drawing.fillGlow(this.posX, this.posY, Game.tile_size / 2, this.size * 8, this.size * 8, false, false);
		Drawing.drawing.fillGlow(this.posX, this.posY, Game.tile_size / 2, this.size * 4, this.size * 4, false, false);

		if (!this.isRemote)
			Game.eventsOut.add(new EventTankElectroKingGlow(this.networkID, intensity));
	}

	public void addEffect()
	{
		if (!Game.effectsEnabled)
			return;

		Effect e = Effect.createNewEffect(this.posX, this.posY, Game.tile_size / 2, Effect.EffectType.teleporterPiece);
		double var = 50;

		e.colR = Math.min(255, Math.max(0, 0 + Math.random() * var - var / 2));
		e.colG = Math.min(255, Math.max(0, 200 + Math.random() * var - var / 2));
		e.colB = Math.min(255, Math.max(0, 255 + Math.random() * var - var / 2));

		if (Game.enable3d)
			e.set3dPolarMotion(Math.random() * 2 * Math.PI, Math.random() * Math.PI * 2, Math.random() * 4);
		else
			e.setPolarMotion(Math.random() * 2 * Math.PI, Math.random() * 4);

		Game.effects.add(e);
	}
}
