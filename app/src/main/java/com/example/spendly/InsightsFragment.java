package com.example.spendly;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.Arrays;
import java.util.List;

public class InsightsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_insights, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DBHelper db = DBHelper.get(requireContext());
        TextView tvSubtitle = view.findViewById(R.id.tvSubtitle);
        LinearLayout chartRow = view.findViewById(R.id.chartRow);
        LinearLayout catList = view.findViewById(R.id.catList);
        TextView tvNoData = view.findViewById(R.id.tvNoData);

        List<Expense> month = db.getSince(Utils.monthStart());
        double total = Utils.sum(month);
        tvSubtitle.setText(total == 0
                ? "No spending yet this month"
                : "You spent " + Utils.money(total) + " in " + Utils.monthLabel());

        buildChart(chartRow, db);
        buildCategories(catList, tvNoData, month, total);
    }

    // Bar chart for the last 7 days, built from plain views
    private void buildChart(LinearLayout row, DBHelper db) {
        Context ctx = requireContext();
        long start = Utils.daysAgoStart(6);

        double[] totals = new double[7];
        for (Expense e : db.getSince(start)) {
            int index = (int) Math.round((Utils.dayStart(e.date) - start) / (double) Utils.DAY);
            if (index >= 0 && index < 7) {
                totals[index] += e.amount;
            }
        }

        double max = 0;
        for (double t : totals) {
            max = Math.max(max, t);
        }
        int maxBar = Utils.dp(ctx, 110);

        for (int i = 0; i < 7; i++) {
            long dayTime = start + i * Utils.DAY + Utils.DAY / 2;

            LinearLayout column = new LinearLayout(ctx);
            column.setOrientation(LinearLayout.VERTICAL);
            column.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
            column.setLayoutParams(new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.MATCH_PARENT, 1f));

            TextView value = new TextView(ctx);
            value.setText(totals[i] > 0 ? Utils.compact(totals[i]) : "");
            value.setTextSize(11);
            value.setTextColor(Color.parseColor("#64748B"));
            value.setGravity(Gravity.CENTER);

            int barHeight = totals[i] > 0
                    ? Math.max(Utils.dp(ctx, 8), (int) (totals[i] / max * maxBar))
                    : Utils.dp(ctx, 6);
            View bar = new View(ctx);
            LinearLayout.LayoutParams barParams =
                    new LinearLayout.LayoutParams(Utils.dp(ctx, 22), barHeight);
            barParams.topMargin = Utils.dp(ctx, 4);
            barParams.bottomMargin = Utils.dp(ctx, 6);
            bar.setLayoutParams(barParams);

            GradientDrawable shape = new GradientDrawable();
            float r = Utils.dp(ctx, 8);
            shape.setCornerRadii(new float[]{r, r, r, r, 0, 0, 0, 0});
            shape.setColor(Color.parseColor(totals[i] > 0 ? "#0F766E" : "#E2E8F0"));
            bar.setBackground(shape);

            TextView label = new TextView(ctx);
            label.setText(Utils.dayLabel(dayTime));
            label.setTextSize(11);
            label.setTextColor(Color.parseColor("#64748B"));
            label.setGravity(Gravity.CENTER);

            column.addView(value);
            column.addView(bar);
            column.addView(label);
            row.addView(column);
        }
    }

    // One row per category, biggest first
    private void buildCategories(LinearLayout list, TextView tvNoData,
                                 List<Expense> month, double total) {
        double[] byCategory = new double[Categories.NAMES.length];
        for (Expense e : month) {
            byCategory[Categories.index(e.category)] += e.amount;
        }

        Integer[] order = new Integer[byCategory.length];
        for (int i = 0; i < order.length; i++) {
            order[i] = i;
        }
        Arrays.sort(order, (a, b) -> Double.compare(byCategory[b], byCategory[a]));

        LayoutInflater inflater = LayoutInflater.from(requireContext());
        boolean any = false;

        for (int idx : order) {
            if (byCategory[idx] <= 0) continue;
            any = true;

            View item = inflater.inflate(R.layout.item_category_stat, list, false);
            int percent = (int) Math.round(byCategory[idx] / total * 100);

            TextView tvEmoji = item.findViewById(R.id.tvEmoji);
            tvEmoji.setText(Categories.EMOJIS[idx]);
            tvEmoji.setBackground(Categories.circle(Categories.NAMES[idx]));

            ((TextView) item.findViewById(R.id.tvName)).setText(Categories.NAMES[idx]);
            ((TextView) item.findViewById(R.id.tvPercent)).setText(percent + "% of spending");
            ((TextView) item.findViewById(R.id.tvAmount)).setText(Utils.money(byCategory[idx]));

            LinearProgressIndicator bar = item.findViewById(R.id.bar);
            bar.setIndicatorColor(Categories.COLORS[idx]);
            bar.setProgress(percent);

            list.addView(item);
        }

        tvNoData.setVisibility(any ? View.GONE : View.VISIBLE);
    }
}