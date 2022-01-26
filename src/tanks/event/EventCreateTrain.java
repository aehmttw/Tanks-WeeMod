package tanks.event;

import io.netty.buffer.ByteBuf;
import tanks.Game;
import tanks.obstacle.Obstacle;
import tanks.obstacle.ObstacleElectroPadOff;
import tanks.tank.TankTrain;

public class EventCreateTrain extends PersonalEvent
{
    public double posX;
    public double posY;
    public double vX;
    public double vY;

    public EventCreateTrain()
    {

    }

    public EventCreateTrain(TankTrain t)
    {
        this.posX = t.posX;
        this.posY = t.posY;
        this.vX = t.vX;
        this.vY = t.vY;
    }

    @Override
    public void write(ByteBuf b)
    {
        b.writeDouble(this.posX);
        b.writeDouble(this.posY);
        b.writeDouble(this.vX);
        b.writeDouble(this.vY);
    }

    @Override
    public void read(ByteBuf b)
    {
        this.posX = b.readDouble();
        this.posY = b.readDouble();
        this.vX = b.readDouble();
        this.vY = b.readDouble();
    }

    @Override
    public void execute()
    {
        if (this.clientID != null)
            return;

        TankTrain t = new TankTrain("train", this.posX, this.posY);
        t.vX = vX;
        t.vY = vY;
        Game.movables.add(t);
    }
}
