package app.mobile.settler.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.mobile.settler.R;
import app.mobile.settler.events.CartListEvent;
import app.mobile.settler.netwrokHelpers.VolleyHelper;
import app.mobile.settler.services.CurrentLocationService;
import app.mobile.settler.services.LocationAddress;
import app.mobile.settler.ui.fragments.CardDeckOfferFragment;
import app.mobile.settler.ui.fragments.CartFragment;
import app.mobile.settler.ui.fragments.HomeMapFragment;
import app.mobile.settler.ui.fragments.SettingsFragment;
import app.mobile.settler.utilities.PreferenceManager;
import app.mobile.settler.utilities.SettlerSingleton;
import app.mobile.settler.utilities.UImsgs;
import app.mobile.settler.utilities.Utils;

public class MainActivity extends BaseActivity {
    private static final int ACCESS_FINE_LOCATION_PERMISSION_CONSTANT = 100;
    double latitude, longitude;
    private PreferenceManager preferenceManager;
    private boolean locationPermission;
    private SharedPreferences permissionStatus;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    ImageView serviceListIcon, mapIcon, cartIcon, settingsIcon;
    TextView cartNumTxt;
    private VolleyHelper volleyHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        serviceListIcon = (ImageView) findViewById(R.id.list_icon);
        mapIcon = (ImageView) findViewById(R.id.map_icon);
        cartIcon = (ImageView) findViewById(R.id.cart_icon);
        cartNumTxt = (TextView) findViewById(R.id.cart_num_txt);
        settingsIcon = (ImageView) findViewById(R.id.settings_icon);
        preferenceManager = new PreferenceManager(this);
        volleyHelper = new VolleyHelper(this);

        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            if (Utils.isGPSEnabled(this)) {
                getLocation();
            } else {
                buildAlertMessageNoGps(this);
            }
        } else {
            checkForPermissions();
        }
        serviceListIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new CardDeckOfferFragment());

            }
        });

        mapIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new HomeMapFragment());

            }
        });
        settingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new SettingsFragment());

            }
        });
        cartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* if (SettlerSingleton.getInstance().getCartModelList() != null) {
                    replaceFragment(new CartFragment());
                } else {
                    UImsgs.showToast(MainActivity.this, R.string.cart_is_empty);
                }*/
                replaceFragment(new CartFragment());


            }
        });
    }

    private void checkForPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //if user deny's //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Need location Permission");
                builder.setMessage("This app needs location permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_PERMISSION_CONSTANT);
//                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_PERMISSION_CONSTANT);

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        locationPermission = true;
                        //  dismisPbar();
                        //  addressTxt.setTextSize(12);
                        //   addressTxt.setSingleLine(false);
                        //  addressTxt.setLines(2);
                        //  addressTxt.setText("You are denied location permission!" + System.getProperty("line.separator") + "Click here to goto settings page!");
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_PERMISSION_CONSTANT);
            }
            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(Manifest.permission.ACCESS_FINE_LOCATION, true);
            editor.apply();
        } else {
            getLocation();
         //   replaceFragment(new HomeMapFragment());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCESS_FINE_LOCATION_PERMISSION_CONSTANT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //The External Storage Write Permission is granted to you... Continue your left job...
                getLocation();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    //Show Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                    Toast.makeText(MainActivity.this, "Unable to get Permission", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void getLocation() {
        CurrentLocationService appLocationService = new CurrentLocationService(
                this);

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
            LocationAddress.getAddressFromLocation(latitude, longitude,
                    this, new GeocoderHandler());
            SettlerSingleton.getInstance().setMycurrentLatitude(latitude);
            SettlerSingleton.getInstance().setMycurrentLongitude(longitude);
            LatLng userLocation = new LatLng(latitude, longitude);
            replaceFragment(new HomeMapFragment());

        } else {
          /*  dismisPbar();
            addressTxt.setTextSize(12);
            addressTxt.setText(R.string.select_loc_manually);
*/            // Toast.makeText(this, "Not able to fetch your location!Pls click here to select your location manually!", Toast.LENGTH_SHORT).show();

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
            //  addressTxt.setText(currentAddress);
            Log.d("ADREESS ::::: ", currentAddress);
            preferenceManager.putString("user_location", currentAddress);
            SettlerSingleton.getInstance().setMyCurrentAddress(currentAddress);
            //    dismisPbar();
        }
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CartListEvent event) {
        if (event.success) {
            setCartNumTxt(event.mapsStoreModel.size());
        } else {
            UImsgs.showToast(this, event.msg);
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
    public void onBackPressed() {
        // super.onBackPressed();
        onBackpressedd();
    }

    public void setCartNumTxt(int cartSize) {
        cartNumTxt.setText(cartSize + "");
    }
}