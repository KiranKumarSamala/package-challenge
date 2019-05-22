package com.code.challenge.utility;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * General purpose functions.
 */
public class Util {
    /**
     * Return String value or if it's empty return a default value.
     * @param string string value to return value.
     * @param defaultValue default value to return in case string is empty.
     * @return string value or default if empty.
     */
    public static String defaultIfEmpty(String string, String defaultValue) {
        return string.isEmpty() ? defaultValue : string;
    }

    /**
     * round float value with two decimal points precision.
     * @param value float value to round.
     * @return rounded float value.
     */
    public static float round(float value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.floatValue();
    }
}
