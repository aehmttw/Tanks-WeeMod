package tanks.tank;

import tanks.Game;
import tanks.bullet.Bullet;

public class TankRusset extends TankAIControlled
{
	public TankRusset(String name, double x, double y, double angle)
	{
		super(name, x, y, Game.tile_size, 120, 50, 0, angle, ShootAI.reflect);
		this.enableMovement = false;
		this.enableMineLaying = false;
		this.liveBulletMax = 5;
		this.cooldownRandom = 20;
		this.cooldownBase = 20;
		this.aimTurretSpeed = 0.03;
		this.turretIdleTimerBase = 25;
		this.turretIdleTimerRandom = 500;
		this.enableLookingAtTargetEnemy = false;
		this.enableDefensiveFiring = true;
		
		this.coinValue = 4;

		this.description = "An aggressive stationary tank";
	}
}
