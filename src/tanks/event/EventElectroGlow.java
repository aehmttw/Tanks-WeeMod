package tanks.event;

import io.netty.buffer.ByteBuf;
import tanks.Effect;
import tanks.Game;
import tanks.tank.Tank;

public class EventElectroGlow extends PersonalEvent
{
    public int tank;

    public EventElectroGlow()
    {

    }

    public EventElectroGlow(Tank tank)
    {
        this.tank = tank.networkID;
    }

    @Override
    public void execute()
    {
        if (Game.effectsEnabled && this.clientID == null)
        {
            Tank t = Tank.idMap.get(tank);

            if (t == null)
                return;

            Effect e = Effect.createNewEffect(t.posX, t.posY, t.posZ, Effect.EffectType.teleporterPiece);
            double var = 50;

            e.colR = Math.min(255, Math.max(0, 0 + Math.random() * var - var / 2));
            e.colG = Math.min(255, Math.max(0, 200 + Math.random() * var - var / 2));
            e.colB = Math.min(255, Math.max(0, 255 + Math.random() * var - var / 2));

            if (Game.enable3d)
                e.set3dPolarMotion(Math.random() * 2 * Math.PI, Math.random() * Math.PI * 2, Math.random() * 4);
            else
                e.setPolarMotion(Math.random() * 2 * Math.PI, Math.random() * 4);

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
