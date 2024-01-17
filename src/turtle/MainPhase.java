package turtle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.Random;


import battlecode.common.*;

public class MainPhase {
    
  public static void runMainPhase(RobotController rc) throws GameActionException {

    // Buy global upgrade 
    if(rc.canBuyGlobal(GlobalUpgrade.HEALING)) {
      rc.buyGlobal(GlobalUpgrade.HEALING);
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

      if(rc.canAttack(robot.getLocation())) {
        rc.attack(robot.getLocation());
      }
    }
    
    // 50/50 Chance of Building either a Water or explosive trap
    Random rand = new Random();
    int chance = rand.nextInt(10);
    if(rc.canBuild(TrapType.WATER, rc.getLocation()) && chance > 7) {
      rc.build(TrapType.WATER, rc.getLocation());
    } else if (rc.canBuild(TrapType.EXPLOSIVE, rc.getLocation())){
      rc.build(TrapType.EXPLOSIVE, rc.getLocation());
    }

    // If we can't build either just dig
    else {
      MapLocation waterLoc = rc.getLocation().add(RobotPlayer.directions[RobotPlayer.random.nextInt(8)]);
      if(rc.canDig(waterLoc)) rc.dig(waterLoc);
    }

    //move towards flags and place defenses around them
    MapLocation turtle = new MapLocation(rc.readSharedArray(0), rc.readSharedArray(1));

    // Place traps and water around flag
    Pathfind.moveTowards(rc, turtle, false);
    if(rc.getLocation().distanceSquaredTo(turtle) > 9 && rc.getLocation().distanceSquaredTo(turtle) < 64) {
      MapLocation waterLoc = rc.getLocation().add(RobotPlayer.directions[RobotPlayer.random.nextInt(8)]);
      if(rc.canDig(waterLoc)) rc.dig(waterLoc);
    }
    
    //try to heal friendly robots
    for(RobotInfo robot : rc.senseNearbyRobots(-1, rc.getTeam())) {
      if(rc.canHeal(robot.getLocation())) rc.heal(robot.getLocation());
    }

    // If we don't have the flag and aren't already near the turtle, try to move towards the turtle 
    if(!rc.hasFlag() && rc.getLocation().distanceSquaredTo(turtle) > 9) {
      Pathfind.moveTowards(rc, turtle, false);
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
