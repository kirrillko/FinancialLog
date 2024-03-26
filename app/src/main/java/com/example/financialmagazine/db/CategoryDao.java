package com.example.financialmagazine.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.financialmagazine.models.Category;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert
    void insert(Category category);

    @Query("SELECT * FROM categories")
    List<Category> getAllCategories();

    @Delete
    void delete(Category category);

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    Category getCategoryById(int categoryId);
}
