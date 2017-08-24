package app.mobile.settler.ui.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import app.mobile.settler.R;
import app.mobile.settler.models.MapStoresModel;

/**
 * Created by Madhu on 13/08/17.
 */

public class SwipeDeckAdapter extends BaseAdapter {

    private Context context;
    LayoutInflater inflator;
    public ArrayList<MapStoresModel> mapsStoreModel;

    public SwipeDeckAdapter(ArrayList<MapStoresModel> mapStoreModel
            , Context context) {
        this.context = context;
        mapsStoreModel = mapStoreModel;
        inflator = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return mapsStoreModel.size();
    }

    @Override
    public Object getItem(int position) {
        return mapsStoreModel.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView;
        if (v == null) {

            // normally use a viewholder
            v = inflator.inflate(R.layout.offers_deck_row, parent, false);
        }
        ((TextView) v.findViewById(R.id.merchant_name_txt)).setText(mapsStoreModel.get(position).getStorName());
        ((TextView) v.findViewById(R.id.offer_name_txt)).setText(mapsStoreModel.get(position).getOfferName());
        ((TextView) v.findViewById(R.id.active_hrs)).setText("Expires in " + mapsStoreModel.get(position).getActiveHours());
        ((TextView) v.findViewById(R.id.offer_desc_txt)).setText(mapsStoreModel.get(position).getOfferDesc());

        ImageView servicesIcon = (ImageView) v.findViewById(R.id.services_icon);


        Glide.with(context).load(mapsStoreModel.get(position).getImageUrl())
                .into(servicesIcon);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = (String) getItem(position);
                Log.i("MainActivity", item);
            }
        });

        return v;
    }
}