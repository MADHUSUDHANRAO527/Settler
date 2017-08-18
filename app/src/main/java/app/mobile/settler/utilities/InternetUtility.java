package app.mobile.settler.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by madhu on 14/6/17.
 */

public class InternetUtility {

    public static byte TYPE_WIFI = (byte) 0x1;      //return value if the internet source is wifi
    public static byte TYPE_TWOG = (byte) 0x2;      //return value if the mobile data type is 2G
    public static byte TYPE_THREEG = (byte) 0x3;    //return value if the mobile data type is 3G
    public static byte TYPE_FOURG = (byte) 0x4;     //return value if the mobile data type is 4G
    public static byte TYPE_OTHER = (byte) 0x5;     //return value if the mobile data type is other than wifi and mobile data
    public static byte NOCONNECTION = (byte) 0x6;   //return value if no internet connection
    public static byte TYPE_MOBILE_OTHER = (byte) 0x7;         //return value if the mobile data type is other than 2G, 3G and 4G
    public static ConnectionTypeChangeListener mConnectionTypeChangeListener;    //interface class
    public static Timer mTimer;
    private byte type;
    private static NetworkInfo activeNetwork;

    //method to check internet connection
    public static boolean isConnected(Context mContext){

        ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE); //provides info about state of network connectivity

        activeNetwork = mConnectivityManager.getActiveNetworkInfo();  //getting the state of network
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();  //connection exists or being established
    }

    //method to check the internet connection source is wifi or 2G, 3G, 4G mobile data
    public static byte getConnectionType(Context mContext){

        if (isConnected(mContext)) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                return TYPE_WIFI;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                int networkType = activeNetwork.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        return TYPE_TWOG;                            //connected to 2G data plan
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        return TYPE_THREEG;                          //connected to 3G data plan
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        return TYPE_FOURG;                           //connected to 4G data plan
                    default:
                        return TYPE_MOBILE_OTHER;                   //connected to data plan other than 2G, 3G and 4G
                }
            }else{
                //other than wifi and mobile data
                return TYPE_OTHER;
            }
        } else {
            // not connected to the internet
            return NOCONNECTION;
        }
    }

    /*method which runs a mTimer at a given interval of time and checks for the change in connection type at each interval.
      If the connection type change occurs, the interface method is called by passing the new connection type as parameter
    */
    public void setConnectionTypeChangeListener(ConnectionTypeChangeListener listener, final Context mContext, int delayTimeInMills, int intervalTimeInMills){
        mConnectionTypeChangeListener = listener;
        type = getConnectionType(mContext);   //get the present connection type
        mTimer = new Timer();
        TimerTask mTimerTask = new TimerTask() {
            @Override
            public void run() {

                if(type != getConnectionType(mContext)) {
                    //change in connection type

                    if (mConnectionTypeChangeListener != null) {
                        mConnectionTypeChangeListener.onConnectionTypeChanged(getConnectionType(mContext));  //pass new connection type to interface method
                    }
                    type = getConnectionType(mContext);  //replace previous type with new type
                }

            }
        };
        mTimer.scheduleAtFixedRate(mTimerTask,delayTimeInMills,intervalTimeInMills);  //schedule timer for the given interval of time

    }


    //stop timer if it is running
    public static void stopConnectionTypeChangeListener(){
        if (mTimer !=  null){
            mTimer.cancel();
        }
    }

    //interface having a method which is called on change in connection type
    public interface ConnectionTypeChangeListener {
        void onConnectionTypeChanged(byte type);  //gives the new connection type when the connection type changed between wifi, 2G, 3G, 4G

    }
}