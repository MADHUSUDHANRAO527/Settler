package app.mobile.settler.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import app.mobile.settler.R;
import app.mobile.settler.models.MapStoresModel;
import app.mobile.settler.ui.activity.BaseActivity;
import app.mobile.settler.utilities.SettlerSingleton;
import app.mobile.settler.utilities.Utils;

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
        calculateDistance();

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        long timer = Long.parseLong(String.valueOf(1200000));

        timer = timer * 1000;

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

    private void calculateDistance() {
        LatLng origin = new LatLng(SettlerSingleton.getInstance().getMycurrentLatitude(), SettlerSingleton.getInstance().getMycurrentLongitude());
        double destLat = Double.parseDouble(cartModel.getStoreLatitude());
        double destLongi = Double.parseDouble(cartModel.getStoreLatitude());
        LatLng dest = new LatLng(destLat, destLongi);

        distanceTxt.setText(String.valueOf(Utils.getDistance(origin, dest)));

    }
}

