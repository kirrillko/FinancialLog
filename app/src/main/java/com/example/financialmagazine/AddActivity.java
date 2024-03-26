package com.example.financialmagazine;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AddActivity extends AppCompatActivity {
    List<Category> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FinancialRecordDatabase db = FinancialRecordDatabase.getDatabase(this);
        CategoryDao categoryDao = db.categoryDao();
        FinancialRecordDao financialRecordDao = db.financialRecordDao();
        categories = categoryDao.getAllCategories();

        Map<String, List<Category>> categorizedMap = Utils.categorizeCategories(categories);

        // Получаем ссылку из макета
        Spinner typeSpinner = findViewById(R.id.type_spinner);
        Spinner categorySpinner = findViewById(R.id.category_spinner);
        EditText amountEditText = findViewById(R.id.amount_edit_text);
        DatePicker datePicker = findViewById(R.id.date_picker);
        // Создаем массив строк для категорий
        String[] typeValues = categorizedMap.keySet().toArray(new String[0]);
        // Создаем адаптер для Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, typeValues);
        // Устанавливаем стиль отображения элементов в выпадающем списке
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Устанавливаем адаптер для Spinner
        typeSpinner.setAdapter(adapter);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    String typeValue = typeValues[(int) id];
                    List<Category> cats = categorizedMap.get(typeValue);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, Utils.getCategoryNames(cats).toArray(new String[0]));
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    categorySpinner.setAdapter(adapter);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {

                }
            }
        );

        Button btn = findViewById(R.id.button);
        btn.setOnClickListener(v -> {
            // Получение выбранных значений из спиннеров
            String selectedType = typeSpinner.getSelectedItem().toString();
            String selectedCategory = categorySpinner.getSelectedItem().toString();

            // Получение введенной суммы
            String amountString = amountEditText.getText().toString();

            // Получение выбранной даты
            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth();
            int year = datePicker.getYear();

            // Проверка, не является ли сумма пустой
            if (amountString.isEmpty()) {
                Toast.makeText(AddActivity.this, "Введите сумму", Toast.LENGTH_SHORT).show();
                return;
            }

            // Преобразование суммы в числовой формат
            double amount = Double.parseDouble(amountString);

            // Создание объекта Calendar и установка выбранной даты
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);

            // Получение временной метки (timestamp) в миллисекундах
            long timestamp = calendar.getTimeInMillis();

            int categoryId = getCategory(categories, selectedType, selectedCategory).getId();
            FinancialRecord record = new FinancialRecord(categoryId, amount, timestamp);
            financialRecordDao.insert(record);
            finish();
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Запись");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
            finish();
        });
    }

    public Category getCategory(List<Category> categories, String type, String category) {
        for (Category cat : categories) {
            if (cat.getType().equals(type) && cat.getCategory().equals(category)) {
                return cat; // Если нашли совпадение, возвращаем true
            }
        }
        return null; // Если не нашли совпадений, возвращаем false
    }
}