package tanks.tank;

import tanks.*;
import tanks.bullet.Bullet;
import tanks.bullet.BulletExplosive;
import tanks.event.EventShootBullet;
import tanks.event.EventTankUpdateColor;

public class TankCommanderBoom extends TankAIControlled
{
    public TankCommanderBoom(String name, double x, double y, double angle)
    {
        super(name, x, y, Game.tile_size * 2, 100, 50, 200, angle, ShootAI.wander);

        this.turret.length *= 2;
        this.turret.colorR = 200;
        this.turret.colorG = 120;
        this.turret.colorB = 220;
        this.enableMovement = true;
        this.enableBulletAvoidance = false;
        this.maxSpeed = 0.75;
        this.enableMineLaying = false;
        this.enablePredictiveFiring = false;
        this.cooldownBase = 240;
        this.aimTurretSpeed = 0.02;
        this.bulletBounces = 0;
        this.bulletEffect = Bullet.BulletEffect.fire;
        this.bulletSpeed = 25.0 / 4;
        this.enableLookingAtTargetEnemy = false;
        this.motionChangeChance = 0.001;
        this.avoidSensitivity = 1;
        this.baseHealth = 40;
        this.health = 40;
        this.turretIdleTimerBase = Double.MAX_VALUE;
        this.idleTimer = Double.MAX_VALUE;
        this.idlePhase = RotationPhase.clockwise;
        this.idleTurretSpeed *= 0.6;
        this.liveBulletMax = 16;

        this.coinValue = 40;

        this.description = "A boss tank which has 8 barrels";
    }

    @Override
    public void update()
    {
        this.bulletSize = 10;
        this.bulletHeavy = false;
        this.bulletSpeed = 25.0 / 4;
        this.bulletEffect = Bullet.BulletEffect.fire;

        if (this.health < this.baseHealth * 1 / 3)
        {
            this.bulletEffect = Bullet.BulletEffect.trail;
            this.bulletSpeed = 25.0 / 8;
            this.cooldownBase = 240;
            this.bulletSize = 20;
            this.bulletHeavy = true;
        }
        else if (this.health < this.baseHealth * 2 / 3)
        {
            this.cooldownBase = 120;
        }

        super.update();
    }

    @Override
    public void drawTurret(boolean forInterface, boolean in3d, boolean transparent)
    {
        for (int i = 0; i < 8; i++)
        {
            super.drawTurret(forInterface, in3d, transparent);
            this.angle += Math.PI / 4;
        }
    }

    public void updateTurretWander()
    {
        this.shoot();

        if (this.idlePhase == RotationPhase.clockwise)
            this.angle += this.idleTurretSpeed * Panel.frameFrequency;
        else
            this.angle -= this.idleTurretSpeed * Panel.frameFrequency;

        this.idleTimer -= Panel.frameFrequency;

        if (idleTimer <= 0)
        {
            this.idleTimer = Math.random() * turretIdleTimerRandom + turretIdleTimerBase;
            if (this.idlePhase == RotationPhase.clockwise)
                this.idlePhase = RotationPhase.counterClockwise;
            else
                this.idlePhase = RotationPhase.clockwise;
        }
    }

    /** Prepare to fire a bullet*/
    public void shoot()
    {
        this.aimTimer = 10;
        this.aim = false;

        if (this.cooldown <= 0 && this.liveBullets < this.liveBulletMax && !this.disabled && !this.destroy)
        {
            boolean stop = false;
            for (int i = 0; i < 8; i++)
            {
                Ray a = new Ray(this.posX, this.posY, this.angle + i * Math.PI / 4, this.bulletBounces, this, 2.5);
                a.size = this.bulletSize;
                a.moveOut(this.size / 2.5);

                Movable m = a.getTarget();

                if (Team.isAllied(this, m))
                {
                    stop = true;
                    break;
                }
            }

            if (!stop)
            {
                for (int i = 0; i < 8; i++)
                {
                    this.launchBullet(i * Math.PI / 4);
                }
            }
        }
    }

    public void launchBullet(double offset)
    {
        Drawing.drawing.playGlobalSound("shoot.ogg", (float) (Bullet.bullet_size / this.bulletSize));

        Bullet b;

        if (this.health < this.baseHealth * 1 / 3)
            b = new BulletExplosive(this.posX, this.posY, this.bulletBounces, this);
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
