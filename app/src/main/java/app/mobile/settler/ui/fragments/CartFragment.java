package app.mobile.settler.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.mobile.settler.R;
import app.mobile.settler.events.CartListEvent;
import app.mobile.settler.netwrokHelpers.VolleyHelper;
import app.mobile.settler.ui.activity.MainActivity;
import app.mobile.settler.ui.adapters.CartAdapter;
import app.mobile.settler.utilities.UImsgs;

/**
 * Created by Madhu on 20/08/17.
 */

public class CartFragment extends Fragment {
    private Context mContext;
    RecyclerView cartRecyclerview;
    private VolleyHelper volleyHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fargement_cart, container, false);
        mContext = getActivity();
        volleyHelper = new VolleyHelper(mContext);

        cartRecyclerview = (RecyclerView) v.findViewById(R.id.cart_recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        cartRecyclerview.setLayoutManager(layoutManager);
        volleyHelper.getCartList();
        return v;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CartListEvent event) {
        if (event.success) {
            ((MainActivity) mContext).setCartNumTxt(event.mapsStoreModel.size());
            CartAdapter cartAdapter = new CartAdapter(mContext, event.mapsStoreModel);
            cartRecyclerview.setAdapter(cartAdapter);
        } else {
            UImsgs.showToast(mContext, event.msg);
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

}
