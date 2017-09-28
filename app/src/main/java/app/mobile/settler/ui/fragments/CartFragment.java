package app.mobile.settler.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

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
    ProgressBar progressBar;
    ImageView cartEmptyImg;
    private SwipeRefreshLayout swiperefresh;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fargement_cart, container, false);
        mContext = getActivity();
        volleyHelper = new VolleyHelper(mContext);
        progressBar = (ProgressBar) v.findViewById(R.id.pBar);
        cartEmptyImg = (ImageView) v.findViewById(R.id.cart_empty_img);
        cartRecyclerview = (RecyclerView) v.findViewById(R.id.cart_recyclerview);
        swiperefresh = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        cartRecyclerview.setLayoutManager(layoutManager);
        progressBar.setVisibility(View.VISIBLE);

        volleyHelper.getCartList();
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                volleyHelper.getCartList();

            }
        });
        return v;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CartListEvent event) {
        if (event.success) {
            ((MainActivity) mContext).setCartNumTxt(event.mapsStoreModel.size());
            if (event.mapsStoreModel.size() > 0) {
                CartAdapter cartAdapter = new CartAdapter(mContext, event.mapsStoreModel);
                cartRecyclerview.setAdapter(cartAdapter);
            } else {
                cartEmptyImg.setVisibility(View.VISIBLE);
            }
        } else {
            UImsgs.showToast(mContext, event.msg);
        }
        progressBar.setVisibility(View.GONE);
        swiperefresh.setRefreshing(false);
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
