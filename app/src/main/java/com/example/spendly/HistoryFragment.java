package com.example.spendly;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private final List<Expense> items = new ArrayList<>();
    private ExpenseAdapter adapter;
    private DBHelper db;
    private TextView tvCount, tvEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = DBHelper.get(requireContext());
        tvCount = view.findViewById(R.id.tvCount);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        RecyclerView rv = view.findViewById(R.id.rv);

        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ExpenseAdapter(items);
        rv.setAdapter(adapter);

        // Swipe left or right to delete, with an Undo button
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (position == RecyclerView.NO_POSITION) return;

                Expense removed = items.get(position);
                db.deleteExpense(removed.id);
                load();

                Snackbar.make(view, "Expense deleted", Snackbar.LENGTH_LONG)
                        .setAction("Undo", v -> {
                            db.insertExpense(removed);
                            load();
                        })
                        .show();
            }
        }).attachToRecyclerView(rv);

        load();
    }

    private void load() {
        items.clear();
        items.addAll(db.getAll());
        adapter.notifyDataSetChanged();

        tvCount.setText(items.isEmpty()
                ? "Swipe an expense to delete it"
                : items.size() + (items.size() == 1 ? " expense" : " expenses")
                  + "  \u2022  " + Utils.money(Utils.sum(items)) + " total");
        tvEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
    }
}