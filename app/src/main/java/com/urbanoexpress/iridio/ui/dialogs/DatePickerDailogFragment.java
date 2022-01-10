package com.urbanoexpress.iridio.ui.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.urbanoexpress.iridio.R;

public class DatePickerDailogFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    public static final String TAG = "DatePickerDailogFragment";

    private int year = 0, month = 0, dayOfMonth = 0;

    public static DatePickerDailogFragment newInstance(int year, int month, int dayOfMonth) {
        DatePickerDailogFragment fragment = new DatePickerDailogFragment();
        Bundle args = new Bundle();
        args.putInt("year", year);
        args.putInt("month", month);
        args.putInt("dayOfMonth", dayOfMonth);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            year = getArguments().getInt("year");
            month = getArguments().getInt("month");
            dayOfMonth = getArguments().getInt("dayOfMonth");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new DatePickerDialog(getActivity(),
                R.style.date_picker_theme, this, year, month - 1, dayOfMonth);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        if (getActivity() != null) {
            if (getActivity() instanceof OnDatePickerDailogFragmentListener) {
                ((OnDatePickerDailogFragmentListener) getActivity()).onDateSet(
                        view, year, month + 1, dayOfMonth);
            }
        }
    }

    public interface OnDatePickerDailogFragmentListener {
        void onDateSet(DatePicker view, int year, int month, int dayOfMonth);
    }
}
