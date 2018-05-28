package io.github.salemlockwood.android.smscarmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import br.com.solucoes161.java.smscarmanager.R;

public class TracksActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracks);

        LinearLayout ll_tracks = (LinearLayout) findViewById(R.id.ll_tracks);
        TrackingDao dao = new TrackingDao(getApplicationContext());
        for(final Tracking t : dao.selectAll()){
            Button btn = new Button(getApplicationContext());
            btn.setBackground(getResources().getDrawable(R.drawable.button_bg));
            btn.setText(t.getName());
            btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chamaMapTrackComId(t.getId());
                }
            });
            ll_tracks.addView(btn);
        }
    }

    private void chamaMapTrackComId(int id) {
        Intent i = new Intent(getApplicationContext(),MapTrackActivity.class);
        i.putExtra("id",id);
        startActivityForResult(i,1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //SecondActivity closed
        if(requestCode == 1){
            this.recreate();
        }
    }
}
