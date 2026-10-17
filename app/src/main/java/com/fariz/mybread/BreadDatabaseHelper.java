package com.fariz.mybread;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** Local transactional storage for My Bread. No network access is used. */
public final class BreadDatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "my_bread.db";
    private static final int DATABASE_VERSION = 1;

    public BreadDatabaseHelper(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
        setWriteAheadLoggingEnabled(true);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE daily_records (" +
                "record_date TEXT PRIMARY KEY NOT NULL," +
                "previous_leftover INTEGER NOT NULL CHECK(previous_leftover >= 0)," +
                "new_bread_quantity INTEGER NOT NULL CHECK(new_bread_quantity >= 0)," +
                "sold_quantity INTEGER NOT NULL CHECK(sold_quantity >= 0)," +
                "damaged_quantity INTEGER NOT NULL CHECK(damaged_quantity >= 0)," +
                "cost_per_bread INTEGER NOT NULL CHECK(cost_per_bread > 0)," +
                "selling_price INTEGER NOT NULL CHECK(selling_price > 0))");
        db.execSQL("CREATE TABLE expenses (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "record_date TEXT NOT NULL REFERENCES daily_records(record_date) ON DELETE CASCADE," +
                "description TEXT NOT NULL," +
                "amount INTEGER NOT NULL CHECK(amount >= 0))");
        db.execSQL("CREATE TABLE stock_adjustments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "record_date TEXT NOT NULL REFERENCES daily_records(record_date) ON DELETE CASCADE," +
                "quantity INTEGER NOT NULL CHECK(quantity != 0)," +
                "reason TEXT NOT NULL)");
        db.execSQL("CREATE INDEX idx_expenses_date ON expenses(record_date)");
        db.execSQL("CREATE INDEX idx_adjustments_date ON stock_adjustments(record_date)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Version 1 is the first released schema. Future changes must use explicit migrations.
    }

    public synchronized void saveDay(DayBundle bundle) {
        validateBundle(bundle);
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("record_date", bundle.record.date);
            values.put("previous_leftover", bundle.record.previousLeftover);
            values.put("new_bread_quantity", bundle.record.newBreadQuantity);
            values.put("sold_quantity", bundle.record.soldQuantity);
            values.put("damaged_quantity", bundle.record.damagedQuantity);
            values.put("cost_per_bread", bundle.record.costPerBread);
            values.put("selling_price", bundle.record.sellingPrice);
            db.insertWithOnConflict("daily_records", null, values, SQLiteDatabase.CONFLICT_REPLACE);

            db.delete("expenses", "record_date = ?", new String[]{bundle.record.date});
            db.delete("stock_adjustments", "record_date = ?", new String[]{bundle.record.date});
            insertChildren(db, bundle);

            // A past edit recalculates carried stock for each following day atomically.
            // If a later day's sales/damage no longer fit its recalculated stock, the entire
            // transaction rolls back so existing records remain intact.
            recalculateFollowingOpenings(db, bundle.record.date);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void insertChildren(SQLiteDatabase db, DayBundle bundle) {
        for (ExpenseItem expense : bundle.expenses) {
            ContentValues values = new ContentValues();
            values.put("record_date", bundle.record.date);
            values.put("description", expense.description.trim());
            values.put("amount", expense.amount);
            db.insertOrThrow("expenses", null, values);
        }
        for (StockAdjustment adjustment : bundle.adjustments) {
            ContentValues values = new ContentValues();
            values.put("record_date", bundle.record.date);
            values.put("quantity", adjustment.quantity);
            values.put("reason", adjustment.reason.trim());
            db.insertOrThrow("stock_adjustments", null, values);
        }
    }

    private void recalculateFollowingOpenings(SQLiteDatabase db, String changedDate) {
        DailyRecord previous = getRecord(db, changedDate);
        if (previous == null) {
            return;
        }
        List<DailyRecord> following = readRecords(db, null, changedDate, true, 0);
        int previousEnd = calculateEndingStock(db, previous);
        for (DailyRecord record : following) {
            record.previousLeftover = previousEnd;
            ContentValues update = new ContentValues();
            update.put("previous_leftover", previousEnd);
            db.update("daily_records", update, "record_date = ?",
                    new String[]{record.date});
            try {
                previousEnd = calculateEndingStock(db, record);
            } catch (RuntimeException invalid) {
                throw new IllegalArgumentException(
                        "Perubahan membuat stok tidak cukup pada tanggal " + record.date +
                                ". Penjualan, roti rusak, atau penyesuaian tanggal tersebut perlu " +
                                "dikoreksi. Tidak ada perubahan yang disimpan.", invalid);
            }
        }
    }

    public DayBundle getDay(String date) {
        SQLiteDatabase db = getReadableDatabase();
        DailyRecord record = getRecord(db, date);
        if (record == null) {
            return null;
        }
        List<ExpenseItem> expenses = new ArrayList<>();
        try (Cursor cursor = db.rawQuery(
                "SELECT description, amount FROM expenses WHERE record_date = ? ORDER BY id ASC",
                new String[]{date})) {
            while (cursor.moveToNext()) {
                expenses.add(new ExpenseItem(cursor.getString(0), cursor.getLong(1)));
            }
        }
        List<StockAdjustment> adjustments = new ArrayList<>();
        try (Cursor cursor = db.rawQuery(
                "SELECT quantity, reason FROM stock_adjustments WHERE record_date = ? ORDER BY id ASC",
                new String[]{date})) {
            while (cursor.moveToNext()) {
                adjustments.add(new StockAdjustment(cursor.getInt(0), cursor.getString(1)));
            }
        }
        return new DayBundle(record, expenses, adjustments);
    }

    public List<DailyRecord> getRecentDays(int limit) {
        if (limit < 1) {
            return Collections.emptyList();
        }
        return readRecords(getReadableDatabase(), null, null, false, limit);
    }

    public List<DailyRecord> getAllDays() {
        return readRecords(getReadableDatabase(), null, null, true, 0);
    }

    public DailyRecord getLatestDayBefore(String date) {
        List<DailyRecord> records = readRecords(getReadableDatabase(), null, date, false, 1);
        return records.isEmpty() ? null : records.get(0);
    }

    public int getFollowingDaysCount(String date) {
        try (Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM daily_records WHERE record_date > ?",
                new String[]{date})) {
            return cursor.moveToFirst() ? cursor.getInt(0) : 0;
        }
    }

    public MonthlySummary getMonthlySummary(String month) {
        MonthlySummary summary = new MonthlySummary();
        List<DailyRecord> records = readRecords(
                getReadableDatabase(), month + "-%", null, true, 0);
        for (DailyRecord record : records) {
            summary.days++;
            summary.newBread += record.newBreadQuantity;
            summary.sold += record.soldQuantity;
            summary.damaged += record.damagedQuantity;
            summary.modal += record.getModal();
            summary.revenue += record.getRevenue();
            summary.grossMargin += record.getGrossMargin();
            summary.expenses += record.expenseTotal;
            summary.cashDifference += record.getCashDifference();
            summary.endingStock = record.getEndingStock();
        }
        return summary;
    }

    private List<DailyRecord> readRecords(
            SQLiteDatabase db, String dateLike, String beforeDate,
            boolean ascending, int limit) {
        StringBuilder sql = new StringBuilder(
                "SELECT record_date, previous_leftover, new_bread_quantity, sold_quantity, " +
                        "damaged_quantity, cost_per_bread, selling_price FROM daily_records");
        List<String> args = new ArrayList<>();
        List<String> clauses = new ArrayList<>();
        if (dateLike != null) {
            clauses.add("record_date LIKE ?");
            args.add(dateLike);
        }
        if (beforeDate != null) {
            clauses.add("record_date " + (ascending ? ">" : "<") + " ?");
            args.add(beforeDate);
        }
        if (!clauses.isEmpty()) {
            sql.append(" WHERE ").append(join(clauses, " AND "));
        }
        sql.append(" ORDER BY record_date ").append(ascending ? "ASC" : "DESC");
        if (limit > 0) {
            sql.append(" LIMIT ").append(limit);
        }
        List<DailyRecord> records = new ArrayList<>();
        try (Cursor cursor = db.rawQuery(sql.toString(), args.toArray(new String[0]))) {
            while (cursor.moveToNext()) {
                DailyRecord record = recordFromCursor(cursor);
                populateDerived(db, record);
                records.add(record);
            }
        }
        return records;
    }

    private static String join(List<String> values, String separator) {
        StringBuilder out = new StringBuilder();
        for (String value : values) {
            if (out.length() > 0) out.append(separator);
            out.append(value);
        }
        return out.toString();
    }

    private DailyRecord getRecord(SQLiteDatabase db, String date) {
        try (Cursor cursor = db.rawQuery(
                "SELECT record_date, previous_leftover, new_bread_quantity, sold_quantity, " +
                        "damaged_quantity, cost_per_bread, selling_price " +
                        "FROM daily_records WHERE record_date = ?",
                new String[]{date})) {
            if (!cursor.moveToFirst()) {
                return null;
            }
            DailyRecord record = recordFromCursor(cursor);
            populateDerived(db, record);
            return record;
        }
    }

    private static DailyRecord recordFromCursor(Cursor cursor) {
        return new DailyRecord(
                cursor.getString(0),
                cursor.getInt(1),
                cursor.getInt(2),
                cursor.getInt(3),
                cursor.getInt(4),
                cursor.getLong(5),
                cursor.getLong(6));
    }

    private void populateDerived(SQLiteDatabase db, DailyRecord record) {
        record.expenseTotal = querySum(db,
                "SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE record_date = ?",
                record.date);
        long adjustmentNet = querySum(db,
                "SELECT COALESCE(SUM(quantity), 0) FROM stock_adjustments WHERE record_date = ?",
                record.date);
        record.adjustmentNet = Math.toIntExact(adjustmentNet);
    }

    private static long querySum(SQLiteDatabase db, String sql, String date) {
        try (Cursor cursor = db.rawQuery(sql, new String[]{date})) {
            return cursor.moveToFirst() ? cursor.getLong(0) : 0L;
        }
    }

    private int calculateEndingStock(SQLiteDatabase db, DailyRecord record) {
        long netAdjustment = querySum(db,
                "SELECT COALESCE(SUM(quantity), 0) FROM stock_adjustments WHERE record_date = ?",
                record.date);
        return StockFifoCalculator.calculateEndingStockWithAdjustment(
                record.previousLeftover, record.newBreadQuantity,
                record.soldQuantity, record.damagedQuantity,
                Math.toIntExact(netAdjustment)).getTotalRemainingStock();
    }

    private static int calculateEndingStock(DayBundle bundle) {
        long netAdjustment = 0L;
        for (StockAdjustment adjustment : bundle.adjustments) {
            netAdjustment = Math.addExact(netAdjustment, adjustment.quantity);
        }
        return StockFifoCalculator.calculateEndingStockWithAdjustment(
                bundle.record.previousLeftover, bundle.record.newBreadQuantity,
                bundle.record.soldQuantity, bundle.record.damagedQuantity,
                Math.toIntExact(netAdjustment)).getTotalRemainingStock();
    }

    private static void validateBundle(DayBundle bundle) {
        if (bundle == null || bundle.record == null || !isValidDate(bundle.record.date)) {
            throw new IllegalArgumentException("Tanggal catatan tidak valid.");
        }
        DailyRecord record = bundle.record;
        if (record.previousLeftover < 0 || record.newBreadQuantity < 0 ||
                record.soldQuantity < 0 || record.damagedQuantity < 0) {
            throw new IllegalArgumentException("Jumlah roti tidak boleh negatif.");
        }
        if (record.costPerBread <= 0 || record.sellingPrice <= 0) {
            throw new IllegalArgumentException("Harga modal dan jual harus lebih dari nol.");
        }
        if (bundle.expenses == null || bundle.adjustments == null) {
            throw new IllegalArgumentException("Daftar pengeluaran/penyesuaian tidak valid.");
        }
        for (ExpenseItem expense : bundle.expenses) {
            if (expense == null || expense.description == null ||
                    expense.description.trim().isEmpty() || expense.amount < 0) {
                throw new IllegalArgumentException(
                        "Setiap pengeluaran harus memiliki keterangan dan nominal tidak negatif.");
            }
        }
        long netAdjustment = 0L;
        for (StockAdjustment adjustment : bundle.adjustments) {
            if (adjustment == null || adjustment.quantity == 0 ||
                    adjustment.reason == null || adjustment.reason.trim().isEmpty()) {
                throw new IllegalArgumentException(
                        "Penyesuaian stok harus memiliki jumlah bukan nol dan alasan.");
            }
            netAdjustment = Math.addExact(netAdjustment, adjustment.quantity);
        }
        StockFifoCalculator.calculateEndingStockWithAdjustment(
                record.previousLeftover, record.newBreadQuantity, record.soldQuantity,
                record.damagedQuantity, Math.toIntExact(netAdjustment));
    }

    private static boolean isValidDate(String date) {
        if (date == null || !date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return false;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        format.setLenient(false);
        ParsePosition position = new ParsePosition(0);
        Date parsed = format.parse(date, position);
        return parsed != null && position.getIndex() == date.length();
    }

    public JSONObject exportBackup() throws JSONException {
        JSONObject root = new JSONObject();
        root.put("application", "My Bread");
        root.put("schema_version", DATABASE_VERSION);
        root.put("exported_at_epoch_ms", System.currentTimeMillis());
        JSONArray days = new JSONArray();
        for (DailyRecord record : getAllDays()) {
            DayBundle bundle = getDay(record.date);
            JSONObject day = new JSONObject();
            day.put("date", record.date);
            day.put("previous_leftover", record.previousLeftover);
            day.put("new_bread_quantity", record.newBreadQuantity);
            day.put("sold_quantity", record.soldQuantity);
            day.put("damaged_quantity", record.damagedQuantity);
            day.put("cost_per_bread", record.costPerBread);
            day.put("selling_price", record.sellingPrice);
            JSONArray expenses = new JSONArray();
            for (ExpenseItem expense : bundle.expenses) {
                JSONObject item = new JSONObject();
                item.put("description", expense.description);
                item.put("amount", expense.amount);
                expenses.put(item);
            }
            day.put("expenses", expenses);
            JSONArray adjustments = new JSONArray();
            for (StockAdjustment adjustment : bundle.adjustments) {
                JSONObject item = new JSONObject();
                item.put("quantity", adjustment.quantity);
                item.put("reason", adjustment.reason);
                adjustments.put(item);
            }
            day.put("adjustments", adjustments);
            days.put(day);
        }
        root.put("days", days);
        return root;
    }

    public BackupPreview previewBackup(JSONObject root) throws JSONException {
        List<DayBundle> bundles = parseBackup(root);
        BackupPreview preview = new BackupPreview();
        preview.days = bundles.size();
        for (DayBundle bundle : bundles) {
            preview.expenses += bundle.expenses.size();
            preview.adjustments += bundle.adjustments.size();
        }
        return preview;
    }

    public synchronized void replaceAllFromBackup(JSONObject root) throws JSONException {
        List<DayBundle> bundles = parseBackup(root);
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete("daily_records", null, null);
            for (DayBundle bundle : bundles) {
                validateBundle(bundle);
                ContentValues values = new ContentValues();
                values.put("record_date", bundle.record.date);
                values.put("previous_leftover", bundle.record.previousLeftover);
                values.put("new_bread_quantity", bundle.record.newBreadQuantity);
                values.put("sold_quantity", bundle.record.soldQuantity);
                values.put("damaged_quantity", bundle.record.damagedQuantity);
                values.put("cost_per_bread", bundle.record.costPerBread);
                values.put("selling_price", bundle.record.sellingPrice);
                db.insertOrThrow("daily_records", null, values);
                insertChildren(db, bundle);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private static List<DayBundle> parseBackup(JSONObject root) throws JSONException {
        if (root == null || !"My Bread".equals(root.optString("application")) ||
                root.optInt("schema_version", -1) != DATABASE_VERSION ||
                !(root.opt("days") instanceof JSONArray)) {
            throw new JSONException("Format backup tidak dikenal atau tidak kompatibel.");
        }
        JSONArray days = root.getJSONArray("days");
        List<DayBundle> bundles = new ArrayList<>();
        Set<String> uniqueDates = new HashSet<>();
        for (int i = 0; i < days.length(); i++) {
            JSONObject day = days.getJSONObject(i);
            String date = day.getString("date");
            if (!uniqueDates.add(date)) {
                throw new JSONException("Tanggal duplikat dalam backup: " + date);
            }
            DailyRecord record = new DailyRecord(
                    date,
                    day.getInt("previous_leftover"),
                    day.getInt("new_bread_quantity"),
                    day.getInt("sold_quantity"),
                    day.getInt("damaged_quantity"),
                    day.getLong("cost_per_bread"),
                    day.getLong("selling_price"));
            List<ExpenseItem> expenses = new ArrayList<>();
            JSONArray expenseArray = day.optJSONArray("expenses");
            if (expenseArray != null) {
                for (int e = 0; e < expenseArray.length(); e++) {
                    JSONObject item = expenseArray.getJSONObject(e);
                    expenses.add(new ExpenseItem(
                            item.getString("description"), item.getLong("amount")));
                }
            }
            List<StockAdjustment> adjustments = new ArrayList<>();
            JSONArray adjustmentArray = day.optJSONArray("adjustments");
            if (adjustmentArray != null) {
                for (int a = 0; a < adjustmentArray.length(); a++) {
                    JSONObject item = adjustmentArray.getJSONObject(a);
                    adjustments.add(new StockAdjustment(
                            item.getInt("quantity"), item.getString("reason")));
                }
            }
            DayBundle bundle = new DayBundle(record, expenses, adjustments);
            try {
                validateBundle(bundle);
            } catch (RuntimeException invalid) {
                throw new JSONException("Catatan tanggal " + date + " tidak valid: " +
                        invalid.getMessage());
            }
            bundles.add(bundle);
        }
        Collections.sort(bundles, (left, right) ->
                left.record.date.compareTo(right.record.date));
        return bundles;
    }

    public static final class DailyRecord {
        public final String date;
        public int previousLeftover;
        public final int newBreadQuantity;
        public final int soldQuantity;
        public final int damagedQuantity;
        public final long costPerBread;
        public final long sellingPrice;
        public long expenseTotal;
        public int adjustmentNet;

        public DailyRecord(String date, int previousLeftover, int newBreadQuantity,
                           int soldQuantity, int damagedQuantity,
                           long costPerBread, long sellingPrice) {
            this.date = date;
            this.previousLeftover = previousLeftover;
            this.newBreadQuantity = newBreadQuantity;
            this.soldQuantity = soldQuantity;
            this.damagedQuantity = damagedQuantity;
            this.costPerBread = costPerBread;
            this.sellingPrice = sellingPrice;
        }

        public long getModal() {
            return Math.multiplyExact((long) newBreadQuantity, costPerBread);
        }

        public long getRevenue() {
            return Math.multiplyExact((long) soldQuantity, sellingPrice);
        }

        public long getGrossMargin() {
            return Math.multiplyExact((long) soldQuantity, sellingPrice - costPerBread);
        }

        public long getCashDifference() {
            return Math.subtractExact(Math.subtractExact(getRevenue(), getModal()), expenseTotal);
        }

        public int getEndingStock() {
            return Math.addExact(Math.subtractExact(
                    Math.addExact(Math.addExact(previousLeftover, newBreadQuantity), adjustmentNet),
                    soldQuantity), -damagedQuantity);
        }
    }

    public static final class ExpenseItem {
        public final String description;
        public final long amount;
        public ExpenseItem(String description, long amount) {
            this.description = description;
            this.amount = amount;
        }
    }

    public static final class StockAdjustment {
        public final int quantity;
        public final String reason;
        public StockAdjustment(int quantity, String reason) {
            this.quantity = quantity;
            this.reason = reason;
        }
    }

    public static final class DayBundle {
        public final DailyRecord record;
        public final List<ExpenseItem> expenses;
        public final List<StockAdjustment> adjustments;
        public DayBundle(DailyRecord record, List<ExpenseItem> expenses,
                         List<StockAdjustment> adjustments) {
            this.record = record;
            this.expenses = expenses;
            this.adjustments = adjustments;
        }
    }

    public static final class MonthlySummary {
        public int days;
        public long newBread;
        public long sold;
        public long damaged;
        public long modal;
        public long revenue;
        public long grossMargin;
        public long expenses;
        public long cashDifference;
        public int endingStock;
    }

    public static final class BackupPreview {
        public int days;
        public int expenses;
        public int adjustments;
    }
}
