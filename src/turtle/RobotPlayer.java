package turtle;

import java.util.Random;

import battlecode.common.*;

public class RobotPlayer {

    public static Random random = null;

    public static Direction[] directions = {
                Direction.NORTH,
                Direction.NORTHEAST,
                Direction.EAST,
                Direction.SOUTHEAST,
                Direction.SOUTH,
                Direction.SOUTHWEST,
                Direction.WEST,
                Direction.NORTHWEST,
            };
    
    public static void run(RobotController rc) throws GameActionException{
        while (true){
            try {
                if(random == null) random = new Random(rc.getID());
                trySpawn(rc);
                if(rc.isSpawned()) {
                    int round = rc.getRoundNum();
                    if(round < GameConstants.SETUP_ROUNDS) Setup.runSetup(rc);
                    else MainPhase.runMainPhase(rc);
                }
            } catch (GameActionException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Clock.yield();
            }
        }
    }

    private static void trySpawn(RobotController rc) throws GameActionException {
        MapLocation[] spawnLocs = rc.getAllySpawnLocations();
        for(MapLocation loc : spawnLocs) {
            if(rc.canSpawn(loc)) {
                rc.spawn(loc);
                break;
            }
        }
    }
}
