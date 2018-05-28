package io.github.salemlockwood.android.smscarmanager;

/**
 * Created by Melky on 30/01/2016.
 */
public class Tracking {
    private int id;
    private String phone;
    private String name;
    private String snippetTexts;
    private String latitudes;
    private String longitudes;

    public String[] getSnippetTextsArray(){
        return StringHelper.convertStringToArray(snippetTexts);
    }

    public void setSnippetTexts(String[] snippetTexts){
        this.snippetTexts = StringHelper.convertArrayToString(snippetTexts);
    }
    public Double[] getLatitudesArray(){
        return StringHelper.convertStringToDoubleArray(latitudes);
    }
    public void setLatitudes(Double[] latitudes){
        this.latitudes = StringHelper.convertArrayToString(latitudes);
    }
    public Double[] getLongitudesArray(){
        return StringHelper.convertStringToDoubleArray(longitudes);
    }

    public void setLongitudes(Double[] longitudes){
        this.longitudes = StringHelper.convertArrayToString(longitudes);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSnippetTexts() {
        return snippetTexts;
    }

    public void setSnippetTexts(String snippetTexts) {
        this.snippetTexts = snippetTexts;
    }

    public String getLatitudes() {
        return latitudes;
    }

    public void setLatitudes(String latitudes) {
        this.latitudes = latitudes;
    }

    public String getLongitudes() {
        return longitudes;
    }

    public void setLongitudes(String longitudes) {
        this.longitudes = longitudes;
    }
}
