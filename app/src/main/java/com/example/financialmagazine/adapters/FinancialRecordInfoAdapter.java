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
import com.example.financialmagazine.db.FinancialRecordDao;
import com.example.financialmagazine.models.Category;
import com.example.financialmagazine.models.FinancialRecord;
import com.example.financialmagazine.models.FinancialRecordInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FinancialRecordInfoAdapter extends RecyclerView.Adapter<FinancialRecordInfoAdapter.FinancialRecordInfoViewHolder> implements  AdapterWithDelete {

    private Context mContext;
    private List<FinancialRecordInfo> mFinancialRecordList;
    private FinancialRecordDao mFinancialRecordDao;

    public FinancialRecordInfoAdapter(Context context, List<FinancialRecordInfo> financialRecordList, FinancialRecordDao financialRecordDao) {
        this.mContext = context;
        this.mFinancialRecordList = financialRecordList;
        this.mFinancialRecordDao = financialRecordDao;
    }

    @NonNull
    @Override
    public FinancialRecordInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_financial_record, parent, false);
        return new FinancialRecordInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FinancialRecordInfoViewHolder holder, int position) {
        FinancialRecordInfo financialRecord = mFinancialRecordList.get(position);
        holder.bind(financialRecord);
    }

    @Override
    public int getItemCount() {
        return mFinancialRecordList.size();
    }

    @Override
    public void removeRecord(int position) {
        FinancialRecordInfo f = mFinancialRecordList.get(position);
        FinancialRecord fd = new FinancialRecord(f.getCategory().getId(), f.getAmount(), f.getTimestamp());
        fd.setId(f.getId());
        mFinancialRecordDao.delete(fd);
        mFinancialRecordList.remove(position);
        notifyItemRemoved(position);
    }

    public void addRecords(List<FinancialRecordInfo> fs) {
        mFinancialRecordList.clear();
        mFinancialRecordList.addAll(fs);
        notifyDataSetChanged();
    }

    public class FinancialRecordInfoViewHolder extends RecyclerView.ViewHolder {

        private TextView txtCategory;
        private TextView txtType;
        private TextView txtAmount;
        private TextView txtTimestamp;

        public FinancialRecordInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCategory = itemView.findViewById(R.id.txtCategoryName);
            txtType = itemView.findViewById(R.id.txtCategoryType);
            txtAmount = itemView.findViewById(R.id.txtAmount);
            txtTimestamp = itemView.findViewById(R.id.txtTimestamp);
        }

        public void bind(FinancialRecordInfo financialRecord) {
            Category c = financialRecord.getCategory();
            txtCategory.setText(c.getCategory());
            txtType.setText(c.getType());
            txtAmount.setText(String.valueOf(financialRecord.getAmount()));
            Date date = new Date(financialRecord.getTimestamp());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            String formattedDate = dateFormat.format(date);
            txtTimestamp.setText(formattedDate);
        }
    }
}
