package lecture3;

import battlecode.common.*;

public class Setup {
  
  private static final int EXPLORE_ROUNDS = 150;

  public static void runSetup(RobotController rc) throws GameActionException {

    // Setup Phase
    if(rc.getRoundNum() < EXPLORE_ROUNDS) {
      // Pick up a nearby flag if possible
      FlagInfo[] flags = rc.senseNearbyFlags(-1, rc.getTeam());
      for (FlagInfo flag : flags) {
        if(rc.canPickupFlag(flag.getLocation())) {
          rc.pickupFlag(flag.getLocation());
          break;
        }
      }

      // Explore Randomly
      Pathfind.explore(rc);

    } else {
      // Try to place a flag if it is far enough away from other flags
      if (rc.senseLegalStartingFlagPlacement(rc.getLocation())) {
        if(rc.canDropFlag(rc.getLocation())) rc.dropFlag(rc.getLocation());
      }

      // Search for a nearby placed flag
      FlagInfo[] flags = rc.senseNearbyFlags(-1, rc.getTeam());

      FlagInfo target = null;

      // Iterate through all nearby flags, and set a not already picked up flag as the "target"
      for (FlagInfo flag : flags) {
        if(!flag.isPickedUp()) {
          target = flag;
          break;
        }
      }


      // If there is a placed flag nearby, move closer and build defenses
      if (target != null) {
        Pathfind.moveTowards(rc, target.getLocation());

        // If a flag is found within 3 tiles, fortify the area
        if (rc.getLocation().distanceSquaredTo(target.getLocation()) < 9) {
          // Try to place a trap
          if (rc.canBuild(TrapType.EXPLOSIVE, rc.getLocation())) {
            rc.build(TrapType.EXPLOSIVE, rc.getLocation());
          }
        }

        // Try to place water
        else {
          MapLocation water = rc.getLocation().add(Direction.allDirections()[RobotPlayer.random.nextInt(8)]);
          if (rc.canDig(water)) rc.dig(water);
        }
      }
      else Pathfind.explore(rc);
    }
    
  }
}
