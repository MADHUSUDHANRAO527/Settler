package app.mobile.settler.ui.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import java.util.ArrayList;

import app.mobile.settler.R;
import app.mobile.settler.models.MapStoresModel;
import app.mobile.settler.ui.activity.BaseActivity;
import app.mobile.settler.ui.fragments.CartDetailFragment;
import app.mobile.settler.utilities.SettlerSingleton;
import app.mobile.settler.utilities.UImsgs;

import static android.content.ContentValues.TAG;


/**
 * Created by madhu on 21/6/17.
 */

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ServicesViewHolder> {
    private Context mContext;
    private ArrayList<MapStoresModel> cartModelList;
    private UImsgs uImsgs;
    private MapStoresModel cartModel;
    String distanceStr, timeStr;
    public CartAdapter(Context context, ArrayList<MapStoresModel> model) {
        this.cartModelList = model;
        this.mContext = context;
        uImsgs = new UImsgs(context);
    }

    @Override
    public ServicesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_row, parent, false);
        return new ServicesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ServicesViewHolder holder, final int position) {

        holder.merchantNameTxt.setText(cartModelList.get(position).getMerchantName());
        holder.offerNameTxt.setText(cartModelList.get(position).getOfferName());

        holder.offerDescTxt.setText(cartModelList.get(position).getOfferDesc());
        // holder.expireTxt.setText("Expires in " + cartModelList.get(position).getActiveHours());
        holder.uniqueOfferTxt.setText("Offer Code " + cartModelList.get(position).getOTP());
        //distance
        LatLng origin = new LatLng(SettlerSingleton.getInstance().getMycurrentLatitude(), SettlerSingleton.getInstance().getMycurrentLongitude());
        double destLat = Double.parseDouble(cartModelList.get(position).getStoreLatitude());
        double destLongi = Double.parseDouble(cartModelList.get(position).getStoreLongitude());
        LatLng dest = new LatLng(destLat, destLongi);

        try {
            getUrl(origin, dest,holder.distaceTxt);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (holder.timer != null) {
            holder.timer.cancel();
        }
        long timer = Long.parseLong(String.valueOf(1200000));

        timer = timer * 1000;

        holder.timer = new CountDownTimer(timer, 1000) {
            public void onTick(long millisUntilFinished) {
//              holder.expireTxt.setText("" + millisUntilFinished/1000 + " Sec");


                int seconds = (int) (millisUntilFinished / 1000) % 60;
                int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
                int hours = (int) ((millisUntilFinished / (1000 * 60 * 60)) % 24);
                String newtime = hours + ":" + minutes + ":" + seconds;

                if (newtime.equals("0:0:0")) {
                    holder.expireTxt.setText("00:00:00");
                } else if ((String.valueOf(hours).length() == 1) && (String.valueOf(minutes).length() == 1) && (String.valueOf(seconds).length() == 1)) {
                    holder.expireTxt.setText("0" + hours + ":0" + minutes + ":0" + seconds);
                } else if ((String.valueOf(hours).length() == 1) && (String.valueOf(minutes).length() == 1)) {
                    holder.expireTxt.setText("0" + hours + ":0" + minutes + ":" + seconds);
                } else if ((String.valueOf(hours).length() == 1) && (String.valueOf(seconds).length() == 1)) {
                    holder.expireTxt.setText("0" + hours + ":" + minutes + ":0" + seconds);
                } else if ((String.valueOf(minutes).length() == 1) && (String.valueOf(seconds).length() == 1)) {
                    holder.expireTxt.setText(hours + ":0" + minutes + ":0" + seconds);
                } else if (String.valueOf(hours).length() == 1) {
                    holder.expireTxt.setText("0" + hours + ":" + minutes + ":" + seconds);
                } else if (String.valueOf(minutes).length() == 1) {
                    holder.expireTxt.setText(hours + ":0" + minutes + ":" + seconds);
                } else if (String.valueOf(seconds).length() == 1) {
                    holder.expireTxt.setText(hours + ":" + minutes + ":0" + seconds);
                } else {
                    holder.expireTxt.setText(hours + ":" + minutes + ":" + seconds);
                }

            }

            public void onFinish() {
                holder.expireTxt.setText("00:00:00");
            }
        }.start();
        holder.cartRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettlerSingleton.getInstance().setMapStoresModel(cartModelList.get(position));
                ((BaseActivity) mContext).addFragment(new CartDetailFragment());
            }
        });


     /*   Glide.with(mContext).load(cartModelList.get(position).getImageUrl())
                .into(holder.servicesIcon);*/
       /* holder.historyRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, HistoryActiveDetailActivity.class);
                Log.d("booking_id", cartModelList.get(position).getId());
                i.putExtra("booking_id", cartModelList.get(position).getId());
                
                mContext.startActivity(i);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return cartModelList.size();
    }

    public class ServicesViewHolder extends RecyclerView.ViewHolder {
        public TextView offerDescTxt, merchantNameTxt, offerNameTxt, expireTxt, uniqueOfferTxt,distaceTxt;
        public ImageView servicesIcon;
        public CardView cartRow;
        CountDownTimer timer;

        public ServicesViewHolder(View itemView) {
            super(itemView);
            expireTxt = (TextView) itemView.findViewById(R.id.active_hrs_txt);
            offerDescTxt = (TextView) itemView.findViewById(R.id.offer_desc_txt);
            merchantNameTxt = (TextView) itemView.findViewById(R.id.merchant_name_txt);
            offerNameTxt = (TextView) itemView.findViewById(R.id.offer_name_txt);
            distaceTxt = (TextView) itemView.findViewById(R.id.distance_user_txt);
            uniqueOfferTxt = (TextView) itemView.findViewById(R.id.uniques_offer_txt);

            servicesIcon = (ImageView) itemView.findViewById(R.id.services_icon);
            cartRow = (CardView) itemView.findViewById(R.id.row);
        }
    }

    public String getUrl(LatLng origin, LatLng dest, TextView distaceTxt) throws MalformedURLException {

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
        FetchUrl FetchUrl = new FetchUrl(distaceTxt);

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
        TextView distanceTxt;
        public FetchUrl(TextView distaceTx) {
            distanceTxt = distaceTx;
        }

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

}
