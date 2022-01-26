package tanks.tank;

import tanks.Drawing;
import tanks.Effect;
import tanks.Game;
import tanks.Panel;
import tanks.bullet.Bullet;
import tanks.event.EventAirdropTank;
import tanks.event.EventCreateTank;
import tanks.event.EventTankBoostEffect;
import tanks.event.EventTankElectroTeleport;
import tanks.obstacle.Obstacle;
import tanks.registry.RegistryTank;

import java.util.ArrayList;
import java.util.Iterator;

public class TankCommando extends TankAIControlled
{
    public ArrayList<Tank> spawned = new ArrayList();

    public TankCommando(String name, double x, double y, double angle)
    {
        super(name, x, y, Game.tile_size, 50, 200, 50, angle, ShootAI.straight);
        this.liveBulletMax = 1;
        this.cooldownRandom = 50;
        this.cooldownBase = 100;
        this.bulletBounces = 0;
        this.bulletEffect = Bullet.BulletEffect.fire;
        this.bulletSpeed = 6.25;
        this.health = 3;
        this.baseHealth = 3;
        this.coinValue = 15;
        this.maxSpeed = 1.0;
        this.description = "A tank which summons---reinforcements---from the sky";
    }

    public void postUpdate()
    {
        if (this.avoidTimer > 0)
        {
            this.maxSpeed = 1.75;

            Game.eventsOut.add(new EventTankBoostEffect(this.networkID));
            if (Math.random() * Panel.frameFrequency < Game.effectMultiplier && Game.effectsEnabled)
            {
                Effect e = Effect.createNewEffect(this.posX, this.posY, Game.tile_size / 2, Effect.EffectType.piece);
                double var = 50;

                e.colR = Math.min(255, Math.max(0, 255 + Math.random() * var - var / 2));
                e.colG = Math.min(255, Math.max(0, 180 + Math.random() * var - var / 2));
                e.colB = Math.min(255, Math.max(0, 0 + Math.random() * var - var / 2));

                if (Game.enable3d)
                    e.set3dPolarMotion(Math.random() * 2 * Math.PI, Math.random() * Math.PI, Math.random());
                else
                    e.setPolarMotion(Math.random() * 2 * Math.PI, Math.random());

                Game.effects.add(e);
            }

        }
        else
            this.maxSpeed = 1.0;


        ArrayList<Tank> removeSpawned = new ArrayList<Tank>();

        for (int i = 0; i < this.spawned.size(); i++)
        {
            if (!Game.movables.contains(this.spawned.get(i)))
                removeSpawned.add(this.spawned.get(i));
        }

        for (int i = 0; i < removeSpawned.size(); i++)
        {
            this.spawned.remove(removeSpawned.get(i));
        }

        if (Math.random() < 0.002 * Panel.frameFrequency && this.spawned.size() < 5)
        {
            this.spawnTank();
        }
    }

    public void spawnTank()
    {
        double destX = 0;
        double destY = 0;

        boolean found = false;
        for (int i = 0; i < 5; i++)
        {
            int x = (int) (Math.random() * Game.currentSizeX);
            int y = (int) (Math.random() * Game.currentSizeY);

            if (!Game.game.solidGrid[x][y])
            {
                found = true;
                destX = (x + 0.5) * Game.tile_size;
                destY = (y + 0.5) * Game.tile_size;

                Drawing.drawing.playGlobalSound("flame.ogg", 0.75f);
                break;
            }
        }

        if (!found)
            return;

        RegistryTank.TankEntry e = Game.registryTank.getEntry(this.name);

        while (e.name.equals(this.name) || e.isBoss)
        {
            e = Game.registryTank.getRandomTank();
        }

        Tank t = e.getTank(destX, destY, this.angle);
        t.team = this.team;
        t.coinValue = 0;

        Game.eventsOut.add(new EventAirdropTank(t));
        this.spawned.add(t);

        Game.movables.add(new Crate(t));
    }
}
