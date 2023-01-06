package com.kogundeji.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.icu.util.Calendar;

import com.kogundeji.model.Option;

import org.junit.Test;

import java.util.Random;

public class OptionUtilTest {

    @Test
    public void optionStringTest() {
        String formattedString = OptionUtil.getString("AMZN", 1000, "1/20/2023");
        assertEquals("AMZN_012023C1000", formattedString);
    }

    @Test
    public void randomDaysRemainingTest() {
        int randomDays = new Random().nextInt(10);
        int randomMonths = new Random().nextInt(10);
        int randomYears = new Random().nextInt(10);

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR) + randomYears;
        int month = Math.max(c.get(Calendar.MONTH) + randomMonths, 12);
        int day;
        if (month == 2) {
            day = Math.max(c.get(Calendar.DAY_OF_MONTH) + randomDays, 28);
        } else if (month == 4 || month == 6 || month == 9 || month == 11) {
            day = Math.max(c.get(Calendar.DAY_OF_MONTH) + randomDays, 30);
        } else {
            day = Math.max(c.get(Calendar.DAY_OF_MONTH) + randomDays, 31);
        }

        String expiration = (month + 1) + "/" + day + "/" + year;

        double daysRemaining = OptionUtil.getDaysRemaining(expiration);
        double daysEstimatedRemaining = year;
        assertTrue(daysRemaining > (randomYears * 365));

    }

    @Test
    public void strike25shouldbe25() {
        assertEquals("25",OptionUtil.getFormattedStrikeString(25));
    }

    @Test
    public void strike25Point0ShouldBe25() {
        assertEquals("25",OptionUtil.getFormattedStrikeString(25.0));
    }

    @Test
    public void strike12Point5ShouldBe12Point5() {
        assertEquals("12.5",OptionUtil.getFormattedStrikeString(12.5));
    }

    @Test
    public void strike12Point50ShouldBe12Point5() {
        assertEquals("12.5",OptionUtil.getFormattedStrikeString(12.50));
    }
}