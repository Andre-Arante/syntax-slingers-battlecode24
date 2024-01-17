package turtle;

import battlecode.common.*;


public class Fortify {
    public static void pregameSetup(RobotController rc, int inner, int outer) throws GameActionException {
        // Move towards flags and place defenses around them
        FlagInfo[] flags = rc.senseNearbyFlags(-1);
        FlagInfo targetFlag = null;
        for(FlagInfo flag : flags) {
          if(!flag.isPickedUp()) {
            targetFlag = flag;
            break;
          }
        }

        if(targetFlag != null) {
          Pathfind.moveTowards(rc, targetFlag.getLocation(), false);

          // Only attempt building if we are within the trap radius
          if(rc.getLocation().distanceSquaredTo(flags[0].getLocation()) < 9) {
            if(rc.canBuild(TrapType.EXPLOSIVE, rc.getLocation())) {
              rc.build(TrapType.EXPLOSIVE, rc.getLocation());
            }
            else {
              MapLocation waterLoc = rc.getLocation().add(RobotPlayer.directions[RobotPlayer.random.nextInt(8)]);
              if(rc.canDig(waterLoc)) rc.dig(waterLoc);
            }
          }
        } 
        else {
          Pathfind.moveTowards(rc, new MapLocation(rc.readSharedArray(0), rc.readSharedArray(1)), false);
        } 
    }

}
