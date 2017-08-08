package br.com.solucoes161.java.smscarmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Melky on 14/10/2015.
 */
public class SmsReceiver extends BroadcastReceiver {
    private String TAG = SmsReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the data (SMS data) bound to intent
        Bundle bundle = intent.getExtras();

        SmsMessage[] msgs = null;

        String str = "";

        if (bundle != null) {
            // Retrieve the SMS Messages received
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];

            // For every SMS message received
            for (int i=0; i < msgs.length; i++) {
                // Convert Object array
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                // Sender's phone number
                str += "SMS from " + msgs[i].getOriginatingAddress() + " : ";
                // Fetch the text message
                str += msgs[i].getMessageBody().toString();
                // Newline <img src="http://codetheory.in/wp-includes/images/smilies/simple-smile.png" alt=":-)" class="wp-smiley" style="height: 1em; max-height: 1em;">
                str += "\n";
                List<Phones> phones = new PhonesDao(context).selectAll();
                Boolean listPhone = false;
                for(Phones phone : phones){
                    if(msgs[i].getOriginatingAddress().contains(phone.getPhone().substring(2))){
                        Log.d(TAG,"Aceitavel "+msgs[i].getOriginatingAddress());
                        listPhone = true;
                    }
                }
                if(listPhone) {
                    Phones sphone = new Phones();
                    for(Phones p : phones){
                        if(p.getPhone().contains(msgs[i].getOriginatingAddress().toString().substring(4))){
                            sphone = p;
                        }
                    }
                    if(!sphone.getPhone().isEmpty()) {
                        if (msgs[i].getMessageBody().contains("http://maps.google.com/maps") && !MapsActivity.runing && !msgs[i].getMessageBody().contains("0.000000")) {
                            sphone.setLastLocation(msgs[i].getMessageBody());
                            Intent map = new Intent(context,MapsActivity.class);
                            map.putExtra("url",msgs[i].getMessageBody());
                            map.putExtra("phone",sphone.getPhone());
                            map.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(map);
                        }
                        else if(msgs[i].getMessageBody().contains("begin ok")) sphone.setInitialized(true);
                        else if(msgs[i].getMessageBody().contains("change password ok")) sphone.setThisPassword(true);
                        else if(msgs[i].getMessageBody().contains("admin ok")) sphone.setThisAdmin(true);
                        else if(msgs[i].getMessageBody().contains("set time zone OK")) sphone.setTimezone(3);
                        else if(msgs[i].getMessageBody().contains("stop oil ok")) sphone.setCuttedOil(true);
                        else if(msgs[i].getMessageBody().contains("supply oil ok")) sphone.setCuttedOil(false);
                        else if(msgs[i].getMessageBody().contains("stop electricity ok")) sphone.setCuttedElec(true);
                        else if(msgs[i].getMessageBody().contains("supply electricity ok")) sphone.setCuttedElec(false);
                        else if(msgs[i].getMessageBody().contains("tracker ok")) sphone.setMode(0);
                        else if(msgs[i].getMessageBody().contains("monitor ok")) sphone.setMode(1);
                        else if(msgs[i].getMessageBody().contains("ACC ON OK")) sphone.setSosKey(true);
                        else if(msgs[i].getMessageBody().contains("ACC OFF OK")) sphone.setSosKey(false);
                        else if(msgs[i].getMessageBody().contains("ACC!!")) {
                            Intent in = new Intent(context,AlertDialogActivity.class);
                            in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            in.putExtra("title","Alerta de Chave Acionado!");
                            in.putExtra("message","Atenção! A chave do veículo com dispositivo "+sphone.getPhone()+" foi acionada.");
                            context.startActivity(in);
                        }
                        new PhonesDao(context).update(sphone);
                    }
                }
            }

            // Display the entire SMS Message
            Log.d(TAG, str);
        }
    }
}
