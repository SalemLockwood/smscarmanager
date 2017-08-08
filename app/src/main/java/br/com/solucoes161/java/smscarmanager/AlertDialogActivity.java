package br.com.solucoes161.java.smscarmanager;

import android.app.AlertDialog;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by melky on 24/01/2016.
 */
public class AlertDialogActivity extends Activity
{    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        String title = b.getString("title");
        String message = b.getString("message");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                        AlertDialogActivity.this.onBackPressed();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
