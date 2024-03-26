package com.example.financialmagazine;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financialmagazine.adapters.FinancialRecordInfoAdapter;
import com.example.financialmagazine.adapters.SwipeToDeleteCallback;
import com.example.financialmagazine.db.CategoryDao;
import com.example.financialmagazine.db.FinancialRecordDao;
import com.example.financialmagazine.db.FinancialRecordDatabase;
import com.example.financialmagazine.models.Category;
import com.example.financialmagazine.models.FinancialRecord;
import com.example.financialmagazine.models.FinancialRecordInfo;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    CategoryDao categoryDao;
    FinancialRecordDao financialRecordDao;
    FinancialRecordInfoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FinancialRecordDatabase db = FinancialRecordDatabase.getDatabase(this);
        categoryDao = db.categoryDao();
        financialRecordDao = db.financialRecordDao();

        List<FinancialRecord> financialRecords = financialRecordDao.getAllRecords();
        List<FinancialRecordInfo> financialRecordsInfo = new ArrayList<>();

        for (int i = 0; i < financialRecords.size(); i++) {
            FinancialRecord f = financialRecords.get(i);
            Category c = categoryDao.getCategoryById(f.getCategoryId());
            financialRecordsInfo.add(new FinancialRecordInfo(f.getId(), c, f.getAmount(), f.getTimestamp()));
        }

        RecyclerView recyclerView = findViewById(R.id.recycler_main);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        adapter = new FinancialRecordInfoAdapter(this, financialRecordsInfo, financialRecordDao);
        ItemTouchHelper.Callback callback = new SwipeToDeleteCallback(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);

        // Инициализируем меню
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Финансы");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public void onResume(){
        super.onResume();
        if(financialRecordDao != null && adapter != null)
        {
            List<FinancialRecordInfo> financialRecordsInfo = new ArrayList<>();
            List<FinancialRecord> financialRecords = financialRecordDao.getAllRecords();
            for (int i = 0; i < financialRecords.size(); i++) {
                FinancialRecord f = financialRecords.get(i);
                Category c = categoryDao.getCategoryById(f.getCategoryId());
                financialRecordsInfo.add(new FinancialRecordInfo(f.getId(), c, f.getAmount(), f.getTimestamp()));
            }
            adapter.addRecords(financialRecordsInfo);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.item_add){
            Intent i = new Intent(this, AddActivity.class);
            startActivity(i);
            return true;
        }else if(id == R.id.item_category){
            Intent i = new Intent(this, AddCategoryActivity.class);
            startActivity(i);
            return true;
        }else if(id == R.id.item_filter){
            Intent i = new Intent(this, FilterActivity.class);
            startActivity(i);
            return true;
        } else if(id == R.id.item_ballance){
            Intent i = new Intent(this, BalanceActivity.class);
            startActivity(i);
            return true;
        }
        return false;
    }
}