package app.mobile.settler.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import app.mobile.settler.events.MapStoresHomeEvent;
import app.mobile.settler.models.MapStoresModel;
import app.mobile.settler.netwrokHelpers.VolleyHelper;
import app.mobile.settler.services.CurrentLocationService;
import app.mobile.settler.services.LocationAddress;
import app.mobile.settler.ui.adapters.SwipeDeckAdapter;
import app.mobile.settler.utilities.NServicesSingleton;
import app.mobile.settler.utilities.PreferenceManager;
import app.mobile.settler.utilities.UImsgs;
import app.mobile.settler.utilities.Utils;

import static app.mobile.settler.R.id.map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "Settler-MapActivity.class";
    // private GoogleMap googleMapInstance;
    private TextView addressTxt;
    ImageView serviceListIcon, mapIcon;
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
    private boolean locationPermission;
    double latitude, longitude;
    ProgressBar progressBar;
    SupportMapFragment mapFragment;
    private GoogleMap googleMapInstance;
    SwipeDeck cardStack;
    private void checkForPermissions() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //if user deny's //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Need location Permission");
                builder.setMessage("This app needs location permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_PERMISSION_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        locationPermission = true;
                        dismisPbar();
                        addressTxt.setTextSize(12);
                        //   addressTxt.setSingleLine(false);
                        addressTxt.setLines(2);
                        addressTxt.setText("You are denied location permission!" + System.getProperty("line.separator") + "Click here to goto settings page!");
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(Manifest.permission.ACCESS_FINE_LOCATION, false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Need location Permission");
                builder.setMessage("This app needs locaiton permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);

                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(getBaseContext(), "Go to Permissions to Grant locaiton!", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_PERMISSION_CONSTANT);
            }
            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(Manifest.permission.ACCESS_FINE_LOCATION, true);
            editor.apply();
        } else {
            //You already have the permission, just go ahead.
            if (preferenceManager.getString("user_location") != null && !preferenceManager.getString("user_location").equals("")) {
                addressTxt.setText(preferenceManager.getString("user_location"));

                if (preferenceManager.getString("user_lat") != null) {
                    userLati = preferenceManager.getString("user_lat");
                    userLongi = preferenceManager.getString("user_long");
                }
                // locateUserAddress(Double.parseDouble(userLati), Double.parseDouble(userLongi));


                dismisPbar();
            } else {
                getLocation();
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        volleyHelper = new VolleyHelper(mContext);
        addressTxt = (TextView) findViewById(R.id.address_txt);
        serviceListIcon = (ImageView) findViewById(R.id.list_icon);
        mapIcon = (ImageView) findViewById(R.id.map_icon);
        progressBar = (ProgressBar) findViewById(R.id.pBar);
        //  progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mContext = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        preferenceManager = new PreferenceManager(mContext);
      //  recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
    //    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
   //     recyclerView.setLayoutManager(layoutManager);


        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            serviceId = bundle.getString("service_id");
        }
        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);

        checkForPermissions();
        mapFragment = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(map);
        mapFragment.getMapAsync(this);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("MobileNo", "9999999999");
            jsonObject.put("SettlerId", "Str100");
            jsonObject.put("UserId", "Test100");
            jsonObject.put("UserLatitude", "19.286812");
            jsonObject.put("UserLongitude", "72.874334");


        } catch (JSONException e) {
            e.printStackTrace();
        }
        volleyHelper.getStoresList(jsonObject);
          cardStack = (SwipeDeck) findViewById(R.id.swipe_deck);

        serviceListIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapFragment.getView().setVisibility(View.GONE);
                cardStack.setVisibility(View.VISIBLE);


            }
        });

        mapIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardStack.setVisibility(View.GONE);
                mapFragment.getView().setVisibility(View.VISIBLE);

            }
        });

       /* final ArrayList<String> testData = new ArrayList<>();
        testData.add("0");
        testData.add("1");
        testData.add("2");
        testData.add("3");
        testData.add("4");

        final SwipeDeckAdapter adapter = new SwipeDeckAdapter(testData, this);
        cardStack.setAdapter(adapter);
*/
        cardStack.setEventCallback(new SwipeDeck.SwipeEventCallback() {
            @Override
            public void cardSwipedLeft(int position) {
                Log.i("MainActivity", "card was swiped left, position in adapter: " + position);
            }

            @Override
            public void cardSwipedRight(int position) {
                Log.i("MainActivity", "card was swiped right, position in adapter: " + position);
            }

            @Override
            public void cardsDepleted() {
                Log.i("MainActivity", "no more cards");
            }

            @Override
            public void cardActionDown() {

            }

            @Override
            public void cardActionUp() {

            }
        });
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


    private void getLocation() {
        appLocationService = new CurrentLocationService(
                MainActivity.this);

        Location location = appLocationService
                .getLocation(LocationManager.GPS_PROVIDER);

        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            //   LocationAddress locationAddress = new LocationAddress();
            Log.d("LAT: LONG ", latitude + ":" + longitude);
            //updating user lat longi in shared pref
            preferenceManager.putString("user_lat", String.valueOf(latitude));
            preferenceManager.putString("user_long", String.valueOf(longitude));
            userLati = String.valueOf(latitude);
            userLongi = String.valueOf(longitude);
            //   locateUserAddress(latitude, longitude);
            LocationAddress.getAddressFromLocation(latitude, longitude,
                    getApplicationContext(), new GeocoderHandler());
            NServicesSingleton.getInstance().setMycurrentLatitude(latitude);
            NServicesSingleton.getInstance().setMycurrentLongitude(longitude);
            LatLng userLocation = new LatLng(latitude, longitude);
            onMapReady(googleMapInstance);
        } else {
            dismisPbar();
            addressTxt.setTextSize(12);
            addressTxt.setText(R.string.select_loc_manually);
            // Toast.makeText(this, "Not able to fetch your location!Pls click here to select your location manually!", Toast.LENGTH_SHORT).show();

        }
    }

    public void dismisPbar() {
        //    progressBar.setVisibility(View.GONE);
        addressTxt.setVisibility(View.VISIBLE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMapInstance = googleMap;
        //  googleMapInstance.setMyLocationEnabled(true);

        //  locateUserAddress(Double.parseDouble(userLati), Double.parseDouble(userLongi));
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

            //setting dummy data

           /* Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<MapStoresModel>>() {
            }.getType();
            ArrayList<MapStoresModel> mapsStoreModel = gson.fromJson(jsonResponse, type);
            //  EventBus.getDefault().post(new MapStoresHomeEvent(true, 200, model));
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            for (int i = 0; i < mapsStoreModel.size(); i++) {
                MapStoresModel servicesMapBaseModel = mapsStoreModel.get(i);

                //the include method will calculate the min and max bound.
                builder.include(createMarker(Double.parseDouble(servicesMapBaseModel.getStoreLatitude()),
                        Double.parseDouble(servicesMapBaseModel.getStoreLongitude()), servicesMapBaseModel.getStorName(), servicesMapBaseModel.getProductName(),
                        getBitmapFromURL(servicesMapBaseModel.getImageUrl()))
                        .getPosition());
            }
            final LatLngBounds bounds = builder.build();

            final int padding = 0; // offset from edges of the map in pixels
            final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);*/
           /* googleMapInstance.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    googleMapInstance.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
                    googleMapInstance.animateCamera(CameraUpdateFactory.zoomTo(14.5f));
                }
            });*/
        }
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String currentAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    currentAddress = bundle.getString("address");
                    break;
                default:
                    currentAddress = null;
            }
            //  addressTxt.setText(currentAddress);
            addressTxt.setText(currentAddress);
            Log.d("ADREESS ::::: ", currentAddress);
            preferenceManager.putString("user_location", currentAddress);
            NServicesSingleton.getInstance().setMyCurrentAddress(currentAddress);
            dismisPbar();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MapStoresHomeEvent event) {
        if (event.success) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            for (int i = 0; i < event.mapsStoreModel.size(); i++) {
                MapStoresModel servicesMapBaseModel = event.mapsStoreModel.get(i);
                //the include method will calculate the min and max bound.
                builder.include(createMarker(Double.parseDouble(servicesMapBaseModel.getStoreLatitude()),
                        Double.parseDouble(servicesMapBaseModel.getStoreLongitude()), servicesMapBaseModel.getStorName(), servicesMapBaseModel.getProductName(), getBitmapFromURL(servicesMapBaseModel.getImageUrl()))
                        .getPosition());
                //        spLat = servicesMapBaseModel.getLat();
                //      spLongi = servicesMapBaseModel.getLng();
                // Drawing marker on the map
                //     createMarker(Double.parseDouble(servicesMapBaseModel.getLat()), Double.parseDouble(servicesMapBaseModel.getLng()), servicesMapBaseModel.getSpname(), R.drawable.marker);
            }
            LatLngBounds bounds = builder.build();

            int padding = 0; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            googleMapInstance.moveCamera(cu);
            googleMapInstance.animateCamera(CameraUpdateFactory.zoomTo(14.5f));
            //   UImsgs.dismissProgressDialog();
            progressBar.setVisibility(View.GONE);

       //     ServicesListAdapter allServicesAdapter = new ServicesListAdapter(mContext, event.mapsStoreModel);
       //     recyclerView.setAdapter(allServicesAdapter);
            SwipeDeckAdapter adapter = new SwipeDeckAdapter( event.mapsStoreModel, this);
            cardStack.setAdapter(adapter);

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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCESS_FINE_LOCATION_PERMISSION_CONSTANT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //The External Storage Write Permission is granted to you... Continue your left job...
                getLocation();

            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    //Show Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Need location Permission");
                    builder.setMessage("This app needs location permission");
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();


                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_PERMISSION_CONSTANT);


                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else {
                    Toast.makeText(getBaseContext(), "Unable to get Permission", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void locateUserAddress(Double latitude, Double longitude) {
        LatLng latLong = new LatLng(latitude, longitude);
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(latLong, 15);
        googleMapInstance.animateCamera(yourLocation);
    }
}
