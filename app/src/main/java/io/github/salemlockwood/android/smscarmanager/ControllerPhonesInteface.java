package io.github.salemlockwood.android.smscarmanager;

/**
 * Created by Melky on 22/01/2016.
 */
public interface ControllerPhonesInteface {
    public void beginDevice(Phones p);
    public void changePassword(Phones p, String oldPassword);
    public void setTimezone(Phones p);
    public void setAdmin(Phones p, String ap);
    public void setAPN(Phones p, String content, String user, String pass);
    public void setIP(Phones p, String ip, int port);
    public void setAutoTrackTime(Phones p, int time);
    public void simpleLocation(Phones p);
    public void trackLocation(Phones p);
    public void oilManage(Phones p);
    public void ElecManage(Phones p);
    public void deviceMode(Phones P, int mode); // 0 for tracker, 1 for monitor, 2 for call
    public void callDevice(Phones p);
    public void setNoAdmin(Phones p, String phone);
}
