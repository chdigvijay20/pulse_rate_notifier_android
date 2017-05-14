package com.test.digvijay.pulseratenotifier.notifications;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.telephony.SmsManager;

public class NotificationUtil {

    private static final String TAG = "NotificationUtil";
    public static SmsManager smsManager = SmsManager.getDefault();

    private final static String simSlotName[] = {
            "extra_asus_dial_use_dualsim",
            "com.android.phone.extra.slot",
            "slot",
            "simslot",
            "sim_slot",
            "subscription",
            "Subscription",
            "phone",
            "com.android.phone.DialingMode",
            "simSlot",
            "slot_id",
            "simId",
            "simnum",
            "phone_type",
            "slotId",
            "slotIdx"
    };

    public void sendSMS(String contact, String name) {
        smsManager.sendTextMessage(contact, null, "Emergency SMS from " + name, null, null);
    }

    public void call(Context context, String contact) {
        Intent intent = getCallingIntent(contact);
        context.startActivity(intent);
    }

    public static Intent getCallingIntent(String contact) {

        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("com.android.phone.force.slot", true);
        intent.putExtra("Cdma_Supp", true);

        for (String s : simSlotName) {
            intent.putExtra(s, 0);
        }

        intent.setData(Uri.parse("tel:" + contact));

        return intent;
    }

    public boolean isCallActive(Context context){
        AudioManager manager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        if(manager.getMode()==AudioManager.MODE_IN_CALL){
            return true;
        }
        else{
            return false;
        }
    }
}
