package com.urbanoexpress.iridio.ui.dialogs;

import static com.urbanoexpress.iridio.ui.dialogs.DATE_PICKER_MODE.CALENDAR;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.urbanoexpress.iridio.R;

import java.util.Calendar;

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

    /**
     * Returns a DatePicker setted on today
     */
    public static DatePickerDailogFragment newInstance(DATE_PICKER_MODE mode) {
        DatePickerDailogFragment fragment = new DatePickerDailogFragment();
        fragment.mode = mode;
        final Calendar c = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", c.get(Calendar.YEAR));
        args.putInt("month", c.get(Calendar.MONTH));
        args.putInt("dayOfMonth", c.get(Calendar.DAY_OF_MONTH));
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

    private DATE_PICKER_MODE mode = CALENDAR;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        int styleMode = 0;
        switch (mode) {
            case CALENDAR: {
                styleMode = R.style.date_picker_theme;
            }
            break;
            case SPINNER: {
                styleMode = R.style.MySpinnerDatePickerStyle;
            }
            break;
            default:
        }

        DatePickerDialog mDiag = new DatePickerDialog(getActivity(), styleMode, this, year, month - 1, dayOfMonth);

        mDiag.setOnShowListener(arg0 -> {
            mDiag.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.red_1));
            mDiag.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
        });
        return mDiag;
    }

    public OnDatePickerDailogFragmentListener dateListener;

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        if (getActivity() != null) {
            if (getActivity() instanceof OnDatePickerDailogFragmentListener) {
                ((OnDatePickerDailogFragmentListener) getActivity()).onDateSet(
                        view, year, month + 1, dayOfMonth);
            } else {
                dateListener.onDateSet(view, year, month + 1, dayOfMonth);
            }
        }
    }

    public interface OnDatePickerDailogFragmentListener {
        void onDateSet(DatePicker view, int year, int month, int dayOfMonth);
    }
}

