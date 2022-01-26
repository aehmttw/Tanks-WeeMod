package tanks.bullet;

import tanks.*;
import tanks.event.EventBulletBounce;
import tanks.event.EventLavaGroundUpdate;
import tanks.hotbar.item.ItemBullet;
import tanks.obstacle.Obstacle;
import tanks.obstacle.ObstacleLavaGround;
import tanks.tank.Tank;

import java.util.ArrayList;

public class BulletRocket extends Bullet
{
    public static String bullet_name = "rocket";

    public ArrayList<Double> pastPosX = new ArrayList<>();
    public ArrayList<Double> pastPosY = new ArrayList<>();
    public ArrayList<Double> pastPosZ = new ArrayList<>();
    public ArrayList<Double> pastTime = new ArrayList<>();

    public double finalX;
    public double finalY;

    public BulletRocket(double x, double y, Tank t, boolean affectsMaxLiveBullets, ItemBullet ib)
    {
        super(x, y, 0, t, affectsMaxLiveBullets, ib);
        this.playPopSound = false;
        this.name = bullet_name;
        //this.effect = BulletEffect.trail;
        this.itemSound = "boost.ogg";

        this.enableExternalCollisions = false;
        this.playPopSound = false;
        this.playBounceSound = false;
        this.enableCollision = false;
        this.posZ = Game.tile_size / 2;
        this.maxDestroyTimer = 100;
        this.obstacleCollision = false;
        this.canBeCanceled = false;
        this.moveOut = false;
        this.vZ = 6.25;

        this.autoZ = false;
    }

    public BulletRocket(double x, double y, int bounces, Tank t)
    {
        this(x, y, t, true, null);
    }

    /**
     * Do not use, instead use the constructor with primitive data types.
     */
    @Deprecated
    public BulletRocket(Double x, Double y, Integer bounces, Tank t, ItemBullet ib)
    {
        this(x, y, t, true, ib);
    }

    @Override
    public void update()
    {
        super.update();

        if (this.posZ <= Game.tile_size / 2 && !this.destroy)
        {
            this.vX = 0;
            this.vY = 0;
            this.vZ = 0;

            if (!this.tank.isRemote)
                this.checkCollision();

            this.checkCollisionLocal();
            this.destroy = true;
            Drawing.drawing.playSound("bullet_explode.ogg", (float) (Bullet.bullet_size / this.size));
        }

        if (!this.destroy && Game.glowEnabled)
        {
            Effect e;

            if (Game.enable3d)
                e = Effect.createNewEffect(this.posX, this.posY, this.posZ, Effect.EffectType.piece);
            else
                e = Effect.createNewEffect(this.posX, this.posY - this.posZ, Effect.EffectType.piece);

            double var = 50;
            e.maxAge /= 4;
            e.colR = 255;
            e.colG = Math.min(255, Math.max(0, 180 + Math.random() * var - var / 2));
            e.colB = Math.min(255, Math.max(0, 64 + Math.random() * var - var / 2));

            double mul = -Math.signum(this.vZ);

            if (Game.enable3d)
                e.set3dPolarMotion(Math.random() * 2 * Math.PI, Math.random() * Math.PI * mul, Math.random() * this.size / 50.0 * 12);
            else
                e.setPolarMotion(-Math.random() * Math.PI * mul, Math.random() * this.size / 50.0 * 4);

            Game.effects.add(e);
        }

        if (this.posZ > 1100)
        {
            this.vZ = -this.vZ;
            this.posZ -= (this.posZ - 1100) * 2;

            if (!this.isRemote)
            {
                this.posX = this.finalX;
                this.posY = this.finalY;
                this.collisionX = this.finalX;
                this.collisionY = this.finalY;
                Game.eventsOut.add(new EventBulletBounce(this));
            }

            Drawing.drawing.playSound("beep.ogg", 1.25f);
        }

        if (!this.destroy)
        {
            this.pastPosX.add(this.posX);

            if (Game.enable3d)
                this.pastPosY.add(this.posY);
            else
                this.pastPosY.add(this.posY - this.posZ + 25);

            this.pastPosZ.add(this.posZ);
            this.pastTime.add(this.age);
        }
    }

    public void draw()
    {
        if (this.vZ < 0)
        {
            Drawing.drawing.setColor(0, 0, 0, (60 - this.posZ / 32) * (1 - Math.min(this.destroyTimer / 60, 1)));
            Drawing.drawing.fillGlow(this.posX, this.posY, this.size * 2, this.size * 2, true);
            Drawing.drawing.fillOval(this.posX, this.posY, this.size, this.size);
        }

        if (!Game.enable3d)
            this.posY -= this.posZ - Game.tile_size / 2;

        if (Game.bulletTrails)
        {
            boolean stop = false;
            double length = 100;
            Drawing.drawing.setColor(80, 80, 80, 64 * (1 - this.destroyTimer / this.maxDestroyTimer));

            if (Game.enable3d)
                Drawing.drawing.fillOval(this.posX, this.posY, this.posZ, this.size, this.size, true, false);
            else
                Drawing.drawing.fillOval(this.posX, this.posY, this.size, this.size);

            Drawing.drawing.setColor(0, 0, 0, 0, 0.5);
            Game.game.window.shapeRenderer.setBatchMode(true, true, true, false);
            for (int i = this.pastTime.size() - 1; i >= 1; i--)
            {
                if (stop)
                    break;

                double t = this.age - this.pastTime.get(i);

                if (t > length)
                    stop = true;

                double x = this.pastPosX.get(i);
                double y = this.pastPosY.get(i);
                double z = this.pastPosZ.get(i);

                double x1 = this.pastPosX.get(i - 1);
                double y1 = this.pastPosY.get(i - 1);
                double z1 = this.pastPosZ.get(i - 1);

                double a = Math.PI / 2;

                for (int j = 0; j < 16; j++)
                {
                    double ax = Math.cos(a);
                    double ay = Math.sin(a);

                    a += Math.PI / 8;
                    double ax2 = Math.cos(a);
                    double ay2 = Math.sin(a);

                    Drawing.drawing.setColor(80, 80, 80, (length - t) / length * 64, 0.5);
                    Drawing.drawing.addVertex(x + this.size / 2 * ax, y + this.size / 2 * ay, z);
                    Drawing.drawing.addVertex(x + this.size / 2 * ax2, y + this.size / 2 * ay2, z);

                    double t1 = Math.max(this.age - this.pastTime.get(i - 1), 0);
                    Drawing.drawing.setColor(80, 80, 80, (length - t1) / length * 64, 0.5);
                    Drawing.drawing.addVertex(x1 + this.size / 2 * ax2, y1 + this.size / 2 * ay2, z1);
                    Drawing.drawing.addVertex(x1 + this.size / 2 * ax, y1 + this.size / 2 * ay, z1);
                }
            }
            Game.game.window.shapeRenderer.setBatchMode(false, true, true, false);

            stop = false;
            Drawing.drawing.setColor(0, 0, 0, 0, 0.5);
            Game.game.window.shapeRenderer.setBatchMode(true, true, true, false);
            for (int i = this.pastTime.size() - 1; i >= 1; i--)
            {
                if (stop)
                    break;

                double t = this.age - this.pastTime.get(i);

                if (t > length * 0.1)
                    stop = true;

                double x = this.pastPosX.get(i);
                double y = this.pastPosY.get(i);
                double z = this.pastPosZ.get(i);

                double x1 = this.pastPosX.get(i - 1);
                double y1 = this.pastPosY.get(i - 1);
                double z1 = this.pastPosZ.get(i - 1);

                double a = Math.PI / 2;

                for (int j = 0; j < 16; j++)
                {
                    double ax = Math.cos(a);
                    double ay = Math.sin(a);

                    a += Math.PI / 8;
                    double ax2 = Math.cos(a);
                    double ay2 = Math.sin(a);

                    double v = (length * 0.1 - t) / (length * 0.1);
                    Drawing.drawing.setColor(255, v * 255, 0, v * 255, 0.5);
                    double frac = (t / 10 + 1) / 2;
                    Drawing.drawing.addVertex(x + this.size * frac * ax, y + this.size * frac * ay, z);
                    Drawing.drawing.addVertex(x + this.size * frac * ax2, y + this.size * frac * ay2, z);

                    double t1 = Math.max(this.age - this.pastTime.get(i - 1), 0);
                    double v1 = (length * 0.1 - t1) / (length * 0.1);
                    double frac2 = (t1 / 10 + 1) / 2;
                    Drawing.drawing.setColor(255, v1 * 255, 0, v1 * 255, 0.5);
                    Drawing.drawing.addVertex(x1 + this.size * frac2 * ax2, y1 + this.size * frac2 * ay2, z1);
                    Drawing.drawing.addVertex(x1 + this.size * frac2 * ax, y1 + this.size * frac2 * ay, z1);
                }
            }
            Game.game.window.shapeRenderer.setBatchMode(false, true, true, false);
        }

        if (this.destroyTimer <= 60)
            super.draw();

        if (!Game.enable3d)
            this.posY += this.posZ - Game.tile_size / 2;
    }

    @Override
    public void setTargetLocation(double x, double y)
    {
        this.vX = 0;
        this.vY = 0;
        this.finalX = x;
        this.finalY = y;
    }

    @Override
    public void onDestroy()
    {
        for (Obstacle o: Game.obstacles)
        {
            if (o instanceof ObstacleLavaGround && Math.pow(this.posX - o.posX, 2) + Math.pow(this.posY - o.posY, 2) < 22500)
            {
                ((ObstacleLavaGround) o).health--;
                Game.eventsOut.add(new EventLavaGroundUpdate(o.posX, o.posY, ((ObstacleLavaGround) o).health));
            }
        }
    }
}
