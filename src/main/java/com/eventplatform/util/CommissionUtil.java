package com.eventplatform.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class CommissionUtil {

    private CommissionUtil() {
        // Utility class — prevent instantiation
    }

    /**
     * Calculates platform commission amount.
     *
     * @param total   total transaction amount
     * @param percent commission percentage (e.g. 10 for 10%)
     * @return commission amount rounded to 2 decimals
     */
    public static BigDecimal platformFee(BigDecimal total, BigDecimal percent) {

        Objects.requireNonNull(total, "Total amount cannot be null");
        Objects.requireNonNull(percent, "Commission percent cannot be null");

        if (total.signum() < 0 || percent.signum() < 0) {
            throw new IllegalArgumentException("Amounts must be non-negative");
        }

        return total
                .multiply(percent)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
}
