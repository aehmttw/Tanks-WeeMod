package tanks.event;

import io.netty.buffer.ByteBuf;
import tanks.Effect;
import tanks.Game;
import tanks.tank.Tank;

public class EventTankElectroKingCharge extends PersonalEvent
{
    public int tank;
    public double charge;

    public EventTankElectroKingCharge()
    {

    }

    public EventTankElectroKingCharge(int tank, double charge)
    {
        this.tank = tank;
        this.charge = charge;
    }

    @Override
    public void execute()
    {
        if (this.clientID != null)
            return;

        Tank t = Tank.idMap.get(this.tank);

        if (t == null)
            return;

        if (Math.random() * Game.effectMultiplier < charge && Game.effectsEnabled)
        {
            Effect e = Effect.createNewEffect(t.posX, t.posY, t.size / 4, Effect.EffectType.charge);
            e.maxAge *= 2;
            e.posX -= t.posX - e.posX;
            e.posY -= t.posY - e.posY;
            e.posZ -= t.posZ - e.posZ;

            double var = 50;
            e.colR = Math.min(255, Math.max(0, t.turret.colorR + Math.random() * var - var / 2));
            e.colG = Math.min(255, Math.max(0, t.turret.colorG + Math.random() * var - var / 2));
            e.colB = Math.min(255, Math.max(0, t.turret.colorB + Math.random() * var - var / 2));

            Game.effects.add(e);
        }
    }

    @Override
    public void write(ByteBuf b)
    {
        b.writeInt(this.tank);
        b.writeDouble(this.charge);
    }

    @Override
    public void read(ByteBuf b)
    {
        this.tank = b.readInt();
        this.charge = b.readDouble();
    }
}
