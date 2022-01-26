package tanks.event;

import io.netty.buffer.ByteBuf;
import tanks.Game;
import tanks.obstacle.Obstacle;
import tanks.obstacle.ObstacleElectroPadOff;
import tanks.obstacle.ObstacleLavaGround;

public class EventElectroPadActivate extends PersonalEvent
{
    public double posX;
    public double posY;
    public boolean warmUp;

    public EventElectroPadActivate()
    {

    }

    public EventElectroPadActivate(double x, double y, boolean warmUp)
    {
        this.posX = x;
        this.posY = y;
        this.warmUp = warmUp;
    }

    @Override
    public void write(ByteBuf b)
    {
        b.writeDouble(this.posX);
        b.writeDouble(this.posY);
        b.writeBoolean(this.warmUp);
    }

    @Override
    public void read(ByteBuf b)
    {
        this.posX = b.readDouble();
        this.posY = b.readDouble();
        this.warmUp = b.readBoolean();
    }

    @Override
    public void execute()
    {
        if (this.clientID != null)
            return;

        for (int i = 0; i < Game.obstacles.size(); i++)
        {
            Obstacle o = Game.obstacles.get(i);
            if (o instanceof ObstacleElectroPadOff && o.posX == this.posX && o.posY == this.posY)
            {
                if (warmUp)
                    ((ObstacleElectroPadOff) o).warmingUp = true;
                else
                    ((ObstacleElectroPadOff) o).onTimer = 300;
            }
        }
    }
}
