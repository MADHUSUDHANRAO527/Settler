package app.mobile.settler.utilities;

import java.util.ArrayList;

import app.mobile.settler.models.MapStoresModel;

/**
 * Created by madhu on 28/6/17.
 */

public class SettlerSingleton {
    private static final SettlerSingleton instance = new SettlerSingleton();
    private String myCurrentAddress;
    private Double mycurrentLatitude;
    private Double mycurrentLongitude;
    private ArrayList<MapStoresModel> cartModelList;

    public ArrayList<MapStoresModel> getSetOffersDataModel() {
        return setOffersDataModel;
    }

    public void setSetOffersDataModel(ArrayList<MapStoresModel> setOffersDataModel) {
        this.setOffersDataModel = setOffersDataModel;
    }

    private ArrayList<MapStoresModel> setOffersDataModel;


    public ArrayList<MapStoresModel> getCartModelList() {
        return cartModelList;
    }

    public void setCartModelList(ArrayList<MapStoresModel> cartModelList) {
        this.cartModelList = cartModelList;
    }

    public String getUserSelectedTime() {
        return userSelectedTime;
    }

    public void setUserSelectedTime(String userSelectedTime) {
        this.userSelectedTime = userSelectedTime;
    }

    private String userSelectedTime;


    private Double setUserSelectedLatitude;


    public static SettlerSingleton getInstance() {
        return instance;
    }

    public String getMyCurrentAddress() {
        return myCurrentAddress;
    }

    public void setMyCurrentAddress(String myCurrentAddress) {
        this.myCurrentAddress = myCurrentAddress;
    }

    public Double getMycurrentLatitude() {
        return mycurrentLatitude;
    }

    public void setMycurrentLatitude(Double mycurrentLatitude) {
        this.mycurrentLatitude = mycurrentLatitude;
    }

    public Double getMycurrentLongitude() {
        return mycurrentLongitude;
    }

    public void setMycurrentLongitude(Double mycurrentLongitude) {
        this.mycurrentLongitude = mycurrentLongitude;
    }

    public Double getSetUserSelectedLatitude() {
        return setUserSelectedLatitude;
    }

    public void setSetUserSelectedLatitude(Double setUserSelectedLatitude) {
        this.setUserSelectedLatitude = setUserSelectedLatitude;
    }

    public Double getSetuserSelectedLongitude() {
        return setuserSelectedLongitude;
    }

    public void setSetuserSelectedLongitude(Double setuserSelectedLongitude) {
        this.setuserSelectedLongitude = setuserSelectedLongitude;
    }

    private Double setuserSelectedLongitude;


}
