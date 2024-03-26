package com.example.financialmagazine;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.financialmagazine.db.CategoryDao;
import com.example.financialmagazine.db.FinancialRecordDao;
import com.example.financialmagazine.db.FinancialRecordDatabase;
import com.example.financialmagazine.models.Category;
import com.example.financialmagazine.models.FinancialRecord;
import com.example.financialmagazine.models.FinancialRecordInfo;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BalanceActivity extends AppCompatActivity {
    private LinearLayout filterFixed;
    private LinearLayout balanceView;
    private LinearLayout chartView;
    private Spinner spinner_filter_date;
    private Button button_filter_fixed;
    private LinearLayout filterUser;
    private DatePicker datePickerBegin;
    private DatePicker datePickerEnd;
    private Button button_filer_user;
    private PieChart pieChart;
    private TextView incomeText;
    private TextView outcomeText;
    private TextView balanceText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_balance);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FinancialRecordDatabase db = FinancialRecordDatabase.getDatabase(this);
        CategoryDao categoryDao = db.categoryDao();
        FinancialRecordDao financialRecordDao = db.financialRecordDao();

        // Инициализация всех именованных виджетов
        filterFixed = findViewById(R.id.balance_fixed);
        spinner_filter_date = findViewById(R.id.spinner_balance_date);
        button_filter_fixed = findViewById(R.id.button_balance_fixed);
        filterUser = findViewById(R.id.balance_user);
        datePickerBegin = findViewById(R.id.date_picker_begin);
        datePickerEnd = findViewById(R.id.date_picker_end);
        button_filer_user = findViewById(R.id.button_balance_user);
        balanceView = findViewById(R.id.balance_result);
        chartView = findViewById(R.id.balance_chart);
        incomeText = findViewById(R.id.text_view_income);
        outcomeText = findViewById(R.id.text_view_outcome);
        balanceText = findViewById(R.id.text_view_balance);
        pieChart = findViewById(R.id.pieChart);

        // Адаптер для спиннера "День/Неделя/Месяц"
        String[] periodItems = {"День", "Неделя", "Месяц"};
        ArrayAdapter<String> periodAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, periodItems);
        periodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_filter_date.setAdapter(periodAdapter);

        button_filter_fixed.setOnClickListener(v -> {
            balanceView.setVisibility(View.VISIBLE);
            chartView.setVisibility(View.VISIBLE);
            long date_index = spinner_filter_date.getSelectedItemId();
            // Получаем текущую дату и время
            Calendar calendar = Calendar.getInstance();
            Date currentDate = calendar.getTime();
            switch ((int) date_index)
            {
                case 0:
                {
                    // Устанавливаем время на начало текущего дня (00:00:00)
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    Date startOfCurrentDay = calendar.getTime();
                    List<FinancialRecord> financialRecords = financialRecordDao.getRecordsForLastDay(startOfCurrentDay.getTime());
                    List<FinancialRecordInfo> financialRecordsInfo = new ArrayList<>();
                    for (int i = 0; i < financialRecords.size(); i++) {
                        FinancialRecord f = financialRecords.get(i);
                        Category c = categoryDao.getCategoryById(f.getCategoryId());
                        financialRecordsInfo.add(new FinancialRecordInfo(f.getId(), c, f.getAmount(), f.getTimestamp()));
                    }
                    calculateBalance(financialRecordsInfo);
                    getExpenceChart(financialRecordsInfo);
                }
                break;
                case 1:
                {
                    // Получаем временную метку для начала недели назад
                    calendar.setTime(currentDate);
                    calendar.add(Calendar.WEEK_OF_YEAR, -1);
                    Date startOfLastWeek = calendar.getTime();
                    List<FinancialRecord> financialRecords = financialRecordDao.getRecordsForLastDay(startOfLastWeek.getTime());
                    List<FinancialRecordInfo> financialRecordsInfo = new ArrayList<>();
                    for (int i = 0; i < financialRecords.size(); i++) {
                        FinancialRecord f = financialRecords.get(i);
                        Category c = categoryDao.getCategoryById(f.getCategoryId());
                        financialRecordsInfo.add(new FinancialRecordInfo(f.getId(), c, f.getAmount(), f.getTimestamp()));
                    }
                    calculateBalance(financialRecordsInfo);
                    getExpenceChart(financialRecordsInfo);
                }
                case 2:
                {
                    // Получаем временную метку для начала месяца назад
                    calendar.setTime(currentDate);
                    calendar.add(Calendar.MONTH, -1);
                    calendar.set(Calendar.DAY_OF_MONTH, 1); // Устанавливаем первое число месяца
                    Date startOfLastMonth = calendar.getTime();
                    List<FinancialRecord> financialRecords = financialRecordDao.getRecordsForLastDay(startOfLastMonth.getTime());
                    List<FinancialRecordInfo> financialRecordsInfo = new ArrayList<>();
                    for (int i = 0; i < financialRecords.size(); i++) {
                        FinancialRecord f = financialRecords.get(i);
                        Category c = categoryDao.getCategoryById(f.getCategoryId());
                        financialRecordsInfo.add(new FinancialRecordInfo(f.getId(), c, f.getAmount(), f.getTimestamp()));
                    }
                    calculateBalance(financialRecordsInfo);
                    getExpenceChart(financialRecordsInfo);
                }
                break;
            }
            filterFixed.setVisibility(View.GONE);
        });

        button_filer_user.setOnClickListener(v -> {
            balanceView.setVisibility(View.VISIBLE);
            chartView.setVisibility(View.VISIBLE);
            // Получение выбранной даты
            int day_begin = datePickerBegin.getDayOfMonth();
            int month_begin = datePickerBegin.getMonth();
            int year_begin = datePickerBegin.getYear();

            Calendar calendar_begin = Calendar.getInstance();
            calendar_begin.set(year_begin, month_begin, day_begin);

            int day_end = datePickerEnd.getDayOfMonth();
            int month_end = datePickerEnd.getMonth();
            int year_end = datePickerEnd.getYear();

            Calendar calendar_end = Calendar.getInstance();
            calendar_end.set(year_end, month_end, day_end);

            List<FinancialRecord> financialRecords = financialRecordDao.getRecordsForPeriod(calendar_begin.getTime().getTime(), calendar_end.getTime().getTime());
            List<FinancialRecordInfo> financialRecordsInfo = new ArrayList<>();
            for (int i = 0; i < financialRecords.size(); i++) {
                FinancialRecord f = financialRecords.get(i);
                Category c = categoryDao.getCategoryById(f.getCategoryId());
                financialRecordsInfo.add(new FinancialRecordInfo(f.getId(), c, f.getAmount(), f.getTimestamp()));
            }
            calculateBalance(financialRecordsInfo);
            filterUser.setVisibility(View.GONE);

            getExpenceChart(financialRecordsInfo);
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Баланс");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
            finish();
        });
    }

    private void getExpenceChart(List<FinancialRecordInfo> financialRecordsInfo) {
        Map<String, Double> expensesByCategory = calculateExpensesByCategory(financialRecordsInfo);
        ArrayList<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Double> entry : expensesByCategory.entrySet()) {
            entries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
        }
        PieDataSet dataSet = new PieDataSet(entries, "Траты по категориям");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));
        pieChart.setData(data);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setHoleRadius(58f);
        pieChart.setDrawCenterText(true);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setRotationEnabled(false);
        pieChart.animateY(1000);
        pieChart.invalidate();
    }

    // Метод для построения круговой диаграммы трат по категориям
    public Map<String, Double> calculateExpensesByCategory(List<FinancialRecordInfo> records) {
        Map<String, Double> expensesByCategory = new HashMap<>();

        for (FinancialRecordInfo record : records) {
            if(record.getCategory().getType().equals("Доход"))
                continue;
            String category = record.getCategory().getCategory();
            double amount = record.getAmount();

            expensesByCategory.put(category, expensesByCategory.getOrDefault(category, 0.0) + amount);
        }

        return expensesByCategory;
    }

    // Метод для рассчета баланса
    public void calculateBalance(List<FinancialRecordInfo> records) {
        double expenses = 0;
        double income = 0;

        for (FinancialRecordInfo record : records) {
            if (record.getCategory().getType().equals("Расход")) {
                expenses += record.getAmount();
            } else if (record.getCategory().getType().equals("Доход")) {
                income += record.getAmount();
            }
        }

        incomeText.setText("+" + income);
        outcomeText.setText("-" + expenses);
        if(income - expenses > 0){
            balanceText.setText("+" + (income - expenses));
            balanceText.setTextColor(Color.GREEN);
        }else{
            balanceText.setText("" + (income - expenses));
            balanceText.setTextColor(Color.RED);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.item_date){
            filterFixed.setVisibility(View.VISIBLE);
            filterUser.setVisibility(View.GONE);
        }else if(id == R.id.item_period){
            filterFixed.setVisibility(View.GONE);
            filterUser.setVisibility(View.VISIBLE);
        }
        return false;
    }
}