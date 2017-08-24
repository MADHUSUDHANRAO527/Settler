package app.mobile.settler.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daprlabs.cardstack.SwipeDeck;

import java.util.ArrayList;

import app.mobile.settler.R;
import app.mobile.settler.models.MapStoresModel;
import app.mobile.settler.ui.activity.MainActivity;
import app.mobile.settler.ui.adapters.SwipeDeckAdapter;
import app.mobile.settler.utilities.SettlerSingleton;
import app.mobile.settler.utilities.UImsgs;

/**
 * Created by Madhu on 23/08/17.
 */

public class CardDeckOfferFragment extends Fragment {
    private Context mContext;
    SwipeDeck cardStack;
    TextView noMoreCardsTxt, cartNumTxt;
    ArrayList<MapStoresModel> cartModelList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.offers_fragment, container, false);
        mContext = getActivity();
        cardStack = (SwipeDeck) v.findViewById(R.id.swipe_deck);
        noMoreCardsTxt = (TextView) v.findViewById(R.id.no_more_cards_txt);
        SwipeDeckAdapter adapter = new SwipeDeckAdapter(SettlerSingleton.getInstance().getSetOffersDataModel(), mContext);
        cardStack.setAdapter(adapter);

        cardStack.setEventCallback(new SwipeDeck.SwipeEventCallback() {
            @Override
            public void cardSwipedLeft(int position) {
                Log.i("MainActivity", "card was swiped left, position in adapter: " + position);
            }

            @Override
            public void cardSwipedRight(int position) {
                Log.i("MainActivity", "card was swiped right, position in adapter: " + position);
                cartModelList.add(SettlerSingleton.getInstance().getSetOffersDataModel().get(position));
                Log.d("CART SIZE: ", SettlerSingleton.getInstance().getSetOffersDataModel().size() + "");
                UImsgs.showToast(mContext, R.string.offer_add_to_cart);
                SettlerSingleton.getInstance().setCartModelList(cartModelList);
                ((MainActivity)mContext).setCartNumTxt();

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
}
