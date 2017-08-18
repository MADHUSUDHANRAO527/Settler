package app.mobile.settler.utilities;

/**
 * Created by madhu on 28/6/17.
 */

public class NServicesSingleton {
    private static final NServicesSingleton instance = new NServicesSingleton();
    private String myCurrentAddress;
    private Double mycurrentLatitude;
    private Double mycurrentLongitude;
    private String setuserSelectedAddress;
    private String selectedServiceName;
    private String userSelectedDate;

    public String getUserSelectedTime() {
        return userSelectedTime;
    }

    public void setUserSelectedTime(String userSelectedTime) {
        this.userSelectedTime = userSelectedTime;
    }

    private String userSelectedTime;

    public String getUserSelectedDate() {
        return userSelectedDate;
    }

    public void setUserSelectedDate(String userSelectedDate) {
        this.userSelectedDate = userSelectedDate;
    }

    public String getSelectedServiceName() {
        return selectedServiceName;
    }

    public void setSelectedServiceName(String selectedServiceName) {
        this.selectedServiceName = selectedServiceName;
    }



    private Double setUserSelectedLatitude;


    public static NServicesSingleton getInstance() {
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

    public String getSetuserSelectedAddress() {
        return setuserSelectedAddress;
    }

    public void setSetuserSelectedAddress(String setuserSelectedAddress) {
        this.setuserSelectedAddress = setuserSelectedAddress;
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
