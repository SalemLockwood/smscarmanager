package br.com.solucoes161.java.smscarmanager;

/**
 * Created by melky on 13/10/2015.
 */
public class Commands {
    private String LOC_CMD;
    private String CUT_OIL_CMD;
    private String SUP_OIL_CMD;
    private String CUT_ELEC_CMD;
    private String SUP_ELEC_CMD;
    private String TRK_CMD;
    private String LTN_CMD;
    private String SOS_KEY_ON_CMD;

    public String getSOS_KEY_ON_CMD() {
        return SOS_KEY_ON_CMD;
    }

    public void setSOS_KEY_ON_CMD(String SOS_KEY_ON_CMD) {
        this.SOS_KEY_ON_CMD = SOS_KEY_ON_CMD;
    }

    public String getSOS_KEY_OFF_CMD() {
        return SOS_KEY_OFF_CMD;
    }

    public void setSOS_KEY_OFF_CMD(String SOS_KEY_OFF_CMD) {
        this.SOS_KEY_OFF_CMD = SOS_KEY_OFF_CMD;
    }

    private String SOS_KEY_OFF_CMD;

    public String getLOC_CMD() {
        return LOC_CMD;
    }

    public void setLOC_CMD(String LOC_CMD) {
        this.LOC_CMD = LOC_CMD;
    }

    public String getCUT_OIL_CMD() {
        return CUT_OIL_CMD;
    }

    public void setCUT_OIL_CMD(String CUT_OIL_CMD) {
        this.CUT_OIL_CMD = CUT_OIL_CMD;
    }

    public String getSUP_OIL_CMD() {
        return SUP_OIL_CMD;
    }

    public void setSUP_OIL_CMD(String SUP_OIL_CMD) {
        this.SUP_OIL_CMD = SUP_OIL_CMD;
    }

    public String getCUT_ELEC_CMD() {
        return CUT_ELEC_CMD;
    }

    public void setCUT_ELEC_CMD(String CUT_ELEC_CMD) {
        this.CUT_ELEC_CMD = CUT_ELEC_CMD;
    }

    public String getSUP_ELEC_CMD() {
        return SUP_ELEC_CMD;
    }

    public void setSUP_ELEC_CMD(String SUP_ELEC_CMD) {
        this.SUP_ELEC_CMD = SUP_ELEC_CMD;
    }

    public String getTRK_CMD() {
        return TRK_CMD;
    }

    public void setTRK_CMD(String TRK_CMD) {
        this.TRK_CMD = TRK_CMD;
    }

    public String getLTN_CMD() {
        return LTN_CMD;
    }

    public void setLTN_CMD(String LTN_CMD) {
        this.LTN_CMD = LTN_CMD;
    }
}
