package tanks;

import basewindow.Model;
import main.Tanks;
import tanks.bullet.BulletElectro;
import tanks.bullet.BulletElectroRocket;
import tanks.bullet.BulletRocket;
import tanks.event.*;
import tanks.extension.Extension;
import tanks.network.NetworkEventMap;
import tanks.obstacle.ObstacleElectroPad;
import tanks.obstacle.ObstacleElectroPadOff;
import tanks.obstacle.ObstacleLavaGround;
import tanks.obstacle.ObstacleTrainTrack;
import tanks.tank.*;

import java.util.ArrayList;

public class WeeExtension extends Extension
{
    public static Model sphere;

    public WeeExtension()
    {
        super("weemod");
    }

    @Override
    public void setUp()
    {
        Game.registerBullet(BulletRocket.class, "rocket", "bullet_fire.png");
        Game.registerBullet(BulletElectro.class, "electro", "bullet_electric.png");
        Game.registerBullet(BulletElectroRocket.class, "electro_rocket", "bullet_electric.png");

        Game.registerTank(TankShotgun.class, "shotgun", 1.0 / 4);
        Game.registerTank(TankExplosive.class, "explosive", 1.0 / 10);
        Game.registerTank(TankRusset.class, "russet", 1.0 / 10);
        Game.registerTank(TankRocketDefender.class, "rocketdefender", 1.0 / 10);
        Game.registerTank(TankElectro.class, "electro", 1.0 / 10);
        Game.registerTank(TankCherry.class, "cherry", 1.0 / 10);
        Game.registerTank(TankPeach.class, "peach", 1.0 / 4);
        Game.registerTank(TankCommando.class, "commando", 1.0 / 15);
        Game.registerTank(TankSilver.class, "silver", 1.0 / 10);
        Game.registerTank(TankBigRed.class, "bigred", 1.0 / 20, true);
        Game.registerTank(TankCommanderBoom.class, "commanderboom", 1.0 / 40, true);
        Game.registerTank(TankRocketKing.class, "rocketking", 1.0 / 40, true);
        Game.registerTank(TankElectroKing.class, "electroking", 1.0 / 40, true);

        registerTankMusic("shotgun", "tank/battle_strings.ogg");
        registerTankMusic("explosive", "tank/battle_timpani.ogg");
        registerTankMusic("explosive", "tank/battle_bassdrum.ogg");
        registerTankMusic("russet", "tank/battle_cymbal.ogg");
        registerTankMusic("russet", "tank/battle_triangle.ogg");
        registerTankMusic("rocketdefender", "tank/battle_strings.ogg");
        registerTankMusic("rocketdefender", "tank/battle_mellow_chimes.ogg");
        registerTankMusic("rocketdefender", "tank/battle_timpani.ogg");
        registerTankMusic("rocketdefender", "tank/battle_bassdrum.ogg");
        registerTankMusic("electro", "tank/battle_stack_synth.ogg");
        registerTankMusic("cherry", "tank/battle_flute.ogg");
        registerTankMusic("peach", "tank/battle_choir.ogg");
        registerTankMusic("commando", "tank/battle_snare_drum.ogg");
        registerTankMusic("silver", "tank/battle_celesta.ogg");

        registerTankMusic("bigred", "tank/battle_pizzicato_violin.ogg");
        registerTankMusic("bigred", "tank/battle_timpani.ogg");
        registerTankMusic("bigred", "tank/battle_bassdrum.ogg");

        registerTankMusic("commanderboom", "tank/battle_timpani.ogg");
        registerTankMusic("commanderboom", "tank/battle_bassdrum.ogg");

        registerTankMusic("rocketking", "tank/battle_strings.ogg");
        registerTankMusic("rocketking", "tank/battle_mellow_chimes.ogg");
        registerTankMusic("rocketking", "tank/battle_timpani.ogg");
        registerTankMusic("rocketking", "tank/battle_bassdrum.ogg");

        registerTankMusic("electroking", "tank/battle_stack_synth.ogg");
        registerTankMusic("electroking", "tank/battle_timpani.ogg");
        registerTankMusic("electroking", "tank/battle_bassdrum.ogg");

        Game.registerObstacle(ObstacleElectroPad.class, "electropad");
        Game.registerObstacle(ObstacleTrainTrack.class, "traintrack");
        Game.registerObstacle(ObstacleElectroPadOff.class, "electropadoff");
        Game.registerObstacle(ObstacleLavaGround.class, "lavaground");

        NetworkEventMap.register(EventElectroGlow.class);
        NetworkEventMap.register(EventTankElectroTeleport.class);
        NetworkEventMap.register(EventLavaGroundUpdate.class);
        NetworkEventMap.register(EventElectroPadActivate.class);
        NetworkEventMap.register(EventTankElectroKingCharge.class);
        NetworkEventMap.register(EventTankElectroKingElectrify.class);
        NetworkEventMap.register(EventTankElectroKingGlow.class);
        NetworkEventMap.register(EventCreateTrain.class);
        NetworkEventMap.register(EventAirdropTank.class);
        NetworkEventMap.register(EventTankBoostEffect.class);
    }

    public static void registerTankMusic(String tank, String track)
    {
        if (!Game.registryTank.tankMusics.containsKey(tank))
            Game.registryTank.tankMusics.put(tank, new ArrayList<>());

        Game.registryTank.tankMusics.get(tank).add(track);
    }

    @Override
    public void loadResources()
    {
        this.registerSound("charge.ogg");
        this.registerSound("electro_teleport.ogg");
        sphere = new Model(Game.game.window, "", this.getFileContents("models/sphere/model.obj"));
    }

    public static void main(String[] args)
    {
        Tanks.launchWithExtensions(args, new Extension[]{new WeeExtension()}, null);
    }
}
