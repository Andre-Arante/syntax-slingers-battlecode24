package turtle;

import java.util.HashSet;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

import java.util.HashSet;

import battlecode.common.*;

public class Pathfind {

    private static Direction dir;

    private static MapLocation prevDest = null;
    private static HashSet<MapLocation> line = null;
    private static int obstacleStartDist = 0;

    private static int innerRadius = 6;

    public static void lineup(RobotController rc) throws GameActionException {
        MapLocation loc = rc.getLocation();
        MapLocation turtle = new MapLocation(rc.readSharedArray(4), rc.readSharedArray(5));
        Direction dir = rc.getLocation().directionTo(turtle);

        rc.setIndicatorString(Integer.toString(loc.distanceSquaredTo(turtle)));

            if (loc.distanceSquaredTo(turtle) < innerRadius*innerRadius) {
            rc.setIndicatorString("Inside inner radius");
            Pathfind.moveAwayFromFlag(rc);
        }

        else {
            rc.setIndicatorString("Perfect");
            if (rc.canMove(dir)) rc.move(dir);
        }
    }

    public static void explore(RobotController rc) throws GameActionException {
        if(rc.isMovementReady()) {
            MapLocation[] crumbLocs = rc.senseNearbyCrumbs(-1);
            if(crumbLocs.length > 0) {
                bugNav2(rc, crumbLocs[0]);
            }

            if(dir == null || !rc.canMove(dir)) {
                dir = RobotPlayer.directions[RobotPlayer.random.nextInt(8)];
            }
            if(rc.canMove(dir)) rc.move(dir);
        }
    }

    public static void moveAwayFromFlag(RobotController rc) throws GameActionException {
      MapLocation flag = new MapLocation(rc.readSharedArray(4), rc.readSharedArray(5));
      Direction dir = rc.getLocation().directionTo(flag);
      dir = dir.opposite();
      if (rc.canMove(dir)) rc.move(dir);
      else {
        dir = dir.rotateLeft();
        if (rc.canMove(dir)) rc.move(dir); 
      }
    }

    public static void findCorner(RobotController rc) throws GameActionException {
      if (rc.isMovementReady()) {
          MapLocation corner = new MapLocation(rc.readSharedArray(4),rc.readSharedArray(5));
          dir = rc.getLocation().directionTo(corner);
          bugNav2(rc, corner);
          rc.setIndicatorString("Finding farthest location");

     }
    }

    private static int bugState = 0; // 0 head to target, 1 circle obstacle
    private static MapLocation closestObstacle = null;
    private static int closestObstacleDist = 10000;
    private static Direction bugDir = null;

    public static void resetBug(){
        bugState = 0; // 0 head to target, 1 circle obstacle
        closestObstacle = null;
        closestObstacleDist = 10000;
        bugDir = null;
    }

    public static void bugNav2(RobotController rc, MapLocation destination) throws GameActionException{
        
        if(!destination.equals(prevDest)) {
            prevDest = destination;
            line = createLine(rc.getLocation(), destination);
        }

        for(MapLocation loc : line) {
            rc.setIndicatorDot(loc, 255, 0, 0);
        }

        if(bugState == 0) {
            bugDir = rc.getLocation().directionTo(destination);
            if(rc.canMove(bugDir)){
                rc.move(bugDir);
            } else {
                bugState = 1;
                obstacleStartDist = rc.getLocation().distanceSquaredTo(destination);
                bugDir = rc.getLocation().directionTo(destination);
            }
        } else {
            if(line.contains(rc.getLocation()) && rc.getLocation().distanceSquaredTo(destination) < obstacleStartDist) {
                bugState = 0;
            }

            for(int i = 0; i < 9; i++){
                if(rc.canMove(bugDir)){
                    rc.move(bugDir);
                    bugDir = bugDir.rotateRight();
                    bugDir = bugDir.rotateRight();
                    break;
                } else {
                    bugDir = bugDir.rotateLeft();
                }
            }
        }
    }

    private static HashSet<MapLocation> createLine(MapLocation a, MapLocation b) {
        HashSet<MapLocation> locs = new HashSet<>();
        int x = a.x, y = a.y;
        int dx = b.x - a.x;
        int dy = b.y - a.y;
        int sx = (int) Math.signum(dx);
        int sy = (int) Math.signum(dy);
        dx = Math.abs(dx);
        dy = Math.abs(dy);
        int d = Math.max(dx,dy);
        int r = d/2;
        if (dx > dy) {
            for (int i = 0; i < d; i++) {
                locs.add(new MapLocation(x, y));
                x += sx;
                r += dy;
                if (r >= dx) {
                    locs.add(new MapLocation(x, y));
                    y += sy;
                    r -= dx;
                }
            }
        }
        else {
            for (int i = 0; i < d; i++) {
                locs.add(new MapLocation(x, y));
                y += sy;
                r += dx;
                if (r >= dy) {
                    locs.add(new MapLocation(x, y));
                    x += sx;
                    r -= dy;
                }
            }
        }
        locs.add(new MapLocation(x, y));
        return locs;
    }
}
