package com.example.spendly;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String PREFS = "spendly_prefs";
    private static final String KEY_BUDGET = "budget";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DBHelper db = DBHelper.get(requireContext());
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        float budget = prefs.getFloat(KEY_BUDGET, 10000f);

        TextView tvGreeting = view.findViewById(R.id.tvGreeting);
        TextView tvMonth = view.findViewById(R.id.tvMonth);
        TextView tvSpent = view.findViewById(R.id.tvSpent);
        TextView tvBudgetInfo = view.findViewById(R.id.tvBudgetInfo);
        TextView tvToday = view.findViewById(R.id.tvToday);
        TextView tvTop = view.findViewById(R.id.tvTop);
        TextView tvSeeAll = view.findViewById(R.id.tvSeeAll);
        TextView tvEmpty = view.findViewById(R.id.tvEmpty);
        LinearProgressIndicator progress = view.findViewById(R.id.progress);
        RecyclerView rv = view.findViewById(R.id.rvRecent);

        // Numbers for this month
        List<Expense> month = db.getSince(Utils.monthStart());
        double spent = Utils.sum(month);
        long todayStart = Utils.dayStart(System.currentTimeMillis());
        double today = 0;
        double[] byCategory = new double[Categories.NAMES.length];
        for (Expense e : month) {
            if (e.date >= todayStart) {
                today += e.amount;
            }
            byCategory[Categories.index(e.category)] += e.amount;
        }
        int top = -1;
        double topValue = 0;
        for (int i = 0; i < byCategory.length; i++) {
            if (byCategory[i] > topValue) {
                topValue = byCategory[i];
                top = i;
            }
        }

        tvGreeting.setText(Utils.greeting() + " \uD83D\uDC4B");
        tvMonth.setText(Utils.monthLabel());
        tvSpent.setText(Utils.money(spent));
        tvToday.setText(Utils.money(today));
        tvTop.setText(top == -1 ? "-" : Categories.EMOJIS[top] + " " + Categories.NAMES[top]);

        // Budget bar
        int percent = budget > 0 ? (int) Math.min(100, Math.round(spent / budget * 100)) : 0;
        progress.setProgress(percent);
        if (spent > budget) {
            tvBudgetInfo.setText("Over budget by " + Utils.money(spent - budget));
            tvBudgetInfo.setTextColor(Color.parseColor("#EF4444"));
            progress.setIndicatorColor(Color.parseColor("#EF4444"));
        } else {
            tvBudgetInfo.setText(Utils.money(budget - spent) + " left of " + Utils.money(budget));
        }

        // Recent list (last 5)
        List<Expense> all = db.getAll();
        List<Expense> recent = new ArrayList<>(all.subList(0, Math.min(5, all.size())));
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(new ExpenseAdapter(recent));
        tvEmpty.setVisibility(recent.isEmpty() ? View.VISIBLE : View.GONE);
        rv.setVisibility(recent.isEmpty() ? View.GONE : View.VISIBLE);

        view.findViewById(R.id.btnBudget).setOnClickListener(v -> showBudgetDialog(prefs, budget));
        tvSeeAll.setOnClickListener(v -> ((MainActivity) requireActivity()).openHistory());
    }

    private void showBudgetDialog(SharedPreferences prefs, float current) {
        EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("Monthly budget");
        input.setText(String.valueOf((int) current));

        FrameLayout box = new FrameLayout(requireContext());
        int pad = Utils.dp(requireContext(), 24);
        box.setPadding(pad, Utils.dp(requireContext(), 8), pad, 0);
        box.addView(input);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Monthly budget")
                .setView(box)
                .setPositiveButton("Save", (dialog, which) -> {
                    try {
                        float value = Float.parseFloat(input.getText().toString().trim());
                        if (value > 0) {
                            prefs.edit().putFloat(KEY_BUDGET, value).apply();
                            ((MainActivity) requireActivity()).refresh();
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(requireContext(), "Enter a valid number",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
