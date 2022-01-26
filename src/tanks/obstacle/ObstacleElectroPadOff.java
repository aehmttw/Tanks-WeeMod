package tanks.obstacle;

import tanks.*;
import tanks.bullet.Bullet;
import tanks.event.EventBulletElectricStunEffect;
import tanks.gui.screen.ScreenGame;
import tanks.tank.Tank;
import tanks.tank.TankElectroKing;
import tanks.tank.TankRemote;

public class ObstacleElectroPadOff extends Obstacle
{
    public double onTimer = 0;

    public boolean warmingUp = false;
    public double warmupTimer = 300;

    public double prevColor = 0;

    public ObstacleElectroPadOff(String name, double posX, double posY)
    {
        super(name, posX, posY);

        this.drawLevel = 1;

        this.destructible = false;
        this.tankCollision = false;
        this.bulletCollision = false;
        this.checkForObjects = true;
        this.enableStacking = false;

        this.isSurfaceTile = true;
        this.update = true;
        this.replaceTiles = true;

        this.colorR = 0;
        this.colorG = 0;
        this.colorB = 60;

        this.description = "A electric panel powered---on by electric rockets---or the electro king";
    }

    public void drawTile(double r, double g, double b, double d, double extra)
    {
        Drawing.drawing.setColor(r, g, b);
        Drawing.drawing.fillBox(this, this.posX, this.posY, -extra, Game.tile_size, Game.tile_size, extra + d * (1 - Obstacle.draw_size / Game.tile_size));
    }

    @Override
    public void onObjectEntryLocal(Movable m)
    {
        if (this.onTimer <= 0)
            return;

        boolean effect = true;

        if (m instanceof Bullet)
        {
            for (AttributeModifier am : m.attributes)
            {
                if (am.name.equals("boost_glow"))
                    effect = false;
            }

            if (effect)
                Drawing.drawing.playSound("boost.ogg");

            if (Game.effectsEnabled && !ScreenGame.finished)
            {
                if (effect)
                {
                    for (int i = 0; i < 25 * Game.effectMultiplier; i++)
                    {
                        this.addEffect(m.posX, m.posY, 0.5);
                    }
                }
                else if (Math.random() < Panel.frameFrequency * Game.effectMultiplier)
                    this.addEffect(m.posX, m.posY, 0);
            }
        }
    }

    @Override
    public void onObjectEntry(Movable m)
    {
        if (this.onTimer <= 0)
            return;

        this.onObjectEntryLocal(m);

        if (m instanceof Tank)
        {
            if (m instanceof TankElectroKing || m instanceof TankRemote)
                return;

            boolean skip = false;
            for (AttributeModifier a: m.attributes)
            {
                if (a.name.equals("electropad_time"))
                {
                    skip = true;
                    break;
                }
            }

            if (!skip)
            {
                AttributeModifier d = new AttributeModifier("electropad_stun", "velocity", AttributeModifier.Operation.multiply, -1);
                d.duration = 200;
                d.deteriorationAge = 200;
                m.addUnduplicateAttribute(d);

                Drawing.drawing.playSound("laser.ogg", 2f);
                Game.eventsOut.add(new EventBulletElectricStunEffect(m.posX, m.posY, ((Tank)m).size / 2, 2));

                if (Game.effectsEnabled)
                {
                    for (int i = 0; i < 25 * Game.effectMultiplier; i++)
                    {
                        Effect e = Effect.createNewEffect(m.posX, m.posY, ((Tank)m).size / 2, Effect.EffectType.stun);
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

            AttributeModifier c = new AttributeModifier("electropad_time", "", AttributeModifier.Operation.add, 1);
            c.duration = 100;
            c.deteriorationAge = 100;
            m.addUnduplicateAttribute(c);
        }
        else
        {
            AttributeModifier c = new AttributeModifier("boost_speed", "velocity", AttributeModifier.Operation.multiply, 1);
            c.duration = -1;
            c.deteriorationAge = -1;
            m.addUnduplicateAttribute(c);

            AttributeModifier a = new AttributeModifier("boost_glow", "glow", AttributeModifier.Operation.multiply, 1);
            a.duration = -1;
            a.deteriorationAge = -1;
            m.addUnduplicateAttribute(a);

            AttributeModifier b = new AttributeModifier("boost_slip", "friction", AttributeModifier.Operation.multiply, -0.75);
            b.duration = -1;
            b.deteriorationAge = -1;
            m.addUnduplicateAttribute(b);
        }
    }

    public boolean colorChanged()
    {
        double c = this.colorR + this.colorG * 1000 + this.colorB * 1000000;

        if (c != this.prevColor)
        {
            this.prevColor = c;
            return true;
        }

        return false;
    }

    @Override
    public void draw()
    {
        double f = Panel.frameFrequency * 2;
        if (this.onTimer > 0)
        {
            this.colorR = Math.min(this.colorR + f, 180);
            this.colorG = Math.min(this.colorG + f, 225);
            this.colorB = Math.min(this.colorB + f, 255);
        }
        else if (this.warmingUp)
        {
            this.colorR = Math.min(this.colorR + f, 90);
            this.colorG = Math.min(this.colorG + f, 113);
            this.colorB = Math.min(this.colorB + f, 168);
        }
        else
        {
            this.colorR = Math.max(this.colorR - f, 0);
            this.colorG = Math.max(this.colorG - f, 0);
            this.colorB = Math.max(this.colorB - f, 80);
        }

        if (!Game.enable3d)
        {
            Drawing.drawing.setColor(0, 0, 127, 255, 0.5);
            Drawing.drawing.fillRect(this, this.posX, this.posY, Obstacle.draw_size * 0.8, Obstacle.draw_size * 0.8);
            Drawing.drawing.setColor(this.colorR, this.colorG, this.colorB, 255, 1.0);
            Drawing.drawing.fillRect(this, this.posX, this.posY, Obstacle.draw_size * 0.95, Obstacle.draw_size * 0.95);

            //if (Game.glowEnabled)
            //    Drawing.drawing.fillGlow(this.posX, this.posY, Obstacle.draw_size * 2, Obstacle.draw_size * 2);
        }
        else
        {
            Drawing.drawing.setColor(0, 0, 127, 255, 0.5);
            Drawing.drawing.fillBox(this, this.posX, this.posY, 0, Obstacle.draw_size * 0.95, Obstacle.draw_size * 0.95, 10);
            Drawing.drawing.setColor(this.colorR, this.colorG, this.colorB, 255, 1.0);
            Drawing.drawing.fillBox(this, this.posX, this.posY, 0, Obstacle.draw_size * 0.8, Obstacle.draw_size * 0.8, 11);

            //if (Game.glowEnabled)
            //    Drawing.drawing.fillGlow(this.posX, this.posY, 10, Obstacle.draw_size * 2, Obstacle.draw_size * 2);
        }
    }

    public void update()
    {
        this.onTimer -= Panel.frameFrequency;

        if (this.warmingUp)
        {
            this.warmupTimer -= Panel.frameFrequency;

            if (this.warmupTimer <= 0)
            {
                this.onTimer = 300;
                this.warmupTimer = 300;
                this.warmingUp = false;
            }
        }
    }

    public void addEffect(double x, double y, double extra)
    {
        Effect e = Effect.createNewEffect(x, y, Game.tile_size / 2, Effect.EffectType.piece);
        double var = 50;

        e.colR = Math.min(255, Math.max(0, this.colorR + Math.random() * var - var / 2));
        e.colG = Math.min(255, Math.max(0, this.colorG + Math.random() * var - var / 2));
        e.colB = Math.min(255, Math.max(0, this.colorB + Math.random() * var - var / 2));

        if (Game.enable3d)
            e.set3dPolarMotion(Math.random() * 2 * Math.PI, Math.random() * Math.PI, Math.random() + extra);
        else
            e.setPolarMotion(Math.random() * 2 * Math.PI, Math.random() + extra);

        Game.effects.add(e);
    }

    public double getTileHeight()
    {
        return 0;
    }
}
