package tanks.event;

import io.netty.buffer.ByteBuf;
import tanks.Effect;
import tanks.Game;
import tanks.tank.Tank;
import tanks.tank.TankElectroKing;
import tanks.tank.TankRemote;

public class EventTankElectroKingElectrify extends PersonalEvent
{
    public int tank;

    public EventTankElectroKingElectrify()
    {

    }

    public EventTankElectroKingElectrify(int tank)
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

        for (int i = 0; i < t.size * 4 * Game.effectMultiplier; i++)
        {
            Effect e = Effect.createNewEffect(t.posX, t.posY, t.size / 4, Effect.EffectType.piece);
            double var = 50;

            e.colR = Math.min(255, Math.max(0, t.turret.colorR + Math.random() * var - var / 2));
            e.colG = Math.min(255, Math.max(0, t.turret.colorG + Math.random() * var - var / 2));
            e.colB = Math.min(255, Math.max(0, t.turret.colorB + Math.random() * var - var / 2));

            if (Game.enable3d)
                e.set3dPolarMotion(Math.random() * 2 * Math.PI, Math.random() * Math.PI, Math.random() * t.size / 50.0 * 4);
            else
                e.setPolarMotion(Math.random() * 2 * Math.PI, Math.random() * t.size / 50.0 * 4);

            Game.effects.add(e);
        }

        if (t instanceof TankRemote && ((TankRemote) t).tank instanceof TankElectroKing)
        {
            ((TankElectroKing) ((TankRemote) t).tank).shot = true;
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
