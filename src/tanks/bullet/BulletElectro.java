package tanks.bullet;

import tanks.*;
import tanks.event.EventBulletElectricStunEffect;
import tanks.hotbar.item.ItemBullet;
import tanks.tank.Tank;

public class BulletElectro extends Bullet
{
    public static String bullet_name = "electro";

    public BulletElectro(double x, double y, int bounces, Tank t)
    {
        this(x, y, bounces, t, true, null);
    }

    public BulletElectro(double x, double y, int bounces, Tank t, boolean affectsLiveBulletCount, ItemBullet ib)
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
    public BulletElectro(Double x, Double y, Integer bounces, Tank t, ItemBullet ib)
    {
        this(x.doubleValue(), y.doubleValue(), bounces.intValue(), t, true, ib);
    }

    @Override
    public void collidedWithTank(Tank t)
    {
        this.destroy = true;

        AttributeModifier c = new AttributeModifier("stun", "velocity", AttributeModifier.Operation.multiply, -1);
        c.duration = 200;
        c.deteriorationAge = 200;
        t.addUnduplicateAttribute(c);

        Drawing.drawing.playGlobalSound("laser.ogg", 2f);
        Game.eventsOut.add(new EventBulletElectricStunEffect(t.posX, t.posY, t.size / 2, 2));

        if (Game.effectsEnabled)
        {
            for (int i = 0; i < 25 * Game.effectMultiplier; i++)
            {
                Effect e = Effect.createNewEffect(t.posX, t.posY, t.size / 2, Effect.EffectType.stun);
                double var = 50;
                e.colR = Math.min(255, Math.max(0, 0 + Math.random() * var - var / 2));
                e.colG = Math.min(255, Math.max(0, 255 + Math.random() * var - var / 2));
                e.colB = Math.min(255, Math.max(0, 255 + Math.random() * var - var / 2));
                e.glowR = 0;
                e.glowG = 128;
                e.glowB = 128;
                e.maxAge *= 2;
                Game.effects.add(e);
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
