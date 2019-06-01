package frc.robot.util;

/**
 * Collection of Utility methods
 *
 */
public class Util {
    public static double limit(double v, double maxMag) {
        return limit(v, -maxMag, maxMag);
    }

    public static double limit(double v, double min, double max) {
        return Math.min(max, Math.max(min, v));
    }
    
    public static boolean epsilonEquals(double a, double b, double epsilon) {
        return (a - epsilon <= b) && (a + epsilon >= b);
    }
    
    public static double gToInPerSecSquared(double g) {
        return g * 386.088;
    }
}