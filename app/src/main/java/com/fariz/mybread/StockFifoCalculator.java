package com.fariz.mybread;

/**
 * Applies sales and damaged bread to the oldest stock first.
 * Manually entered leftover stock is treated as older than newly purchased bread.
 */
public final class StockFifoCalculator {
    private StockFifoCalculator() { }

    public static Result calculateEndingStock(
            int previousLeftover, int newBreadQuantity, int soldQuantity, int damagedQuantity) {
        BreadBusinessCalculator.validateDailyQuantities(
                previousLeftover, newBreadQuantity, soldQuantity, damagedQuantity);

        long totalOutgoing = (long) soldQuantity + damagedQuantity;
        int remainingOld = (int) Math.max(0L, previousLeftover - totalOutgoing);
        long outgoingAfterOld = Math.max(0L, totalOutgoing - previousLeftover);
        int remainingNew = (int) (newBreadQuantity - outgoingAfterOld);
        return new Result(remainingOld, remainingNew);
    }

    public static final class Result {
        private final int remainingOldStock;
        private final int remainingNewStock;

        private Result(int remainingOldStock, int remainingNewStock) {
            this.remainingOldStock = remainingOldStock;
            this.remainingNewStock = remainingNewStock;
        }

        public int getRemainingOldStock() {
            return remainingOldStock;
        }

        public int getRemainingNewStock() {
            return remainingNewStock;
        }

        public int getTotalRemainingStock() {
            return remainingOldStock + remainingNewStock;
        }
    }
}
