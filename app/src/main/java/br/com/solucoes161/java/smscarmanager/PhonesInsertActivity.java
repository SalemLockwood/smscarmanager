package br.com.solucoes161.java.smscarmanager;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.github.lzyzsd.circleprogress.CircleProgress;

import java.util.ArrayList;
import java.util.List;

public class PhonesInsertActivity extends AppCompatActivity {
    private String telefone = "";
    private String oldPassword = "123456";
    private Phones DBPhone = null;
    PhonesController control;
    EditText edt_phone = null;
    EditText edt_password = null;
    TextSwitcher switcher = null;
    CircleProgress cprogress = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phones_insert);

        edt_phone = (EditText) findViewById(R.id.edt_phone_add);
        edt_password = (EditText) findViewById(R.id.edt_password_add);
        switcher = (TextSwitcher) findViewById(R.id.textSwitcher);
        switcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView txv = new TextView(getApplicationContext());
                txv.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                txv.setTextColor(Color.WHITE);
                return txv;
            }
        });
        switcher.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(),android.R.anim.slide_in_left));
        switcher.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(),android.R.anim.slide_out_right));
        cprogress = (CircleProgress) findViewById(R.id.circle_progress);
        cprogress.setMax(100);
        cprogress.setFinishedColor(Color.BLUE);

        Button add_phone = (Button) findViewById(R.id.btn_add_phone_insert);
        Button del_phone = (Button) findViewById(R.id.btn_del_phone_insert);
        DBPhone = new Phones();
        Bundle extras = getIntent().getExtras();
        if(extras != null && !(extras.getString("Telefone").isEmpty())){
            telefone = extras.getString("Telefone");
            DBPhone = (new PhonesDao(getApplicationContext())).select(telefone);
            edt_phone.setText(DBPhone.getPhone());
            edt_password.setText(DBPhone.getPassword());
            oldPassword = DBPhone.getPassword();
        } else {
            DBPhone.setPhone(telefone);
            DBPhone.setPassword("123456");
            del_phone.setVisibility(View.GONE);
        }

        add_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adicionaTelefone();
            }
        });

        del_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletaTelefone();
            }
        });
    }

    private void deletaTelefone() {
        PhonesDao dao = new PhonesDao(getApplicationContext());
        new PhonesController(DBPhone).beginDevice(DBPhone);
        new PhonesController(DBPhone).setNoAdmin(DBPhone,new ConfigDao(getApplicationContext()).getConfig().getPhoneNumber());
        dao.delete(edt_phone.getText().toString());
        super.onBackPressed();
    }

    private void adicionaTelefone() {
        if(edt_phone.getText().toString().length()>10){
            if(edt_password.getText().toString().length()==6){
                final Phones p = new Phones();
                final PhonesDao dao = new PhonesDao(getApplicationContext());
                p.setPassword(edt_password.getText().toString());
                p.setPhone(edt_phone.getText().toString());
                control = new PhonesController(p);
                //registerReceiver(control, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
                final String[] msgs = new String[] {"Enviando SMS de inicialização.","Enviando SMS de timezone.","Enviando SMS de Password.","Enviando SMS de Administrador."};
                if(telefone.isEmpty()) {
                    switcher.setVisibility(View.VISIBLE);
                    cprogress.setVisibility(View.VISIBLE);
                    Handler hbd = new Handler();
                    Handler htz = new Handler();
                    Handler hcp = new Handler();
                    Handler had = new Handler();
                    hbd.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            switcher.setText(msgs[0]);
                            control.beginDevice(p);
                            cprogress.setProgress(25);
                        }
                    },0);
                    htz.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            switcher.setText(msgs[1]);
                            control.setTimezone(p);
                            cprogress.setProgress(50);
                        }
                    },3000);
                    hcp.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            switcher.setText(msgs[2]);
                            control.changePassword(p, oldPassword);
                            cprogress.setProgress(75);
                        }
                    },6000);
                    had.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            switcher.setText(msgs[3]);
                            control.setAdmin(p, (new ConfigDao(getApplicationContext()).getConfig().getPhoneNumber()));
                            cprogress.setProgress(100);
                            PhonesInsertActivity.this.onBackPressed();
                        }
                    }, 9000);
                    dao.insert(p);
                }
                else {
                    control.changePassword(p, oldPassword);
                    p.setThisPassword(false);
                    dao.update(p);
                }
            }
        } else {
            AlertDialog alert = new AlertDialog(this,"Telefone ou Senha inválidos","Telefone deve conter 11 ou 12 dígitos (0+DDD+Telefone).\nSenha deve conter 6 digitos.");
            alert.show();
        }
    }
}
