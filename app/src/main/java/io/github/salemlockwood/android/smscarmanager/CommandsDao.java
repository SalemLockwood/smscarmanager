package io.github.salemlockwood.android.smscarmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by melky on 14/10/2015.
 */
public class CommandsDao {
    private Context context;
    public CommandsDao(Context ctx){
        context = ctx;
    }
    public Commands getConfiguration(){
        Commands commands = null;
        Cursor cursor = null;

        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();

        cursor = db.rawQuery("SELECT * FROM COMMANDS",null);

        if(cursor != null && cursor.moveToLast()){
            commands = new Commands();
            commands.setLOC_CMD(cursor.getString(cursor.getColumnIndex("LOC_CMD")));
            commands.setCUT_OIL_CMD(cursor.getString(cursor.getColumnIndex("CUT_OIL_CMD")));
            commands.setSUP_OIL_CMD(cursor.getString(cursor.getColumnIndex("SUP_OIL_CMD")));
            commands.setCUT_ELEC_CMD(cursor.getString(cursor.getColumnIndex("CUT_ELEC_CMD")));
            commands.setSUP_ELEC_CMD(cursor.getString(cursor.getColumnIndex("SUP_ELEC_CMD")));
            commands.setTRK_CMD(cursor.getString(cursor.getColumnIndex("TRK_CMD")));
            commands.setLTN_CMD(cursor.getString(cursor.getColumnIndex("LTN_CMD")));
            commands.setSOS_KEY_ON_CMD(cursor.getString(cursor.getColumnIndex("SOS_KEY_ON_CMD")));
            commands.setSOS_KEY_OFF_CMD(cursor.getString(cursor.getColumnIndex("SOS_KEY_OFF_CMD")));
            cursor.moveToNext();
        }

        if(cursor!=null){
            cursor.close();
        }

        return commands;
    }

    public long insert(Commands c){
        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();

        ContentValues content = new ContentValues();

        content.put("LOC_CMD",c.getLOC_CMD());
        content.put("CUT_OIL_CMD",c.getCUT_OIL_CMD());
        content.put("SUP_OIL_CMD",c.getSUP_OIL_CMD());
        content.put("CUT_ELEC_CMD",c.getCUT_ELEC_CMD());
        content.put("SUP_ELEC_CMD",c.getSUP_ELEC_CMD());
        content.put("TRK_CMD",c.getTRK_CMD());
        content.put("LTN_CMD",c.getLTN_CMD());
        content.put("SOS_KEY_ON_CMD",c.getSOS_KEY_ON_CMD());
        content.put("SOS_KEY_OFF_CMD",c.getSOS_KEY_OFF_CMD());

        return db.insert("COMMANDS",null,content);
    }
}
