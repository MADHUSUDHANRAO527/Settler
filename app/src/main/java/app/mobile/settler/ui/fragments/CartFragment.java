package app.mobile.settler.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.mobile.settler.R;
import app.mobile.settler.ui.adapters.CartAdapter;
import app.mobile.settler.utilities.SettlerSingleton;

/**
 * Created by Madhu on 20/08/17.
 */

public class CartFragment extends Fragment {
    private Context mContext;
    RecyclerView cartRecyclerview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fargement_cart, container, false);
        mContext = getActivity();
        cartRecyclerview = (RecyclerView) v.findViewById(R.id.cart_recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        cartRecyclerview.setLayoutManager(layoutManager);
        CartAdapter cartAdapter = new CartAdapter(mContext, SettlerSingleton.getInstance().getCartModelList());
        cartRecyclerview.setAdapter(cartAdapter);
        return v;
    }
}
