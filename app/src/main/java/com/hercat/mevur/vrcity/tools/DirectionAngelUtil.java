package com.hercat.mevur.vrcity.tools;

public class DirectionAngelUtil {
    private static final int NTU_FACTOR = 100000;
    private static final int PI_TO_DEGREE = 180;

    /**
     * line two gps coordinate and calculate the direction angel between the line and the North direction
     *
     * @param lat1 lat of point1
     * @param lng1 lng of point1
     * @param lat2 lat of point2
     * @param lng2 lng of point2
     * @return the direction angel
     */
    public static double relativeDirection(double lat1, double lng1, double lat2, double lng2) {
        //convert to ntu lat & lng
        lat1 *= NTU_FACTOR;
        lng1 *= NTU_FACTOR;
        lat2 *= NTU_FACTOR;
        lng2 *= NTU_FACTOR;

        double dx = lat2 - lat1;
        double dy = lng2 - lng1;
        System.out.println(dx + " : " + dy);
        if (dx == 0) {
            return dy >= 0 ? 0 : 180;
        }
        if (dy == 0) {
            return dx >= 0 ? 90 : 270;
        }

        if (dx > 0 && dy > 0) {
            double theta = Math.atan(dx / dy);
            //consider the first cycle
            while (theta > 0.5) {
                theta -= 0.5;
            }
            return theta * PI_TO_DEGREE;
        } else if (dx > 0 && dy < 0) {
            double theta = Math.atan(Math.abs(dy) / dx);
            while (theta > 0.5) {
                theta -= 0.5;
            }
            return 90 + theta * PI_TO_DEGREE;
        } else if (dx < 0 && dy < 0) {
            double theta = Math.atan(dx / dy);
            while (theta > 0.5) {
                theta -= 0.5;
            }
            return 180 + theta * PI_TO_DEGREE;
        } else {
            double theta = Math.atan(dy / Math.atan(dx));
            while (theta > 0.5) {
                theta -= 0.5;
            }
            return 270 + theta * PI_TO_DEGREE;
        }
    }

    /**
     * check if the theta between in currentDirection +- area
     *
     * @param currentDirection current Direction base North direction
     * @param theta            theta base North direction
     * @param area             float area
     * @return if theta in the range of currentDirection - area to currentDirection + area,
     * return true;
     * otherwise return false
     */
    public static boolean inFanArea(double currentDirection, double theta, double area) {
        return theta >= currentDirection - theta && theta <= currentDirection + area;
    }
}
