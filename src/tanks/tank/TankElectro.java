package tanks.tank;

import tanks.Drawing;
import tanks.Effect;
import tanks.Game;
import tanks.Panel;
import tanks.bullet.Bullet;
import tanks.bullet.BulletElectro;
import tanks.event.EventElectroGlow;
import tanks.event.EventShootBullet;
import tanks.event.EventTankElectroTeleport;
import tanks.event.EventTankUpdateColor;
import tanks.gui.screen.ScreenGame;

public class TankElectro extends TankAIControlled
{
    public double phaseTimerBase = 500;
    public double phaseTimerRandom = 200;
    public double phaseTimerCharge = 100;
    public boolean canTeleport = true;

    public double teleportTimerMax = 50;
    public double teleportTimer = 0;
    public double teleX = 0;
    public double teleY = 0;
    public double destX = 0;
    public double destY = 0;

    public double phaseTimer = phaseTimerBase + phaseTimerRandom * Math.random();

    public TankElectro(String name, double x, double y, double angle)
    {
        super(name, x, y, Game.tile_size, 0, 50, 200, angle, ShootAI.straight);
        this.cooldownBase = 25;
        this.cooldownRandom = 25;
        this.maxSpeed = 1.75;
        this.enableDefensiveFiring = true;
        this.bulletBounces = 0;
        this.aimTurretSpeed = 0.06;
        this.enablePathfinding = false;
        this.enableMineLaying = false;

        this.coinValue = 10;

        this.description = "An agressive, stunning,---teleporting tank";
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
                this.size = Game.tile_size;
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
                this.bulletSpeed = 6.25;

                if (this.turret.colorG != 150)
                {
                    Drawing.drawing.playGlobalSound("charge.ogg", 1f, 0.5f);
                    this.turret.colorG = 150;
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
                this.bulletSpeed = 3.125;

                if (this.turret.colorG == 150)
                {
                    this.turret.colorG = Turret.calculateSecondaryColor(this.colorG);
                    Game.eventsOut.add(new EventTankUpdateColor(this));
                }
            }
        }
    }

    public void draw()
    {
        if (this.teleportTimer > 0)
        {
            for (int i = 0; i < Game.tile_size; i++)
            {
                Drawing.drawing.setColor(i * 2.5, i * 5, 255, 20);

                if (Game.enable3d)
                    Drawing.drawing.fillOval(this.posX, this.posY, this.posZ, i, i, false, true);
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
    }

    public void addEffect()
    {
        if (!Game.effectsEnabled)
            return;

        Effect e = Effect.createNewEffect(this.posX, this.posY, this.posZ, Effect.EffectType.teleporterPiece);
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

    public void launchBullet(double offset)
    {
        Drawing.drawing.playGlobalSound("shoot.ogg", (float) (Bullet.bullet_size / this.bulletSize));

        Bullet b;

        if (this.phaseTimer <= this.phaseTimerCharge)
            b = new BulletElectro(this.posX, this.posY, this.bulletBounces, this);
        else
            b = new Bullet(this.posX, this.posY, this.bulletBounces, this);

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
}
