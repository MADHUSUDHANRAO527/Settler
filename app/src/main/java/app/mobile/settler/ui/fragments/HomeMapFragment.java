package app.mobile.settler.ui.fragments;

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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import app.mobile.settler.databinding.FragmentHomeMapBinding;
import app.mobile.settler.events.MapStoresHomeEvent;
import app.mobile.settler.models.MapStoresModel;
import app.mobile.settler.netwrokHelpers.VolleyHelper;
import app.mobile.settler.services.CurrentLocationService;
import app.mobile.settler.services.LocationAddress;
import app.mobile.settler.ui.activity.BaseActivity;
import app.mobile.settler.ui.adapters.SwipeDeckAdapter;
import app.mobile.settler.utilities.PreferenceManager;
import app.mobile.settler.utilities.SettlerSingleton;
import app.mobile.settler.utilities.UImsgs;
import app.mobile.settler.utilities.Utils;

import static android.content.Context.MODE_PRIVATE;
import static app.mobile.settler.R.id.map;


public class HomeMapFragment extends Fragment implements OnMapReadyCallback {
    private static final String TAG = "Settler-MapActivity.class";
    // private GoogleMap googleMapInstance;
    private TextView addressTxt, cartNumTxt;
    ImageView serviceListIcon, mapIcon, cartIcon;
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
    TextView noMoreCardsTxt;
    FragmentHomeMapBinding homeMapBinding;
    ArrayList<MapStoresModel> cartModelList = new ArrayList<>();
    public ArrayList<MapStoresModel> mapsStoreModel = new ArrayList<>();

    private void checkForPermissions() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                //if user deny's //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Need location Permission");
                builder.setMessage("This app needs location permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_PERMISSION_CONSTANT);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Need location Permission");
                builder.setMessage("This app needs locaiton permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                        intent.setData(uri);

                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(mContext, "Go to Permissions to Grant locaiton!", Toast.LENGTH_LONG).show();
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
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_PERMISSION_CONSTANT);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home_map, container, false);
        mContext = getActivity();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        volleyHelper = new VolleyHelper(mContext);
        addressTxt = (TextView) v.findViewById(R.id.address_txt);
        noMoreCardsTxt = (TextView) v.findViewById(R.id.no_more_cards_txt);
        cartNumTxt = (TextView) v.findViewById(R.id.cart_num_txt);
        serviceListIcon = (ImageView) v.findViewById(R.id.list_icon);
        mapIcon = (ImageView) v.findViewById(R.id.map_icon);
        cartIcon = (ImageView) v.findViewById(R.id.cart_icon);
        progressBar = (ProgressBar) v.findViewById(R.id.pBar);
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        preferenceManager = new PreferenceManager(mContext);
        permissionStatus = mContext.getSharedPreferences("permissionStatus", MODE_PRIVATE);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            if (Utils.isGPSEnabled(mContext)) {
                getLocation();
            } else {
                buildAlertMessageNoGps(mContext);
            }
        } else {
            checkForPermissions();
        }
        mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(map);
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
        cardStack = (SwipeDeck) v.findViewById(R.id.swipe_deck);
        cardStack.setVisibility(View.GONE);

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
        cartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CartFragment cartFragment = new CartFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.layout_frame_content, cartFragment);
                ((BaseActivity) mContext).addFragmentToStack(cartFragment);
                fragmentTransaction.commit();
            }
        });
        cardStack.setEventCallback(new SwipeDeck.SwipeEventCallback() {
            @Override
            public void cardSwipedLeft(int position) {
                Log.i("MainActivity", "card was swiped left, position in adapter: " + position);
            }

            @Override
            public void cardSwipedRight(int position) {
                Log.i("MainActivity", "card was swiped right, position in adapter: " + position);
                cartModelList.add(mapsStoreModel.get(position));
                Log.d("CART SIZE: ", cartModelList.size() + "");
                cartNumTxt.setText(cartModelList.size() + "");
                UImsgs.showToast(mContext, R.string.offer_add_to_cart);
                SettlerSingleton.getInstance().setCartModelList(cartModelList);
            }

            @Override
            public void cardsDepleted() {
                Log.i("MainActivity", "no more cards");
                noMoreCardsTxt.setVisibility(View.VISIBLE);
            }

            @Override
            public void cardActionDown() {

            }

            @Override
            public void cardActionUp() {

            }
        });
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


    private void getLocation() {
        appLocationService = new CurrentLocationService(
                getActivity());

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
                    mContext, new GeocoderHandler());
            SettlerSingleton.getInstance().setMycurrentLatitude(latitude);
            SettlerSingleton.getInstance().setMycurrentLongitude(longitude);
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
            googleMapInstance.setMyLocationEnabled(true);
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
            SettlerSingleton.getInstance().setMyCurrentAddress(currentAddress);
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
            mapsStoreModel = event.mapsStoreModel;
            for (int i = 0; i < event.mapsStoreModel.size(); i++) {
                MapStoresModel servicesMapBaseModel = event.mapsStoreModel.get(i);
                //the include method will calculate the min and max bound.
                builder.include(createMarker(Double.parseDouble(servicesMapBaseModel.getStoreLatitude()),
                        Double.parseDouble(servicesMapBaseModel.getStoreLongitude()), servicesMapBaseModel.getStorName(), servicesMapBaseModel.getProductName(), getBitmapFromURL(servicesMapBaseModel.getImageUrl()))
                        .getPosition());break;

            }
            LatLngBounds bounds = builder.build();

            int padding = 0; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            googleMapInstance.moveCamera(cu);
            googleMapInstance.animateCamera(CameraUpdateFactory.zoomTo(14.5f));
            //   UImsgs.dismissProgressDialog();
            progressBar.setVisibility(View.GONE);

            //     CartAdapter allServicesAdapter = new CartAdapter(mContext, event.mapsStoreModel);
            //     recyclerView.setAdapter(allServicesAdapter);
            SwipeDeckAdapter adapter = new SwipeDeckAdapter(event.mapsStoreModel, mContext);
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
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                    //Show Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Need location Permission");
                    builder.setMessage("This app needs location permission");
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();


                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_PERMISSION_CONSTANT);


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
                    Toast.makeText(mContext, "Unable to get Permission", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void locateUserAddress(Double latitude, Double longitude) {
        LatLng latLong = new LatLng(latitude, longitude);
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(latLong, 15);
        googleMapInstance.animateCamera(yourLocation);
    }

    private void buildAlertMessageNoGps(final Context mContext) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        //  mContext.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}

