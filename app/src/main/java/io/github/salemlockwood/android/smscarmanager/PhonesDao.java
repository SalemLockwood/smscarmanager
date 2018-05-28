package io.github.salemlockwood.android.smscarmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by melky on 13/10/2015.
 */
public class PhonesDao {
    private Context context;
    public PhonesDao(Context ctx){
        context = ctx;
    }
    public Phones select(String phone){
        Phones phones = null;
        Cursor cursor = null;

        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();

        String where = "PHONE = ?";

        String[] colunas = new String[] {"PHONE","PASSWORD", "INITIALIZED", "TIMEZONE", "THISADMIN", "THISPASSWORD", "CUTTEDOIL", "CUTTEDELEC", "MODE", "LASTLOCATION", "SOSKEY"};

        String[] argumentos = new String[] {phone};

        cursor = db.query("PHONES", colunas, where, argumentos, null, null, null);

        if(cursor != null && cursor.moveToFirst()){
            phones = new Phones();
            phones.setPhone(cursor.getString(cursor.getColumnIndex("PHONE")));
            phones.setPassword(cursor.getString(cursor.getColumnIndex("PASSWORD")));
            phones.setInitialized(cursor.getInt(cursor.getColumnIndex("INITIALIZED")));
            phones.setTimezone(cursor.getInt(cursor.getColumnIndex("TIMEZONE")));
            phones.setThisAdmin(cursor.getInt(cursor.getColumnIndex("THISADMIN")));
            phones.setThisPassword(cursor.getInt(cursor.getColumnIndex("THISPASSWORD")));
            phones.setCuttedOil(cursor.getInt(cursor.getColumnIndex("CUTTEDOIL")));
            phones.setCuttedElec(cursor.getInt(cursor.getColumnIndex("CUTTEDELEC")));
            phones.setMode(cursor.getInt(cursor.getColumnIndex("MODE")));
            phones.setLastLocation(cursor.getString(cursor.getColumnIndex("LASTLOCATION")));
            phones.setSosKey(cursor.getInt(cursor.getColumnIndex("SOSKEY")));
        }

        if(cursor != null){
            cursor.close();
        }

        return phones;
    }

    public List<Phones> selectAll(){
        List<Phones> phones = new ArrayList<Phones>();
        Cursor cursor = null;

        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();

        cursor = db.rawQuery("SELECT * FROM PHONES",null);

        if(cursor != null && cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                Phones p = new Phones();
                p.setPhone(cursor.getString(cursor.getColumnIndex("PHONE")));
                p.setPassword(cursor.getString(cursor.getColumnIndex("PASSWORD")));
                p.setInitialized(cursor.getInt(cursor.getColumnIndex("INITIALIZED")));
                p.setTimezone(cursor.getInt(cursor.getColumnIndex("TIMEZONE")));
                p.setThisAdmin(cursor.getInt(cursor.getColumnIndex("THISADMIN")));
                p.setThisPassword(cursor.getInt(cursor.getColumnIndex("THISPASSWORD")));
                p.setCuttedOil(cursor.getInt(cursor.getColumnIndex("CUTTEDOIL")));
                p.setCuttedElec(cursor.getInt(cursor.getColumnIndex("CUTTEDELEC")));
                p.setMode(cursor.getInt(cursor.getColumnIndex("MODE")));
                p.setLastLocation(cursor.getString(cursor.getColumnIndex("LASTLOCATION")));
                p.setSosKey(cursor.getInt(cursor.getColumnIndex("SOSKEY")));
                phones.add(p);
                cursor.moveToNext();
            }
        }

        if(cursor!=null){
            cursor.close();
        }

        return phones;
    }

    public long insert(Phones phones){
        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();

        ContentValues content = new ContentValues();

        content.put("PHONE",phones.getPhone());
        content.put("PASSWORD",phones.getPassword());
        content.put("INITIALIZED",phones.getInitialized());
        content.put("THISADMIN",phones.getThisAdmin());
        content.put("TIMEZONE",phones.getTimezone());
        content.put("THISPASSWORD",phones.getThisPassword());
        content.put("CUTTEDOIL",phones.getCuttedOil());
        content.put("CUTTEDELEC",phones.getCuttedElec());
        content.put("MODE",phones.getMode());
        content.put("LASTLOCATION",phones.getLastLocation());
        content.put("SOSKEY",phones.getSosKey());

        long value = db.insert("PHONES",null,content);
        if(db!=null) db.close();
        return value;
    }
    public int update(Phones phones){
        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();

        ContentValues content = new ContentValues();

        content.put("PHONE",phones.getPhone());
        content.put("PASSWORD",phones.getPassword());
        content.put("INITIALIZED",phones.getInitialized());
        content.put("THISADMIN",phones.getThisAdmin());
        content.put("TIMEZONE",phones.getTimezone());
        content.put("THISPASSWORD",phones.getThisPassword());
        content.put("CUTTEDOIL",phones.getCuttedOil());
        content.put("CUTTEDELEC",phones.getCuttedElec());
        content.put("MODE",phones.getMode());
        content.put("LASTLOCATION",phones.getLastLocation());
        content.put("SOSKEY",phones.getSosKey());

        String where = "PHONE = ?";

        String argumentos[] = {phones.getPhone()};

        int value = db.update("PHONES", content, where, argumentos);
        if(db!=null) db.close();
        return value;
    }
    public int delete(String phone){
        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();

        String where = "PHONE = ?";

        String[] args = new String[] {phone};

        int value = db.delete("PHONES",where, args);
        if(db!=null) db.close();
        return value;
    }
}
