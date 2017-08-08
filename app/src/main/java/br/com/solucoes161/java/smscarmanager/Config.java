package br.com.solucoes161.java.smscarmanager;

/**
 * Created by Melky on 18/01/2016.
 */
public class Config {
    private int TimerLoop;
    private String PhoneNumber;

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public Config(){
        TimerLoop = 30;
    }

    public int getTimerLoop() {
        return TimerLoop;
    }

    public void setTimerLoop(int timerLoop) {
        TimerLoop = timerLoop;
    }
}
