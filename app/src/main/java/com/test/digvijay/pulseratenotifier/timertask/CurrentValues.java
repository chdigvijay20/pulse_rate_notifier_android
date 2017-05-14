package com.test.digvijay.pulseratenotifier.timertask;

public class CurrentValues {

    private static volatile Integer currentPulseRate;

    public static Integer getCurrentPulseRate() {
        return currentPulseRate;
    }

    public static void setCurrentPulseRate(Integer currentPulseRate) {
        CurrentValues.currentPulseRate = currentPulseRate;
    }
}
