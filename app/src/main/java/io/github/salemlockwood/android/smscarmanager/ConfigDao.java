package io.github.salemlockwood.android.smscarmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Melky on 18/01/2016.
 */
public class ConfigDao {
    private Context context;
    public ConfigDao(Context ctx){
        context = ctx;
    }
    public Config getConfig(){
        Config cf = null;
        Cursor cs = null;

        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();

        cs = db.rawQuery("SELECT * FROM CONFIG",null);

        if(cs!=null && cs.moveToLast()){
            cf = new Config();
            cf.setTimerLoop(cs.getInt(cs.getColumnIndex("TIMERLOOP")));
            cf.setPhoneNumber(cs.getString(cs.getColumnIndex("PHONENUMBER")));
            cs.moveToNext();
        }
        if(cs!=null){
            cs.close();
        }
        return cf;

    }
    public long setConfig(Config c){
        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("TIMERLOOP",c.getTimerLoop());
        cv.put("PHONENUMBER",c.getPhoneNumber());

        return db.insert("CONFIG",null,cv);
    }
}
