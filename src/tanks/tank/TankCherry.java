package tanks.tank;

import tanks.Game;
import tanks.Panel;
import tanks.bullet.Bullet;
import tanks.event.EventTankUpdateColor;

public class TankCherry extends TankAIControlled
{
	public double angerTimer = 0;

	public TankCherry(String name, double x, double y, double angle)
	{
		super(name, x, y, Game.tile_size, 100, 0, 0, angle, ShootAI.straight);
		this.cooldownBase = 25;
		this.cooldownRandom = 0;
		this.maxSpeed = 1.0;
		this.enableDefensiveFiring = true;
		this.bulletSpeed = 25.0 / 4;
		this.bulletBounces = 0;
		this.bulletEffect = Bullet.BulletEffect.fire;
		this.aimTurretSpeed = 0.06;
		this.enablePathfinding = false;
		this.seekChance = 1;
		this.motionChangeChance = 0.001;

		this.coinValue = 10;

		this.description = "A tank which gets angry---if it sees an enemy";
	}

	public void postUpdate()
	{
		double prevTimer = this.angerTimer;

		if (this.seesTargetEnemy)
			this.angerTimer = 500;
		else
			this.angerTimer -= Panel.frameFrequency;

		if (this.angerTimer <= 0)
		{
			this.maxSpeed = 1.0;
			this.colorR = 100;
			this.enablePathfinding = false;

			if (prevTimer > 0)
				Game.eventsOut.add(new EventTankUpdateColor(this));
		}
		else
		{
			this.maxSpeed = 2.0;
			this.colorR = 200;
			this.enablePathfinding = true;

			if (prevTimer <= 0)
				Game.eventsOut.add(new EventTankUpdateColor(this));
		}
	}
}
