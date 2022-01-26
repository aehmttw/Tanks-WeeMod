package tanks.event;

import io.netty.buffer.ByteBuf;
import tanks.Effect;
import tanks.Game;
import tanks.obstacle.Obstacle;
import tanks.obstacle.ObstacleLavaGround;
import tanks.obstacle.ObstacleShrubbery;

public class EventLavaGroundUpdate extends PersonalEvent
{
    public double posX;
    public double posY;
    public int health;

    public EventLavaGroundUpdate()
    {

    }

    public EventLavaGroundUpdate(double x, double y, int health)
    {
        this.posX = x;
        this.posY = y;
        this.health = health;
    }

    @Override
    public void write(ByteBuf b)
    {
        b.writeDouble(this.posX);
        b.writeDouble(this.posY);
        b.writeInt(this.health);
    }

    @Override
    public void read(ByteBuf b)
    {
        this.posX = b.readDouble();
        this.posY = b.readDouble();
        this.health = b.readInt();
    }

    @Override
    public void execute()
    {
        if (this.clientID != null)
            return;

        for (int i = 0; i < Game.obstacles.size(); i++)
        {
            Obstacle o = Game.obstacles.get(i);
            if (o instanceof ObstacleLavaGround && o.posX == this.posX && o.posY == this.posY)
            {
                ((ObstacleLavaGround) o).health = this.health;
            }
        }
    }
}
