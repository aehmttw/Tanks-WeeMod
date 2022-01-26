package tanks.tank;

import tanks.*;

public class ElectroTeleport extends Movable
{
    public double teleportTimerMax = 50;
    public double teleportTimer = 50;
    public double teleX;
    public double teleY;
    public double destX;
    public double destY;
    public double size;
    public double tsize;

    public Tank tank;

    public ElectroTeleport(double x, double y, double dx, double dy, Tank tank)
    {
        super(x, y);

        this.teleX = x;
        this.teleY = y;
        this.destX = dx;
        this.destY = dy;
        this.tank = tank;

        this.tank.invulnerable = true;
        this.tank.targetable = false;
        this.tank.inControlOfMotion = false;
        this.tank.positionLock = true;
        this.posZ = this.tank.size / 2;
        this.size = this.tank.size;
        this.tsize = this.tank.turret.size;
        this.tank.size = 0;
        this.tank.turret.length = 0;
        this.tank.turret.size = 0;
    }

    @Override
    public void update()
    {
        this.teleportTimer -= Panel.frameFrequency;

        double frac = this.teleportTimer / this.teleportTimerMax;
        this.posX = this.teleX * frac + this.destX * (1 - frac);
        this.posY = this.teleY * frac + this.destY * (1 - frac);

        this.tank.posX = this.posX;
        this.tank.posY = this.posY;

        this.addEffect();

        if (this.teleportTimer <= 0)
        {
            this.tank.invulnerable = false;
            this.tank.targetable = true;
            this.tank.inControlOfMotion = true;
            this.tank.positionLock = false;
            this.tank.size = this.size;
            this.tank.turret.size = this.tsize;
            this.tank.turret.length = this.size;

            Game.removeMovables.add(this);

            for (int i = 0; i < this.size; i++)
            {
                this.addEffect();
            }
        }
    }

    @Override
    public void draw()
    {
        for (int i = 0; i < this.size; i++)
        {
            Drawing.drawing.setColor(i * 2.5, i * 5, 255, 20);

            if (Game.enable3d)
                Drawing.drawing.fillOval(this.posX, this.posY, this.posZ, i, i, false, true);
            else
                Drawing.drawing.fillOval(this.posX, this.posY, i, i);
        }

        Drawing.drawing.setColor(50, 150, 255, 255);

        if (Game.enable3d)
            Drawing.drawing.fillGlow(this.posX, this.posY, Game.tile_size / 2, Game.tile_size * 2, Game.tile_size * 2, false, true);
        else
            Drawing.drawing.fillGlow(this.posX, this.posY, Game.tile_size * 2, Game.tile_size * 2);
    }

    public void addEffect()
    {
        if (!Game.effectsEnabled)
            return;

        Effect e = Effect.createNewEffect(this.posX, this.posY, Game.tile_size / 2, Effect.EffectType.teleporterPiece);
        double var = 50;

        e.colR = Math.min(255, Math.max(0, 0 + Math.random() * var - var / 2));
        e.colG = Math.min(255, Math.max(0, 200 + Math.random() * var - var / 2));
        e.colB = Math.min(255, Math.max(0, 255 + Math.random() * var - var / 2));

        if (Game.enable3d)
            e.set3dPolarMotion(Math.random() * 2 * Math.PI, Math.random() * Math.PI * 2, Math.random() * 4);
        else
            e.setPolarMotion(Math.random() * 2 * Math.PI, Math.random() * 4);

        Game.effects.add(e);
    }
}
