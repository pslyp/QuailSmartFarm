package com.pslyp.dev.quailsmartfarm;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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

public class AddBoardDialog extends AppCompatDialogFragment implements DialogListener {

    private final int SCAN_QR_CODE = 2000;

    private DialogListener dialogListener;

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

                        String name = inputLayoutBoardName.getEditText().getText().toString();
                        dialogListener.applyTexts("AFAFFA", "adaf");
                    }
                });

        buttonScanQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent scanQRCodeIntent = new Intent(getContext(), ScanActivity.class);
                startActivityForResult(scanQRCodeIntent, SCAN_QR_CODE);
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            dialogListener = (DialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString());
        }
    }

    @Override
    public void applyTexts(String token, String name) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SCAN_QR_CODE) {
            if(resultCode == RESULT_OK) {
                String barcode = data.getStringExtra("SCAN_RESULT");
                Toast.makeText(getContext(), barcode, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
