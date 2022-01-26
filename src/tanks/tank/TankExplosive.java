package tanks.tank;

import tanks.Drawing;
import tanks.Game;
import tanks.bullet.Bullet;
import tanks.bullet.BulletExplosive;
import tanks.event.EventShootBullet;

public class TankExplosive extends TankOrangeRed
{
    public TankExplosive(String name, double x, double y, double angle)
    {
        super(name, x, y, angle);
        this.colorR = 60;
        this.colorG = 60;
        this.colorB = 60;

        this.turret.colorR = Turret.calculateSecondaryColor(this.colorR);
        this.turret.colorG = Turret.calculateSecondaryColor(this.colorG);
        this.turret.colorB = Turret.calculateSecondaryColor(this.colorB);

        this.health = 3;
        this.baseHealth = 3;
        this.coinValue = 10;
        this.bulletHeavy = true;
    }

    @Override
    public void launchBullet(double offset)
    {
        Drawing.drawing.playGlobalSound("shoot.ogg");

        Bullet b = new BulletExplosive(this.posX, this.posY, this.bulletBounces, this);
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
