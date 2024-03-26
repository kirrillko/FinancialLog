package com.example.financialmagazine.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.financialmagazine.models.Category;
import com.example.financialmagazine.models.FinancialRecord;

@Database(entities = {Category.class, FinancialRecord.class}, version = 1)
public abstract class FinancialRecordDatabase extends RoomDatabase {
    public abstract CategoryDao categoryDao();
    public abstract FinancialRecordDao financialRecordDao();

    private static volatile FinancialRecordDatabase INSTANCE;

    public static FinancialRecordDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (FinancialRecordDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    FinancialRecordDatabase.class, "financial_record_db")
                            .addCallback(roomCallback)
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new Thread(() -> {
                CategoryDao categoryDao = INSTANCE.categoryDao();
                categoryDao.insert(new Category("Расход", "Квартплата"));
                categoryDao.insert(new Category("Доход", "Работа"));
            }).start();
        }
    };
}