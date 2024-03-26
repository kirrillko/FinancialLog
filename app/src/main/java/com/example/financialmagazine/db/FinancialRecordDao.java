package com.example.financialmagazine.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.financialmagazine.models.FinancialRecord;

import java.util.List;

@Dao
public interface FinancialRecordDao {
    @Insert
    void insert(FinancialRecord record);

    @Query("SELECT * FROM financial_records")
    List<FinancialRecord> getAllRecords();

    @Delete
    void delete(FinancialRecord financialRecord);

    // Получаем записи за последний день
    @Query("SELECT * FROM financial_records WHERE timestamp >= :startTime")
    List<FinancialRecord> getRecordsForLastDay(long startTime);

    // Получаем записи за последнюю неделю
    @Query("SELECT * FROM financial_records WHERE timestamp >= :startTime")
    List<FinancialRecord> getRecordsForLastWeek(long startTime);

    // Получаем записи за последний месяц
    @Query("SELECT * FROM financial_records WHERE timestamp >= :startTime")
    List<FinancialRecord> getRecordsForLastMonth(long startTime);

    // Получаем записи за указанный период
    @Query("SELECT * FROM financial_records WHERE timestamp BETWEEN :startTime AND :endTime")
    List<FinancialRecord> getRecordsForPeriod(long startTime, long endTime);
}