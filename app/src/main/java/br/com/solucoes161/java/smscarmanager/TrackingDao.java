package br.com.solucoes161.java.smscarmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Melky on 04/02/2016.
 */
public class TrackingDao {
    private Context context;
    public TrackingDao(Context context){
        this.context = context;
    }
    public Tracking select(int id){
        Tracking trk = null;
        Cursor cursor = null;

        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();

        String where = "ID = ?";
        String[] colunas = new String[] {"ID","PHONE","NAME","SNIPPETTEXTS","LATITUDES","LONGITUDES"};
        String[] argumentos = new String[] {String.valueOf(id)};

        cursor = db.query("TRACKING",colunas,where,argumentos,null,null,null);
        if(cursor != null && cursor.moveToFirst()){
            trk = new Tracking();
            trk.setId(cursor.getInt(cursor.getColumnIndex("ID")));
            trk.setPhone(cursor.getString(cursor.getColumnIndex("PHONE")));
            trk.setName(cursor.getString(cursor.getColumnIndex("NAME")));
            trk.setSnippetTexts(cursor.getString(cursor.getColumnIndex("SNIPPETTEXTS")));
            trk.setLatitudes(cursor.getString(cursor.getColumnIndex("LATITUDES")));
            trk.setLongitudes(cursor.getString(cursor.getColumnIndex("LONGITUDES")));
        }
        if(cursor != null) cursor.close();
        if(db != null) db.close();

        return trk;
    }
    public List<Tracking> selectAll(){
        List<Tracking> list = new ArrayList<Tracking>();
        Cursor cursor = null;

        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();

        cursor = db.rawQuery("SELECT * FROM TRACKING", null);
        if(cursor != null && cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                Tracking trk = new Tracking();
                trk.setId(cursor.getInt(cursor.getColumnIndex("ID")));
                trk.setPhone(cursor.getString(cursor.getColumnIndex("PHONE")));
                trk.setName(cursor.getString(cursor.getColumnIndex("NAME")));
                trk.setSnippetTexts(cursor.getString(cursor.getColumnIndex("SNIPPETTEXTS")));
                trk.setLatitudes(cursor.getString(cursor.getColumnIndex("LATITUDES")));
                trk.setLongitudes(cursor.getString(cursor.getColumnIndex("LONGITUDES")));
                list.add(trk);
                cursor.moveToNext();
            }
        }
        if(cursor!=null) cursor.close();
        if(db!=null) db.close();
        return list;
    }
    public long insert(Tracking trk){
        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put("PHONE",trk.getPhone());
        content.put("NAME",trk.getName());
        content.put("SNIPPETTEXTS",trk.getSnippetTexts());
        content.put("LATITUDES",trk.getLatitudes());
        content.put("LONGITUDES",trk.getLongitudes());
        long value = db.insert("TRACKING",null,content);
        if(db!=null) db.close();
        return value;
    }
    public int update(Tracking trk){
        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put("ID",trk.getId());
        content.put("PHONE",trk.getPhone());
        content.put("NAME",trk.getName());
        content.put("SNIPPETTEXTS",trk.getSnippetTexts());
        content.put("LATITUDES",trk.getLatitudes());
        content.put("LONGITUDES",trk.getLongitudes());
        String where = "ID = ?";
        String[] argumentos = new String[]{String.valueOf(trk.getId())};
        int values = db.update("TRACKING", content, where, argumentos);
        if(db != null) db.close();
        return values;
    }
    public int delete(int id){
        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        String where = "ID = ?";
        String[] argumentos = new String[]{String.valueOf(id)};
        int values = db.delete("TRACKING",where,argumentos);
        if(db!=null) db.close();
        return values;
    }
}
