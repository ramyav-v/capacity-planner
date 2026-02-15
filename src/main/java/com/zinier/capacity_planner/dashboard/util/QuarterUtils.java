package com.zinier.capacity_planner.dashboard.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.Month;

public class QuarterUtils {

    public static LocalDate getCurrentQuarterStart() {
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();

        if (month <= 3)
            return LocalDate.of(now.getYear(), Month.JANUARY, 1);
        else if (month <= 6)
            return LocalDate.of(now.getYear(), Month.APRIL, 1);
        else if (month <= 9)
            return LocalDate.of(now.getYear(), Month.JULY, 1);
        else
            return LocalDate.of(now.getYear(), Month.OCTOBER, 1);
    }

    public static LocalDate getCurrentQuarterEnd() {
        return getCurrentQuarterStart().plusMonths(3).minusDays(1);
    }
    public static QuarterRange currentQuarter() {

        LocalDate now = LocalDate.now();
        int currentQuarter = (now.getMonthValue() - 1) / 3 + 1;

        int startMonth = (currentQuarter - 1) * 3 + 1;

        LocalDate start = LocalDate.of(now.getYear(), startMonth, 1);
        LocalDate end = start.plusMonths(3).minusDays(1);

        return new QuarterRange(start, end);
    }

    @AllArgsConstructor
    @Getter
    public static class QuarterRange {
        private LocalDate start;
        private LocalDate end;
    }


}
