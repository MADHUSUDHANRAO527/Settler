package app.mobile.settler.ui.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daprlabs.cardstack.SwipeDeck;
import com.google.android.gms.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import app.mobile.settler.R;
import app.mobile.settler.events.AddToCartEvent;
import app.mobile.settler.models.MapStoresModel;
import app.mobile.settler.netwrokHelpers.VolleyHelper;
import app.mobile.settler.ui.adapters.SwipeDeckAdapter;
import app.mobile.settler.utilities.SettlerSingleton;
import app.mobile.settler.utilities.UImsgs;

import static android.content.ContentValues.TAG;

/**
 * Created by Madhu on 23/08/17.
 */

public class CardDeckOfferFragment extends Fragment {
    private Context mContext;
    SwipeDeck cardStack;
    TextView noMoreCardsTxt, distanceTxt, expireTxt;
    ArrayList<MapStoresModel> cartModelList = new ArrayList<>();
    ImageView declineOfferImage, acceptCartImg;
    int globalPos;
    private VolleyHelper volleyHelper;
    CountDownTimer countDownTimer;
    RelativeLayout botmLay;
    String distanceStr, timeStr;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.offers_fragment, container, false);
        mContext = getActivity();
        cardStack = (SwipeDeck) v.findViewById(R.id.swipe_deck);
        noMoreCardsTxt = (TextView) v.findViewById(R.id.no_more_cards_txt);
        distanceTxt = (TextView) v.findViewById(R.id.distance_user_txt);
        progressBar = (ProgressBar) v.findViewById(R.id.pBar);
        expireTxt = (TextView) v.findViewById(R.id.active_hrs_txt);
        declineOfferImage = (ImageView) v.findViewById(R.id.close_icon);
        acceptCartImg = (ImageView) v.findViewById(R.id.accept_icon);
        botmLay = (RelativeLayout) v.findViewById(R.id.btm_lay);
        volleyHelper = new VolleyHelper(mContext);
        if (SettlerSingleton.getInstance().getSetOffersDataModel().size() > 0) {
            SwipeDeckAdapter adapter = new SwipeDeckAdapter(SettlerSingleton.getInstance().getSetOffersDataModel(), mContext);
            cardStack.setAdapter(adapter);
            try {
                calculateDistance(0);
                startTimer(SettlerSingleton.getInstance().getSetOffersDataModel().get(0).getActiveHours());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            noMoreCardsTxt.setVisibility(View.VISIBLE);
            botmLay.setVisibility(View.GONE);
        }

       // countDownStart();
        cardStack.setEventCallback(new SwipeDeck.SwipeEventCallback() {
            @Override
            public void cardSwipedLeft(int position) {
                Log.i("MainActivity", "card was swiped left, position in adapter: " + position);
                globalPos = position + 1;
                try {
                    if (SettlerSingleton.getInstance().getSetOffersDataModel().size() > globalPos)
                        calculateDistance(globalPos);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                startTimer(SettlerSingleton.getInstance().getSetOffersDataModel().get(position).getActiveHours());
            }

            @Override
            public void cardSwipedRight(int position) {
                Log.i("MainActivity", "card was swiped right, position in adapter: " + position);
                globalPos = position + 1;

                if (SettlerSingleton.getInstance().getSetOffersDataModel().size() > globalPos) {
                    startTimer(SettlerSingleton.getInstance().getSetOffersDataModel().get(position).getActiveHours());
                    volleyHelper.addToCart(SettlerSingleton.getInstance().getSetOffersDataModel().get(globalPos).getOfferId());
                    //cartModelList.add(SettlerSingleton.getInstance().getSetOffersDataModel().get(position));
                    Log.d("CART SIZE: ", SettlerSingleton.getInstance().getSetOffersDataModel().size() + "");
                    UImsgs.showToast(mContext, R.string.offer_add_to_cart);
                    // SettlerSingleton.getInstance().setCartModelList(cartModelList);
                    //  ((MainActivity) mContext).setCartNumTxt();

                    callCartListAPI();
                    try {
                        calculateDistance(globalPos);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void cardsDepleted() {
                Log.i("MainActivity", "no more cards");
                noMoreCardsTxt.setVisibility(View.VISIBLE);
                botmLay.setVisibility(View.GONE);
            }

            @Override
            public void cardActionDown() {

            }

            @Override
            public void cardActionUp() {

            }
        });

        declineOfferImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardStack.swipeTopCardLeft(globalPos);
                //  volleyHelper.addToCart(SettlerSingleton.getInstance().getSetOffersDataModel().get(globalPos).getOfferId());
            }
        });
        acceptCartImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SettlerSingleton.getInstance().getSetOffersDataModel().size() > 0) {
                    cardStack.swipeTopCardRight(globalPos);
                    volleyHelper.addToCart(SettlerSingleton.getInstance().getSetOffersDataModel().get(globalPos).getOfferId());
                    callCartListAPI();
                } else {
                    UImsgs.showToast(mContext, R.string.cart_is_empty);
                }

            }
        });


        return v;
    }

    private void calculateDistance(int pos) throws MalformedURLException {
        LatLng origin = new LatLng(SettlerSingleton.getInstance().getMycurrentLatitude(), SettlerSingleton.getInstance().getMycurrentLongitude());
        double destLat = Double.parseDouble(SettlerSingleton.getInstance().getSetOffersDataModel().get(pos).getStoreLatitude());
        double destLongi = Double.parseDouble(SettlerSingleton.getInstance().getSetOffersDataModel().get(pos).getStoreLongitude());
        LatLng dest = new LatLng(destLat, destLongi);

        // distanceTxt.setText(String.valueOf(Utils.getDistance(origin, dest)));

        getUrl(origin, dest);
    }

    public void callCartListAPI() {
        volleyHelper.getCartList();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AddToCartEvent event) {
        if (event.success) {
            globalPos = 0;
            UImsgs.showToast(mContext, R.string.offer_add_to_cart);
        } else {
            UImsgs.showToastErrorMessage(mContext, event.errorCode);
        }
    }

    public void startTimer(String activeHrsInMin) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        //long timer = Long.parseLong(String.valueOf(120000));

        long timer = TimeUnit.MINUTES.toMillis(Integer.parseInt(activeHrsInMin));

       // timer = timer * 1000;

        countDownTimer = new CountDownTimer(timer, 1000) {
            public void onTick(long millisUntilFinished) {

                /*long secondss = millisUntilFinished / 1000;
                long minutess = secondss / 60;
                long hourss = minutess / 60;
                long dayss = hourss / 24;
                String time = dayss + ":" + hourss % 24 + ":" + minutess % 60 + ":" + secondss % 60;*/


//               expireTxt.setText("" + millisUntilFinished/1000 + " Sec");
                int seconds = (int) (millisUntilFinished / 1000) % 60;
                int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
                int hours = (int) ((millisUntilFinished / (1000 * 60 * 60)) % 24);
                long days = hours / 24;
                String newtime = days + "D " + hours + "H " + minutes + "M " + seconds;
                Log.d("Time: ", newtime);
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
        URL distanceUrl = new URL("http://maps.googleapis.com/maps/api/directions/json?key=" + getResources().getString(R.string.map_key) + "&origin=" + str_origin + "&destination=" + str_dest + "&sensor=false&units=metric&mode=driving");
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
                if (array.length() > 0) {
                    JSONObject routes = array.getJSONObject(0);
                    JSONArray legs = routes.getJSONArray("legs");
                    JSONObject steps = legs.getJSONObject(0);
                    JSONObject distance = steps.getJSONObject("distance");
                    JSONObject duration = steps.getJSONObject("duration");

                    distanceStr = distance.getString("text");
                    //   timeStr = duration.getString("text");
                    distanceTxt.setText("(" + distanceStr + ")");
                } else if (jsonObject.has("status")) {
                    if (jsonObject.getString("status").equalsIgnoreCase("ZERO_RESULTS")) {
                        distanceTxt.setText(" No results found ");
                    }
                }
                // timeTxt.setText(timeStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressBar.setVisibility(View.GONE);
            // Invokes the thread for parsing the JSON data

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    public void countDownStart() {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                            "yyyy-MM-dd");
                    // Here Set your Event Date
                    Date eventDate = dateFormat.parse("2017-12-30");
                    Date currentDate = new Date();
                    if (!currentDate.after(eventDate)) {
                        long diff = eventDate.getTime()
                                - currentDate.getTime();
                        long days = diff / (24 * 60 * 60 * 1000);
                        diff -= days * (24 * 60 * 60 * 1000);
                        long hours = diff / (60 * 60 * 1000);
                        diff -= hours * (60 * 60 * 1000);
                        long minutes = diff / (60 * 1000);
                        diff -= minutes * (60 * 1000);
                        long seconds = diff / 1000;

                        String newtime = String.format("%02d", days) + "D " + String.format("%02d", hours) + "H "
                                + String.format("%02d", minutes) + "M " + String.format("%02d", seconds);
                        Log.d("Time: ", newtime);

                        /*tvDay.setText("" + String.format("%02d", days));
                        tvHour.setText("" + String.format("%02d", hours));
                        tvMinute.setText("" + String.format("%02d", minutes));
                        tvSecond.setText("" + String.format("%02d", seconds));*/
                    } else {
                      /*  linearLayout1.setVisibility(View.VISIBLE);
                        linearLayout2.setVisibility(View.GONE);
                        tvEvent.setText("Android Event Start");*/
                      //  handler.removeCallbacks(runnable);
                        // handler.removeMessages(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 0);
    }
}
