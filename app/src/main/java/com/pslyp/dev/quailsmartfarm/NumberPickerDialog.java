package com.pslyp.dev.quailsmartfarm;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.NumberPicker;

public class NumberPickerDialog extends DialogFragment {

    private NumberPicker.OnValueChangeListener valueChangeListener;
    private NumberDialogListener listener;
    private int id, min, max, value;

    public interface NumberDialogListener {
        void getValue(int id, int value);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMinValue(int min) {
        this.min = min;
    }

    public void setMaxValue(int max) {
        this.max = max;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final NumberPicker numberPicker = new NumberPicker(getActivity());

        numberPicker.setMinValue(min);
        numberPicker.setMaxValue(max);
        numberPicker.setValue(value);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                value = numberPicker.getValue();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Value");
        builder.setMessage("Choose a number :");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                valueChangeListener.onValueChange(numberPicker,
//                        numberPicker.getValue(), numberPicker.getValue());
                listener.getValue(id, value);

            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setView(numberPicker);
        return builder.create();
    }

    public NumberPicker.OnValueChangeListener getValueChangeListener() {
        return valueChangeListener;
    }

    public void setValueChangeListener(NumberPicker.OnValueChangeListener valueChangeListener) {
        this.valueChangeListener = valueChangeListener;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (NumberDialogListener) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context.toString());
        }
    }
}