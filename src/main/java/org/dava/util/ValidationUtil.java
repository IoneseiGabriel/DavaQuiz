package org.dava.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ValidationUtil {

    public void checkIntegerInput(Integer input, String paramName) {
        if (input == null || input < 0) {
            throw new IllegalArgumentException(String.format("Invalid %s number", paramName));
        }
    }
}
