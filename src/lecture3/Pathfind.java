package lecture3;

import battlecode.common.*;

public class Pathfind {
  
  static Direction direction;

  public static void moveTowards(RobotController rc, MapLocation loc) throws GameActionException {
    // if can move towards location, move towards location
    Direction dir = rc.getLocation().directionTo(loc);
    if (rc.canMove(dir)) rc.move(dir);
    // if there is water in the way, fill
    else if (rc.canFill(rc.getLocation().add(dir))) rc.fill(rc.getLocation()); 

    // if there is another obstacle, move in random direction
    else {
      Direction randomDir = Direction.allDirections()[RobotPlayer.random.nextInt(8)];
    } 
  }

  public static void explore(RobotController rc) throws GameActionException {
    // Try to move towards crumbs, otherwise move in current direction
    // If can't continue in current direction, pick a new random direction

    MapLocation[] crumbLocs = rc.senseNearbyCrumbs(-1);
    if (crumbLocs.length > 0) {
      moveTowards(rc, crumbLocs[0]);
    }
    
    if (rc.isMovementReady()) {
      if (direction != null && rc.canMove(direction)) rc.move(direction);
      else {
        direction = Direction.allDirections()[RobotPlayer.random.nextInt(8)];
        if (rc.canMove(direction)) rc.move(direction);
      }
    }
  }
}
