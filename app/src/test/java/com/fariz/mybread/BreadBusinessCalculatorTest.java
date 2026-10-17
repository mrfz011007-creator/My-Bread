package com.fariz.mybread;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class BreadBusinessCalculatorTest {
    @Test
    public void calculatesExampleFromSpecification() {
        assertEquals(420_000L, BreadBusinessCalculator.calculateModal(300));
        assertEquals(625_000L, BreadBusinessCalculator.calculateRevenue(250));
        assertEquals(275_000L, BreadBusinessCalculator.calculateGrossMargin(250));
        assertEquals(205_000L,
                BreadBusinessCalculator.calculateCashDifference(250, 300, 0L));
    }

    @Test
    public void addsSeparateExpenseItems() {
        assertEquals(13_000L,
                BreadBusinessCalculator.calculateExpenseTotal(Arrays.asList(10_000L, 3_000L)));
    }

    @Test
    public void emptyExpenseListTotalsZero() {
        assertEquals(0L,
                BreadBusinessCalculator.calculateExpenseTotal(Collections.<Long>emptyList()));
    }

    @Test
    public void zeroQuantitiesAreValid() {
        BreadBusinessCalculator.validateDailyQuantities(0, 0, 0, 0);
        assertEquals(0L, BreadBusinessCalculator.calculateModal(0));
        assertEquals(0L, BreadBusinessCalculator.calculateRevenue(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNegativeNewBreadQuantity() {
        BreadBusinessCalculator.calculateModal(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNegativeExpense() {
        BreadBusinessCalculator.calculateExpenseTotal(Arrays.asList(1_000L, -1L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNullExpenseList() {
        BreadBusinessCalculator.calculateExpenseTotal(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNullExpenseEntry() {
        BreadBusinessCalculator.calculateExpenseTotal(Arrays.asList(1_000L, null));
    }

    @Test(expected = ArithmeticException.class)
    public void rejectsOverflowingExpenseTotal() {
        BreadBusinessCalculator.calculateExpenseTotal(Arrays.asList(Long.MAX_VALUE, 1L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsSalesAndDamageAboveAvailableStock() {
        BreadBusinessCalculator.validateDailyQuantities(10, 5, 12, 4);
    }
}
