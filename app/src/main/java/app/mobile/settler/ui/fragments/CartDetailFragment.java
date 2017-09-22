package app.mobile.settler.ui.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import app.mobile.settler.R;
import app.mobile.settler.models.MapStoresModel;
import app.mobile.settler.ui.activity.BaseActivity;
import app.mobile.settler.utilities.SettlerSingleton;

import static android.content.ContentValues.TAG;

/**
 * Created by Madhu on 27/08/17.
 */

public class CartDetailFragment extends Fragment {
    private Context mContext;
    private MapStoresModel cartModel;
    public TextView offerDescTxt, merchantNameTxt, offerNameTxt, expireTxt, uniqueOfferTxt, distanceTxt;
    public ImageView servicesIcon;
    public RelativeLayout cartRow;
    CountDownTimer countDownTimer;
    LinearLayout bottomLayout;
    String distanceStr;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.cart_detail_fragment, container, false);
        cartModel = SettlerSingleton.getInstance().getMapStoresModel();
        mContext = getActivity();

        offerDescTxt = (TextView) v.findViewById(R.id.offer_desc_txt);
        merchantNameTxt = (TextView) v.findViewById(R.id.merchant_name_txt);
        offerNameTxt = (TextView) v.findViewById(R.id.offer_name_txt);
        expireTxt = (TextView) v.findViewById(R.id.active_hrs_txt);
        distanceTxt = (TextView) v.findViewById(R.id.distance_user_txt);

        uniqueOfferTxt = (TextView) v.findViewById(R.id.uniques_offer_txt);
        bottomLayout = (LinearLayout) v.findViewById(R.id.bottom_lay);

        merchantNameTxt.setText(cartModel.getMerchantName());
        offerNameTxt.setText(cartModel.getOfferName());

        offerDescTxt.setText(cartModel.getOfferDesc());
        //  expireTxt.setText("Expires in " + cartModel.getActiveHours());
        uniqueOfferTxt.setText("Offer Code " + cartModel.getOTP());
        try {
            calculateDistance();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
       /* long timer = Long.parseLong(String.valueOf(cartModel.getActiveHours()));

        timer = timer * 1000;
      */
        long timer =  SettlerSingleton.getInstance().getCurrentMilliSec();
        countDownTimer = new CountDownTimer(timer, 1000) {
            public void onTick(long millisUntilFinished) {
//               expireTxt.setText("" + millisUntilFinished/1000 + " Sec");


                int seconds = (int) (millisUntilFinished / 1000) % 60;
                int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
                int hours = (int) ((millisUntilFinished / (1000 * 60 * 60)) % 24);
                String newtime = hours + ":" + minutes + ":" + seconds;

                if (newtime.equals("0:0:0")) {
                    expireTxt.setText("00:00:00");
                } else if ((String.valueOf(hours).length() == 1) && (String.valueOf(minutes).length() == 1) && (String.valueOf(seconds).length() == 1)) {
                    expireTxt.setText("0" + hours + ":0" + minutes + ":0" + seconds);
                } else if ((String.valueOf(hours).length() == 1) && (String.valueOf(minutes).length() == 1)) {
                    expireTxt.setText("0" + hours + ":0" + minutes + ":" + seconds);
                } else if ((String.valueOf(hours).length() == 1) && (String.valueOf(seconds).length() == 1)) {
                    expireTxt.setText("0" + hours + ":" + minutes + ":0" + seconds);
                } else if ((String.valueOf(minutes).length() == 1) && (String.valueOf(seconds).length() == 1)) {
                    expireTxt.setText(hours + ":0" + minutes + ":0" + seconds);
                } else if (String.valueOf(hours).length() == 1) {
                    expireTxt.setText("0" + hours + ":" + minutes + ":" + seconds);
                } else if (String.valueOf(minutes).length() == 1) {
                    expireTxt.setText(hours + ":0" + minutes + ":" + seconds);
                } else if (String.valueOf(seconds).length() == 1) {
                    expireTxt.setText(hours + ":" + minutes + ":0" + seconds);
                } else {
                    expireTxt.setText(hours + ":" + minutes + ":" + seconds);
                }

            }

            public void onFinish() {
                expireTxt.setText("00:00:00");
            }
        }.start();
        bottomLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BaseActivity) mContext).onBackpressedd();
            }
        });
        return v;
    }

    private void calculateDistance() throws MalformedURLException {
        LatLng origin = new LatLng(SettlerSingleton.getInstance().getMycurrentLatitude(), SettlerSingleton.getInstance().getMycurrentLongitude());
        double destLat = Double.parseDouble(cartModel.getStoreLatitude());
        double destLongi = Double.parseDouble(cartModel.getStoreLongitude());
        LatLng dest = new LatLng(destLat, destLongi);
        getUrl(origin, dest);


    }



    public String getUrl(LatLng origin, LatLng dest) throws MalformedURLException {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        URL distanceUrl = new URL("http://maps.googleapis.com/maps/api/directions/json?origin=" + str_origin + "&destination=" + str_dest + "&sensor=false&units=metric&mode=driving");
        //  volleyHelper.calculateDistance(String.valueOf(distanceUrl));
        Log.d(TAG, "Distance getUrl: " + url);
        FetchUrl FetchUrl = new FetchUrl();

        // Start downloading json data from Google Directions API
        FetchUrl.execute(url);
        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // ParserTask parserTask = new ParserTask();
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(result);
                JSONArray array = jsonObject.getJSONArray("routes");
                JSONObject routes = array.getJSONObject(0);
                JSONArray legs = routes.getJSONArray("legs");
                JSONObject steps = legs.getJSONObject(0);
                JSONObject distance = steps.getJSONObject("distance");
                JSONObject duration = steps.getJSONObject("duration");

                distanceStr = distance.getString("text");
                //   timeStr = duration.getString("text");
                distanceTxt.setText("(" + distanceStr + ")");
                // timeTxt.setText(timeStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        countDownTimer.cancel();
    }
}

