package io.github.salemlockwood.android.smscarmanager;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;

import br.com.solucoes161.java.smscarmanager.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private String selectedPhone = "";
    private PhonesDao pdao = null;
    private Phones sphone = null;

    TextView txv_phone_number;
    ImageView imv_phone_number;

    Button btn_locate,btn_oil,btn_cut_oil,btn_sup_oil,btn_elec,btn_cut_elec,btn_sup_elec;
    Button btn_sos_key,btn_mode,btn_tracker_mode,btn_monitor_mode,btn_auto_track,btn_call;
    Button btn_sos_key_on,btn_sos_key_off,btn_autotrack_on,btn_autotrack_off;

    Timer timer = null;

    int[] listCounters;

    Handler[] listHandlers;

    boolean timerRuning = false;

    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

    private final static String[] REQUIRED_SDK_PERMISSIONS = new String[]{
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.SYSTEM_ALERT_WINDOW,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        registerReceiver(smsRec, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu m = navigationView.getMenu();
        SubMenu smPhones = m.addSubMenu("Telefones");

        listHandlers = new Handler[12];
        for (int i = 0; i < 12; i++) listHandlers[i] = new Handler();
        listCounters = new int[12];
        for (int i = 0; i < 12; i++) listCounters[i] = 0;

        pdao = new PhonesDao(getApplicationContext());
        List<Phones> phones = pdao.selectAll();
        for (Phones phone : phones) {
            smPhones.add(phone.getPhone());
        }
        MenuItem mi = m.getItem(m.size() - 1);
        mi.setTitle(mi.getTitle());

        CommandsDao cdao = new CommandsDao(getApplicationContext());
        Commands commands = cdao.getConfiguration();

        ConfigDao cfdao = new ConfigDao(getApplicationContext());
        Config cfg = cfdao.getConfig();

        timer = new Timer();

        imv_phone_number = (ImageView) findViewById(R.id.imv_phone_number);
        txv_phone_number = (TextView) findViewById(R.id.txv_phone_number);

        btn_locate = (Button) findViewById(R.id.btn_locate);

        btn_oil = (Button) findViewById(R.id.btn_oil);
        btn_cut_oil = (Button) findViewById(R.id.btn_cut_oil);
        btn_sup_oil = (Button) findViewById(R.id.btn_sup_oil);

        btn_elec = (Button) findViewById(R.id.btn_elec);
        btn_cut_elec = (Button) findViewById(R.id.btn_cut_elec);
        btn_sup_elec = (Button) findViewById(R.id.btn_sup_elec);

        btn_sos_key = (Button) findViewById(R.id.btn_sos_key);

        btn_mode = (Button) findViewById(R.id.btn_mode);
        btn_tracker_mode = (Button) findViewById(R.id.btn_tracker_mode);
        btn_monitor_mode = (Button) findViewById(R.id.btn_monitor_mode);

        btn_auto_track = (Button) findViewById(R.id.btn_auto_track);

        btn_call = (Button) findViewById(R.id.btn_call);

        btn_sos_key_on = (Button) findViewById(R.id.btn_sos_key_on);
        btn_sos_key_off = (Button) findViewById(R.id.btn_sos_key_off);

        btn_autotrack_on = (Button) findViewById(R.id.btn_autotrack_on);
        btn_autotrack_off = (Button) findViewById(R.id.btn_autotrack_off);

        if (commands != null) {
            if (cfg != null) {
                if (!phones.isEmpty()) {
                    selectedPhone = phones.get(0).getPhone();
                    sphone = phones.get(0);
                    confButtonsVisibility();
                    confButtonsListeners();
                } else {
                    chamaActivityPhonesInsert();
                }
            } else {
                chamaActivityConfiguration();
            }
        } else {
            chamaActivityConfigurationCommands();
        }
        navigationView.setNavigationItemSelectedListener(this);
        txv_phone_number.setText(selectedPhone);
    }

    protected void initialize() {

    }

    protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(this, "Permissão necessária '" + permissions[index]
                                + "' não autorizada, saindo", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }
                // all permissions were granted
                break;
        }
    }

    @Override
    public void onStop(){
        unregisterReceiver(smsRec);
        super.onStop();
    }
    @Override
    public void onRestart(){
        registerReceiver(smsRec, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
        super.onRestart();
    }

    private void confButtonsListeners() {
        confButtonLocate();
        confButtonsOil();
        confButtonsElec();
        confButtonsMode();
        confButtonSosKey();
        confButtonAutoTrack();
        confButtonCall();
    }

    private void confButtonCall() {
        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callNumber();
            }
        });
    }

    private void confButtonAutoTrack() {
        disableSubBtn(btn_autotrack_off,R.mipmap.ic_autotrack_off);
        btn_auto_track.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_autotrack),null,getResources().getDrawable(R.mipmap.ic_rightsign),null);
        btn_auto_track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_autotrack_on.getVisibility()==View.GONE) {
                    btn_auto_track.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_autotrack),null,getResources().getDrawable(R.mipmap.ic_downsign),null);
                    btn_oil.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_oil),null,getResources().getDrawable(R.mipmap.ic_rightsign),null);
                    btn_elec.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_elec),null,getResources().getDrawable(R.mipmap.ic_rightsign),null);
                    btn_mode.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_monitor),null,getResources().getDrawable(R.mipmap.ic_rightsign),null);
                    btn_sos_key.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_sos_key),null,getResources().getDrawable(R.mipmap.ic_rightsign),null);
                    btn_autotrack_on.setVisibility(View.VISIBLE);
                    btn_autotrack_off.setVisibility(View.VISIBLE);
                    btn_sos_key_on.setVisibility(View.GONE);
                    btn_sos_key_off.setVisibility(View.GONE);
                    btn_cut_oil.setVisibility(View.GONE);
                    btn_sup_oil.setVisibility(View.GONE);
                    btn_cut_elec.setVisibility(View.GONE);
                    btn_sup_elec.setVisibility(View.GONE);
                    btn_tracker_mode.setVisibility(View.GONE);
                    btn_monitor_mode.setVisibility(View.GONE);
                } else {
                    btn_auto_track.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_autotrack),null,getResources().getDrawable(R.mipmap.ic_rightsign),null);
                    btn_autotrack_on.setVisibility(View.GONE);
                    btn_autotrack_off.setVisibility(View.GONE);
                }
            }
        });
        btn_autotrack_on.setOnClickListener(new View.OnClickListener() {
            final int timeRuning = new ConfigDao(getApplicationContext()).getConfig().getTimerLoop()*100;
            @Override
            public void onClick(View v) {
                listHandlers[8] = new Handler();
                listCounters[8] = 0;
                listHandlers[8].postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        switch (listCounters[8]){
                            case 0:
                                enableSubBtn(btn_autotrack_off,R.mipmap.ic_autotrack_off);
                                disableSubBtn(btn_autotrack_on,R.mipmap.ic_autotrack_on);
                                btn_autotrack_on.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_autotrack_on),null,getResources().getDrawable(R.mipmap.ic_load_0),null);
                                listHandlers[8].postDelayed(this,timeRuning);
                                break;
                            case 1:
                                btn_autotrack_on.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_autotrack_on),null,getResources().getDrawable(R.mipmap.ic_load_10),null);
                                listHandlers[8].postDelayed(this,timeRuning);
                                break;
                            case 2:
                                btn_autotrack_on.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_autotrack_on),null,getResources().getDrawable(R.mipmap.ic_load_20),null);
                                listHandlers[8].postDelayed(this,timeRuning);
                                break;
                            case 3:
                                btn_autotrack_on.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_autotrack_on),null,getResources().getDrawable(R.mipmap.ic_load_30),null);
                                listHandlers[8].postDelayed(this,timeRuning);
                                break;
                            case 4:
                                btn_autotrack_on.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_autotrack_on),null,getResources().getDrawable(R.mipmap.ic_load_40),null);
                                listHandlers[8].postDelayed(this,timeRuning);
                                break;
                            case 5:
                                btn_autotrack_on.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_autotrack_on),null,getResources().getDrawable(R.mipmap.ic_load_50),null);
                                listHandlers[8].postDelayed(this,timeRuning);
                                break;
                            case 6:
                                btn_autotrack_on.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_autotrack_on),null,getResources().getDrawable(R.mipmap.ic_load_60),null);
                                listHandlers[8].postDelayed(this,timeRuning);
                                break;
                            case 7:
                                btn_autotrack_on.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_autotrack_on),null,getResources().getDrawable(R.mipmap.ic_load_70),null);
                                listHandlers[8].postDelayed(this,timeRuning);
                                break;
                            case 8:
                                btn_autotrack_on.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_autotrack_on),null,getResources().getDrawable(R.mipmap.ic_load_80),null);
                                listHandlers[8].postDelayed(this,timeRuning);
                                break;
                            case 9:
                                btn_autotrack_on.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_autotrack_on),null,getResources().getDrawable(R.mipmap.ic_load_90),null);
                                listHandlers[8].postDelayed(this,timeRuning);
                                break;
                            case 10:
                                btn_autotrack_on.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_autotrack_on),null,getResources().getDrawable(R.mipmap.ic_load_100ok),null);
                                listHandlers[8].postDelayed(this,timeRuning);
                                listCounters[8] = -1;
                                sendLocSMS();
                                break;
                        }
                        listCounters[8]++;
                    }
                },0);
            }
        });
        btn_autotrack_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listHandlers[8].removeCallbacksAndMessages(null);
                listCounters[8] = 0;
                enableSubBtn(btn_autotrack_on,R.mipmap.ic_autotrack_on);
                disableSubBtn(btn_autotrack_off, R.mipmap.ic_autotrack_off);
            }
        });
    }

    private void confButtonSosKey() {
        boolean SosKeyOn = sphone.isSosKey();
        btn_sos_key.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_sos_key),null,getResources().getDrawable(R.mipmap.ic_rightsign),null);
        btn_sos_key.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_sos_key_on.getVisibility()==View.GONE) {
                    btn_sos_key.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_sos_key), null, getResources().getDrawable(R.mipmap.ic_downsign), null);
                    btn_oil.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_oil),null,getResources().getDrawable(R.mipmap.ic_rightsign),null);
                    btn_elec.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_elec),null,getResources().getDrawable(R.mipmap.ic_rightsign),null);
                    btn_auto_track.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_autotrack),null,getResources().getDrawable(R.mipmap.ic_rightsign),null);
                    btn_mode.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_monitor),null,getResources().getDrawable(R.mipmap.ic_rightsign),null);
                    btn_sos_key_on.setVisibility(View.VISIBLE);
                    btn_sos_key_off.setVisibility(View.VISIBLE);
                    btn_autotrack_on.setVisibility(View.GONE);
                    btn_autotrack_off.setVisibility(View.GONE);
                    btn_cut_oil.setVisibility(View.GONE);
                    btn_sup_oil.setVisibility(View.GONE);
                    btn_cut_elec.setVisibility(View.GONE);
                    btn_sup_elec.setVisibility(View.GONE);
                    btn_tracker_mode.setVisibility(View.GONE);
                    btn_monitor_mode.setVisibility(View.GONE);
                } else {
                    btn_sos_key.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_sos_key),null,getResources().getDrawable(R.mipmap.ic_rightsign),null);
                    btn_sos_key_on.setVisibility(View.GONE);
                    btn_sos_key_off.setVisibility(View.GONE);
                }
            }
        });
        btn_sos_key_on.setOnClickListener(configureOnClickListenerSubButton(btn_sos_key_on,R.mipmap.ic_sos_key_on,7));
        btn_sos_key_off.setOnClickListener(configureOnClickListenerSubButton(btn_sos_key_off,R.mipmap.ic_sos_key_off,8));
        if(SosKeyOn){
            enableSubBtn(btn_sos_key_off,R.mipmap.ic_sos_key_off);
            disableSubBtnWithOK(btn_sos_key_on, R.mipmap.ic_sos_key_on);
        } else {
            disableSubBtnWithOK(btn_sos_key_off,R.mipmap.ic_sos_key_off);
            enableSubBtn(btn_sos_key_on, R.mipmap.ic_sos_key_on);
        }
    }

    private void confButtonsOil() {
        boolean CuttedOil = sphone.isCuttedOil();
        btn_oil.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_oil),null,getResources().getDrawable(R.mipmap.ic_rightsign),null);
        btn_oil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_cut_oil.getVisibility()==View.GONE) {
                    btn_oil.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_oil),null,getResources().getDrawable(R.mipmap.ic_downsign),null);
                    btn_mode.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_monitor),null,getResources().getDrawable(R.mipmap.ic_rightsign),null);
                    btn_elec.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_elec),null,getResources().getDrawable(R.mipmap.ic_rightsign),null);
                    btn_auto_track.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_autotrack),null,getResources().getDrawable(R.mipmap.ic_rightsign),null);
                    btn_sos_key.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_sos_key),null,getResources().getDrawable(R.mipmap.ic_rightsign),null);
                    btn_cut_oil.setVisibility(View.VISIBLE);
                    btn_sup_oil.setVisibility(View.VISIBLE);
                    btn_cut_elec.setVisibility(View.GONE);
                    btn_sup_elec.setVisibility(View.GONE);
                    btn_tracker_mode.setVisibility(View.GONE);
                    btn_monitor_mode.setVisibility(View.GONE);
                    btn_autotrack_on.setVisibility(View.GONE);
                    btn_autotrack_off.setVisibility(View.GONE);
                    btn_sos_key_on.setVisibility(View.GONE);
                    btn_sos_key_off.setVisibility(View.GONE);

                } else {
                    btn_oil.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_oil),null,getResources().getDrawable(R.mipmap.ic_rightsign),null);
                    btn_cut_oil.setVisibility(View.GONE);
                    btn_sup_oil.setVisibility(View.GONE);
                }
            }
        });
        btn_cut_oil.setOnClickListener(configureOnClickListenerSubButton(btn_cut_oil,R.mipmap.ic_cutoil,1));
        btn_sup_oil.setOnClickListener(configureOnClickListenerSubButton(btn_sup_oil,R.mipmap.ic_supoil,2));
        if(CuttedOil){
            enableSubBtn(btn_sup_oil,R.mipmap.ic_supoil);
            disableSubBtnWithOK(btn_cut_oil,R.mipmap.ic_cutoil);
        } else {
            disableSubBtnWithOK(btn_sup_oil,R.mipmap.ic_supoil);
            enableSubBtn(btn_cut_oil,R.mipmap.ic_cutoil);
        }
    }

    private void confButtonsElec() {
        boolean CuttedElec = sphone.isCuttedElec();
        btn_elec.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_elec),null,getResources().getDrawable(R.mipmap.ic_rightsign),null);
        btn_elec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_cut_elec.getVisibility()==View.GONE) {
                    btn_elec.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_elec),null,getResources().getDrawable(R.mipmap.ic_downsign),null);
                    btn_oil.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_oil),null,getResources().getDrawable(R.mipmap.ic_rightsign),null);
                    btn_mode.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_monitor),null,getResources().getDrawable(R.mipmap.ic_rightsign),null);
                    btn_auto_track.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_autotrack),null,getResources().getDrawable(R.mipmap.ic_rightsign),null);
                    btn_sos_key.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_sos_key),null,getResources().getDrawable(R.mipmap.ic_rightsign),null);
                    btn_cut_elec.setVisibility(View.VISIBLE);
                    btn_sup_elec.setVisibility(View.VISIBLE);
                    btn_cut_oil.setVisibility(View.GONE);
                    btn_sup_oil.setVisibility(View.GONE);
                    btn_tracker_mode.setVisibility(View.GONE);
                    btn_monitor_mode.setVisibility(View.GONE);
                    btn_autotrack_on.setVisibility(View.GONE);
                    btn_autotrack_off.setVisibility(View.GONE);
                    btn_sos_key_on.setVisibility(View.GONE);
                    btn_sos_key_off.setVisibility(View.GONE);
                } else {
                    btn_oil.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_elec),null,getResources().getDrawable(R.mipmap.ic_rightsign),null);
                    btn_cut_elec.setVisibility(View.GONE);
                    btn_sup_elec.setVisibility(View.GONE);
                }
            }
        });
        btn_cut_elec.setOnClickListener(configureOnClickListenerSubButton(btn_cut_elec,R.mipmap.ic_cutelec,3));
        btn_sup_elec.setOnClickListener(configureOnClickListenerSubButton(btn_sup_elec,R.mipmap.ic_supelec,4));
        if(CuttedElec){
            enableSubBtn(btn_sup_elec,R.mipmap.ic_supelec);
            disableSubBtnWithOK(btn_cut_elec,R.mipmap.ic_cutelec);
        } else {
            disableSubBtnWithOK(btn_sup_elec,R.mipmap.ic_supelec);
            enableSubBtn(btn_cut_elec,R.mipmap.ic_cutelec);
        }
    }

    private void confButtonsMode() {
        int Mode = sphone.getMode();
        btn_mode.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_monitor),null,getResources().getDrawable(R.mipmap.ic_rightsign),null);
        btn_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_tracker_mode.getVisibility()==View.GONE) {
                    btn_mode.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_monitor),null,getResources().getDrawable(R.mipmap.ic_downsign),null);
                    btn_oil.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_oil),null,getResources().getDrawable(R.mipmap.ic_rightsign),null);
                    btn_elec.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_elec),null,getResources().getDrawable(R.mipmap.ic_rightsign),null);
                    btn_auto_track.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_autotrack),null,getResources().getDrawable(R.mipmap.ic_rightsign),null);
                    btn_sos_key.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_sos_key),null,getResources().getDrawable(R.mipmap.ic_rightsign),null);
                    btn_cut_elec.setVisibility(View.GONE);
                    btn_sup_elec.setVisibility(View.GONE);
                    btn_cut_oil.setVisibility(View.GONE);
                    btn_sup_oil.setVisibility(View.GONE);
                    btn_tracker_mode.setVisibility(View.VISIBLE);
                    btn_monitor_mode.setVisibility(View.VISIBLE);
                    btn_autotrack_on.setVisibility(View.GONE);
                    btn_autotrack_off.setVisibility(View.GONE);
                    btn_sos_key_on.setVisibility(View.GONE);
                    btn_sos_key_off.setVisibility(View.GONE);
                } else {
                    btn_mode.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_call),null,getResources().getDrawable(R.mipmap.ic_rightsign),null);
                    btn_tracker_mode.setVisibility(View.GONE);
                    btn_monitor_mode.setVisibility(View.GONE);
                }
            }
        });
        btn_tracker_mode.setOnClickListener(configureOnClickListenerSubButton(btn_tracker_mode,R.mipmap.ic_monitor,5));
        btn_monitor_mode.setOnClickListener(configureOnClickListenerSubButton(btn_monitor_mode,R.mipmap.ic_call,6));
        if(Mode==0){
            enableSubBtn(btn_monitor_mode,R.mipmap.ic_call);
            disableSubBtnWithOK(btn_tracker_mode,R.mipmap.ic_monitor);
        } else {
            disableSubBtnWithOK(btn_monitor_mode,R.mipmap.ic_call);
            enableSubBtn(btn_tracker_mode,R.mipmap.ic_monitor);
        }
    }

    private View.OnClickListener configureOnClickListenerSubButton(final Button btn, final int ico, final int n){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listHandlers[n] = new Handler();
                listCounters[n] = 0;
                sendSMSFor(n);
                listHandlers[n].postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        switch (listCounters[n]){
                            case 0:
                                disableSubBtn(btn,ico);
                                btn.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(ico),null,getResources().getDrawable(R.mipmap.ic_load_0),null);
                                listHandlers[n].postDelayed(this,3000);
                                break;
                            case 1:
                                btn.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(ico),null,getResources().getDrawable(R.mipmap.ic_load_10),null);
                                listHandlers[n].postDelayed(this,3000);
                                break;
                            case 2:
                                btn.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(ico),null,getResources().getDrawable(R.mipmap.ic_load_20),null);
                                listHandlers[n].postDelayed(this,3000);
                                break;
                            case 3:
                                btn.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(ico),null,getResources().getDrawable(R.mipmap.ic_load_30),null);
                                listHandlers[n].postDelayed(this,3000);
                                break;
                            case 4:
                                btn.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(ico),null,getResources().getDrawable(R.mipmap.ic_load_40),null);
                                listHandlers[n].postDelayed(this,3000);
                                break;
                            case 5:
                                btn.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(ico),null,getResources().getDrawable(R.mipmap.ic_load_50),null);
                                listHandlers[n].postDelayed(this,3000);
                                break;
                            case 6:
                                btn.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(ico),null,getResources().getDrawable(R.mipmap.ic_load_60),null);
                                listHandlers[n].postDelayed(this,3000);
                                break;
                            case 7:
                                btn.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(ico),null,getResources().getDrawable(R.mipmap.ic_load_70),null);
                                listHandlers[n].postDelayed(this,3000);
                                break;
                            case 8:
                                btn.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(ico),null,getResources().getDrawable(R.mipmap.ic_load_80),null);
                                listHandlers[n].postDelayed(this,3000);
                                break;
                            case 9:
                                btn.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(ico),null,getResources().getDrawable(R.mipmap.ic_load_90),null);
                                listHandlers[n].postDelayed(this,3000);
                                break;
                            case 10:
                                enableSubBtn(btn,ico);
                                listCounters[n] = 0;
                                break;
                        }
                        listCounters[n]++;
                    }
                },0);
            }
        };
    }

    private void sendSMSFor(int i) {
        switch(i){
            case 0:
                sendLocSMS();
                break;
            case 1:
                sendCutOilSMS();
                break;
            case 2:
                sendSupOilSMS();
                break;
            case 3:
                sendCutElecSMS();
                break;
            case 4:
                sendSupElecSMS();
                break;
            case 5:
                sendTrkSMS();
                break;
            case 6:
                sendLtnSMS();
            case 7:
                if(sphone.isSosKey()) sendOffSosKey();
                else sendOnSosKey();
        }
    }

    private void sendOnSosKey() {
        SmsManager smsManager = SmsManager.getDefault();
        String LT_CMD = new CommandsDao(getApplicationContext()).getConfiguration().getSOS_KEY_ON_CMD();
        String PASS_PHONE = new PhonesDao(getApplicationContext()).select(selectedPhone).getPassword();

        LT_CMD = LT_CMD.replaceAll("\\{password\\}",PASS_PHONE);
        LT_CMD = LT_CMD.replaceAll("\\{phone\\}", selectedPhone);
        smsManager.sendTextMessage(selectedPhone, null, LT_CMD, null, null);
    }

    private void sendOffSosKey() {
        SmsManager smsManager = SmsManager.getDefault();
        String LT_CMD = new CommandsDao(getApplicationContext()).getConfiguration().getSOS_KEY_OFF_CMD();
        String PASS_PHONE = new PhonesDao(getApplicationContext()).select(selectedPhone).getPassword();

        LT_CMD = LT_CMD.replaceAll("\\{password\\}",PASS_PHONE);
        LT_CMD = LT_CMD.replaceAll("\\{phone\\}", selectedPhone);
        smsManager.sendTextMessage(selectedPhone, null, LT_CMD, null, null);
    }

    private void confButtonLocate() {
        btn_locate.setOnClickListener(configureOnClickListenerSubButton(btn_locate, R.mipmap.ic_locate, 0));
    }

    private void confButtonsVisibility() {
        switch (sphone.getMode()){
            case 0:
                btn_call.setVisibility(View.GONE);
                btn_cut_oil.setVisibility(View.GONE);
                btn_sup_oil.setVisibility(View.GONE);
                btn_cut_elec.setVisibility(View.GONE);
                btn_sup_elec.setVisibility(View.GONE);
                btn_tracker_mode.setVisibility(View.GONE);
                btn_monitor_mode.setVisibility(View.GONE);
                btn_sos_key_on.setVisibility(View.GONE);
                btn_sos_key_off.setVisibility(View.GONE);
                btn_autotrack_on.setVisibility(View.GONE);
                btn_autotrack_off.setVisibility(View.GONE);
                break;
            case 1:
                btn_locate.setVisibility(View.GONE);
                btn_oil.setVisibility(View.GONE);
                btn_cut_oil.setVisibility(View.GONE);
                btn_sup_oil.setVisibility(View.GONE);
                btn_elec.setVisibility(View.GONE);
                btn_cut_elec.setVisibility(View.GONE);
                btn_sup_elec.setVisibility(View.GONE);
                btn_sos_key.setVisibility(View.GONE);
                btn_tracker_mode.setVisibility(View.GONE);
                btn_monitor_mode.setVisibility(View.GONE);
                btn_auto_track.setVisibility(View.GONE);
                btn_sos_key_on.setVisibility(View.GONE);
                btn_sos_key_off.setVisibility(View.GONE);
                btn_autotrack_on.setVisibility(View.GONE);
                btn_autotrack_off.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_update) {
            checkUpdate();
        } else if(id == R.id.action_phones){
            chamaActivityPhonesInsert();
        } else if(id == R.id.action_commands){
            chamaActivityConfigurationCommands();
        } else if(id == R.id.action_config){
            chamaActivityConfiguration();
        } else if(id == R.id.action_tracking){
            chamaActivityTracking();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        selectedPhone = item.getTitle().toString();
        txv_phone_number.setText(selectedPhone);
        sphone = pdao.select(selectedPhone);
        createPhonesClick();
        return true;
    }

    private void createPhonesClick(){
        
    }

    private void chamaActivityConfigurationCommands(){
        Intent i = new Intent(getApplicationContext(), CommandsSettingsActivity.class);
        startActivityForResult(i, 1);
    }

    private void chamaActivityTracking(){
        Intent i = new Intent(getApplicationContext(), TracksActivity.class);
        startActivity(i);
    }

    private void chamaActivityPhonesInsert(){
        Intent i = new Intent(getApplicationContext(), PhonesListActivity.class);
        startActivityForResult(i,1);
    }

    private void chamaActivityConfiguration(){
        Intent i = new Intent(getApplicationContext(), ConfigActivity.class);
        startActivityForResult(i,1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //SecondActivity closed
        if(requestCode == 1){
            this.recreate();
        }
    }

    private void sendLocSMS(){
        SmsManager smsManager = SmsManager.getDefault();
        String LOC_CMD = new CommandsDao(getApplicationContext()).getConfiguration().getLOC_CMD();
        String PASS_PHONE = new PhonesDao(getApplicationContext()).select(selectedPhone).getPassword();

        LOC_CMD = LOC_CMD.replaceAll("\\{password\\}",PASS_PHONE);
        LOC_CMD = LOC_CMD.replaceAll("\\{phone\\}", selectedPhone);
        smsManager.sendTextMessage(selectedPhone, null, LOC_CMD, null, null);
    }

    private void callNumber(){
        Intent icall = new Intent(Intent.ACTION_CALL);
        icall.setData(Uri.parse("tel:" + selectedPhone));
        startActivity(icall);
    }

    private void sendCutOilSMS(){
        SmsManager smsManager = SmsManager.getDefault();
        String CO_CMD = new CommandsDao(getApplicationContext()).getConfiguration().getCUT_OIL_CMD();
        String PASS_PHONE = new PhonesDao(getApplicationContext()).select(selectedPhone).getPassword();

        CO_CMD = CO_CMD.replaceAll("\\{password\\}",PASS_PHONE);
        CO_CMD = CO_CMD.replaceAll("\\{phone\\}", selectedPhone);
        smsManager.sendTextMessage(selectedPhone, null, CO_CMD, null, null);
    }

    private void sendSupOilSMS(){
        SmsManager smsManager = SmsManager.getDefault();
        String SO_CMD = new CommandsDao(getApplicationContext()).getConfiguration().getSUP_OIL_CMD();
        String PASS_PHONE = new PhonesDao(getApplicationContext()).select(selectedPhone).getPassword();

        SO_CMD = SO_CMD.replaceAll("\\{password\\}",PASS_PHONE);
        SO_CMD = SO_CMD.replaceAll("\\{phone\\}", selectedPhone);
        smsManager.sendTextMessage(selectedPhone, null, SO_CMD, null, null);
    }

    private void sendCutElecSMS(){
        SmsManager smsManager = SmsManager.getDefault();
        String CE_CMD = new CommandsDao(getApplicationContext()).getConfiguration().getCUT_ELEC_CMD();
        String PASS_PHONE = new PhonesDao(getApplicationContext()).select(selectedPhone).getPassword();

        CE_CMD = CE_CMD.replaceAll("\\{password\\}",PASS_PHONE);
        CE_CMD = CE_CMD.replaceAll("\\{phone\\}", selectedPhone);
        smsManager.sendTextMessage(selectedPhone, null, CE_CMD, null, null);
    }

    private void sendSupElecSMS(){
        SmsManager smsManager = SmsManager.getDefault();
        String SE_CMD = new CommandsDao(getApplicationContext()).getConfiguration().getSUP_ELEC_CMD();
        String PASS_PHONE = new PhonesDao(getApplicationContext()).select(selectedPhone).getPassword();

        SE_CMD = SE_CMD.replaceAll("\\{password\\}",PASS_PHONE);
        SE_CMD = SE_CMD.replaceAll("\\{phone\\}", selectedPhone);
        smsManager.sendTextMessage(selectedPhone, null, SE_CMD, null, null);
    }

    private void sendTrkSMS(){
        SmsManager smsManager = SmsManager.getDefault();
        String TR_CMD = new CommandsDao(getApplicationContext()).getConfiguration().getTRK_CMD();
        String PASS_PHONE = new PhonesDao(getApplicationContext()).select(selectedPhone).getPassword();

        TR_CMD = TR_CMD.replaceAll("\\{password\\}",PASS_PHONE);
        TR_CMD = TR_CMD.replaceAll("\\{phone\\}", selectedPhone);
        smsManager.sendTextMessage(selectedPhone, null, TR_CMD, null, null);
    }

    private void sendLtnSMS(){
        SmsManager smsManager = SmsManager.getDefault();
        String LT_CMD = new CommandsDao(getApplicationContext()).getConfiguration().getLTN_CMD();
        String PASS_PHONE = new PhonesDao(getApplicationContext()).select(selectedPhone).getPassword();

        LT_CMD = LT_CMD.replaceAll("\\{password\\}",PASS_PHONE);
        LT_CMD = LT_CMD.replaceAll("\\{phone\\}", selectedPhone);
        smsManager.sendTextMessage(selectedPhone, null, LT_CMD, null, null);
    }

    private void checkUpdate(){
        if(isOnline()) {
            int appVersion = getAppVersion();
            int remoteVersion = getRemoteVersion();
            if (appVersion < remoteVersion) {
                InstallAPK downloadAndInstall = new InstallAPK();
                ProgressDialog progress = new ProgressDialog(MainActivity.this);
                progress.setCancelable(false);
                progress.setMessage("Baixando Atualização...");
                progress.show();
                downloadAndInstall.setContext(getApplicationContext(), progress);
                new AlertDialog(this,"Atualização","Instalação começará em breve...").show();
                downloadAndInstall.execute("http://179.188.12.89/smscarmanager/sms.apk");
            } else {
                new AlertDialog(this,"Atualização","Não existem atualizações!").show();
            }
        }else{
            new AlertDialog(this,"Atualização","Sem conexão com a internet.").show();
        }
    }

    private int getAppVersion(){
        PackageManager pm = getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(getApplicationContext().getPackageName(),0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private int getRemoteVersion(){
        String downloaded = downloadText();
        return Integer.parseInt(downloaded);
    }

    private String downloadText() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        URL url;
        BufferedReader in = null;
        String retorno = "";
        try {
            url = new URL("http://179.188.12.89/smscarmanager/version.txt");
            in = new BufferedReader(
                    new InputStreamReader(
                            url.openStream()));

            String inputLine;

            while ((inputLine = in.readLine()) != null){
                retorno = inputLine;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(in instanceof BufferedReader) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return retorno;
    }

    private boolean isOnline()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }

    private void startLoopTimer(){
        timerRuning = true;
        int timeLoop = 30;
        Config cfg = null;
        ConfigDao dao = new ConfigDao(getApplicationContext());
        cfg = dao.getConfig();
        if(cfg != null){
            if(cfg.getTimerLoop() > 9){
                timeLoop = cfg.getTimerLoop();
            }
        }
        final int timeToLoop = timeLoop;
        listHandlers[9] = new Handler();
        listHandlers[9].postDelayed(new Runnable() {
            @Override
            public void run() {
                sendLocSMS();
                listHandlers[9].postDelayed(this,timeToLoop*1000);
            }
        },0);
    }
    private void stopLoopTimer(){
        timerRuning = false;
        listHandlers[9].removeCallbacksAndMessages(null);
    }

    private BroadcastReceiver smsRec = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null;
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                msgs = new SmsMessage[pdus.length];
                for(int i = 0;i<pdus.length;i++)
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                for(SmsMessage sms : msgs){
                    if(sms != null) {
                        if (sms.getOriginatingAddress().contains(selectedPhone.substring(3))) {
                            if(sms.getMessageBody().startsWith("http://maps.google.com/maps")) {
                                listHandlers[0].removeCallbacksAndMessages(null);
                                btn_locate.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_locate),null,getResources().getDrawable(R.mipmap.ic_load_100ok),null);
                                btn_locate.setEnabled(true);
                                btn_locate.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_bg));
                                listCounters[0] = 0;
                            }
                            if(sms.getMessageBody().contains("gps not fixed")) {
                                listHandlers[0].removeCallbacksAndMessages(null);
                                listCounters[0] = 0;
                                new AlertDialog(MainActivity.this, "Erro no GPS", "Coordenadas GPS ainda não encontradas, tente novamente em alguns minutos.").show();
                                btn_locate.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_locate), null, getResources().getDrawable(R.mipmap.ic_load_100error), null);
                                btn_locate.setEnabled(true);
                                btn_locate.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_bg));
                            }else if(sms.getMessageBody().contains("stop oil ok")){
                                listHandlers[1].removeCallbacksAndMessages(null);
                                listCounters[1] = 0;
                                //new AlertDialog(MainActivity.this, "Combustível cortado", "O combustível foi cortado com sucesso.").show();
                                disableSubBtnWithOK(btn_cut_oil, R.mipmap.ic_cutoil);
                                enableSubBtn(btn_sup_oil, R.mipmap.ic_supoil);
                            }else if(sms.getMessageBody().contains("supply oil ok")){
                                listHandlers[2].removeCallbacksAndMessages(null);
                                listCounters[2] = 0;
                                //new AlertDialog(MainActivity.this, "Combustível ligado", "O combustível foi ligado com sucesso.").show();
                                disableSubBtnWithOK(btn_sup_oil,R.mipmap.ic_supoil);
                                enableSubBtn(btn_cut_oil,R.mipmap.ic_cutoil);
                            }else if(sms.getMessageBody().contains("stop electricity ok")){
                                listHandlers[3].removeCallbacksAndMessages(null);
                                listCounters[3] = 0;
                                //new AlertDialog(MainActivity.this, "Eletricidade cortada", "A eletricidade foi cortada com sucesso.").show();
                                disableSubBtnWithOK(btn_cut_elec, R.mipmap.ic_cutelec);
                                enableSubBtn(btn_sup_elec, R.mipmap.ic_supelec);
                            }else if(sms.getMessageBody().contains("supply electricity ok")){
                                listHandlers[4].removeCallbacksAndMessages(null);
                                listCounters[4] = 0;
                                //new AlertDialog(MainActivity.this, "Eletricidade ligada", "A eletricidade foi ligada com sucesso.").show();
                                disableSubBtnWithOK(btn_sup_elec, R.mipmap.ic_supelec);
                                enableSubBtn(btn_cut_elec, R.mipmap.ic_cutelec);
                            }else if(sms.getMessageBody().contains("tracker ok")) {
                                listHandlers[5].removeCallbacksAndMessages(null);
                                listCounters[5] = 0;
                                //new AlertDialog(MainActivity.this, "Modo Rastreamento", "O modo rastreamento foi ligado com sucesso.").show();
                                disableSubBtnWithOK(btn_tracker_mode, R.mipmap.ic_track);
                                enableSubBtn(btn_tracker_mode, R.mipmap.ic_track);
                                MainActivity.this.recreate();
                            }else if(sms.getMessageBody().contains("monitor ok")) {
                                listHandlers[6].removeCallbacksAndMessages(null);
                                listCounters[6] = 0;
                                //new AlertDialog(MainActivity.this, "Modo Monitoramento", "O modo monitoramento foi ligado com sucesso.").show();
                                disableSubBtnWithOK(btn_monitor_mode, R.mipmap.ic_monitor);
                                enableSubBtn(btn_monitor_mode, R.mipmap.ic_monitor);
                                MainActivity.this.recreate();
                            }else if(sms.getMessageBody().contains("ACC ON OK")){
                                listHandlers[7].removeCallbacksAndMessages(null);
                                listCounters[7] = 0;
                                //new AlertDialog(MainActivity.this, "Alarme de Chave", "Alarme de chave ligado com sucesso.").show();
                                enableSubBtn(btn_sos_key_off, R.mipmap.ic_sos_key_off);
                                disableSubBtnWithOK(btn_sos_key_on, R.mipmap.ic_sos_key_on);
                                btn_sos_key.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_sos_key),null,getResources().getDrawable(R.mipmap.ic_load_100ok),null);
                            }else if(sms.getMessageBody().contains("ACC OFF OK")){
                                listHandlers[8].removeCallbacksAndMessages(null);
                                listCounters[8] = 0;
                                //new AlertDialog(MainActivity.this, "Alarme de Chave", "Alarme de chave desligado com sucesso.").show();
                                enableSubBtn(btn_sos_key_on, R.mipmap.ic_sos_key_on);
                                disableSubBtnWithOK(btn_sos_key_off, R.mipmap.ic_sos_key_off);
                                btn_sos_key.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.ic_sos_key),null,null,null);
                            }
                        }
                    }
                }
            }
        }
    };

    private void disableSubBtnWithOK(Button btn, int ico) {
        btn.setEnabled(false);
        btn.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(ico),null,getResources().getDrawable(R.mipmap.ic_load_100ok),null);
        btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_bg_disabled));
    }

    private void disableSubBtn(Button btn, int ico) {
        btn.setEnabled(false);
        btn.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(ico),null,null,null);
        btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_bg_disabled));
    }

    private void enableSubBtn(Button btn, int ico){
        enableBtn(btn,ico,R.drawable.acc_button_bg);
    }

    private void enableBtn(Button btn, int ico,int bgbtn) {
        btn.setEnabled(true);
        btn.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(ico),null,null,null);
        btn.setBackgroundDrawable(getResources().getDrawable(bgbtn));
    }
}
