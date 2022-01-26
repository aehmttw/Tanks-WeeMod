package tanks.event;

import io.netty.buffer.ByteBuf;
import tanks.Effect;
import tanks.Game;
import tanks.Panel;
import tanks.tank.Tank;

public class EventTankBoostEffect extends PersonalEvent
{
    public int tank;

    public EventTankBoostEffect()
    {

    }

    public EventTankBoostEffect(int tank)
    {
        this.tank = tank;
    }

    @Override
    public void execute()
    {
        if (this.clientID != null)
            return;

        Tank t = Tank.idMap.get(this.tank);

        if (t == null)
            return;

        if (Math.random() * Game.effectMultiplier < Panel.frameFrequency && Game.effectsEnabled)
        {
            Effect e = Effect.createNewEffect(t.posX, t.posY, Game.tile_size / 2, Effect.EffectType.piece);
            double var = 50;

            e.colR = Math.min(255, Math.max(0, 255 + Math.random() * var - var / 2));
            e.colG = Math.min(255, Math.max(0, 180 + Math.random() * var - var / 2));
            e.colB = Math.min(255, Math.max(0, 0 + Math.random() * var - var / 2));

            if (Game.enable3d)
                e.set3dPolarMotion(Math.random() * 2 * Math.PI, Math.random() * Math.PI, Math.random());
            else
                e.setPolarMotion(Math.random() * 2 * Math.PI, Math.random());

            Game.effects.add(e);
        }
    }

    @Override
    public void write(ByteBuf b)
    {
        b.writeInt(this.tank);
    }

    @Override
    public void read(ByteBuf b)
    {
        this.tank = b.readInt();
    }
}
