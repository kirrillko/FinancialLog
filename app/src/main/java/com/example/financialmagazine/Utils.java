package com.example.financialmagazine;

import com.example.financialmagazine.models.Category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {
    public static Map<String, List<Category>> categorizeCategories(List<Category> categories) {
        Map<String, List<Category>> categorizedMap = new HashMap<>();

        for (Category category : categories) {
            if (!categorizedMap.containsKey(category.getType())) {
                categorizedMap.put(category.getType(), new ArrayList<>());
            }
            categorizedMap.get(category.getType()).add(category);
        }
        return categorizedMap;
    }

    public static List<String> getCategoryNames(List<Category> categories) {
        List<String> categoryNames = new ArrayList<>();
        for (Category category : categories) {
            categoryNames.add(category.getCategory());
        }
        return categoryNames;
    }
}
