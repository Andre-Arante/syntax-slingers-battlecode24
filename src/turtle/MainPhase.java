package turtle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import battlecode.common.*;

public class MainPhase {
    
    public static void runMainPhase(RobotController rc) throws GameActionException {
    

        // Buy global upgrade (prioritize capturing)
        if(rc.canBuyGlobal(GlobalUpgrade.CAPTURING)) {
            rc.buyGlobal(GlobalUpgrade.CAPTURING);
        } 
        else if(rc.canBuyGlobal(GlobalUpgrade.ACTION)) {
            rc.buyGlobal(GlobalUpgrade.ACTION);
        }

        //attack enemies, prioritizing enemies that have your flag
        RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        for(RobotInfo robot : nearbyEnemies) {
            if(robot.hasFlag()) {
                Pathfind.moveTowards(rc, robot.getLocation(), true);
                if(rc.canAttack(robot.getLocation())) rc.attack(robot.getLocation());
            }
        }
        for(RobotInfo robot : nearbyEnemies) {
            if(rc.canAttack(robot.getLocation())) {
                rc.attack(robot.getLocation());
            }
        }
        //try to heal friendly robots
        for(RobotInfo robot : rc.senseNearbyRobots(-1, rc.getTeam())) {
            if(rc.canHeal(robot.getLocation())) rc.heal(robot.getLocation());
        }

        if(!rc.hasFlag()) {
          FlagInfo[] flags = rc.senseNearbyFlags(-1);
          Pathfind.moveTowards(rc, new MapLocation(rc.readSharedArray(0), rc.readSharedArray(1)), false);
        }
        else {
            //if we have the flag, move towards the closest ally spawn zone
            MapLocation[] spawnLocs = rc.getAllySpawnLocations();
            MapLocation closestSpawn = findClosestLocation(rc.getLocation(), Arrays.asList(spawnLocs));
            Pathfind.moveTowards(rc, closestSpawn, true);
        }
    }

    public static MapLocation findClosestLocation(MapLocation me, List<MapLocation> otherLocs) {
        MapLocation closest = null;
        int minDist = Integer.MAX_VALUE;
        for(MapLocation loc : otherLocs) {
            int dist = me.distanceSquaredTo(loc);
            if(dist < minDist) {
                minDist = dist;
                closest = loc;
            }
        }
        return closest;
    }
}
