package io.github.salemlockwood.android.smscarmanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

/**
 * Created by melky on 13/10/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper{
    private static final int DBVERSION = 1;
    private static final String DBNAME = "dbscm";
    DatabaseHelper(Context ctx){
        super(ctx, DBNAME,null,DBVERSION);
    }
    private static final String COMMANDS_TABLE_SQL = "CREATE TABLE COMMANDS (LOC_CMD TEXT, CUT_OIL_CMD TEXT, SUP_OIL_CMD, CUT_ELEC_CMD TEXT, SUP_ELEC_CMD, TRK_CMD TEXT, LTN_CMD TEXT, SOS_KEY_ON_CMD TEXT, SOS_KEY_OFF_CMD TEXT)";
    private static final String PHONES_TABLE_SQL = "CREATE TABLE PHONES (PHONE TEXT, PASSWORD TEXT, INITIALIZED INTEGER, TIMEZONE INTEGER, THISADMIN INTEGER, THISPASSWORD INTEGER, CUTTEDOIL INTEGER, CUTTEDELEC INTEGER, MODE INTEGER, LASTLOCATION TEXT, SOSKEY INT)";
    private static final String CONFIG_TABLE_SQL = "CREATE TABLE CONFIG (TIMERLOOP INTEGER, PHONENUMBER TEXT)";
    private static final String SMSRECEIVED_TABLE_SQL = "CREATE TABLE SMSRECEIVED(NUMBER TEXT, MESSAGE TEXT)";
    private static final String TRACKING_TRABLE_SQL = "CREATE TABLE TRACKING(ID INTEGER PRIMARY KEY AUTOINCREMENT, PHONE TEXT, NAME TEXT, SNIPPETTEXTS TEXT, LATITUDES TEXT, LONGITUDES TEXT)";

    public void onCreate(SQLiteDatabase db){
        db.execSQL(CONFIG_TABLE_SQL);
        db.execSQL(PHONES_TABLE_SQL);
        db.execSQL(COMMANDS_TABLE_SQL);
        db.execSQL(SMSRECEIVED_TABLE_SQL);;
        db.execSQL(TRACKING_TRABLE_SQL);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //
    }
}
