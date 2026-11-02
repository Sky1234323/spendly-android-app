package com.example.spendly;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.TimeZone;

public class AddExpenseSheet extends BottomSheetDialogFragment {

    private long selectedDate = System.currentTimeMillis();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sheet_add_expense, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextInputEditText etAmount = view.findViewById(R.id.etAmount);
        TextInputEditText etTitle = view.findViewById(R.id.etTitle);
        ChipGroup chipGroup = view.findViewById(R.id.chipGroup);
        MaterialButton btnDate = view.findViewById(R.id.btnDate);
        MaterialButton btnSave = view.findViewById(R.id.btnSave);

        // One chip for every category
        LayoutInflater li = LayoutInflater.from(requireContext());
        for (int i = 0; i < Categories.NAMES.length; i++) {
            Chip chip = (Chip) li.inflate(R.layout.chip_category, chipGroup, false);
            chip.setId(View.generateViewId());
            chip.setTag(i);
            chip.setText(Categories.EMOJIS[i] + " " + Categories.NAMES[i]);
            chipGroup.addView(chip);
            if (i == 0) {
                chip.setChecked(true);
            }
        }

        // Date picker (only today or earlier)
        btnDate.setOnClickListener(v -> {
            CalendarConstraints constraints = new CalendarConstraints.Builder()
                    .setValidator(DateValidatorPointBackward.now())
                    .build();
            MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select date")
                    .setCalendarConstraints(constraints)
                    .build();
            picker.addOnPositiveButtonClickListener(selection -> {
                // The picker returns UTC midnight, so convert it to the same day in local time
                Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                utc.setTimeInMillis(selection);
                Calendar local = Calendar.getInstance();
                local.set(utc.get(Calendar.YEAR), utc.get(Calendar.MONTH),
                        utc.get(Calendar.DAY_OF_MONTH));
                selectedDate = local.getTimeInMillis();
                btnDate.setText(Utils.formatDate(selectedDate));
            });
            picker.show(getParentFragmentManager(), "date");
        });

        btnSave.setOnClickListener(v -> {
            String amountText = etAmount.getText() == null ? "" : etAmount.getText().toString().trim();
            double amount;
            try {
                amount = Double.parseDouble(amountText);
            } catch (NumberFormatException e) {
                amount = 0;
            }
            if (amount <= 0) {
                etAmount.setError("Enter an amount");
                return;
            }

            Chip checked = chipGroup.findViewById(chipGroup.getCheckedChipId());
            int index = checked == null ? Categories.NAMES.length - 1 : (Integer) checked.getTag();
            String category = Categories.NAMES[index];

            String title = etTitle.getText() == null ? "" : etTitle.getText().toString().trim();
            if (title.isEmpty()) {
                title = category;
            }

            DBHelper.get(requireContext())
                    .insertExpense(new Expense(0, title, amount, category, selectedDate));
            Toast.makeText(requireContext(), "Expense added", Toast.LENGTH_SHORT).show();
            ((MainActivity) requireActivity()).refresh();
            dismiss();
        });
    }
}