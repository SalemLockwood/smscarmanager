package br.com.solucoes161.java.smscarmanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

/**
 * Created by melky on 13/10/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper{
    private static final int DBVERSION = 10;
    private static final String DBNAME = "dbscm";
    DatabaseHelper(Context ctx){
        super(ctx, DBNAME,null,DBVERSION);
    }
    private static final String COMMANDS_TABLE_SQL = "CREATE TABLE COMMANDS (LOC_CMD TEXT, CUT_OIL_CMD TEXT, SUP_OIL_CMD, CUT_ELEC_CMD TEXT, SUP_ELEC_CMD, TRK_CMD TEXT, LTN_CMD TEXT, SOS_KEY_CMD TEXT, SOS_KEY_OFF_CMD TEXT)";
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
        switch (oldVersion){
            case 1:
                db.execSQL("ALTER TABLE CONFIG ADD COLUMN SUP_OIL_CMD TEXT");
                db.execSQL("ALTER TABLE CONFIG ADD COLUMN SUP_ELEC_CMD TEXT");
            case 2:
                db.execSQL("ALTER TABLE CONFIG RENAME TO COMMANDS");
                db.execSQL("CREATE TABLE CONFIG (TIMERLOOP INTEGER)");
            case 3:
                db.execSQL("ALTER TABLE CONFIG ADD COLUMN PHONENUMBER TEXT");
            case 4:
                db.execSQL(SMSRECEIVED_TABLE_SQL);
            case 5:
                db.execSQL("ALTER TABLE PHONES ADD COLUMN INITIALIZED INTEGER");
                db.execSQL("ALTER TABLE PHONES ADD COLUMN TIMEZONE INTEGER");
                db.execSQL("ALTER TABLE PHONES ADD COLUMN THISADMIN INTEGER");
                db.execSQL("ALTER TABLE PHONES ADD COLUMN THISPASSWORD INTEGER");
                db.execSQL("ALTER TABLE PHONES ADD COLUMN CUTTEDOIL INTEGER");
                db.execSQL("ALTER TABLE PHONES ADD COLUMN CUTTEDELEC INTEGER");
                db.execSQL("ALTER TABLE PHONES ADD COLUMN MODE INTEGER");
            case 6:
                db.execSQL("ALTER TABLE PHONES ADD COLUMN LASTLOCATION TEXT");
            case 7:
                db.execSQL("ALTER TABLE PHONES ADD COLUMN SOSKEY INT");
            case 8:
                db.execSQL(TRACKING_TRABLE_SQL);
            case 9:
                db.execSQL("ALTER TABLE COMMANDS ADD COLUMN SOS_KEY_ON_CMD TEXT");
                db.execSQL("ALTER TABLE COMMANDS ADD COLUMN SOS_KEY_OFF_CMD TEXT");
        }
    }
}
