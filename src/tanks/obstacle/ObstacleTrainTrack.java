package tanks.obstacle;

import tanks.Drawing;
import tanks.Effect;
import tanks.Game;
import tanks.Panel;
import tanks.event.EventCreateCustomTank;
import tanks.event.EventCreateTrain;
import tanks.gui.screen.ScreenGame;
import tanks.gui.screen.ScreenPartyLobby;
import tanks.tank.TankTrain;

public class ObstacleTrainTrack extends Obstacle
{
    public double timer = Math.random() * 500 + 200;
    public double spawnTimer = 0;
    public int count = 0;

    public ObstacleTrainTrack(String name, double posX, double posY)
    {
        super(name, posX, posY);

        this.drawLevel = 1;

        this.destructible = false;
        this.tankCollision = false;
        this.bulletCollision = false;
        this.checkForObjects = true;
        this.enableStacking = false;

        this.replaceTiles = false;
        this.isSurfaceTile = true;
        this.update = true;

        this.colorR = 127;
        this.colorG = 127;
        this.colorB = 127;

        this.description = "A train track which---summons trains";
    }

    public void draw()
    {
        if (Game.enable3d)
        {
            Drawing.drawing.setColor(120, 80, 0);
            Drawing.drawing.fillBox(this, this.posX - Game.tile_size / 3, this.posY, 5, Game.tile_size / 8, Obstacle.draw_size, 10);
            Drawing.drawing.fillBox(this, this.posX, this.posY, 10, Game.tile_size / 8, Obstacle.draw_size, 10);
            Drawing.drawing.fillBox(this, this.posX + Game.tile_size / 3, this.posY, 5, Game.tile_size / 8, Obstacle.draw_size, 10);

            Drawing.drawing.setColor(180, 180, 180);
            Drawing.drawing.fillBox(this, this.posX, this.posY - Obstacle.draw_size * 3 / 8, 10, Game.tile_size, Obstacle.draw_size / 12, 10);
            Drawing.drawing.fillBox(this, this.posX, this.posY + Obstacle.draw_size * 3 / 8, 10, Game.tile_size, Obstacle.draw_size / 12, 10);
        }
        else
        {
            Drawing.drawing.setColor(120, 80, 0);
            Drawing.drawing.fillRect(this, this.posX - Game.tile_size / 3, this.posY, Game.tile_size / 8, Obstacle.draw_size);
            Drawing.drawing.fillRect(this, this.posX, this.posY, Game.tile_size / 8, Obstacle.draw_size);
            Drawing.drawing.fillRect(this, this.posX + Game.tile_size / 3, this.posY, Game.tile_size / 8, Obstacle.draw_size);

            Drawing.drawing.setColor(180, 180, 180);
            Drawing.drawing.fillRect(this, this.posX, this.posY - Obstacle.draw_size * 3 / 8, Game.tile_size, Obstacle.draw_size / 12);
            Drawing.drawing.fillRect(this, this.posX, this.posY + Obstacle.draw_size * 3 / 8, Game.tile_size, Obstacle.draw_size / 12);
        }
    }

    public void drawForInterface(double x, double y)
    {
        Drawing.drawing.setColor(120, 80, 0);
        Drawing.drawing.fillInterfaceRect(x - Game.tile_size / 3, y, Game.tile_size / 8, Obstacle.draw_size);
        Drawing.drawing.fillInterfaceRect(x, y, Game.tile_size / 8, Obstacle.draw_size);
        Drawing.drawing.fillInterfaceRect(x + Game.tile_size / 3, y, Game.tile_size / 8, Obstacle.draw_size);

        Drawing.drawing.setColor(180, 180, 180);
        Drawing.drawing.fillInterfaceRect(x, y - Obstacle.draw_size * 3 / 8, Game.tile_size, Obstacle.draw_size / 12);
        Drawing.drawing.fillInterfaceRect(x, y + Obstacle.draw_size * 3 / 8, Game.tile_size, Obstacle.draw_size / 12);
    }

    public void update()
    {
        if (ScreenGame.finishedQuick || ScreenPartyLobby.isClient)
            return;

        if (count <= 0)
        {
            this.timer -= Panel.frameFrequency;
        }

        if (timer <= 0 && count <= 0)
        {
            this.timer = Math.random() * 500 + 200;
            this.spawnTimer = 100;
            if ((this.posX < Game.tile_size && this.posY > Game.tile_size / 2 * Game.currentSizeY) ||
                    (this.posX > Game.currentSizeX * Game.tile_size - Game.tile_size && this.posY <= Game.tile_size / 2 * Game.currentSizeY))
            {
                Drawing.drawing.playGlobalSound("timer.ogg", 0.5f);
                this.count = (int) (Math.random() * 11 + 5);
                Effect e1 = Effect.createNewEffect(this.posX, this.posY, Game.tile_size / 2, Effect.EffectType.exclamation);
                e1.size = 50;
                Game.effects.add(e1);
            }
        }

        this.spawnTimer -= Panel.frameFrequency;
        if (count > 0 && spawnTimer <= 0)
        {
            this.count--;
            this.spawnTimer = 20;

            if (this.posX < Game.tile_size && this.posY > Game.tile_size / 2 * Game.currentSizeY)
            {
                TankTrain t = new TankTrain("train", this.posX - Game.tile_size, this.posY);
                t.vX = 3.125;
                Game.eventsOut.add(new EventCreateTrain(t));
                Game.movables.add(t);
            }
            else if (this.posX > Game.currentSizeX * Game.tile_size - Game.tile_size && this.posY <= Game.tile_size / 2 * Game.currentSizeY)
            {
                TankTrain t = new TankTrain("train", this.posX + Game.tile_size, this.posY);
                t.vX = -3.125;
                Game.eventsOut.add(new EventCreateTrain(t));
                Game.movables.add(t);
            }
        }
    }

    public double getTileHeight()
    {
        return 0;
    }
}
