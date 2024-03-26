package com.example.financialmagazine;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financialmagazine.adapters.FinancialRecordInfoAdapter;
import com.example.financialmagazine.db.CategoryDao;
import com.example.financialmagazine.db.FinancialRecordDao;
import com.example.financialmagazine.db.FinancialRecordDatabase;
import com.example.financialmagazine.models.Category;
import com.example.financialmagazine.models.FinancialRecord;
import com.example.financialmagazine.models.FinancialRecordInfo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FilterActivity extends AppCompatActivity {
    private LinearLayout filterFixed;
    private Spinner spinner_filter_fixed_type;
    private Spinner spinner_filter_date;
    private Button button_filter_fixed;
    private LinearLayout filterUser;
    private Spinner spinner_filter_user;
    private DatePicker datePickerBegin;
    private DatePicker datePickerEnd;
    private Button button_filer_user;
    private RecyclerView recyclerFilter;
    FinancialRecordInfoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_filter);
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
        spinner_filter_fixed_type = findViewById(R.id.spinner_filter_fixed_type);
        spinner_filter_date = findViewById(R.id.spinner_balance_date);
        button_filter_fixed = findViewById(R.id.button_balance_fixed);
        filterUser = findViewById(R.id.balance_user);
        spinner_filter_user = findViewById(R.id.spinner_filter_user);
        datePickerBegin = findViewById(R.id.date_picker_begin);
        datePickerEnd = findViewById(R.id.date_picker_end);
        button_filer_user = findViewById(R.id.button_balance_user);
        recyclerFilter = findViewById(R.id.recycler_filter);
        recyclerFilter.setLayoutManager(new LinearLayoutManager(FilterActivity.this));
        adapter = new FinancialRecordInfoAdapter(this, new ArrayList<>(), financialRecordDao);
        recyclerFilter.setAdapter(adapter);

        // Адаптер для спиннера "Доход/Расход"
        String[] incomeExpenseItems = {"Доход/Расход", "Доход", "Расход"};
        ArrayAdapter<String> incomeExpenseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, incomeExpenseItems);
        incomeExpenseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_filter_user.setAdapter(incomeExpenseAdapter);
        spinner_filter_fixed_type.setAdapter(incomeExpenseAdapter);

        // Адаптер для спиннера "День/Неделя/Месяц"
        String[] periodItems = {"День", "Неделя", "Месяц"};
        ArrayAdapter<String> periodAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, periodItems);
        periodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_filter_date.setAdapter(periodAdapter);

        button_filter_fixed.setOnClickListener(v -> {
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
                    setListToAdapter(financialRecordsInfo, spinner_filter_fixed_type.getSelectedItemId());
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
                    setListToAdapter(financialRecordsInfo, spinner_filter_fixed_type.getSelectedItemId());
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
                    setListToAdapter(financialRecordsInfo, spinner_filter_fixed_type.getSelectedItemId());
                }
                break;
            }
        });

        button_filer_user.setOnClickListener(v -> {
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
            setListToAdapter(financialRecordsInfo, spinner_filter_user.getSelectedItemId());
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Поиск");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
            finish();
        });
    }

    private void setListToAdapter(List<FinancialRecordInfo> financialRecordsInfo, long type_index) {
        if(type_index == 0)
            adapter.addRecords(financialRecordsInfo);
        else{
            String type = spinner_filter_fixed_type.getSelectedItem().toString();
            adapter.addRecords(filterRecordsByType(financialRecordsInfo, type));
        }
    }

    List<FinancialRecordInfo> filterRecordsByType(List<FinancialRecordInfo> records, String type)
    {
        return IntStream.range(0, records.size()).filter(i -> Objects.equals(records.get(i).getCategory().getType(), type)).mapToObj(records::get).collect(Collectors.toList());
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