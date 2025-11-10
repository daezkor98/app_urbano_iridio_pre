package com.urbanoexpress.iridio3.pre.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.urbanoexpress.iridio3.pre.R;

public class AdderButton extends ConstraintLayout implements View.OnClickListener, TextWatcher,
        View.OnFocusChangeListener {

    private Context context;
    private EditText valueEditText;
    private Button minusButton;
    private Button plusButton;
    private int minValue = 0;
    private int maxValue = 9999;
    private int value;

    private OnValueChangeListener onValueChangeListener;

    public AdderButton(@NonNull Context context) {
        super(context);
    }

    public AdderButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
        initStyle(attrs, 0);
    }

    public AdderButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
        initStyle(attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        valueEditText.setText(String.valueOf(value));

        refreshBackgroundButtons();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.minus_button) {
            minus();
            refreshBackgroundButtons();
        } else if (v.getId() == R.id.plus_button) {
            plus();
            refreshBackgroundButtons();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        try {
            String sValue = valueEditText.getText().toString().trim();
            if (!sValue.isEmpty()) {
                value = Integer.parseInt(sValue);

                if (value < minValue) {
                    setValue(minValue);
                } else if (value > maxValue) {
                    setValue(maxValue);
                }
                notifyChangeValueListener();
            }
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            setValue(minValue);
            notifyChangeValueListener();
        }
        refreshBackgroundButtons();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            String sValue = valueEditText.getText().toString().trim();
            if (sValue.isEmpty()) setValue(minValue);
        }
    }

    public void setEnabled(boolean enabled) {
        valueEditText.setEnabled(enabled);
        minusButton.setEnabled(enabled);
        plusButton.setEnabled(enabled);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
        valueEditText.setText(String.valueOf(value));
    }

    public void setOnValueChangeListener(OnValueChangeListener onValueChangeListener) {
        this.onValueChangeListener = onValueChangeListener;
    }

    private void initView() {
        LayoutInflater.from(context).inflate(R.layout.adder_button_view, this, true);

        valueEditText = findViewById(R.id.value_edit_text);
        minusButton = findViewById(R.id.minus_button);
        plusButton = findViewById(R.id.plus_button);

        minusButton.setOnClickListener(this);
        plusButton.setOnClickListener(this);

        valueEditText.addTextChangedListener(this);
        valueEditText.setOnFocusChangeListener(this);
    }

    private void initStyle(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AdderButton);
        value = a.getInteger(R.styleable.AdderButton_value, minValue);
        minValue = a.getInteger(R.styleable.AdderButton_minValue, minValue);
        maxValue = a.getInteger(R.styleable.AdderButton_maxValue, maxValue);
        a.recycle();
    }

    private void minus() {
        if (value > minValue) {
            value--;
            setValue(value);
            notifyChangeValueListener();
        }
    }

    private void plus() {
        if (value < maxValue) {
            value++;
            setValue(value);
            notifyChangeValueListener();
        }
    }

    private void refreshBackgroundButtons() {
        /*if (value == minValue) {
            minusButton.setBackgroundColor(ContextCompat.getColor(context, R.color.gris_9));
        } else if (value > minValue && value < maxValue) {
            minusButton.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
            plusButton.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
        } else if (value == maxValue) {
            plusButton.setBackgroundColor(ContextCompat.getColor(context, R.color.gris_9));
        }*/
        minusButton.setBackgroundColor(ContextCompat.getColor(context,
                value == minValue ? R.color.gris_9 : R.color.colorPrimary));
        plusButton.setBackgroundColor(ContextCompat.getColor(context,
                value == maxValue ? R.color.gris_9 : R.color.colorPrimary));
    }

    private void notifyChangeValueListener() {
        if (onValueChangeListener != null) {
            onValueChangeListener.onValueChanged(value);
        }
    }

    public interface OnValueChangeListener {
        void onValueChanged(int value);
    }
}
