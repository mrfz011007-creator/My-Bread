package com.fariz.mybread;

import java.util.List;

/** Pure business calculations for My Bread. */
public final class BreadBusinessCalculator {
    public static final long DEFAULT_COST_PER_BREAD = 1_400L;
    public static final long DEFAULT_SELLING_PRICE_PER_BREAD = 2_500L;
    public static final long DEFAULT_GROSS_MARGIN_PER_BREAD = 1_100L;

    private BreadBusinessCalculator() { }

    public static long calculateModal(int newBreadQuantity) {
        requireNonNegative(newBreadQuantity, "Jumlah roti baru");
        return Math.multiplyExact((long) newBreadQuantity, DEFAULT_COST_PER_BREAD);
    }

    public static long calculateRevenue(int soldQuantity) {
        requireNonNegative(soldQuantity, "Jumlah terjual");
        return Math.multiplyExact((long) soldQuantity, DEFAULT_SELLING_PRICE_PER_BREAD);
    }

    public static long calculateGrossMargin(int soldQuantity) {
        requireNonNegative(soldQuantity, "Jumlah terjual");
        return Math.multiplyExact((long) soldQuantity, DEFAULT_GROSS_MARGIN_PER_BREAD);
    }

    public static long calculateExpenseTotal(List<Long> expenseAmounts) {
        if (expenseAmounts == null) {
            throw new IllegalArgumentException("Daftar pengeluaran tidak boleh null");
        }
        long total = 0L;
        for (Long amount : expenseAmounts) {
            if (amount == null || amount < 0L) {
                throw new IllegalArgumentException("Nominal pengeluaran tidak boleh negatif atau null");
            }
            total = Math.addExact(total, amount);
        }
        return total;
    }

    /**
     * Selisih kas = omzet - modal roti baru hari ini - pengeluaran.
     * Ini bukan laba bersih karena stok yang belum terjual masih memiliki nilai modal.
     */
    public static long calculateCashDifference(
            int soldQuantity, int newBreadQuantity, long totalExpenses) {
        requireNonNegative(soldQuantity, "Jumlah terjual");
        requireNonNegative(newBreadQuantity, "Jumlah roti baru");
        if (totalExpenses < 0L) {
            throw new IllegalArgumentException("Total pengeluaran tidak boleh negatif");
        }
        return Math.subtractExact(
                Math.subtractExact(calculateRevenue(soldQuantity), calculateModal(newBreadQuantity)),
                totalExpenses);
    }

    public static void validateDailyQuantities(
            int previousLeftover, int newBreadQuantity, int soldQuantity, int damagedQuantity) {
        requireNonNegative(previousLeftover, "Sisa roti lama");
        requireNonNegative(newBreadQuantity, "Roti baru");
        requireNonNegative(soldQuantity, "Jumlah terjual");
        requireNonNegative(damagedQuantity, "Jumlah rusak");
        long available = (long) previousLeftover + newBreadQuantity;
        long outgoing = (long) soldQuantity + damagedQuantity;
        if (outgoing > available) {
            throw new IllegalArgumentException(
                    "Jumlah terjual dan rusak melebihi stok yang tersedia");
        }
    }

    private static void requireNonNegative(int value, String label) {
        if (value < 0) {
            throw new IllegalArgumentException(label + " tidak boleh negatif");
        }
    }
}
