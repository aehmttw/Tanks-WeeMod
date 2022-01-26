package tanks.tank;

import tanks.*;
import tanks.bullet.Bullet;
import tanks.bullet.BulletHealing;
import tanks.event.EventLayMine;
import tanks.event.EventTankUpdateColor;

public class TankPeach extends TankAIControlled
{
	boolean suicidal = false;
	double timeUntilDeath = 500 + Math.random() * 250;

	public TankPeach(String name, double x, double y, double angle)
	{
		super(name, x, y, Game.tile_size, 255, 220, 140, angle, ShootAI.straight);

		this.enableMovement = true;
		this.maxSpeed = 0.75;
		this.enableMineLaying = false;
		this.enablePredictiveFiring = false;
		this.liveBulletMax = 1;
		this.cooldownBase = 200;
		this.cooldownRandom = 0;
		this.turret.size = 0;

		this.coinValue = 4;

		this.description = "A tank which adds extra health---to its allies and becomes---explosive as a last stand";
	}

	@Override
	public void postUpdate()
	{
		if (!this.suicidal)
		{
			boolean die = true;
			for (int i = 0; i < Game.movables.size(); i++)
			{
				Movable m = Game.movables.get(i);
				if (m != this && m.team == this.team && m.dealsDamage && !m.destroy)
				{
					die = false;
					break;
				}
			}

			if (die)
				this.suicidal = true;
		}

		if (this.suicidal)
		{
			this.timeUntilDeath -= Panel.frameFrequency;
			this.maxSpeed = 3 - 2 * Math.min(this.timeUntilDeath, 500) / 500;
			this.enableBulletAvoidance = false;
			this.enableMineAvoidance = false;
		}

		if (this.timeUntilDeath < 500)
		{
			this.colorG = this.timeUntilDeath / 500 * 255;
			this.colorB = this.timeUntilDeath / 500 * 255;

			if (this.timeUntilDeath < 150 && ((int) this.timeUntilDeath % 16) / 8 == 1)
			{
				this.colorR = 255;
				this.colorG = 255;
				this.colorB = 0;
			}

			Game.eventsOut.add(new EventTankUpdateColor(this));
		}

		if (this.timeUntilDeath <= 0)
		{
			Mine m = new Mine(this.posX, this.posY, 0, this);
			Game.eventsOut.add(new EventLayMine(m));
			Game.movables.add(m);
			this.destroy = true;
			this.health = 0;
		}
	}

	@Override
	public void shoot()
	{
		if (this.cooldown > 0)
			return;

		for (Movable m: Game.movables)
		{
			if (!(m instanceof TankPeach) && Movable.distanceBetween(m, this) < Game.tile_size * 8 && m instanceof Tank && ((Tank) m).health < ((Tank) m).baseHealth + 3 && Team.isAllied(this, m))
			{
				((Tank) m).health++;
			}
		}

		this.cooldown = this.cooldownBase;
	}

	@Override
	public void updateTarget()
	{
		if (this.suicidal)
		{
			super.updateTarget();
			return;
		}

		double nearestDist = Double.MAX_VALUE;
		Movable nearest = null;
		this.hasTarget = false;

		for (int i = 0; i < Game.movables.size(); i++)
		{
			Movable m = Game.movables.get(i);

			if (m instanceof Tank && m != this && Team.isAllied(this, m) && !((Tank) m).hidden && !((Tank) m).invulnerable && ((Tank) m).health - ((Tank) m).baseHealth < 3 && Team.isAllied(this, m))
			{
				Ray r = new Ray(this.posX, this.posY, this.getAngleInDirection(m.posX, m.posY), 0, this);
				r.moveOut(5);
				if (r.getTarget() != m)
					continue;

				double distance = Movable.distanceBetween(this, m);

				if (distance < nearestDist)
				{
					this.hasTarget = true;
					nearestDist = distance;
					nearest = m;
				}
			}
		}

		this.targetEnemy = nearest;
	}

	public void draw()
	{
		super.draw();
		Drawing.drawing.setColor(this.colorR, this.colorG, this.colorB, 128 * this.cooldown / this.cooldownBase);
		double size = (this.cooldownBase - this.cooldown) / this.cooldownBase * 500;
		Drawing.drawing.drawModel(WeeExtension.sphere, this.posX, this.posY, this.posZ, size, size, size, 0, 0, 0);
	}

	public void reactToTargetEnemySight()
	{
		if (this.suicidal && this.targetEnemy != null)
		{
			this.overrideDirection = true;
			this.setAccelerationInDirection(targetEnemy.posX, targetEnemy.posY, acceleration);
		}
	}

	public boolean isInterestingPathTarget(Movable m)
	{
		return m instanceof Tank && Team.isAllied(m, this) && m != this && ((Tank) m).health - ((Tank) m).baseHealth < 3 && !(m instanceof TankMedic);
	}

}