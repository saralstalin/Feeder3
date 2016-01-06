package net.azurewebsites.fishprice.feeder3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MainActivityStartUp extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){

                Intent i = new Intent(context, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context.startActivity(i);
            }
        }
    }
}
