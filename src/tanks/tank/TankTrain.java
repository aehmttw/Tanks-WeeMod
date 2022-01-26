package tanks.tank;

import tanks.*;

import java.util.ArrayList;

public class TankTrain extends Tank
{
    public double age = 0;
    public double iPosX;
    public double iPosY;
    public double iVX;
    public double iVY;

    public TankTrain(String name, double x, double y)
    {
        super(name, x, y, Game.tile_size, 190, 120, 80, false);
        this.invulnerable = true;
        this.targetable = false;
        this.team = null;
        this.drawAge = 50;

        this.iPosX = x;
        this.iPosY = y;
    }

    public void checkCollision()
    {
        if (this.size <= 0)
            return;

        for (int i = 0; i < Game.movables.size(); i++)
        {
            Movable o = Game.movables.get(i);
            if (this != o && o instanceof Tank && ((Tank)o).size > 0 && !((Tank) o).invulnerable)
            {
                Tank t = (Tank) o;
                double distSq = Math.pow(this.posX - o.posX, 2) + Math.pow(this.posY - o.posY, 2);

                if (distSq <= Math.pow((this.size + t.size) / 2, 2))
                    t.health = 0;
            }
        }

        hasCollided = false;
    }

    public void update()
    {
        this.posX = this.iPosX + this.age * this.iVX;
        this.posY = this.iPosY + this.age * this.iVY;

        if (this.age == 0)
        {
            this.iVX = this.vX;
            this.iVY = this.vY;
        }
        else
        {
            this.vX = this.iVX;
            this.vY = this.iVY;
        }

        this.age += Panel.frameFrequency;
        this.angle = this.getPolarDirection();

        if (this.posX > (Game.currentSizeX + 0.5) * Game.tile_size || this.posX < -Game.tile_size / 2 ||
                this.posY > (Game.currentSizeY + 0.5) * Game.tile_size || this.posY < -Game.tile_size / 2 && !this.isRemote)
            Game.removeMovables.add(this);

        this.checkCollision();

        ArrayList<Team> aliveTeams = new ArrayList<>();
        for (int i = 0; i < Game.movables.size(); i++)
        {
            Movable m = Game.movables.get(i);

            if (m instanceof Tank && !(m instanceof TankTrain))
            {
                Team t;

                if (m.team == null)
                {
                    if (m instanceof TankPlayer)
                        t = new Team(Game.clientID.toString());
                    else if (m instanceof TankPlayerRemote)
                        t = new Team(((TankPlayerRemote) m).player.clientID.toString());
                    else
                        t = new Team("*");
                }
                else
                    t = m.team;

                if (!aliveTeams.contains(t))
                    aliveTeams.add(t);
            }
        }

        if (aliveTeams.size() <= 1)
            this.destroy = true;

        if (this.destroy)
        {
            this.destroyTimer += Panel.frameFrequency;
            if (this.destroyTimer > 60)
                Game.removeMovables.add(this);
        }
    }
}
