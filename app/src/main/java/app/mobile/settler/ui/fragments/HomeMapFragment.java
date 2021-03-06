package app.mobile.settler.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daprlabs.cardstack.SwipeDeck;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import app.mobile.settler.R;
import app.mobile.settler.databinding.FragmentHomeMapBinding;
import app.mobile.settler.events.MapStoresHomeEvent;
import app.mobile.settler.models.MapStoresModel;
import app.mobile.settler.netwrokHelpers.VolleyHelper;
import app.mobile.settler.services.CurrentLocationService;
import app.mobile.settler.utilities.PreferenceManager;
import app.mobile.settler.utilities.SettlerSingleton;
import app.mobile.settler.utilities.UImsgs;
import app.mobile.settler.utilities.Utils;

import static app.mobile.settler.R.id.map;


public class HomeMapFragment extends Fragment implements OnMapReadyCallback {
    private static final String TAG = "Settler-MapActivity.class";
    // private GoogleMap googleMapInstance;
    private TextView addressTxt, cartNumTxt;
    String locationAddress;
    private PreferenceManager preferenceManager;
    private Context mContext;
    ArrayList<String> latLongi = new ArrayList<>();
    private VolleyHelper volleyHelper;
    private String userLati, userLongi, serviceId, spLat = "", spLongi = "";
    Button nextBtn;
    CurrentLocationService appLocationService;
    private SharedPreferences permissionStatus;
    private String userLocation;
    private static final int ACCESS_FINE_LOCATION_PERMISSION_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private boolean sentToSettings = false;
    double latitude, longitude;
    ProgressBar progressBar;
    SupportMapFragment mapFragment;
    private GoogleMap googleMapInstance;
    SwipeDeck cardStack;
    FragmentHomeMapBinding homeMapBinding;
    public ArrayList<MapStoresModel> mapsStoreModel = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home_map, container, false);
        mContext = getActivity();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        volleyHelper = new VolleyHelper(mContext);
        addressTxt = (TextView) v.findViewById(R.id.address_txt);
        progressBar = (ProgressBar) v.findViewById(R.id.pBar);
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        preferenceManager = new PreferenceManager(mContext);

        mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(map);
        mapFragment.getMapAsync(this);
        volleyHelper.getCartList();


        //   cardStack = (SwipeDeck) v.findViewById(R.id.swipe_deck);
        //   cardStack.setVisibility(View.GONE);


        return v;
    }


    public Bitmap getBitmapFromURL(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            if (e instanceof FileNotFoundException) {
                UImsgs.dismissProgressDialog();
            }
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMapInstance = googleMap;
        //  googleMapInstance.setMyLocationEnabled(true);

        //  locateUserAddress(Double.parseDouble(userLati), Double.parseDouble(userLongi));
        /*if (SettlerSingleton.getInstance().getMycurrentLatitude() == null)
            userLati = preferenceManager.getString("user_lat");
        else
            userLati = SettlerSingleton.getInstance().getMycurrentLatitude() + "";
*/
        userLati = SettlerSingleton.getInstance().getMycurrentLatitude() + "";

       /* if (SettlerSingleton.getInstance().getMycurrentLongitude() == null)
            userLongi = preferenceManager.getString("user_long");
        else
            userLongi = SettlerSingleton.getInstance().getMycurrentLongitude() + "";
*/
        userLongi = SettlerSingleton.getInstance().getMycurrentLongitude() + "";


        if (googleMapInstance != null && userLati != null) {
            LatLng latLong = new LatLng(Double.parseDouble(userLati), Double.parseDouble(userLongi));
            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(latLong, 15);
            googleMapInstance.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    spLat = String.valueOf(marker.getPosition().latitude);
                    spLongi = String.valueOf(marker.getPosition().longitude);
                    Log.e("SP-LATI:LONGI ", spLat + "-" + spLongi);
                    return false;
                }
            });
            Log.e("ADDRSS", SettlerSingleton.getInstance().getMyCurrentAddress() + "-" + preferenceManager.getString("user_location"));
            addressTxt.setText(SettlerSingleton.getInstance().getMyCurrentAddress());

            /*if (SettlerSingleton.getInstance().getMyCurrentAddress() != null)
                addressTxt.setText(SettlerSingleton.getInstance().getMyCurrentAddress());
            else
                addressTxt.setText(preferenceManager.getString("user_location"));*/
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("MobileNo", "9999999999");
                jsonObject.put("SettlerId", "Str100");
                jsonObject.put("UserId", "Test100");

                jsonObject.put("UserLatitude", userLati);
                jsonObject.put("UserLongitude", userLongi);


            } catch (JSONException e) {
                e.printStackTrace();
            }
            volleyHelper.getStoresList(jsonObject);
            googleMapInstance.setMyLocationEnabled(true);
        }
    }


    protected Marker createMarker(double latitude, double longitude, String spName, String serviceName, Bitmap bitmap) {
        if (bitmap != null) {
            return googleMapInstance.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .anchor(0.5f, 0.5f)
                    .title(spName)
                    .icon(BitmapDescriptorFactory.fromBitmap(Utils.getResizedBitmap(bitmap))));

        } else {
            return googleMapInstance.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .anchor(0.5f, 0.5f)
                    .title(spName)
                    .icon(BitmapDescriptorFactory.fromBitmap(Utils.setMarkerSize(mContext))));
        }

        // googleMap.addMarker(new MarkerOptions().position(pointList.get(x)).title(nameList.get(x))).showInfoWindow();

    }

    private void locateUserAddress() {
        LatLng latLong = new LatLng(Double.parseDouble(userLati), Double.parseDouble(userLongi));
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(latLong, 15);
        googleMapInstance.animateCamera(yourLocation);
        progressBar.setVisibility(View.GONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MapStoresHomeEvent event) {
        if (event.success) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            mapsStoreModel = event.mapsStoreModel;
            SettlerSingleton.getInstance().setSetOffersDataModel(mapsStoreModel);
            if (event.mapsStoreModel.size() > 0) {

                new LoadMarkers().execute();


                /*for (int i = 0; i < event.mapsStoreModel.size(); i++) {
                    MapStoresModel servicesMapBaseModel = event.mapsStoreModel.get(i);
                    //the include method will calculate the min and max bound.
                    builder.include(createMarker(Double.parseDouble(servicesMapBaseModel.getStoreLatitude()),
                            Double.parseDouble(servicesMapBaseModel.getStoreLongitude()), servicesMapBaseModel.getStorName(), servicesMapBaseModel.getProductName(), getBitmapFromURL(servicesMapBaseModel.getImageUrl()))
                            .getPosition());

                }
                LatLngBounds bounds = builder.build();

                int padding = 0; // offset from edges of the map in pixels
            //    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            //    googleMapInstance.moveCamera(cu);
             //   googleMapInstance.animateCamera(CameraUpdateFactory.zoomTo(14.5f));
                //   UImsgs.dismissProgressDialog();
                progressBar.setVisibility(View.GONE);*/

                //     CartAdapter allServicesAdapter = new CartAdapter(mContext, event.mapsStoreModel);
                //     recyclerView.setAdapter(allServicesAdapter);
            } else {
                locateUserAddress();
            }


        } else {
            UImsgs.showToast(mContext, event.msg);
            locateUserAddress(latitude, longitude);
            //    UImsgs.dismissProgressDialog();

            progressBar.setVisibility(View.GONE);
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

    private void locateUserAddress(Double latitude, Double longitude) {
        LatLng latLong = new LatLng(latitude, longitude);
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(latLong, 15);
        googleMapInstance.animateCamera(yourLocation);
        progressBar.setVisibility(View.GONE);
    }

    public class LoadMarkers extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            for (int i = 0; i < mapsStoreModel.size(); i++) {
                MapStoresModel servicesMapBaseModel = mapsStoreModel.get(i);
                //the include method will calculate the min and max bound.
                builder.include(createMarker(Double.parseDouble(servicesMapBaseModel.getStoreLatitude()),
                        Double.parseDouble(servicesMapBaseModel.getStoreLongitude()), servicesMapBaseModel.getStorName(), servicesMapBaseModel.getProductName(), getBitmapFromURL(servicesMapBaseModel.getImageUrl()))
                        .getPosition());

            }
            LatLngBounds bounds = builder.build();

            int padding = 0; // offset from edges of the map in pixels
            //    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            //    googleMapInstance.moveCamera(cu);
            //   googleMapInstance.animateCamera(CameraUpdateFactory.zoomTo(14.5f));
            //   UImsgs.dismissProgressDialog();
            progressBar.setVisibility(View.GONE);

        }
    }

}

