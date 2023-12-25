package edu.ubbcluj.cs.groutes.generalhelper;

import android.content.res.Resources;

import com.google.maps.model.TravelMode;

import java.util.Map;

import edu.ubbcluj.cs.groutes.R;
import edu.ubbcluj.cs.groutes.model.SettingEntity;
import io.grpc.internal.SharedResourceHolder;

public class SettingsManager {
    private static SettingsManager settingsManager;

    public SettingEntity getSettingEntity() {
        return settingEntity;
    }

    private SettingEntity settingEntity;

    public TravelMode getTravelMode() {
        return travelMode;
    }

    public void setTravelMode(TravelMode travelMode) {
        this.travelMode = travelMode;
    }

    private TravelMode travelMode = TravelMode.WALKING;

    public static SettingsManager getInstance() {
        if (settingsManager == null) {
            settingsManager = new SettingsManager();
        }

        return settingsManager;
    }

    private SettingsManager() {
        settingEntity = new SettingEntity();
    }

    public String initSettings(String key){
        String result = "";
        int res = 0;

        switch (key) {
            case "Use exact algorithm until given number of nodes":
                res = settingEntity.getConfUseExactUntil();
                break;
            case "Travel mode":
                res = settingEntity.getConfTravelMode();
                break;
            case "Default map zoom":
                res = settingEntity.getConfZoom();
                break;
            case "GPS positioning interval":
                res = settingEntity.getConfPositionInterval();
                break;
            case "Auto zoom interval":
                res = settingEntity.getConfZoomInterval();
                break;
            case "Travel history size":
                res = settingEntity.getConfNavHistory();
                break;
            case "Language":
                res = settingEntity.getConfLang();
                break;
            case "Search departure time":
                res = settingEntity.getConfDirectionTime();
                break;
            case "Calculate route based on":
                res = settingEntity.getConfDistanceTime();
                break;
        }

        result = Integer.toString(res);

        return result;
    }

    public void updateSetting(String key, int value) {
        int c = key.lastIndexOf(" ");
        boolean found = true;

        if (c != -1) {
            key = key.substring(0, c);
        }

        switch (key) {
            case "Use exact algorithm until given number of nodes":
                settingEntity.setConfUseExactUntil(value);
                break;
            case "Travel mode":
                settingEntity.setConfTravelMode(value);
                break;
            case "Default map zoom":
                settingEntity.setConfZoom(value);
                break;
            case "GPS positioning interval":
                settingEntity.setConfPositionInterval(value);
                break;
            case "Auto zoom interval":
                settingEntity.setConfZoomInterval(value);
                break;
            case "Travel history size":
                settingEntity.setConfNavHistory(value);
                break;
            case "Language":
                settingEntity.setConfLang(value);
                break;
            case "Search departure time":
                settingEntity.setConfDirectionTime(value);
                break;
            case "Calculate route based on":
                settingEntity.setConfDistanceTime(value);
                break;
            default:
                found = false;
                break;
        }

        if (found) {
            // update sett database
            CacheManager.getInstance().updateSettings(settingEntity);
        }
    }

    public void setEntity(Map<String, Object> o){
        settingEntity = new SettingEntity(o);
    }
}
