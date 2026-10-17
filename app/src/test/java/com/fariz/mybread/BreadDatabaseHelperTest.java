package com.fariz.mybread;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35)
public class BreadDatabaseHelperTest {
    private Context context;
    private BreadDatabaseHelper database;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();
        context.deleteDatabase(BreadDatabaseHelper.DATABASE_NAME);
        database = new BreadDatabaseHelper(context);
    }

    @After
    public void tearDown() {
        if (database != null) database.close();
        context.deleteDatabase(BreadDatabaseHelper.DATABASE_NAME);
    }

    @Test
    public void savesSeparateExpensesAndStockAdjustments() {
        BreadDatabaseHelper.DailyRecord record = record("2026-10-15", 10, 5, 2, 1);
        BreadDatabaseHelper.DayBundle bundle = new BreadDatabaseHelper.DayBundle(
                record,
                Arrays.asList(
                        new BreadDatabaseHelper.ExpenseItem("Bensin", 12_000L),
                        new BreadDatabaseHelper.ExpenseItem("Parkir", 2_000L)),
                Collections.singletonList(new BreadDatabaseHelper.StockAdjustment(3, "Koreksi hitung")));
        database.saveDay(bundle);

        BreadDatabaseHelper.DayBundle loaded = database.getDay("2026-10-15");
        assertNotNull(loaded);
        assertEquals(2, loaded.expenses.size());
        assertEquals(1, loaded.adjustments.size());
        assertEquals(14_000L, loaded.record.expenseTotal);
        assertEquals(15, loaded.record.getEndingStock());
        assertEquals(14_000L, loaded.record.expenseTotal);
    }

    @Test
    public void editingAnEarlierDayRecalculatesFollowingOpeningStock() {
        database.saveDay(new BreadDatabaseHelper.DayBundle(
                record("2026-10-15", 20, 20, 10, 0),
                Collections.emptyList(), Collections.emptyList()));
        database.saveDay(new BreadDatabaseHelper.DayBundle(
                record("2026-10-16", 30, 10, 5, 0),
                Collections.emptyList(), Collections.emptyList()));

        database.saveDay(new BreadDatabaseHelper.DayBundle(
                record("2026-10-15", 20, 20, 15, 0),
                Collections.emptyList(), Collections.emptyList()));

        BreadDatabaseHelper.DayBundle following = database.getDay("2026-10-16");
        assertEquals(25, following.record.previousLeftover);
        assertEquals(30, following.record.getEndingStock());
    }

    @Test
    public void impossiblePastEditRollsBackAllChanges() {
        database.saveDay(new BreadDatabaseHelper.DayBundle(
                record("2026-10-15", 20, 0, 10, 0),
                Collections.emptyList(), Collections.emptyList()));
        database.saveDay(new BreadDatabaseHelper.DayBundle(
                record("2026-10-16", 10, 0, 10, 0),
                Collections.emptyList(), Collections.emptyList()));

        try {
            database.saveDay(new BreadDatabaseHelper.DayBundle(
                    record("2026-10-15", 20, 0, 15, 0),
                    Collections.emptyList(), Collections.emptyList()));
            fail("Expected invalid downstream stock to be rejected");
        } catch (IllegalArgumentException expected) {
            // Expected: the transaction must leave both rows unchanged.
        }
        assertEquals(10, database.getDay("2026-10-15").record.soldQuantity);
        assertEquals(10, database.getDay("2026-10-16").record.previousLeftover);
    }

    @Test
    public void backupCanBeRestoredAndInvalidBackupIsRejectedWithoutReplacingData() throws Exception {
        database.saveDay(new BreadDatabaseHelper.DayBundle(
                record("2026-10-15", 4, 10, 5, 1),
                Collections.singletonList(new BreadDatabaseHelper.ExpenseItem("Parkir", 2_000L)),
                Collections.emptyList()));
        JSONObject backup = database.exportBackup();
        assertEquals(1, database.previewBackup(backup).days);

        JSONObject empty = new JSONObject();
        empty.put("application", "My Bread");
        empty.put("schema_version", 1);
        empty.put("days", new JSONArray());
        database.replaceAllFromBackup(empty);
        assertNull(database.getDay("2026-10-15"));

        database.replaceAllFromBackup(backup);
        assertNotNull(database.getDay("2026-10-15"));
        assertEquals(2_000L, database.getDay("2026-10-15").record.expenseTotal);

        JSONObject malformed = new JSONObject();
        try {
            database.replaceAllFromBackup(malformed);
            fail("Expected invalid backup to be rejected");
        } catch (org.json.JSONException expected) {
            // Validation happens before the replacement transaction.
        }
        assertNotNull(database.getDay("2026-10-15"));
    }

    private BreadDatabaseHelper.DailyRecord record(
            String date, int opening, int fresh, int sold, int damaged) {
        return new BreadDatabaseHelper.DailyRecord(
                date, opening, fresh, sold, damaged, 1_400L, 2_500L);
    }
}
