package tanks.event;

import io.netty.buffer.ByteBuf;
import tanks.Game;
import tanks.tank.ElectroTeleport;
import tanks.tank.Tank;

public class EventTankElectroTeleport extends PersonalEvent
{
    public double posX;
    public double posY;
    public double destX;
    public double destY;
    public int tank;

    public EventTankElectroTeleport()
    {

    }

    public EventTankElectroTeleport(double x, double y, double dx, double dy, Tank tank)
    {
        this.posX = x;
        this.posY = y;
        this.destX = dx;
        this.destY = dy;
        this.tank = tank.networkID;
    }

    @Override
    public void execute()
    {
        Game.movables.add(new ElectroTeleport(this.posX, this.posY, this.destX, this.destY, Tank.idMap.get(this.tank)));
    }

    @Override
    public void write(ByteBuf b)
    {
        b.writeDouble(this.posX);
        b.writeDouble(this.posY);
        b.writeDouble(this.destX);
        b.writeDouble(this.destY);
        b.writeInt(this.tank);
    }

    @Override
    public void read(ByteBuf b)
    {
        this.posX = b.readDouble();
        this.posY = b.readDouble();
        this.destX = b.readDouble();
        this.destY = b.readDouble();
        this.tank = b.readInt();
    }
}
