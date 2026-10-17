package com.fariz.mybread;

import android.app.Application;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35)
public class MainActivityFormValidationTest {
    private Application application;
    private MainActivity activity;

    @Before
    public void setUp() {
        application = RuntimeEnvironment.getApplication();
        application.deleteDatabase(BreadDatabaseHelper.DATABASE_NAME);
    }

    @After
    public void tearDown() {
        if (activity != null) activity.finish();
        application.deleteDatabase(BreadDatabaseHelper.DATABASE_NAME);
    }

    @Test
    public void newEntrySaveIsDisabledUntilValidDataChanges() {
        openDailyForm();
        Button save = findButton(activity.getWindow().getDecorView(), "Simpan catatan");
        assertNotNull(save);
        assertFalse(save.isEnabled());

        EditText fresh = findEditTextByHint(
                activity.getWindow().getDecorView(), "Roti baru dibawa hari ini");
        assertNotNull(fresh);
        fresh.setText("2");
        assertTrue(save.isEnabled());

        fresh.setText("0");
        assertFalse(save.isEnabled());
    }

    @Test
    public void invalidQuantityDisablesSave() {
        openDailyForm();
        Button save = findButton(activity.getWindow().getDecorView(), "Simpan catatan");
        EditText fresh = findEditTextByHint(
                activity.getWindow().getDecorView(), "Roti baru dibawa hari ini");
        assertNotNull(save);
        assertNotNull(fresh);

        fresh.setText("-1");
        assertFalse(save.isEnabled());
    }

    @Test
    public void editingUnchangedRecordKeepsSaveDisabled() {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        BreadDatabaseHelper database = new BreadDatabaseHelper(application);
        database.saveDay(new BreadDatabaseHelper.DayBundle(
                new BreadDatabaseHelper.DailyRecord(date, 0, 1, 0, 0, 1_400L, 2_500L),
                Collections.emptyList(), Collections.emptyList()));
        database.close();

        openDailyForm();
        Button save = findButton(activity.getWindow().getDecorView(), "Simpan perubahan");
        EditText fresh = findEditTextByHint(
                activity.getWindow().getDecorView(), "Roti baru dibawa hari ini");
        assertNotNull(save);
        assertNotNull(fresh);
        assertFalse(save.isEnabled());

        fresh.setText("3");
        assertTrue(save.isEnabled());

        fresh.setText("1");
        assertFalse(save.isEnabled());
    }

    @Test
    public void incompleteExpenseDisablesSave() {
        openDailyForm();
        Button save = findButton(activity.getWindow().getDecorView(), "Simpan catatan");
        EditText description = findEditTextByHint(
                activity.getWindow().getDecorView(), "Keterangan, mis. bensin");
        assertNotNull(save);
        assertNotNull(description);

        description.setText("Bensin");
        assertFalse(save.isEnabled());
    }

    private void openDailyForm() {
        activity = Robolectric.buildActivity(MainActivity.class).setup().get();
        Button daily = findButton(activity.getWindow().getDecorView(), "Catat");
        assertNotNull(daily);
        daily.performClick();
    }

    private Button findButton(View root, String label) {
        if (root instanceof Button && label.contentEquals(((Button) root).getText())) {
            return (Button) root;
        }
        if (root instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) root;
            for (int i = 0; i < group.getChildCount(); i++) {
                Button found = findButton(group.getChildAt(i), label);
                if (found != null) return found;
            }
        }
        return null;
    }

    private EditText findEditTextByHint(View root, String hint) {
        if (root instanceof EditText) {
            CharSequence value = ((EditText) root).getHint();
            if (value != null && hint.contentEquals(value)) return (EditText) root;
        }
        if (root instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) root;
            for (int i = 0; i < group.getChildCount(); i++) {
                EditText found = findEditTextByHint(group.getChildAt(i), hint);
                if (found != null) return found;
            }
        }
        return null;
    }
}
