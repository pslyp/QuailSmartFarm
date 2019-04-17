package com.pslyp.dev.quailsmartfarm;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConfigFragment extends Fragment implements TempDialog.TempDialogListener {

    private Button setTimeBt;
    private TextView textView;

    public ConfigFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_config, container, false);

        textView = view.findViewById(R.id.text_view_temp);
        final int number = Integer.valueOf(textView.getText().toString());

        setTimeBt = view.findViewById(R.id.button_set_time);
        setTimeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TempDialog tempDialog = new TempDialog();
                tempDialog.setNumber(number);
                tempDialog.show(getActivity().getSupportFragmentManager(), "temp dialog");
            }
        });

        return view;
    }

    @Override
    public void appTexts(int number) {
        Toast.makeText(getContext(), number, Toast.LENGTH_SHORT).show();
    }
}
