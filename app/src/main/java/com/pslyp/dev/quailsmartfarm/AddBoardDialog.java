package com.pslyp.dev.quailsmartfarm;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import static android.app.Activity.RESULT_OK;

public class AddBoardDialog extends AppCompatDialogFragment {

    private Button buttonScanQrCode;
    private TextInputLayout inputLayoutBoardName;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_add_board, null);

        buttonScanQrCode = view.findViewById(R.id.button_scan_qr_code);
        inputLayoutBoardName = view.findViewById(R.id.text_input_board_name);

        builder.setView(view)
                .setTitle("Add Board")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(), "OK", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.putExtra("Name", inputLayoutBoardName.getEditText().getText());
                    }
                });

        buttonScanQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ScanActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        return builder.create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 0) {
            if(resultCode == RESULT_OK) {
                String barcode = data.getStringExtra("SCAN_RESULT");
                Toast.makeText(getContext(), barcode, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
