package io.github.salemlockwood.android.smscarmanager;

/**
 * Created by melky on 13/10/2015.
 */
public class Phones {
    private String phone;
    private String password;
    private int initialized;
    private int timezone;
    private int thisAdmin;
    private int thisPassword;
    private int cuttedOil;
    private int cuttedElec;
    private int mode;
    private String lastLocation;
    private int sosKey;

    public Phones(){
        super();
        this.initialized = 0;
        this.timezone = 0;
        this.thisAdmin = 0;
        this.thisPassword = 0;
        this.cuttedElec = 0;
        this.cuttedElec = 0;

        this.mode = 0;
    }

    public int getSosKey() {
        return sosKey;
    }

    public void setSosKey(int sosKey) {
        this.sosKey = sosKey;
    }

    public boolean isSosKey() {
        return sosKey == 0 ? false : true;
    }

    public void setSosKey(boolean sosKey) {
        this.sosKey = sosKey ? 1 : 0;
    }

    public String getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(String lastLocation) {
        this.lastLocation = lastLocation;
    }


    public int getInitialized() {
        return initialized;
    }

    public int getThisAdmin() {
        return thisAdmin;
    }

    public int getThisPassword() {
        return thisPassword;
    }

    public int getCuttedOil() {
        return cuttedOil;
    }

    public int getCuttedElec() {
        return cuttedElec;
    }

    public void setInitialized(int initialized) {

        this.initialized = initialized;
    }

    public void setThisAdmin(int thisAdmin) {
        this.thisAdmin = thisAdmin;
    }

    public void setThisPassword(int thisPassword) {
        this.thisPassword = thisPassword;
    }

    public void setCuttedOil(int cuttedOil) {
        this.cuttedOil = cuttedOil;
    }

    public void setCuttedElec(int cuttedElec) {
        this.cuttedElec = cuttedElec;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isInitialized() {
        return initialized == 0 ? false : true;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized == false ? 0 : 1;
    }

    public int getTimezone() {
        return timezone;
    }

    public void setTimezone(int timezone) {
        this.timezone = timezone;
    }

    public boolean isThisAdmin() {
        return thisAdmin == 0 ? false : true;
    }

    public void setThisAdmin(boolean thisAdmin) {
        this.thisAdmin = thisAdmin == false ? 0 : 1;
    }

    public boolean isThisPassword() {
        return thisPassword == 0 ? false : true;
    }

    public void setThisPassword(boolean thisPassword) {
        this.thisPassword = thisPassword == false ? 0 : 1;
    }

    public boolean isCuttedOil() {
        return cuttedOil == 0 ? false : true;
    }

    public void setCuttedOil(boolean cuttedOil) {
        this.cuttedOil = cuttedOil == false ? 0 : 1;
    }

    public boolean isCuttedElec() {
        return cuttedElec == 0 ? false : true;
    }

    public void setCuttedElec(boolean cuttedElec) {
        this.cuttedElec = cuttedElec == false ? 0 : 1;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
