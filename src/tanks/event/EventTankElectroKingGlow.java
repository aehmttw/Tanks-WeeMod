package tanks.event;

import io.netty.buffer.ByteBuf;
import tanks.Effect;
import tanks.Game;
import tanks.tank.Tank;
import tanks.tank.TankElectroKing;
import tanks.tank.TankRemote;

public class EventTankElectroKingGlow extends PersonalEvent
{
    public int tank;
    public double charge;

    public EventTankElectroKingGlow()
    {

    }

    public EventTankElectroKingGlow(int tank, double charge)
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

        if (!(t instanceof TankRemote && ((TankRemote) t).tank instanceof TankElectroKing))
            return;

        ((TankElectroKing) ((TankRemote) t).tank).glowOverride = charge;
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
