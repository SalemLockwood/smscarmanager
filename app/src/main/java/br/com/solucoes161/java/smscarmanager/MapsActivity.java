package br.com.solucoes161.java.smscarmanager;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private HashMap<String,LatLng> lastPosition;
    private HashMap<String,ArrayList<LatLng>> allPositionHash;
    private HashMap<String,ArrayList<String>> allSnippetTextsHash;
    private Time lastTime;
    private GoogleMap mMap;
    static boolean runing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.setRetainInstance(true);
        runing = true;
        registerReceiver(smsRec, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
        lastPosition = new HashMap<>();
        allPositionHash = new HashMap<>();
        allSnippetTextsHash = new HashMap<>();
    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null) {
            String[] keys = savedInstanceState.getStringArray("phonekeys");
            for(String key : keys){
                double[] savedPosition = savedInstanceState.getDoubleArray("l"+key);
                lastPosition.put(key,(new LatLng(savedPosition[0],savedPosition[1])));
                ArrayList<String> allPositionSaved = savedInstanceState.getStringArrayList("p"+key);
                ArrayList<LatLng> allPositionLatLngSaved = new ArrayList<>();
                for(String position : allPositionSaved){
                    double lat = Double.parseDouble(position.substring(0,position.indexOf(',')));
                    double lng = Double.parseDouble(position.substring(position.indexOf(',')+1));
                    allPositionLatLngSaved.add(new LatLng(lat,lng));
                }
                allPositionHash.put(key,allPositionLatLngSaved);
                String[] savedSnippetTextsHash = savedInstanceState.getStringArray("s"+key);
                ArrayList<String> savedSnippetTextsHashList = new ArrayList<>();
                for(String snippet : savedSnippetTextsHash){
                    savedSnippetTextsHashList.add(snippet);
                }
                allSnippetTextsHash.put(key,savedSnippetTextsHashList);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        String[] keys = new String[allPositionHash.keySet().size()];
        int aux = 0;
        for(String key : allPositionHash.keySet()){
            double[] lastLocation = new double[]{lastPosition.get(key).latitude,lastPosition.get(key).longitude};
            outState.putDoubleArray("l"+key,lastLocation);
            ArrayList<String> allPositionSaved = new ArrayList<>();
            for(LatLng latLng : allPositionHash.get(key)){
                allPositionSaved.add(String.valueOf(latLng.latitude)+","+String.valueOf(latLng.longitude));
            }
            outState.putStringArrayList("p"+key,allPositionSaved);
            String[] allSnippetSaved = new String[allSnippetTextsHash.get(key).size()];
            int aux2 = 0;
            for(String snippet : allSnippetTextsHash.get(key)){
                allSnippetSaved[aux] = snippet;
                aux2++;
            }
            outState.putStringArray("s"+key,allSnippetSaved);
            keys[aux] = key;
            aux++;
        }
        outState.putStringArray("phonekeys",keys);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop(){
        runing = false;
        unregisterReceiver(smsRec);
        super.onStop();
    }
    @Override
    public void onRestart(){
        runing = true;
        registerReceiver(smsRec, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
        super.onRestart();
    }

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
        if(lastPosition.isEmpty()) {
            LatLng goiania = new LatLng(-16.6845352, -49.3012962);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(goiania, 12f));
            String url = getIntent().getExtras().getString("url");
            Phones phone = new PhonesDao(getApplicationContext()).select(getIntent().getExtras().getString("phone"));
            setNewMarker(url,phone);
        }
    }
    BroadcastReceiver smsRec = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            SmsMessage[] smss;
            if(b!=null){
                Object[] pdus = (Object[]) b.get("pdus");
                smss = new SmsMessage[pdus.length];
                for(int i=0;i<pdus.length;i++) smss[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                for(SmsMessage sms : smss){
                    List<Phones> phones = new PhonesDao(context).selectAll();
                    for(Phones phone : phones){
                        if(sms.getOriginatingAddress().contains(phone.getPhone().substring(2))){
                            if(sms.getMessageBody().contains("http://maps.google.com/maps")){
                                String url = sms.getMessageBody().substring(sms.getMessageBody().indexOf("http://maps.google.com/maps"));
                                setNewMarker(url,phone);
                            }
                        }
                    }
                }
            }
        }
    };
    public void setNewMarker(String url,Phones phone){
        float speed = 0.0f;
        String sspeed = url;
        Time time = new Time();
        time.setToNow();
        String stime = url;
        String sdate = url;
        if(stime.contains("Time:")){
            try {
                Pattern patternTime = Pattern.compile("(?<=Time:(\\s)?)(\\d\\d:\\d\\d:\\d\\d)");
                Matcher mt = patternTime.matcher(stime);
                if (mt.find()) {
                    String timeFound = mt.group();
                    int sec = Integer.parseInt(timeFound.substring(timeFound.lastIndexOf(':') + 1));
                    int min = Integer.parseInt(timeFound.substring(timeFound.indexOf(':') + 1, timeFound.lastIndexOf(':')));
                    int hou = Integer.parseInt(timeFound.substring(0, timeFound.indexOf(':')));
                    if ((sec > -1 && sec < 60) && (min > -1 && min < 60) && (hou > -1 && hou < 24)) {
                        if(sdate.contains("Date:")){
                            try {
                                Pattern patternDate = Pattern.compile("(?<=Date:(\\s)?)(\\d\\d/\\d\\d/\\d\\d)");
                                Matcher md = patternDate.matcher(sdate);
                                if (md.find()) {
                                    String dateFound = md.group();
                                    int day = Integer.parseInt(dateFound.substring(dateFound.lastIndexOf('/') + 1));
                                    int mon = Integer.parseInt(dateFound.substring(dateFound.indexOf('/') + 1, dateFound.lastIndexOf('/')));
                                    int yea = Integer.parseInt(dateFound.substring(0, dateFound.indexOf('/'))) + 2000;
                                    if ((day > 0 && day < 32) && (mon > 0 && mon < 13) && (yea > 1999)) {
                                        time.set(sec, min, hou, day, mon, yea);
                                    }
                                }
                            } catch(NumberFormatException e){
                                Log.e(MapsActivity.class.getCanonicalName(),e.getMessage());
                            }
                        }
                    }
                }
            } catch(NumberFormatException e){
                Log.e(MapsActivity.class.getCanonicalName(),e.getMessage());
            }
        }

        if(sspeed.contains("Speed:")){
            try {
                Pattern patternSpeed = Pattern.compile("(?<=Speed:(\\s)?)(\\d+.\\d+)(?=\\skm/h)");
                Matcher ms = patternSpeed.matcher(sspeed);
                if(ms.find())
                    speed = Float.valueOf(ms.group());
            } catch(NumberFormatException e){
                //
            }
        }
        Pattern patternCoordinates = Pattern.compile("(N|S|-)?(\\d+\\.\\d+,)(W|E|-)?(\\d+\\.\\d+)");
        Matcher mc = patternCoordinates.matcher(url);
        if(mc.find()) {
            String coordinatesFound = mc.group();
            String slat = coordinatesFound.substring(0, coordinatesFound.indexOf(','));
            slat = slat.replace('S', '-');
            slat = slat.replaceAll("N", "");
            String slng = coordinatesFound.substring(coordinatesFound.indexOf(',') + 1);
            slng = slng.replace('W', '-');
            slng = slng.replaceAll("E", "");
            try {
                Double lat = Double.valueOf(slat);
                Double lng = Double.valueOf(slng);
                LatLng newPosition = new LatLng(lat, lng);
                if (lat != 0.0) {
                    if (lastPosition.get(phone.getPhone()) != null) {
                        float[] results = new float[]{0};
                        Location.distanceBetween(lastPosition.get(phone.getPhone()).latitude, lastPosition.get(phone.getPhone()).longitude, newPosition.latitude, newPosition.longitude, results);
                        if (results[0] > 10f) {
                            if(speed==0) speed = speedBetween(lastPosition.get(phone.getPhone()),newPosition,lastTime,time);
                            MarkerOptions mo = new MarkerOptions().position(newPosition).title(phone.getPhone());
                            String line1 = "Combustível: " + (phone.isCuttedOil() ? "Cortado" : "Ligado");
                            String line2 = "Eletricidade: " + (phone.isCuttedElec() ? "Cortado" : "Ligado");
                            String line3 = "Velocidade: " + (new DecimalFormat("#.##").format(speed)) + " km/h";
                            String line4 = "Data: " + time.format("%d/%m/%Y %H:%M:%S");
                            String snippet = line1 + "\n" + line2 + "\n" + line3 + "\n" + line4;
                            mo.snippet(snippet);
                            ArrayList<String> allSnippetTexts = allSnippetTextsHash.get(phone.getPhone());
                            allSnippetTexts.add(snippet);
                            allSnippetTextsHash.put(phone.getPhone(), allSnippetTexts);
                            mo.icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker));
                            mMap.addPolyline(new PolylineOptions().add(lastPosition.get(phone.getPhone()), newPosition).color(Color.RED));
                            mMap.addMarker(mo);
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(newPosition));
                        }
                    } else {
                        MarkerOptions mo = new MarkerOptions().position(newPosition).title(phone.getPhone());
                        String line1 = "Combustível: " + (phone.isCuttedOil() ? "Cortado" : "Ligado");
                        String line2 = "Eletricidade: " + (phone.isCuttedElec() ? "Cortado" : "Ligado");
                        String line3 = "Velocidade: " + (new DecimalFormat("#.##").format(speed)) + " km/h";
                        String line4 = "Data:" + time.format("%d/%m/%Y %H:%M:%S");
                        String snippet = line1 + "\n" + line2 + "\n" + line3 + "\n" + line4;
                        mo.snippet(snippet);
                        ArrayList<String> allSnippetTexts = allSnippetTextsHash.get(phone.getPhone());
                        if(allSnippetTexts==null) allSnippetTexts = new ArrayList<String>();
                        allSnippetTexts.add(snippet);
                        allSnippetTextsHash.put(phone.getPhone(), allSnippetTexts);
                        mo.icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker));
                        mMap.addMarker(mo);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newPosition, 17f));
                    }
                }
                lastPosition.put(phone.getPhone(),newPosition);
                ArrayList<LatLng> allPosition = allPositionHash.get(phone.getPhone());
                if(allPosition == null) allPosition = new ArrayList<LatLng>();
                allPosition.add(newPosition);
                allPositionHash.put(phone.getPhone(),allPosition);
                lastTime = time;
            } catch (NumberFormatException e) {
                Log.e(MapsActivity.class.getCanonicalName(), "Error: " + e.getMessage() + "\nLat: " + slat + " - Lng: " + slng + "\n\n\n\n\n");
            }
        }
    }

    private float speedBetween(LatLng p1, LatLng p2, Time t1, Time t2) {
        float[] results = new float[]{0};
        float speed = 0;
        Location.distanceBetween(p1.latitude, p1.longitude, p2.latitude, p2.longitude, results);
        if(results[0]>0){
            float distanceMeters = results[0];
            long diffInMillis = Math.abs(t1.toMillis(true) - t2.toMillis(true));
            float timeSecs = TimeUnit.MILLISECONDS.toSeconds(diffInMillis);
            speed = (distanceMeters / timeSecs) * 3.6f;
        }
        return speed;
    }

    @Override
    public void onBackPressed(){
        if(!allSnippetTextsHash.isEmpty()&&!allPositionHash.isEmpty()){
            for(String key : allSnippetTextsHash.keySet()){
                if(allSnippetTextsHash.containsKey(key)&&allPositionHash.containsKey(key)){
                    ArrayList<LatLng> positions = allPositionHash.get(key);
                    Double[] latitudes = new Double[positions.size()];
                    Double[] longitudes = new Double[positions.size()];
                    String[] snippets = new String[positions.size()];
                    int aux = 0;
                    for(LatLng pos : positions){
                        latitudes[aux] = pos.latitude;
                        longitudes[aux] = pos.longitude;
                        aux++;
                    }
                    aux = 0;
                    for(String snip : allSnippetTextsHash.get(key)){
                        snippets[aux] = snip;
                        aux++;
                    }
                    final Tracking trk = new Tracking();
                    trk.setLatitudes(latitudes);
                    trk.setLongitudes(longitudes);
                    trk.setPhone(key);
                    trk.setSnippetTexts(snippets);
                    final Dialog trk_name_dialog = new Dialog(MapsActivity.this);
                    trk_name_dialog.setContentView(R.layout.dialog_name_tracking);
                    trk_name_dialog.setTitle("Nome do Rastreamento");
                    TextView txv_tracking_name = (TextView) trk_name_dialog.findViewById(R.id.txv_tracking_name);
                    txv_tracking_name.setText("Digite um nome para salvar o rastreamento do telefone " + key);
                    final EditText edt_tracking_name = (EditText) trk_name_dialog.findViewById(R.id.edt_tracking_name);
                    Time time = new Time();
                    time.setToNow();
                    edt_tracking_name.setText(time.format("%d/%m/%Y %H:%M:%S") + " " + key);
                    Button btn_tracking_name = (Button) trk_name_dialog.findViewById(R.id.btn_tracking_name);
                    btn_tracking_name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            trk_name_dialog.dismiss();
                            trk.setName(edt_tracking_name.getText().toString());
                            new TrackingDao(MapsActivity.this).insert(trk);
                            MapsActivity.super.onBackPressed();
                        }
                    });
                    Button btn_tracking_cancel = (Button) trk_name_dialog.findViewById(R.id.btn_tracking_cancel);
                    btn_tracking_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MapsActivity.super.onBackPressed();
                        }
                    });
                    trk_name_dialog.show();
                }
            }
        }
    }
}
