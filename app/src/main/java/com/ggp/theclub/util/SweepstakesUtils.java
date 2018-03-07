package com.ggp.theclub.util;

import com.ggp.theclub.model.Sweepstakes;

import java.util.List;

import java8.util.stream.StreamSupport;

public class SweepstakesUtils {

    public static boolean isSweepstakesActive(Sweepstakes sweepstakes) {
        return sweepstakes != null && DateUtils.isTodayInDateRange(sweepstakes.getStartDate(), sweepstakes.getEndDate());
    }

    public static boolean isSweepstakesActive(List<Sweepstakes> sweepstakesList) {
        return StreamSupport.stream(sweepstakesList).anyMatch(sweep -> isSweepstakesActive(sweep));
    }

}
