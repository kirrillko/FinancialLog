package com.example.financialmagazine;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financialmagazine.adapters.CategoryAdapter;
import com.example.financialmagazine.adapters.SwipeToDeleteCallback;
import com.example.financialmagazine.db.CategoryDao;
import com.example.financialmagazine.db.FinancialRecordDatabase;
import com.example.financialmagazine.models.Category;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AddCategoryActivity extends AppCompatActivity {
    List<Category> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView recyclerView = findViewById(R.id.category_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(AddCategoryActivity.this));

        FinancialRecordDatabase db = FinancialRecordDatabase.getDatabase(this);
        CategoryDao categoryDao = db.categoryDao();
        categories = categoryDao.getAllCategories();

        CategoryAdapter mAdapter = new CategoryAdapter(this, categories, categoryDao);
        ItemTouchHelper.Callback callback = new SwipeToDeleteCallback(mAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(mAdapter);

        Map<String, List<Category>> categorizedMap = Utils.categorizeCategories(categories);

        Spinner typeSpinner = findViewById(R.id.spinner_type);
        String[] typeValues = categorizedMap.keySet().toArray(new String[0]);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, typeValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);

        TextView categoryView = findViewById(R.id.editTextCategory);

        Button button = findViewById(R.id.button2);
        button.setOnClickListener( v -> {
            String type = typeSpinner.getSelectedItem().toString();
            String category = categoryView.getText().toString().trim();
            // Проверяем строку на пустоту
            if (TextUtils.isEmpty(category)) {
                // Если строка пустая, выводим сообщение
                Toast.makeText(this, "Нужно задать название категории", Toast.LENGTH_SHORT).show();
                return;
            }
            if(isCategoryExists(categories, type, category)){
                Toast.makeText(this, "Категория с таким названием уже есть", Toast.LENGTH_SHORT).show();
            }else{
                Category mCategory = new Category(type, category);
                categoryDao.insert(mCategory);
                Toast.makeText(this, "Категория добавлена", Toast.LENGTH_SHORT).show();
                categoryView.setText("");
                mAdapter.addCategory(mCategory);
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Категории");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
            finish();
        });
    }

    public boolean isCategoryExists(List<Category> categories, String type, String category) {
        for (Category cat : categories) {
            if (cat.getType().equals(type) && cat.getCategory().equals(category)) {
                return true; // Если нашли совпадение, возвращаем true
            }
        }
        return false; // Если не нашли совпадений, возвращаем false
    }
}