package tanks.tank;

import tanks.*;
import tanks.bullet.Bullet;
import tanks.bullet.BulletRocket;
import tanks.event.EventShootBullet;

public class TankRocketKing extends TankAIControlled
{
    public double strafeDirection = Math.PI / 2;
    public int phase = 3;
    public double rocketTimer = 400 + Math.random() * 30;

    public TankRocketKing(String name, double x, double y, double angle)
    {
        super(name, x, y, Game.tile_size, 0, 0, 0, angle, ShootAI.straight);
        this.size *= 1.5;
        this.turret.length *= 1.5;
        this.turret.colorR = 140;
        this.cooldownBase = 60;
        this.cooldownRandom = 0;
        this.maxSpeed = 0.75;
        this.enableDefensiveFiring = true;
        this.bulletSpeed = 25.0 / 4;
        this.bulletBounces = 0;
        this.bulletEffect = Bullet.BulletEffect.fire;
        this.aimTurretSpeed = 0.06;
        this.enablePathfinding = false;
        this.baseHealth = 25;
        this.health = 25;
        this.enableMineLaying = false;

        this.coinValue = 40;

        this.description = "A smart boss tank---which fires and lobs rockets";
    }

    @Override
    public void reactToTargetEnemySight()
    {
        if (Math.random() < 0.01)
            strafeDirection = -strafeDirection;

        this.setAccelerationInDirectionWithOffset(Game.playerTank.posX, Game.playerTank.posY, 3.5, strafeDirection);
    }

    @Override
    public void postUpdate()
    {
        if (this.hasTarget && this.targetEnemy != null)
        {
            this.rocketTimer -= Panel.frameFrequency;

            if (this.rocketTimer <= 0)
            {
                if (phase == 0)
                {
                    this.rocketTimer = 400 + Math.random() * 30;
                    phase = 3;
                }
                else
                {
                    phase--;
                    this.rocketTimer = 50;
                }

                double a = Math.random() * Math.PI * 2;
                double d = Math.pow(Math.random(), 2) * Game.tile_size * 3;

                double x = this.targetEnemy.posX + d * Math.cos(a);
                double y = this.targetEnemy.posY + d * Math.sin(a);

                for (Movable m: Game.movables)
                {
                    if (m instanceof Tank && Team.isAllied(this, m) && Math.sqrt(Math.pow(m.posX - x, 2) + Math.pow(m.posY - y, 2)) < Game.tile_size)
                        return;
                }

                BulletRocket r = new BulletRocket(this.posX, this.posY, 0, this);
                r.size = 20;
                r.setTargetLocation(x, y);
                Game.movables.add(r);
                Game.eventsOut.add(new EventShootBullet(r));
                Drawing.drawing.playGlobalSound("boost.ogg", 0.5f);
            }
        }
    }
}
