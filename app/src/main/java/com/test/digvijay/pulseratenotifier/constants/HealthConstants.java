package com.test.digvijay.pulseratenotifier.constants;

public class HealthConstants {
    
    private static int normalRestingHeartRateMinimum = 60;
    private static int normalRestingHeartRateMaximum = 100;

    public static int getNormalRestingHeartRateMinimum() {
        return normalRestingHeartRateMinimum;
    }

    public static void setNormalRestingHeartRateMinimum(int normalRestingHeartRateMinimum) {
        HealthConstants.normalRestingHeartRateMinimum = normalRestingHeartRateMinimum;
    }

    public static int getNormalRestingHeartRateMaximum() {
        return normalRestingHeartRateMaximum;
    }

    public static void setNormalRestingHeartRateMaximum(int normalRestingHeartRateMaximum) {
        HealthConstants.normalRestingHeartRateMaximum = normalRestingHeartRateMaximum;
    }
}
