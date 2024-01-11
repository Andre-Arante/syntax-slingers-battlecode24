package lecture3;

import java.util.*;
import java.util.List;

import battlecode.common.*;

public class MainPhase {

  public static void runMainPhase(RobotController rc) throws GameActionException {
    // Try to buy action and capturing upgrade
    if (rc.canBuyGlobal(GlobalUpgrade.ACTION)) {
      rc.buyGlobal(GlobalUpgrade.ACTION);
    } 
    
    if (rc.canBuyGlobal(GlobalUpgrade.CAPTURING)) {
      rc.buyGlobal(GlobalUpgrade.CAPTURING);
    }

    // Attack enemies, prioritizing enemies that are currently holding a flag
    RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
    for (RobotInfo robot : nearbyEnemies) {
      if (robot.hasFlag()) {
        Pathfind.moveTowards(rc, robot.getLocation());
        if (rc.canAttack(robot.getLocation())) rc.attack(robot.getLocation());
      }
    }

    for (RobotInfo robot : nearbyEnemies) {
      if (rc.canAttack(robot.getLocation())) {
        rc.attack(robot.getLocation());
      }
    }

    // Try to heal friendly robots
    for (RobotInfo robot : rc.senseNearbyRobots(-1, rc.getTeam())) {
      if (rc.canHeal(robot.getLocation())) rc.heal(robot.getLocation());
    }

    if (!rc.hasFlag()) {
      // If we don't have a flag, move towards the closest enemy flag
      ArrayList<MapLocation> flagLocs = new ArrayList<>();
      FlagInfo[] enemyFlags = rc.senseNearbyFlags(-1, rc.getTeam().opponent());

      for (FlagInfo flag : enemyFlags) flagLocs.add(flag.getLocation());
      
      // If there are no nearby flags, check which flags are visible by teammates
      if (flagLocs.size() == 0) {
        MapLocation[] broadcastLocs = rc.senseBroadcastFlagLocations();
        for (MapLocation flagLoc : broadcastLocs) flagLocs.add(flagLoc);
      }

      // If we found a nearby enemy flag, move towards it and pick it up
      MapLocation closestFlag = findClosestLocation(rc.getLocation(), flagLocs);
      if (closestFlag != null) {
        Pathfind.moveTowards(rc, closestFlag);
        if (rc.canPickupFlag(closestFlag)) rc.pickupFlag(closestFlag);
      }

      // If there is no flag to capture, explore randomly
      Pathfind.explore(rc);
    }
    else {
      // If we have the flag, move towards the closest ally spawn zone
      MapLocation[] spawnLocs = rc.getAllySpawnLocations();
      MapLocation closestSpawn = findClosestLocation(rc.getLocation(), Arrays.asList(spawnLocs));
      Pathfind.moveTowards(rc, closestSpawn);
    }
  }

  public static MapLocation findClosestLocation(MapLocation me, List<MapLocation> otherLocs) {
    MapLocation closest = null;
    int minDist = Integer.MAX_VALUE;
    for (MapLocation loc : otherLocs) {
      int dist = me.distanceSquaredTo(loc);
      if (dist < minDist) {
        minDist = dist;
        closest = loc;
      }
    }
    return closest;
  }
}
