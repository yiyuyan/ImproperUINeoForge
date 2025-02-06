package cn.ksmcbrigade.iui_nf.util;

public class MathUtils {

    public static int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    public static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }

    public static double clamp(double progress) {
        return clamp(progress, 0.0, 1.0);
    }

    public static double round(double val, int decimalPlaces) {
        double nth = Math.pow(10, decimalPlaces);
        return Math.floor(val * nth) / nth;
    }
}
