package io.github.salemlockwood.android.smscarmanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import br.com.solucoes161.java.smscarmanager.R;

public class CommandsSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commands_settings);
        Commands commands;
        CommandsDao dao = new CommandsDao(getApplicationContext());
        commands = dao.getConfiguration();
        if(commands != null) {
            ((EditText) findViewById(R.id.edt_locate_cmd)).setText(commands.getLOC_CMD());
            ((EditText) findViewById(R.id.edt_cut_oil_cmd)).setText(commands.getCUT_OIL_CMD());
            ((EditText) findViewById(R.id.edt_sup_oil_cmd)).setText(commands.getSUP_OIL_CMD());
            ((EditText) findViewById(R.id.edt_cut_elec_cmd)).setText(commands.getCUT_ELEC_CMD());
            ((EditText) findViewById(R.id.edt_sup_elec_cmd)).setText(commands.getSUP_ELEC_CMD());
            ((EditText) findViewById(R.id.edt_trk_cmd)).setText(commands.getTRK_CMD());
            ((EditText) findViewById(R.id.edt_ltn_cmd)).setText(commands.getLTN_CMD());
            ((EditText) findViewById(R.id.edt_sos_key_on_cmd)).setText(commands.getSOS_KEY_ON_CMD());
            ((EditText) findViewById(R.id.edt_sos_key_off_cmd)).setText(commands.getSOS_KEY_OFF_CMD());
        }
        TextView txv_password = (TextView) findViewById(R.id.txv_password);
        TextView txv_phone = (TextView) findViewById(R.id.txv_phone);
        txv_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View cv = CommandsSettingsActivity.this.getCurrentFocus();
                if(cv.getClass().getCanonicalName().contains("EditText"))
                    ((EditText) cv).setText(((EditText) cv).getText()+"{password}");
            }
        });
        txv_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View cv = CommandsSettingsActivity.this.getCurrentFocus();
                if(cv.getClass().getCanonicalName().contains("EditText"))
                    ((EditText) cv).setText(((EditText) cv).getText()+"{phone}");
            }
        });
    }

    @Override
    public void onBackPressed(){
        Commands commands = new Commands();
        CommandsDao dao = new CommandsDao(getApplicationContext());
        commands.setLOC_CMD(((EditText) findViewById(R.id.edt_locate_cmd)).getText().toString());
        commands.setCUT_OIL_CMD(((EditText) findViewById(R.id.edt_cut_oil_cmd)).getText().toString());
        commands.setSUP_OIL_CMD(((EditText) findViewById(R.id.edt_sup_oil_cmd)).getText().toString());
        commands.setCUT_ELEC_CMD(((EditText) findViewById(R.id.edt_cut_elec_cmd)).getText().toString());
        commands.setSUP_ELEC_CMD(((EditText) findViewById(R.id.edt_sup_elec_cmd)).getText().toString());
        commands.setTRK_CMD(((EditText) findViewById(R.id.edt_trk_cmd)).getText().toString());
        commands.setLTN_CMD(((EditText) findViewById(R.id.edt_ltn_cmd)).getText().toString());
        commands.setSOS_KEY_ON_CMD(((EditText) findViewById(R.id.edt_sos_key_on_cmd)).getText().toString());
        commands.setSOS_KEY_OFF_CMD(((EditText) findViewById(R.id.edt_sos_key_off_cmd)).getText().toString());
        dao.insert(commands);
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_commands, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id){
            case R.id.mit_gt06:
                updateCommandsWith("GT06");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateCommandsWith(String device) {
        if(device == "GT06") {
            ((EditText) findViewById(R.id.edt_locate_cmd)).setText("#locate#");
            ((EditText) findViewById(R.id.edt_cut_oil_cmd)).setText("#cutoil#{password}#");
            ((EditText) findViewById(R.id.edt_sup_oil_cmd)).setText("#startoil#{password}#");
            ((EditText) findViewById(R.id.edt_cut_elec_cmd)).setText("#cutelec#{password}#");
            ((EditText) findViewById(R.id.edt_sup_elec_cmd)).setText("#startelec#{password}#");
            ((EditText) findViewById(R.id.edt_trk_cmd)).setText("#track#");
            ((EditText) findViewById(R.id.edt_ltn_cmd)).setText("#monitor#");
            ((EditText) findViewById(R.id.edt_sos_key_on_cmd)).setText("#alarmkey#{password}#");
            ((EditText) findViewById(R.id.edt_sos_key_off_cmd)).setText("#stopalarm#{password}#");
        }
    }
}
