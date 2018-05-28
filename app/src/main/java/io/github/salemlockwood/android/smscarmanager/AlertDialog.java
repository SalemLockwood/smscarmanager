package io.github.salemlockwood.android.smscarmanager;

import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by Melky on 05/12/2015.
 */
public class AlertDialog {
    private android.app.AlertDialog alertDialog;
    public AlertDialog(Context context, String title, String message){
        android.app.AlertDialog.Builder alertBuilder = new android.app.AlertDialog.Builder(context);
        alertBuilder.setTitle(title);
        alertBuilder.setMessage(message);
        alertBuilder.setCancelable(false);
        alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog = alertBuilder.create();
    }
    public void show(){
        alertDialog.show();
    }
}
