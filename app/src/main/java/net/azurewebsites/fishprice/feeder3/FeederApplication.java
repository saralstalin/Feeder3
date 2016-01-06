package net.azurewebsites.fishprice.feeder3;

import android.app.Application;

/**
 * Created by Saral on 01-01-2016.
 */
public class FeederApplication extends Application {

    private boolean isAllowedDevice;
    public boolean getIsAllowedDevice() {
        return isAllowedDevice;
    }
    public void setIsAllowedDevice(boolean isAllowed) {
        isAllowedDevice = isAllowed;
    }
}
