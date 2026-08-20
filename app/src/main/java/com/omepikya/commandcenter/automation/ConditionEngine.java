package com.omepikya.commandcenter.automation;

import android.content.Context;
import android.os.BatteryManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConditionEngine {
    private final Context context;
    public ConditionEngine(Context context){this.context=context.getApplicationContext();}
    public boolean batteryBelow(int percent){
        BatteryManager bm=(BatteryManager)context.getSystemService(Context.BATTERY_SERVICE);
        if(bm==null)return false; int level=bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY); return level>=0&&level<percent;
    }
    public boolean wifiConnected(){
        ConnectivityManager cm=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm==null)return false; NetworkInfo n=cm.getActiveNetworkInfo(); return n!=null&&n.isConnected()&&n.getType()==ConnectivityManager.TYPE_WIFI;
    }
}
