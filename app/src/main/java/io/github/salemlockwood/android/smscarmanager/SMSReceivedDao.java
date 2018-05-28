package io.github.salemlockwood.android.smscarmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Melky on 21/01/2016.
 */
public class SMSReceivedDao {
    Context ctx;
    public SMSReceivedDao(Context context){ ctx = context; };
    public long insert(SMSReceived sms){
        SQLiteDatabase db = new DatabaseHelper(ctx).getWritableDatabase();

        ContentValues content = new ContentValues();
        content.put("NUMBER",sms.getNumber());
        content.put("MESSAGE", sms.getMessage());
        return db.insert("SMSRECEIVED",null,content);
    }
    public SMSReceived getLastFromNumber(String number){
        SMSReceived sms = null;
        SQLiteDatabase db = new DatabaseHelper(ctx).getWritableDatabase();
        Cursor cursor = null;

        String where = "NUMBER = ?";

        String[] colunas = new String[] {"NUMBER, MESSAGE"};

        String[] argumentos = new String[] {number};

        cursor = db.query("SMSRECEIVED", colunas, where, argumentos, null, null, null);

        if(cursor != null && cursor.moveToLast()){
            sms = new SMSReceived();
            sms.setNumber(cursor.getString(cursor.getColumnIndex("NUMBER")));
            sms.setMessage(cursor.getString(cursor.getColumnIndex("MESSAGE")));
        }
        if(cursor != null){
            cursor.close();
        }
        db.close();
        return sms;
    }
}
