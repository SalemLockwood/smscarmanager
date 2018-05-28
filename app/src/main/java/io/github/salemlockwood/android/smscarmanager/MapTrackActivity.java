package io.github.salemlockwood.android.smscarmanager;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import br.com.solucoes161.java.smscarmanager.R;

public class MapTrackActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Tracking tracking;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_track);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                Context context = getApplicationContext(); //or getActivity(), YourActivity.this, etc.

                LinearLayout info = new LinearLayout(context);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(context);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(context);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
        Bundle extra = getIntent().getExtras();
        if(extra!=null){
            int id = (int) extra.get("id");
            if(id>-1){
                tracking = new TrackingDao(getApplicationContext()).select(id);
                for(int aux=0;aux<tracking.getSnippetTextsArray().length;aux++){
                    MarkerOptions mo = new MarkerOptions().title(tracking.getPhone()).position(new LatLng(tracking.getLatitudesArray()[aux],tracking.getLongitudesArray()[aux]));
                    mo.snippet(tracking.getSnippetTextsArray()[aux]);
                    mo.icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker));
                    mMap.addMarker(mo);
                    if(aux>0){
                        mMap.addPolyline(new PolylineOptions().add(new LatLng(tracking.getLatitudesArray()[aux-1],tracking.getLongitudesArray()[aux-1]),new LatLng(tracking.getLatitudesArray()[aux],tracking.getLongitudesArray()[aux])).color(Color.RED));
                    }
                }
            }
        } else {
            MapTrackActivity.this.onBackPressed();
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(tracking.getLatitudesArray()[0],tracking.getLongitudesArray()[0]),17f));
        Button btn_delete_tracking = (Button) findViewById(R.id.btn_delete_tracking);
        btn_delete_tracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TrackingDao(getApplicationContext()).delete(tracking.getId());
                MapTrackActivity.this.onBackPressed();
            }
        });
    }
}
