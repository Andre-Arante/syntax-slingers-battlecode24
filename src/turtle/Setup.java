package turtle;

import battlecode.common.*;

public class Setup {

    private static final int EXPLORE_ROUNDS = 150;
    
    public static void runSetup(RobotController rc) throws GameActionException {

        if(rc.getRoundNum() < EXPLORE_ROUNDS) {
            // pickup flag if possible
            FlagInfo[] flags = rc.senseNearbyFlags(-1);
            for(FlagInfo flag : flags) {
                MapLocation flagLoc = flag.getLocation();
                if(rc.senseMapInfo(flagLoc).isSpawnZone() && rc.canPickupFlag(flagLoc)) {
                    rc.pickupFlag(flag.getLocation());
                }
            }
            
            // If we don't have a flag, explore randomly (search for bread crumbs)
            if (!rc.hasFlag()) Pathfind.explore(rc);

            // If we do have a flag, try to bring it to a corner
            else {
              Pathfind.findCorner(rc);
              rc.setIndicatorString("holding friendly flag");
            }
        } 
        // During turns 175 - 200, try and place a flag (that has hopefully been brought to a corner)
        else if (rc.getRoundNum() < 200) {

          //try to place flag if it is far enough away from other flags
          if(rc.senseLegalStartingFlagPlacement(rc.getLocation())) {
            if(rc.canDropFlag(rc.getLocation())) {
              rc.dropFlag(rc.getLocation());
              rc.writeSharedArray(0, rc.getLocation().x);
              rc.writeSharedArray(1, rc.getLocation().y);
            }
          } else {
            // If we can't find a spot to place flag, move away from the nearest flag
            Pathfind.placeFlag(rc);
          }

          //move towards flags and place defenses around them
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

        //THIS DOESNT RUN BECAUSE THE TOP CODE RUNS FOR FIRST 200 ROUNDS
        else {
            //try to place flag if it is far enough away from other flags
            if(rc.senseLegalStartingFlagPlacement(rc.getLocation())) {
                if(rc.canDropFlag(rc.getLocation())) rc.dropFlag(rc.getLocation());
            }

            //move towards flags and place defenses around them
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
            else Pathfind.explore(rc);
        }
    }
}
