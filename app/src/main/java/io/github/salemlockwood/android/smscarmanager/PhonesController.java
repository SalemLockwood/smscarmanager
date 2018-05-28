package io.github.salemlockwood.android.smscarmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Melky on 22/01/2016.
 */
public class PhonesController extends BroadcastReceiver implements ControllerPhonesInteface{
    private SMSEvent smsEvent;
    private Phones phone = null;
    private PhonesDao pdao = null;
    private boolean beginOk = false;
    private boolean passwordOk = false;
    private boolean timezoneOk = false;
    private boolean adminOk = false;

    public PhonesController(Phones phone){
        super();
        this.phone = phone;
    }

    public PhonesController(PhonesDao pdao){
        super();
        this.pdao = pdao;
    };

    @Override
    public void beginDevice(Phones p) {
        sendSMS(p.getPhone(), "#begin#123456#");
    }

    @Override
    public void changePassword(Phones p, String oldPassword) {
        String[] var = {"\\{oldpassword\\}","\\{newpassword\\}"};
        String[] val = {oldPassword,p.getPassword()};
        String message = "#password#{oldpassword}#{newpassword}#";
        sendSMS(p.getPhone(), replaceVars(var, val, message));
    }

    @Override
    public void setTimezone(Phones p) {
        String message = "#timezone#123456#W#3#00#";
        sendSMS(p.getPhone(),message);
    }

    @Override
    public void setAdmin(Phones p, String ap) {
        String[] var = {"\\{password\\}","\\{phone\\}"};
        String[] val = {p.getPassword(),ap};
        String message = "#admin#{password}#{phone}#";
        sendSMS(p.getPhone(),replaceVars(var,val,message));
    }

    @Override
    public void setAPN(Phones p, String content, String user, String pass) {
        String[] var = {"\\{password\\}","\\{content\\}","\\{user\\}","\\{pass\\}"};
        String[] val = {p.getPassword(),content,user,pass};
        String message = "#APN#{password}#{content}#{user}#{pass}#";
        sendSMS(p.getPhone(),replaceVars(var,val,message));
    }

    @Override
    public void setIP(Phones p, String ip, int port) {
        String[] var = {"\\{password\\}","\\{ip\\}","\\{port\\}"};
        String[] val = {p.getPassword(), ip, String.valueOf(port)};
        String message = "#IP#{password}#{ip}#{port}#";
        sendSMS(p.getPhone(),replaceVars(var,val,message));
    }

    @Override
    public void setAutoTrackTime(Phones p, int time) {
        String[] var = {"\\{password\\}","\\{time\\}"};
        String[] val = {p.getPassword(),String.valueOf(time)};
        String message = "#AT#{password}#{time}#00#";
        sendSMS(p.getPhone(),replaceVars(var,val,message));
    }

    @Override
    public void simpleLocation(Phones p) {

    }

    @Override
    public void trackLocation(Phones p) {

    }

    @Override
    public void oilManage(Phones p) {

    }

    @Override
    public void ElecManage(Phones p) {

    }

    @Override
    public void deviceMode(Phones P, int mode) {

    }

    @Override
    public void callDevice(Phones p) {

    }

    @Override
    public void setNoAdmin(Phones p, String phone) {
        String message = "#noadmin#123456#"+phone+"#";
        sendSMS(p.getPhone(),message);

    }

    private void sendSMS(String phone, String message){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phone,null,message,null,null);
    }

    private String replaceVars(String[] listVars, String[] listVals, String text){
        if(listVars.length == listVals.length){
            for(int i=0;i<listVars.length;i++){
               text = text.replaceAll(listVars[i],listVals[i]);
            }
        }
        return text;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = {};
        String str = "";
        if(bundle != null){
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for(SmsMessage sms : msgs){
                List<Phones> list = new ArrayList<Phones>();
                if(this.phone==null)
                    if(this.pdao!=null)list = pdao.selectAll();
                else list.add(this.phone);
                for(Phones p : list){
                    if(sms.getOriginatingAddress().contains(p.getPhone().substring(3))){
                        tratarSMS(sms);
                    }
                }
            }
        }
    }

    private void tratarSMS(SmsMessage sms) {
        switch (sms.getMessageBody()){
            case "begin ok!":
                beginOk = true;
            case "change password ok":
                passwordOk = true;
            case "admin ok!":
                adminOk = true;
            case "set time zone OK!":
                timezoneOk = true;
        }
    }

    public void setSmsEvent(SMSEvent e){
        this.smsEvent = e;
    }
}
