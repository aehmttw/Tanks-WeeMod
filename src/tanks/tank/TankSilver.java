package tanks.tank;

import tanks.Game;

public class TankSilver extends TankAIControlled
{
	public TankSilver(String name, double x, double y, double angle)
	{
		super(name, x, y, Game.tile_size, 220, 220, 220, angle, ShootAI.alternate);
		this.enableDefensiveFiring = false;
		this.cooldownBase = 40;
		this.cooldownRandom = 80;
		this.enablePathfinding = true;
		this.liveBulletMax = 5;
		this.bulletHeavy = true;
		this.bulletSpeed = (3.125 + 6.25) / 2;
		this.coinValue = 10;
		this.bulletSize = 15;

		this.description = "A smart, fast tank---which shoots heavy,---fast bullets";
	}
}
