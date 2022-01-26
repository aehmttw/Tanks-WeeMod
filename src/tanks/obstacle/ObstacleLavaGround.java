package tanks.obstacle;

import tanks.Drawing;
import tanks.Game;
import tanks.Movable;
import tanks.tank.Tank;
import tanks.tank.TankRocketKing;

public class ObstacleLavaGround extends Obstacle
{
    public int maxHealth = 7;
    public int health = maxHealth;
    public int prevHealth = maxHealth;

    public ObstacleLavaGround(String name, double posX, double posY)
    {
        super(name, posX, posY);
        this.destructible = false;
        this.bulletCollision = false;
        this.tankCollision = false;
        this.replaceTiles = true;
        this.drawLevel = 0;
        this.checkForObjects = true;

        this.colorR = 255;
        this.colorG = 0;
        this.colorB = 0;

        this.description = "A ground tile that turns---to lava from rockets";
    }

    public boolean colorChanged()
    {
        boolean newCol = health != prevHealth;
        this.prevHealth = this.health;
        return newCol;
    }

    @Override
    public void draw()
    {
        if (!Game.enable3d)
        {
            if (this.health > 0)
                Drawing.drawing.setColor(this.colorR, this.colorG, this.colorB, (1 - 1.0 * this.health / maxHealth) * 255);
            else
                Drawing.drawing.setColor(255, 0, 0, 255, 1);

            Drawing.drawing.fillRect(this.posX, this.posY, Obstacle.draw_size, Obstacle.draw_size);
        }
    }

    @Override
    public void drawTile(double r, double g, double b, double d, double extra)
    {
        double frac = this.health * 1.0 / maxHealth;

        if (this.health > 0)
            Drawing.drawing.setColor(frac * r, frac * g, frac * b);
        else
        {
            Drawing.drawing.setColor(255, 0, 0, 255, 1);
            d = 0;
        }

        Drawing.drawing.fillBox(this, this.posX, this.posY, -extra, Game.tile_size, Game.tile_size, d + extra);
    }

    public double getTileHeight()
    {
        return 0;
    }

    @Override
    public void onObjectEntry(Movable m)
    {
        if (m instanceof Tank && !(m instanceof TankRocketKing) && this.health <= 0)
        {
            ((Tank) m).health = 0;
        }
    }
}
