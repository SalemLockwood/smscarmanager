package io.github.salemlockwood.android.smscarmanager;

import android.drm.DrmManagerClient;
import android.provider.Telephony;

/**
 * Created by Melky on 22/01/2016.
 */
public class SMSEvent {
    public interface SMSEventListener{
        public void onSMSReceive(Telephony.Sms sms);
    }
}
