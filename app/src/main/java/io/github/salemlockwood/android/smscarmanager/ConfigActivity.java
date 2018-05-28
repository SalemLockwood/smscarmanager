package io.github.salemlockwood.android.smscarmanager;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.EditText;
import android.widget.Toast;

import br.com.solucoes161.java.smscarmanager.R;

public class ConfigActivity extends AppCompatActivity {
    private ConfigDao dao = null;
    private Config config = null;
    private EditText timerLoop = null;
    private EditText phoneNumber = null;
    private TelephonyManager tm = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        dao = new ConfigDao(getApplicationContext());
        config = dao.getConfig();
        timerLoop = (EditText) findViewById(R.id.edt_timerloop);
        phoneNumber = (EditText) findViewById(R.id.edt_phonenumber);
        tm = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);

        if(config!=null){
            if(config.getTimerLoop()>0){
                timerLoop.setText(String.valueOf(config.getTimerLoop()));
            }
            if(config.getPhoneNumber() != null){
                if(config.getPhoneNumber().length()>9) phoneNumber.setText(config.getPhoneNumber());
            } else {
                if(tm.getLine1Number()!=null){
                    if(tm.getLine1Number().length()>9) phoneNumber.setText(tm.getLine1Number());
                }
            }
        }
    }
    @Override
    public void onBackPressed(){
        if(timerLoop.getText().length()<2){
            timerLoop.setText("30");
            Toast.makeText(getApplicationContext(),"Tempo deve ser maior que 10 segundos.",Toast.LENGTH_LONG).show();
        } else {
            int time = Integer.parseInt(timerLoop.getText().toString());
            String num = phoneNumber.getText().toString();
            if(num.length()<10) Toast.makeText(getApplicationContext(),"Você deve inserir o número do telefone deste celular.\nDDD+Número.\nex: 6299999999",Toast.LENGTH_LONG).show();
            else {
                config = new Config();
                config.setTimerLoop(time);
                config.setPhoneNumber(num);
                dao.setConfig(config);
                super.onBackPressed();
            }
        }
    }
}
