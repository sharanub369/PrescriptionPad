package com.prescriptionpad.app.android.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.prescriptionpad.app.android.R;


/**
 * Created by sharana.b on 12/10/2016.
 */
public class DialogHandler implements DialogInterface.OnCancelListener {
    private Dialog dialog;
    private static DialogHandler dialogHandlerInstance;
    private Context context;

    public static DialogHandler getDialogHandlerInstance(Context context) {
        if (dialogHandlerInstance == null) {
            dialogHandlerInstance = new DialogHandler();
        }
        return dialogHandlerInstance;
    }

    public DialogHandler(Context context) {
        this.context = context;
    }

    public DialogHandler() {
    }

    public Dialog displayDialog(String message) {
        dialog = new Dialog(context, android.R.style.Theme_Panel);
        View view = LayoutInflater.from(context).inflate(R.layout.custom_loading_dialog_view, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnCancelListener(this);
        dialog.show();

        return dialog;
    }


    public void dismissDialog() {
        dialog.dismiss();
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        dialog.dismiss();
    }
}
