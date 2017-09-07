package app.mobile.settler.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daprlabs.cardstack.SwipeDeck;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import app.mobile.settler.R;
import app.mobile.settler.events.AddToCartEvent;
import app.mobile.settler.models.MapStoresModel;
import app.mobile.settler.netwrokHelpers.VolleyHelper;
import app.mobile.settler.ui.adapters.SwipeDeckAdapter;
import app.mobile.settler.utilities.SettlerSingleton;
import app.mobile.settler.utilities.UImsgs;

/**
 * Created by Madhu on 23/08/17.
 */

public class CardDeckOfferFragment extends Fragment {
    private Context mContext;
    SwipeDeck cardStack;
    TextView noMoreCardsTxt, expireTxt;
    ArrayList<MapStoresModel> cartModelList = new ArrayList<>();
    ImageView declineOfferImage, acceptCartImg;
    int globalPos;
    private VolleyHelper volleyHelper;
    CountDownTimer countDownTimer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.offers_fragment, container, false);
        mContext = getActivity();
        cardStack = (SwipeDeck) v.findViewById(R.id.swipe_deck);
        noMoreCardsTxt = (TextView) v.findViewById(R.id.no_more_cards_txt);
        expireTxt = (TextView) v.findViewById(R.id.active_hrs_txt);
        declineOfferImage = (ImageView) v.findViewById(R.id.close_icon);
        acceptCartImg = (ImageView) v.findViewById(R.id.accept_icon);
        volleyHelper = new VolleyHelper(mContext);

        SwipeDeckAdapter adapter = new SwipeDeckAdapter(SettlerSingleton.getInstance().getSetOffersDataModel(), mContext);
        cardStack.setAdapter(adapter);

        startTimer();


        cardStack.setEventCallback(new SwipeDeck.SwipeEventCallback() {
            @Override
            public void cardSwipedLeft(int position) {
                Log.i("MainActivity", "card was swiped left, position in adapter: " + position);
                globalPos = position;
                startTimer();
            }

            @Override
            public void cardSwipedRight(int position) {
                Log.i("MainActivity", "card was swiped right, position in adapter: " + position);
                globalPos = position;
                startTimer();
                volleyHelper.addToCart(SettlerSingleton.getInstance().getSetOffersDataModel().get(globalPos).getOfferId());
                //cartModelList.add(SettlerSingleton.getInstance().getSetOffersDataModel().get(position));
                Log.d("CART SIZE: ", SettlerSingleton.getInstance().getSetOffersDataModel().size() + "");
                UImsgs.showToast(mContext, R.string.offer_add_to_cart);
                // SettlerSingleton.getInstance().setCartModelList(cartModelList);
                //  ((MainActivity) mContext).setCartNumTxt();
                callCartListAPI();

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

    public void startTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        long timer = Long.parseLong(String.valueOf(120000));

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
}
