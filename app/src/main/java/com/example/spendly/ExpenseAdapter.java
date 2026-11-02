package com.example.spendly;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.VH> {

    private final List<Expense> items;

    public ExpenseAdapter(List<Expense> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Expense e = items.get(position);
        h.tvEmoji.setText(Categories.emoji(e.category));
        h.tvEmoji.setBackground(Categories.circle(e.category));
        h.tvTitle.setText(e.title);
        h.tvMeta.setText(e.category + "  \u2022  " + Utils.friendlyDate(e.date));
        h.tvAmount.setText("-" + Utils.money(e.amount));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvEmoji, tvTitle, tvMeta, tvAmount;

        VH(@NonNull View itemView) {
            super(itemView);
            tvEmoji = itemView.findViewById(R.id.tvEmoji);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvMeta = itemView.findViewById(R.id.tvMeta);
            tvAmount = itemView.findViewById(R.id.tvAmount);
        }
    }
}