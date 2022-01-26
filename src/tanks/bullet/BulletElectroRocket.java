package tanks.bullet;

import tanks.AttributeModifier;
import tanks.Drawing;
import tanks.Effect;
import tanks.Game;
import tanks.event.EventBulletElectricStunEffect;
import tanks.event.EventElectroPadActivate;
import tanks.hotbar.item.ItemBullet;
import tanks.obstacle.Obstacle;
import tanks.obstacle.ObstacleElectroPadOff;
import tanks.obstacle.ObstacleLavaGround;
import tanks.tank.Tank;

public class BulletElectroRocket extends Bullet
{
    public static String bullet_name = "electro_rocket";

    public BulletElectroRocket(double x, double y, int bounces, Tank t)
    {
        this(x, y, bounces, t, true, null);
    }

    public BulletElectroRocket(double x, double y, int bounces, Tank t, boolean affectsLiveBulletCount, ItemBullet ib)
    {
        super(x, y, bounces, t, affectsLiveBulletCount, ib);
        this.outlineColorR = 0;
        this.outlineColorG = 255;
        this.outlineColorB = 255;
        this.damage = 0;
        this.name = bullet_name;
    }

    /** Do not use, instead use the constructor with primitive data types. Intended for Item use only!*/
    @Deprecated
    public BulletElectroRocket(Double x, Double y, Integer bounces, Tank t, ItemBullet ib)
    {
        this(x.doubleValue(), y.doubleValue(), bounces.intValue(), t, true, ib);
    }

    @Override
    public void onDestroy()
    {
        for (Obstacle o: Game.obstacles)
        {
            if (o instanceof ObstacleElectroPadOff && Math.pow(this.posX - o.posX, 2) + Math.pow(this.posY - o.posY, 2) < 5625)
            {
                ((ObstacleElectroPadOff) o).onTimer = 300;
                Game.eventsOut.add(new EventElectroPadActivate(o.posX, o.posY, false));
            }
        }
    }

    @Override
    public void draw()
    {
        if (Game.glowEnabled)
        {
            Drawing.drawing.setColor(0, 60, 255, 180 * Math.max(0, 1 - this.destroyTimer / 60.0), 1);

            if (Game.enable3d)
                Drawing.drawing.fillGlow(this.posX, this.posY, this.posZ, this.size * 8, this.size * 8);
            else
                Drawing.drawing.fillGlow(this.posX, this.posY, this.size * 8, this.size * 8);
        }

        super.draw();
    }
}
