package tanks.tank;

import tanks.Game;
import tanks.Movable;
import tanks.Team;
import tanks.bullet.Bullet;

public class TankShotgun extends TankAIControlled
{
    public TankShotgun(String name, double x, double y, double angle)
    {
        super(name, x, y, Game.tile_size, 255, 160, 0, angle, ShootAI.straight);

        this.enableMovement = true;
        this.maxSpeed = 0.75;
        this.enableMineLaying = false;
        this.enablePredictiveFiring = false;
        this.enableDefensiveFiring = true;
        this.liveBulletMax = 1;
        this.cooldownRandom = 60;
        this.cooldownBase = 240;
        this.aimTurretSpeed = 0.02;
        this.bulletBounces = 0;
        this.bulletEffect = Bullet.BulletEffect.trail;
        this.enableLookingAtTargetEnemy = false;
        this.motionChangeChance = 0.001;
        this.turret.size *= 1.5;

        this.coinValue = 4;

        this.description = "A tank which shoots 3 bullets at once";
    }

    public void shoot()
    {
        this.aimTimer = 10;
        this.aim = false;

        double spread = Math.PI / 10;
        if (this.cooldown <= 0 && this.liveBullets < this.liveBulletMax && !this.disabled && !this.destroy)
        {
            boolean cancel = false;
            for (double offset = -spread; offset <= spread; offset += spread)
            {
                Ray a = new Ray(this.posX, this.posY, this.angle + offset, this.bulletBounces, this, 2.5);
                a.size = this.bulletSize;
                a.moveOut(this.size / 2.5);

                Movable m = a.getTarget();

                if (Team.isAllied(this, m))
                {
                    cancel = true;
                    break;
                }
            }

            if (!cancel)
            {
                for (double offset = -spread; offset <= spread; offset += spread)
                {
                    this.launchBullet(offset);
                }
            }
        }
    }
}
