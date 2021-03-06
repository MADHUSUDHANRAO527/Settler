package app.mobile.settler.events;

import java.util.ArrayList;

import app.mobile.settler.models.MapStoresModel;

/**
 * Created by Madhu on 30/07/17.
 */

public class MapStoresHomeEvent {
    public final boolean success;
    public final int errorCode;
    public ArrayList<MapStoresModel> mapsStoreModel;
    public String msg;

    public MapStoresHomeEvent(boolean success, int errorCode, ArrayList<MapStoresModel> model) {
        this.success = success;
        this.errorCode = errorCode;
        this.mapsStoreModel = model;
    }

    public MapStoresHomeEvent(boolean success, int errorCode, String message) {
        this.success = success;
        this.errorCode = errorCode;
        this.msg = message;
    }
}
