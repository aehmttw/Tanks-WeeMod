package tanks.tank;

import tanks.Game;
import tanks.bullet.Bullet;
import tanks.event.EventTankUpdateColor;

public class TankBigRed extends TankAIControlled
{
	public TankBigRed(String name, double x, double y, double angle)
	{
		super(name, x, y, Game.tile_size * 1.5, 255, 64, 64, angle, ShootAI.straight);

		this.turret.length *= 1.5;
		this.turret.colorR = 127;
		this.turret.colorG = 127;
		this.turret.colorB = 127;
		this.enableMovement = true;
		this.maxSpeed = 0.75;
		this.enableMineLaying = false;
		this.enablePredictiveFiring = false;
		this.liveBulletMax = 1;
		this.cooldownRandom = 60;
		this.cooldownBase = 120;
		this.aimTurretSpeed = 0.02;
		this.bulletBounces = 0;
		this.bulletEffect = Bullet.BulletEffect.fire;
		this.bulletSpeed = 25.0 / 4;
		this.enableLookingAtTargetEnemy = false;
		this.motionChangeChance = 0.001;
		this.avoidSensitivity = 1;
		this.baseHealth = 12;
		this.health = 12;

		this.coinValue = 20;

		this.description = "A boss tank which shoots rockets";
	}
}
