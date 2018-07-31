package com.hercat.mevur.vrcity.tools;

public class DirectionAngelUtil {
    private static final int NTU_FACTOR = 100000;
    private static final int PI_TO_DEGREE = 180;
    private static final double PI = Math.PI;

    /**
     * line two gps coordinate and calculate the direction angel between the line and the North direction
     *
     * @param lat1 lat of point1
     * @param lng1 lng of point1
     * @param lat2 lat of point2
     * @param lng2 lng of point2
     * @return the direction angel
     */
    public static double relativeDirection(double lat1, double lon1, double lat2, double lon2) {
        // TODO: 18-7-31 convert to validate data

        //convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        double deltaFI = Math.log(Math.tan(lat2 / 2 + PI / 4) / Math.tan(lat1 / 2 + PI / 4));
        double deltaLON = Math.abs(lon1 - lon2) % 180;
        double theta = Math.atan2(deltaLON, deltaFI);
        return Math.toDegrees(theta);
    }
}
