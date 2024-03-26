package com.example.financialmagazine.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financialmagazine.R;
import com.example.financialmagazine.db.CategoryDao;
import com.example.financialmagazine.models.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> implements  AdapterWithDelete{

    private Context mContext;
    private List<Category> mCategoryList;
    private CategoryDao mCategoryDao;

    public CategoryAdapter(Context context, List<Category> categoryList, CategoryDao categoryDao) {
        this.mContext = context;
        this.mCategoryList = categoryList;
        this.mCategoryDao = categoryDao;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = mCategoryList.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return mCategoryList.size();
    }

    public void addCategory(Category category) {
        mCategoryList.add(category);
        notifyDataSetChanged();
    }

    public void removeRecord(int position) {
        mCategoryDao.delete(mCategoryList.get(position));
        mCategoryList.remove(position);
        notifyItemRemoved(position);
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        private TextView txtType, txtCategory;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            txtType = itemView.findViewById(R.id.txtType);
            txtCategory = itemView.findViewById(R.id.txtCategory);
        }

        public void bind(Category category) {
            txtType.setText(category.getType());
            txtCategory.setText(category.getCategory());
        }
    }
}
