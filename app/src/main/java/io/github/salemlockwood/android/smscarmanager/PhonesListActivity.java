package io.github.salemlockwood.android.smscarmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsMessage;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.List;

import br.com.solucoes161.java.smscarmanager.R;

public class PhonesListActivity extends AppCompatActivity {
    private ImageButton btn_add_phone;
    private TableLayout tb_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phones_list);
        registerReceiver(smsRec,new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
        Toolbar toolbar = (Toolbar) findViewById(R.id.barra_listphone);
        setSupportActionBar(toolbar);

        tb_layout = (TableLayout) findViewById(R.id.tb_phones_list);

        insertPhonesOnTableLayout();
    }

    private void insertPhonesOnTableLayout() {
        PhonesDao dao = new PhonesDao(getApplicationContext());
        List<Phones> phones = dao.selectAll();

        for(final Phones p : phones){
            final PhonesController control = new PhonesController(p);
            TableRow tr = new TableRow(getApplicationContext());
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tr.setBackgroundColor(Color.argb(50, 255, 255, 255));
            tr.setOrientation(LinearLayout.HORIZONTAL);
            tr.setGravity(Gravity.CENTER_VERTICAL);
            Button btn = new Button(getApplicationContext());
            btn.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            btn.setText(p.getPhone());
            btn.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.ic_rightsign),null);
            btn.setBackgroundResource(R.drawable.button_bg);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chamaPhoneInsert(p.getPhone());
                }
            });
            int init_drawable = p.isInitialized() ? R.mipmap.ic_device_inited : R.mipmap.ic_device_not_inited;
            int timezone_drawable = p.getTimezone() == 3 ? R.mipmap.ic_device_timezone : R.mipmap.ic_device_not_timezone;
            int admin_drawable = p.isThisAdmin() ? R.mipmap.ic_device_admin : R.mipmap.ic_device_not_admin;
            int password_drawable = p.isThisPassword() ? R.mipmap.ic_device_password : R.mipmap.ic_device_not_password;
            ImageView init_img = new ImageView(getApplicationContext());
            init_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    control.beginDevice(p);
                    Snackbar.make(v,"Enviando SMS de inicialização.",Snackbar.LENGTH_LONG).show();
                }
            });
            ImageView timezone_img = new ImageView(getApplicationContext());
            timezone_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    control.setTimezone(p);
                    Snackbar.make(v,"Enviando SMS de timezone.",Snackbar.LENGTH_LONG).show();
                }
            });
            ImageView admin_img = new ImageView(getApplicationContext());
            admin_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    control.setAdmin(p, (new ConfigDao(getApplicationContext()).getConfig().getPhoneNumber()));
                    Snackbar.make(v,"Enviando SMS de Administrador..",Snackbar.LENGTH_LONG).show();
                }
            });
            ImageView password_img = new ImageView(getApplicationContext());
            password_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    control.changePassword(p,"123456");
                    Snackbar.make(v,"Enviando SMS de Password.",Snackbar.LENGTH_LONG).show();
                }
            });
            init_img.setImageDrawable(getResources().getDrawable(init_drawable));
            timezone_img.setImageDrawable(getResources().getDrawable(timezone_drawable));
            admin_img.setImageDrawable(getResources().getDrawable(admin_drawable));
            password_img.setImageDrawable(getResources().getDrawable(password_drawable));
            tr.addView(btn);
            tr.addView(init_img);
            tr.addView(timezone_img);
            tr.addView(admin_img);
            tr.addView(password_img);
            tb_layout.addView(tr);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_phonelist_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_add_new_phone:
                chamaPhoneInsert("");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void chamaPhoneInsert(String text){
        Intent i = new Intent(getApplicationContext(), PhonesInsertActivity.class);
        i.putExtra("Telefone",text);
        startActivityForResult(i,1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //SecondActivity closed
        if(requestCode == 1){
            this.recreate();
        }
    }
    @Override
    public void onStop(){
        unregisterReceiver(smsRec);
        super.onStop();
    }
    @Override
    public void onRestart(){
        registerReceiver(smsRec,new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
        super.onRestart();
    }

    BroadcastReceiver smsRec = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for (int i=0; i < msgs.length; i++) {
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                List<Phones> phones = new PhonesDao(context).selectAll();
                Boolean listPhone = false;
                for(Phones phone : phones){
                    if(msgs[i].getOriginatingAddress().contains(phone.getPhone().substring(2))){
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
                        unregisterReceiver(smsRec);
                        PhonesListActivity.this.recreate();
                    }
                }
            }
        }
        }
    };
}
