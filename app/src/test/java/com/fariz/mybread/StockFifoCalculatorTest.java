package com.fariz.mybread;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class StockFifoCalculatorTest {
    @Test public void consumesOldStockFirst() {
        StockFifoCalculator.Result result = StockFifoCalculator.calculateEndingStock(20, 30, 15, 10);
        assertEquals(0, result.getRemainingOldStock());
        assertEquals(25, result.getRemainingNewStock());
        assertEquals(25, result.getTotalRemainingStock());
    }

    @Test public void keepsNewStockWhenOldStockRemains() {
        StockFifoCalculator.Result result = StockFifoCalculator.calculateEndingStock(20, 30, 8, 2);
        assertEquals(10, result.getRemainingOldStock());
        assertEquals(30, result.getRemainingNewStock());
        assertEquals(40, result.getTotalRemainingStock());
    }

    @Test public void allowsAllStockToBeRemoved() {
        StockFifoCalculator.Result result = StockFifoCalculator.calculateEndingStock(20, 30, 35, 15);
        assertEquals(0, result.getTotalRemainingStock());
    }

    @Test public void positiveAdjustmentIsKeptAsNewStock() {
        StockFifoCalculator.Result result =
                StockFifoCalculator.calculateEndingStockWithAdjustment(5, 7, 6, 0, 4);
        assertEquals(0, result.getRemainingOldStock());
        assertEquals(10, result.getRemainingNewStock());
        assertEquals(10, result.getTotalRemainingStock());
    }

    @Test public void negativeAdjustmentRemovesOldStockFirst() {
        StockFifoCalculator.Result result =
                StockFifoCalculator.calculateEndingStockWithAdjustment(20, 30, 5, 5, -12);
        assertEquals(0, result.getRemainingOldStock());
        assertEquals(28, result.getRemainingNewStock());
        assertEquals(28, result.getTotalRemainingStock());
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsAdjustmentBeyondRemainingStock() {
        StockFifoCalculator.calculateEndingStockWithAdjustment(2, 3, 4, 0, -2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsOutgoingAboveAvailableStock() {
        StockFifoCalculator.calculateEndingStock(2, 3, 4, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNegativeStock() {
        StockFifoCalculator.calculateEndingStock(-1, 3, 0, 0);
    }
}
