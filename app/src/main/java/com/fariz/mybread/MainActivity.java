package com.fariz.mybread;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/** Offline-first My Bread user interface. */
public final class MainActivity extends Activity {
    private static final int REQUEST_EXPORT = 401;
    private static final int REQUEST_IMPORT = 402;
    private static final int COLOR_BACKGROUND = Color.rgb(246, 248, 244);
    private static final int COLOR_CARD = Color.WHITE;
    private static final int COLOR_TEXT = Color.rgb(32, 45, 35);
    private static final int COLOR_MUTED = Color.rgb(104, 116, 105);
    private static final int COLOR_GREEN = Color.rgb(46, 106, 69);
    private static final int COLOR_LIGHT_GREEN = Color.rgb(228, 239, 226);
    private static final int COLOR_BORDER = Color.rgb(224, 230, 222);

    private BreadDatabaseHelper database;
    private LinearLayout root;
    private LinearLayout content;
    private String editingDate;
    private boolean editingExisting;
    private String pendingBackup;
    private EditText previousLeftoverField;
    private EditText newBreadField;
    private EditText soldField;
    private EditText damagedField;
    private LinearLayout expensesContainer;
    private LinearLayout adjustmentsContainer;
    private TextView previewText;
    private final List<ExpenseRow> expenseRows = new ArrayList<>();
    private final List<AdjustmentRow> adjustmentRows = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = new BreadDatabaseHelper(this);
        showDashboard();
    }

    @Override
    protected void onDestroy() {
        if (database != null) database.close();
        super.onDestroy();
    }

    private String today() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Calendar.getInstance().getTime());
    }

    private String currentMonth() {
        return new SimpleDateFormat("yyyy-MM", Locale.US).format(Calendar.getInstance().getTime());
    }

    private String displayDate(String date) {
        try {
            java.util.Date parsed = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(date);
            return new SimpleDateFormat("EEEE, dd MMM yyyy", new Locale("id", "ID")).format(parsed);
        } catch (Exception ignored) {
            return date;
        }
    }

    private String money(long amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        format.setMaximumFractionDigits(0);
        format.setMinimumFractionDigits(0);
        return format.format(amount);
    }

    private int dp(float value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private void render(String selected) {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(COLOR_BACKGROUND);

        LinearLayout heading = new LinearLayout(this);
        heading.setOrientation(LinearLayout.VERTICAL);
        heading.setPadding(dp(20), dp(18), dp(20), dp(12));
        TextView brand = text("My Bread", 26, COLOR_TEXT, true);
        heading.addView(brand);
        TextView subtitle = text("Pencatatan usaha roti • offline", 13, COLOR_MUTED, false);
        heading.addView(subtitle);
        root.addView(heading, new LinearLayout.LayoutParams(-1, -2));

        HorizontalScrollView navScroll = new HorizontalScrollView(this);
        navScroll.setHorizontalScrollBarEnabled(false);
        LinearLayout nav = new LinearLayout(this);
        nav.setPadding(dp(12), dp(4), dp(12), dp(10));
        nav.setOrientation(LinearLayout.HORIZONTAL);
        String[][] tabs = {
                {"Ringkasan", "dashboard"}, {"Catat", "daily"},
                {"Riwayat", "history"}, {"Laporan", "reports"}, {"Cadangan", "backup"}
        };
        for (String[] tab : tabs) {
            Button button = new Button(this);
            button.setText(tab[0]);
            button.setAllCaps(false);
            button.setTextSize(13);
            button.setMinHeight(dp(42));
            button.setPadding(dp(12), 0, dp(12), 0);
            boolean active = tab[1].equals(selected);
            button.setTextColor(active ? Color.WHITE : COLOR_TEXT);
            button.setBackground(background(active ? COLOR_GREEN : COLOR_CARD, 22, active ? COLOR_GREEN : COLOR_BORDER));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, dp(42));
            lp.setMargins(dp(4), 0, dp(4), 0);
            nav.addView(button, lp);
            button.setOnClickListener(v -> navigate(tab[1]));
        }
        navScroll.addView(nav);
        root.addView(navScroll, new LinearLayout.LayoutParams(-1, -2));

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(12), dp(2), dp(12), dp(28));
        scroll.addView(content);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        setContentView(root);
    }

    private void navigate(String page) {
        switch (page) {
            case "daily": showDailyEntry(today()); break;
            case "history": showHistory(); break;
            case "reports": showReports(); break;
            case "backup": showBackup(); break;
            default: showDashboard();
        }
    }

    private TextView text(String value, int sizeSp, int color, boolean bold) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(sizeSp);
        view.setTextColor(color);
        if (bold) view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return view;
    }

    private GradientDrawable background(int color, int radiusDp, int strokeColor) {
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(color);
        shape.setCornerRadius(dp(radiusDp));
        if (strokeColor != 0) shape.setStroke(dp(1), strokeColor);
        return shape;
    }

    private LinearLayout card(String title) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(15), dp(16), dp(16));
        card.setBackground(background(COLOR_CARD, 18, COLOR_BORDER));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, dp(7), 0, dp(7));
        card.setLayoutParams(lp);
        if (title != null && !title.isEmpty()) {
            TextView heading = text(title, 16, COLOR_TEXT, true);
            LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(-1, -2);
            titleLp.bottomMargin = dp(10);
            card.addView(heading, titleLp);
        }
        return card;
    }

    private Button button(String label, boolean primary) {
        Button button = new Button(this);
        button.setText(label);
        button.setAllCaps(false);
        button.setTextSize(14);
        button.setTextColor(primary ? Color.WHITE : COLOR_GREEN);
        button.setPadding(dp(14), dp(5), dp(14), dp(5));
        button.setBackground(background(primary ? COLOR_GREEN : COLOR_LIGHT_GREEN, 12, 0));
        button.setMinHeight(dp(46));
        return button;
    }

    private void addSectionTitle(String title, String description) {
        content.addView(text(title, 22, COLOR_TEXT, true));
        if (description != null) {
            TextView sub = text(description, 13, COLOR_MUTED, false);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
            lp.topMargin = dp(4);
            lp.bottomMargin = dp(8);
            content.addView(sub, lp);
        }
    }

    private void addMetric(LinearLayout card, String label, String value) {
        LinearLayout metric = new LinearLayout(this);
        metric.setOrientation(LinearLayout.VERTICAL);
        metric.setPadding(0, dp(3), 0, dp(5));
        metric.addView(text(label, 12, COLOR_MUTED, false));
        TextView val = text(value, 19, COLOR_TEXT, true);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.topMargin = dp(3);
        metric.addView(val, lp);
        card.addView(metric);
    }

    private void showDashboard() {
        editingDate = today();
        editingExisting = database.getDay(editingDate) != null;
        render("dashboard");
        addSectionTitle("Ringkasan hari ini", displayDate(today()));
        BreadDatabaseHelper.DayBundle bundle = database.getDay(today());
        BreadDatabaseHelper.DailyRecord record = bundle == null
                ? new BreadDatabaseHelper.DailyRecord(today(), 0, 0, 0, 0,
                    BreadBusinessCalculator.DEFAULT_COST_PER_BREAD,
                    BreadBusinessCalculator.DEFAULT_SELLING_PRICE_PER_BREAD)
                : bundle.record;
        record.expenseTotal = bundle == null ? 0 : expenseTotal(bundle.expenses);
        if (bundle != null) record.adjustmentNet = adjustmentTotal(bundle.adjustments);

        LinearLayout stats = card("Keuangan");
        addMetric(stats, "Omzet", money(record.getRevenue()));
        addMetric(stats, "Margin kotor", money(record.getGrossMargin()));
        addMetric(stats, "Modal roti baru", money(record.getModal()));
        addMetric(stats, "Pengeluaran", money(record.expenseTotal));
        addMetric(stats, "Selisih kas (bukan laba bersih)", money(record.getCashDifference()));
        content.addView(stats);

        LinearLayout stock = card("Stok hari ini");
        try {
            int ending = bundle == null ? 0 : database.getDay(today()).record.getEndingStock();
            stock.addView(text("Sisa stok: " + ending + " roti", 18, COLOR_TEXT, true));
            stock.addView(text("Terjual: " + record.soldQuantity + " • Rusak: " +
                    record.damagedQuantity, 13, COLOR_MUTED, false));
        } catch (RuntimeException invalid) {
            stock.addView(text("Stok belum valid. Periksa catatan hari ini.", 14, Color.RED, true));
        }
        content.addView(stock);

        Button dailyButton = button(bundle == null ? "Catat hari ini" : "Buka catatan hari ini", true);
        dailyButton.setOnClickListener(v -> showDailyEntry(today()));
        content.addView(dailyButton, fullWidthParams());

        LinearLayout chartCard = card("Omzet 7 hari terakhir");
        List<BreadDatabaseHelper.DailyRecord> recent = database.getRecentDays(7);
        Collections.reverse(recent);
        chartCard.addView(new WeeklyChartView(this, recent));
        content.addView(chartCard);

        LinearLayout recentCard = card("Catatan terbaru");
        List<BreadDatabaseHelper.DailyRecord> latest = database.getRecentDays(3);
        if (latest.isEmpty()) {
            recentCard.addView(text("Belum ada catatan. Mulai dari pencatatan harian.", 14, COLOR_MUTED, false));
        } else {
            for (BreadDatabaseHelper.DailyRecord item : latest) {
                recentCard.addView(text(displayDate(item.date) + " • omzet " + money(item.getRevenue()),
                        14, COLOR_TEXT, false));
            }
        }
        content.addView(recentCard);
    }

    private LinearLayout.LayoutParams fullWidthParams() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, dp(6), 0, dp(6));
        return lp;
    }

    private EditText input(String hint, String value, boolean signed) {
        EditText field = new EditText(this);
        field.setSingleLine(true);
        field.setTextSize(15);
        field.setTextColor(COLOR_TEXT);
        field.setHintTextColor(COLOR_MUTED);
        field.setHint(hint);
        field.setPadding(dp(12), dp(9), dp(12), dp(9));
        field.setBackground(background(Color.WHITE, 10, COLOR_BORDER));
        field.setInputType(InputType.TYPE_CLASS_NUMBER |
                (signed ? InputType.TYPE_NUMBER_FLAG_SIGNED : 0));
        field.setText(value);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, dp(48));
        lp.setMargins(0, dp(3), 0, dp(8));
        field.setLayoutParams(lp);
        return field;
    }

    private EditText addLabeledInput(LinearLayout parent, String label, String value, boolean signed) {
        parent.addView(text(label, 13, COLOR_MUTED, true));
        EditText field = input(label, value, signed);
        parent.addView(field);
        return field;
    }

    private void showDailyEntry(String date) {
        editingDate = date;
        BreadDatabaseHelper.DayBundle saved = database.getDay(date);
        editingExisting = saved != null;
        expenseRows.clear();
        adjustmentRows.clear();
        render("daily");
        addSectionTitle(editingExisting ? "Edit catatan harian" : "Catatan harian",
                displayDate(date) + " • tanggal terkunci pada alur edit biasa");

        LinearLayout quantities = card("Jumlah roti");
        int defaultOpening = 0;
        if (saved == null) {
            BreadDatabaseHelper.DailyRecord previous = database.getLatestDayBefore(date);
            if (previous != null) {
                try { defaultOpening = previous.getEndingStock(); } catch (RuntimeException ignored) { }
            }
        }
        previousLeftoverField = addLabeledInput(quantities, "Sisa roti dari kemarin",
                String.valueOf(saved == null ? defaultOpening : saved.record.previousLeftover), false);
        newBreadField = addLabeledInput(quantities, "Roti baru dibawa hari ini",
                String.valueOf(saved == null ? 0 : saved.record.newBreadQuantity), false);
        soldField = addLabeledInput(quantities, "Roti terjual", String.valueOf(saved == null ? 0 : saved.record.soldQuantity), false);
        damagedField = addLabeledInput(quantities, "Roti rusak / tidak layak", String.valueOf(saved == null ? 0 : saved.record.damagedQuantity), false);
        content.addView(quantities);

        LinearLayout expensesCard = card("Pengeluaran terpisah");
        expensesContainer = new LinearLayout(this);
        expensesContainer.setOrientation(LinearLayout.VERTICAL);
        expensesCard.addView(expensesContainer);
        Button addExpense = button("+ Tambah pengeluaran", false);
        addExpense.setOnClickListener(v -> addExpenseRow("", ""));
        expensesCard.addView(addExpense);
        content.addView(expensesCard);

        LinearLayout adjustmentsCard = card("Penyesuaian stok");
        adjustmentsCard.addView(text("Gunakan jumlah positif untuk menambah stok dan negatif untuk mengurangi. Setiap penyesuaian wajib memiliki alasan.", 13, COLOR_MUTED, false));
        adjustmentsContainer = new LinearLayout(this);
        adjustmentsContainer.setOrientation(LinearLayout.VERTICAL);
        adjustmentsCard.addView(adjustmentsContainer);
        Button addAdjustment = button("+ Tambah penyesuaian", false);
        addAdjustment.setOnClickListener(v -> addAdjustmentRow("", ""));
        adjustmentsCard.addView(addAdjustment);
        content.addView(adjustmentsCard);

        if (saved != null) {
            for (BreadDatabaseHelper.ExpenseItem item : saved.expenses) {
                addExpenseRow(item.description, String.valueOf(item.amount));
            }
            for (BreadDatabaseHelper.StockAdjustment item : saved.adjustments) {
                addAdjustmentRow(String.valueOf(item.quantity), item.reason);
            }
        }
        if (expenseRows.isEmpty()) addExpenseRow("", "");
        if (adjustmentRows.isEmpty()) addAdjustmentRow("", "");

        LinearLayout preview = card("Pratinjau langsung");
        previewText = text("", 14, COLOR_TEXT, false);
        previewText.setLineSpacing(dp(4), 1f);
        preview.addView(previewText);
        content.addView(preview);

        TextView note = text("Catatan: modal dihitung dari seluruh roti baru yang dibawa. Selisih kas bukan laba bersih.", 12, COLOR_MUTED, false);
        content.addView(note, fullWidthParams());

        Button save = button(editingExisting ? "Simpan perubahan" : "Simpan catatan", true);
        save.setOnClickListener(v -> requestSaveDay());
        content.addView(save, fullWidthParams());
        Button cancel = button("Batal", false);
        cancel.setOnClickListener(v -> {
            if (!today().equals(editingDate)) showHistory(); else showDashboard();
        });
        content.addView(cancel, fullWidthParams());

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { updatePreview(); }
            @Override public void afterTextChanged(Editable s) { }
        };
        previousLeftoverField.addTextChangedListener(watcher);
        newBreadField.addTextChangedListener(watcher);
        soldField.addTextChangedListener(watcher);
        damagedField.addTextChangedListener(watcher);
        updatePreview();
    }

    private void addExpenseRow(String description, String amount) {
        if (expensesContainer == null) return;
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(0, dp(5), 0, dp(5));
        EditText desc = input("Keterangan, mis. bensin", description, false);
        desc.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        EditText cost = input("Nominal rupiah", amount, false);
        LinearLayout actions = new LinearLayout(this);
        actions.setGravity(Gravity.END);
        Button remove = button("Hapus", false);
        actions.addView(remove);
        row.addView(desc);
        row.addView(cost);
        row.addView(actions);
        expensesContainer.addView(row);
        ExpenseRow item = new ExpenseRow(desc, cost, row);
        expenseRows.add(item);
        remove.setOnClickListener(v -> {
            expenseRows.remove(item);
            expensesContainer.removeView(row);
            updatePreview();
        });
        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { updatePreview(); }
            @Override public void afterTextChanged(Editable s) { }
        };
        desc.addTextChangedListener(watcher);
        cost.addTextChangedListener(watcher);
    }

    private void addAdjustmentRow(String quantity, String reason) {
        if (adjustmentsContainer == null) return;
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(0, dp(5), 0, dp(5));
        EditText qty = input("Jumlah penyesuaian (+/-)", quantity, true);
        EditText why = input("Alasan penyesuaian", reason, false);
        why.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        LinearLayout actions = new LinearLayout(this);
        actions.setGravity(Gravity.END);
        Button remove = button("Hapus", false);
        actions.addView(remove);
        row.addView(qty);
        row.addView(why);
        row.addView(actions);
        adjustmentsContainer.addView(row);
        AdjustmentRow item = new AdjustmentRow(qty, why, row);
        adjustmentRows.add(item);
        remove.setOnClickListener(v -> {
            adjustmentRows.remove(item);
            adjustmentsContainer.removeView(row);
            updatePreview();
        });
        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { updatePreview(); }
            @Override public void afterTextChanged(Editable s) { }
        };
        qty.addTextChangedListener(watcher);
        why.addTextChangedListener(watcher);
    }

    private int readInt(EditText field, String label) {
        String value = field == null ? "" : field.getText().toString().trim();
        if (value.isEmpty()) return 0;
        try { return Integer.parseInt(value); }
        catch (NumberFormatException e) { throw new IllegalArgumentException(label + " tidak valid."); }
    }

    private long readLong(EditText field, String label) {
        String value = field.getText().toString().trim();
        if (value.isEmpty()) return 0L;
        try { return Long.parseLong(value); }
        catch (NumberFormatException e) { throw new IllegalArgumentException(label + " tidak valid."); }
    }

    private List<BreadDatabaseHelper.ExpenseItem> collectExpenses() {
        List<BreadDatabaseHelper.ExpenseItem> items = new ArrayList<>();
        for (ExpenseRow row : expenseRows) {
            String desc = row.description.getText().toString().trim();
            String amountText = row.amount.getText().toString().trim();
            if (desc.isEmpty() && amountText.isEmpty()) continue;
            if (desc.isEmpty() || amountText.isEmpty()) {
                throw new IllegalArgumentException("Lengkapi keterangan dan nominal setiap pengeluaran.");
            }
            long amount = readLong(row.amount, "Nominal pengeluaran");
            if (amount < 0) throw new IllegalArgumentException("Nominal pengeluaran tidak boleh negatif.");
            items.add(new BreadDatabaseHelper.ExpenseItem(desc, amount));
        }
        return items;
    }

    private List<BreadDatabaseHelper.StockAdjustment> collectAdjustments() {
        List<BreadDatabaseHelper.StockAdjustment> items = new ArrayList<>();
        for (AdjustmentRow row : adjustmentRows) {
            String quantityText = row.quantity.getText().toString().trim();
            String reason = row.reason.getText().toString().trim();
            if (quantityText.isEmpty() && reason.isEmpty()) continue;
            int quantity = readInt(row.quantity, "Jumlah penyesuaian");
            if (quantity == 0 || reason.isEmpty()) {
                throw new IllegalArgumentException("Penyesuaian stok memerlukan jumlah bukan nol dan alasan.");
            }
            items.add(new BreadDatabaseHelper.StockAdjustment(quantity, reason));
        }
        return items;
    }

    private BreadDatabaseHelper.DayBundle collectBundle() {
        int opening = readInt(previousLeftoverField, "Sisa roti dari kemarin");
        int fresh = readInt(newBreadField, "Roti baru");
        int sold = readInt(soldField, "Roti terjual");
        int damaged = readInt(damagedField, "Roti rusak");
        BreadDatabaseHelper.DailyRecord record = new BreadDatabaseHelper.DailyRecord(
                editingDate, opening, fresh, sold, damaged,
                BreadBusinessCalculator.DEFAULT_COST_PER_BREAD,
                BreadBusinessCalculator.DEFAULT_SELLING_PRICE_PER_BREAD);
        return new BreadDatabaseHelper.DayBundle(record, collectExpenses(), collectAdjustments());
    }

    private long expenseTotal(List<BreadDatabaseHelper.ExpenseItem> items) {
        long total = 0L;
        for (BreadDatabaseHelper.ExpenseItem item : items) total = Math.addExact(total, item.amount);
        return total;
    }

    private int adjustmentTotal(List<BreadDatabaseHelper.StockAdjustment> items) {
        long total = 0L;
        for (BreadDatabaseHelper.StockAdjustment item : items) total = Math.addExact(total, item.quantity);
        return Math.toIntExact(total);
    }

    private void updatePreview() {
        if (previewText == null || previousLeftoverField == null) return;
        try {
            BreadDatabaseHelper.DayBundle bundle = collectBundle();
            BreadDatabaseHelper.DailyRecord record = bundle.record;
            record.expenseTotal = expenseTotal(bundle.expenses);
            int netAdjustment = adjustmentTotal(bundle.adjustments);
            int stock = StockFifoCalculator.calculateEndingStockWithAdjustment(
                    record.previousLeftover, record.newBreadQuantity, record.soldQuantity,
                    record.damagedQuantity, netAdjustment).getTotalRemainingStock();
            previewText.setText(
                    "Modal: " + money(record.getModal()) +
                    "\nOmzet: " + money(record.getRevenue()) +
                    "\nMargin kotor: " + money(record.getGrossMargin()) +
                    "\nTotal pengeluaran: " + money(record.expenseTotal) +
                    "\nSelisih kas: " + money(record.getCashDifference()) +
                    "\nPerkiraan sisa stok: " + stock + " roti");
            previewText.setTextColor(COLOR_TEXT);
        } catch (RuntimeException invalid) {
            previewText.setText("Input belum valid: " + invalid.getMessage());
            previewText.setTextColor(Color.rgb(160, 54, 40));
        }
    }

    private void requestSaveDay() {
        final BreadDatabaseHelper.DayBundle bundle;
        try {
            bundle = collectBundle();
            // Validate quantities, FIFO, expense rows and adjustment reasons before showing confirmation.
            bundle.record.expenseTotal = expenseTotal(bundle.expenses);
            StockFifoCalculator.calculateEndingStockWithAdjustment(
                    bundle.record.previousLeftover, bundle.record.newBreadQuantity,
                    bundle.record.soldQuantity, bundle.record.damagedQuantity,
                    adjustmentTotal(bundle.adjustments));
        } catch (RuntimeException invalid) {
            showError(invalid.getMessage());
            return;
        }

        int affected = editingExisting ? database.getFollowingDaysCount(editingDate) : 0;
        if (affected > 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Perubahan memengaruhi hari berikutnya")
                    .setMessage("Ada " + affected + " catatan setelah " + editingDate +
                            ". Stok awal hari berikutnya akan dihitung ulang berantai. " +
                            "Jika penjualan/roti rusak menjadi melebihi stok, penyimpanan dibatalkan.")
                    .setNegativeButton("Batal", null)
                    .setPositiveButton("Lanjut simpan", (dialog, which) -> saveBundle(bundle))
                    .show();
        } else {
            saveBundle(bundle);
        }
    }

    private void saveBundle(BreadDatabaseHelper.DayBundle bundle) {
        try {
            database.saveDay(bundle);
            Toast.makeText(this, "Catatan tersimpan.", Toast.LENGTH_SHORT).show();
            showDashboard();
        } catch (RuntimeException failure) {
            showError(failure.getMessage() == null ? "Catatan tidak bisa disimpan." : failure.getMessage());
        }
    }

    private void showHistory() {
        render("history");
        addSectionTitle("Riwayat", "Pilih catatan untuk melihat atau mengedit. Mengubah tanggal lama menghitung ulang stok hari berikutnya.");
        List<BreadDatabaseHelper.DailyRecord> records = database.getRecentDays(60);
        if (records.isEmpty()) {
            LinearLayout empty = card("Belum ada riwayat");
            empty.addView(text("Catatan yang disimpan akan tampil di sini.", 14, COLOR_MUTED, false));
            content.addView(empty);
            return;
        }
        for (BreadDatabaseHelper.DailyRecord record : records) {
            LinearLayout item = card(displayDate(record.date));
            item.addView(text("Roti baru: " + record.newBreadQuantity + " • Terjual: " +
                    record.soldQuantity + " • Rusak: " + record.damagedQuantity, 13, COLOR_MUTED, false));
            item.addView(text("Omzet " + money(record.getRevenue()) + " • Sisa " +
                    record.getEndingStock() + " roti", 15, COLOR_TEXT, true));
            item.addView(text("Modal " + money(record.getModal()) + " • Selisih kas " +
                    money(record.getCashDifference()), 13, COLOR_MUTED, false));
            Button edit = button("Buka / edit catatan", false);
            edit.setOnClickListener(v -> showDailyEntry(record.date));
            item.addView(edit);
            content.addView(item);
        }
    }

    private void showReports() {
        render("reports");
        String month = currentMonth();
        BreadDatabaseHelper.MonthlySummary summary = database.getMonthlySummary(month);
        addSectionTitle("Laporan bulanan", "Periode " + month);
        LinearLayout totals = card("Ringkasan periode");
        addMetric(totals, "Jumlah hari tercatat", String.valueOf(summary.days));
        addMetric(totals, "Roti baru dibawa", String.valueOf(summary.newBread));
        addMetric(totals, "Roti terjual", String.valueOf(summary.sold));
        addMetric(totals, "Roti rusak", String.valueOf(summary.damaged));
        addMetric(totals, "Modal roti baru", money(summary.modal));
        addMetric(totals, "Omzet", money(summary.revenue));
        addMetric(totals, "Margin kotor", money(summary.grossMargin));
        addMetric(totals, "Pengeluaran", money(summary.expenses));
        addMetric(totals, "Selisih kas (bukan laba bersih)", money(summary.cashDifference));
        content.addView(totals);
        LinearLayout chart = card("Grafik omzet 7 hari terakhir");
        List<BreadDatabaseHelper.DailyRecord> recent = database.getRecentDays(7);
        Collections.reverse(recent);
        chart.addView(new WeeklyChartView(this, recent));
        content.addView(chart);
        LinearLayout explanation = card("Cara membaca laporan");
        explanation.addView(text("Modal dihitung dari seluruh roti baru yang dibawa. Margin kotor adalah omzet dikurangi modal per roti terjual. Selisih kas mengurangi modal seluruh roti baru dan semua pengeluaran, sehingga bukan laba bersih ketika masih ada roti tersisa.", 14, COLOR_MUTED, false));
        content.addView(explanation);
    }

    private void showBackup() {
        render("backup");
        addSectionTitle("Cadangan data", "Data disimpan secara lokal di perangkat ini. Simpan berkas backup di tempat aman.");
        LinearLayout exportCard = card("Ekspor");
        exportCard.addView(text("Buat satu berkas JSON berisi catatan harian, pengeluaran, dan penyesuaian stok.", 14, COLOR_MUTED, false));
        Button export = button("Ekspor cadangan", true);
        export.setOnClickListener(v -> {
            try {
                pendingBackup = database.exportBackup().toString(2);
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/json");
                intent.putExtra(Intent.EXTRA_TITLE, "my-bread-backup-" + today() + ".json");
                startActivityForResult(intent, REQUEST_EXPORT);
            } catch (Exception e) {
                showError("Tidak dapat menyiapkan backup: " + e.getMessage());
            }
        });
        exportCard.addView(export);
        content.addView(exportCard);

        LinearLayout importCard = card("Pulihkan");
        importCard.addView(text("Pemulihan akan mengganti seluruh data lokal. Berkas diperiksa terlebih dahulu, lalu aplikasi menampilkan pratinjau dan meminta konfirmasi.", 14, COLOR_MUTED, false));
        Button restore = button("Pilih berkas backup", false);
        restore.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/json");
            startActivityForResult(intent, REQUEST_IMPORT);
        });
        importCard.addView(restore);
        content.addView(importCard);
        LinearLayout safety = card("Perlindungan data");
        safety.addView(text("Berkas yang tidak valid ditolak. Proses pemulihan berjalan dalam satu transaksi; bila gagal, data lama dipertahankan.", 14, COLOR_MUTED, false));
        content.addView(safety);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null || data.getData() == null) return;
        Uri uri = data.getData();
        if (requestCode == REQUEST_EXPORT) {
            writeBackup(uri);
        } else if (requestCode == REQUEST_IMPORT) {
            previewRestore(uri);
        }
    }

    private void writeBackup(Uri uri) {
        if (pendingBackup == null) return;
        try (OutputStream output = getContentResolver().openOutputStream(uri)) {
            if (output == null) throw new IOException("Berkas tujuan tidak dapat dibuka.");
            output.write(pendingBackup.getBytes(StandardCharsets.UTF_8));
            output.flush();
            pendingBackup = null;
            Toast.makeText(this, "Backup berhasil diekspor.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            showError("Ekspor gagal: " + e.getMessage());
        }
    }

    private void previewRestore(Uri uri) {
        try {
            String jsonText;
            try (InputStream input = getContentResolver().openInputStream(uri)) {
                if (input == null) throw new IOException("Berkas tidak dapat dibuka.");
                StringBuilder builder = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
                    char[] buffer = new char[4096];
                    int count;
                    while ((count = reader.read(buffer)) != -1) {
                        builder.append(buffer, 0, count);
                        if (builder.length() > 5_000_000) throw new IOException("Berkas backup terlalu besar.");
                    }
                }
                jsonText = builder.toString();
            }
            JSONObject backup = new JSONObject(jsonText);
            BreadDatabaseHelper.BackupPreview preview = database.previewBackup(backup);
            new AlertDialog.Builder(this)
                    .setTitle("Konfirmasi pemulihan")
                    .setMessage("Isi backup: " + preview.days + " catatan harian, " +
                            preview.expenses + " pengeluaran, " + preview.adjustments +
                            " penyesuaian stok. Semua data lokal yang sekarang akan diganti.")
                    .setNegativeButton("Batal", null)
                    .setPositiveButton("Ganti data", (dialog, which) -> {
                        try {
                            database.replaceAllFromBackup(backup);
                            Toast.makeText(this, "Pemulihan backup berhasil.", Toast.LENGTH_LONG).show();
                            showDashboard();
                        } catch (Exception e) {
                            showError("Pemulihan gagal; data lama dipertahankan. " + e.getMessage());
                        }
                    })
                    .show();
        } catch (Exception e) {
            showError("Backup ditolak: " + e.getMessage());
        }
    }

    private void showError(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Perlu diperiksa")
                .setMessage(message == null ? "Operasi tidak berhasil." : message)
                .setPositiveButton("OK", null)
                .show();
    }

    private final class WeeklyChartView extends View {
        private final List<BreadDatabaseHelper.DailyRecord> records;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        WeeklyChartView(Activity context, List<BreadDatabaseHelper.DailyRecord> records) {
            super(context);
            this.records = new ArrayList<>(records);
            setMinimumHeight(dp(190));
            setLayoutParams(new LinearLayout.LayoutParams(-1, dp(190)));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float width = getWidth();
            float bottom = getHeight() - dp(26);
            float top = dp(30);
            paint.setColor(COLOR_MUTED);
            paint.setTextSize(dp(11));
            canvas.drawText("Omzet (Rp)", 0, dp(15), paint);
            paint.setColor(COLOR_BORDER);
            canvas.drawLine(0, bottom, width, bottom, paint);
            long max = 1L;
            for (BreadDatabaseHelper.DailyRecord record : records) max = Math.max(max, record.getRevenue());
            if (records.isEmpty()) {
                paint.setColor(COLOR_MUTED);
                canvas.drawText("Belum ada data grafik", 0, top + dp(28), paint);
                return;
            }
            float slot = width / records.size();
            float barWidth = slot * 0.48f;
            for (int i = 0; i < records.size(); i++) {
                BreadDatabaseHelper.DailyRecord record = records.get(i);
                float barHeight = (float) ((bottom - top) * (record.getRevenue() / (double) max));
                float left = i * slot + (slot - barWidth) / 2f;
                paint.setColor(COLOR_GREEN);
                canvas.drawRoundRect(new RectF(left, bottom - barHeight, left + barWidth, bottom),
                        dp(4), dp(4), paint);
                paint.setColor(COLOR_MUTED);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText(record.date.substring(8), left + barWidth / 2f, getHeight() - dp(6), paint);
            }
            paint.setTextAlign(Paint.Align.LEFT);
        }
    }

    private static final class ExpenseRow {
        final EditText description;
        final EditText amount;
        final View view;
        ExpenseRow(EditText description, EditText amount, View view) {
            this.description = description;
            this.amount = amount;
            this.view = view;
        }
    }

    private static final class AdjustmentRow {
        final EditText quantity;
        final EditText reason;
        final View view;
        AdjustmentRow(EditText quantity, EditText reason, View view) {
            this.quantity = quantity;
            this.reason = reason;
            this.view = view;
        }
    }
}
