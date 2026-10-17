package com.fariz.mybread;

/**
 * FIFO stock calculation. Old stock is consumed before newly carried stock.
 * A positive adjustment is added to the newest stock lot; a negative adjustment
 * removes remaining old stock first. Adjustments remain separately recorded by
 * the database and are not classified as sales or damaged bread.
 */
public final class StockFifoCalculator {
    private StockFifoCalculator() { }

    public static Result calculateEndingStock(
            int previousLeftover, int newBreadQuantity, int soldQuantity, int damagedQuantity) {
        validateNonNegative(previousLeftover, newBreadQuantity, soldQuantity, damagedQuantity);
        long available = (long) previousLeftover + newBreadQuantity;
        long outgoing = (long) soldQuantity + damagedQuantity;
        if (outgoing > available) {
            throw new IllegalArgumentException(
                    "Jumlah terjual dan rusak melebihi stok yang tersedia");
        }
        int remainingOld = (int) Math.max(0L, previousLeftover - outgoing);
        long outgoingAfterOld = Math.max(0L, outgoing - previousLeftover);
        int remainingNew = (int) (newBreadQuantity - outgoingAfterOld);
        return new Result(remainingOld, remainingNew);
    }

    public static Result calculateEndingStockWithAdjustment(
            int previousLeftover, int newBreadQuantity, int soldQuantity,
            int damagedQuantity, int adjustmentQuantity) {
        validateNonNegative(previousLeftover, newBreadQuantity, soldQuantity, damagedQuantity);
        if (adjustmentQuantity >= 0) {
            long adjustedNewStock = (long) newBreadQuantity + adjustmentQuantity;
            if (adjustedNewStock > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("Jumlah stok terlalu besar");
            }
            return calculateEndingStock(
                    previousLeftover, (int) adjustedNewStock, soldQuantity, damagedQuantity);
        }

        Result afterSalesAndDamage = calculateEndingStock(
                previousLeftover, newBreadQuantity, soldQuantity, damagedQuantity);
        long removal = -(long) adjustmentQuantity;
        if (removal > afterSalesAndDamage.getTotalRemainingStock()) {
            throw new IllegalArgumentException(
                    "Penyesuaian stok mengurangi stok lebih banyak daripada sisa yang tersedia");
        }
        int remainingOld = afterSalesAndDamage.getRemainingOldStock();
        int remainingNew = afterSalesAndDamage.getRemainingNewStock();
        int removeOld = (int) Math.min((long) remainingOld, removal);
        remainingOld -= removeOld;
        removal -= removeOld;
        remainingNew -= (int) removal;
        return new Result(remainingOld, remainingNew);
    }

    private static void validateNonNegative(
            int previousLeftover, int newBreadQuantity, int soldQuantity, int damagedQuantity) {
        if (previousLeftover < 0 || newBreadQuantity < 0 ||
                soldQuantity < 0 || damagedQuantity < 0) {
            throw new IllegalArgumentException("Jumlah roti tidak boleh negatif");
        }
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
            return Math.addExact(remainingOldStock, remainingNewStock);
        }
    }
}
