package app.mobile.settler.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
    TextView offerExpireTxt;

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
        final ViewHolder holder;
        if (convertView == null) {
            convertView = inflator.inflate(R.layout.offers_deck_row, null);
            holder = new ViewHolder();
            holder.merchantNameTxt = (TextView) convertView
                    .findViewById(R.id.merchant_name_txt);
            holder.offerNameTxt = (TextView) convertView
                    .findViewById(R.id.offer_name_txt);

            holder.offerDescTxt = (TextView) convertView
                    .findViewById(R.id.offer_desc_txt);
            holder.offerIcon = (ImageView) convertView
                    .findViewById(R.id.services_icon);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.merchantNameTxt.setText(mapsStoreModel.get(position).getMerchantName());
        holder.offerNameTxt.setText(mapsStoreModel.get(position).getOfferName());
        holder.offerDescTxt.setText(mapsStoreModel.get(position).getOfferDesc());

        Glide.with(context).load(mapsStoreModel.get(position).getImageUrl())
                .into(holder.offerIcon);



        return convertView;
    }

    public class ViewHolder {
        public TextView offerDescTxt, merchantNameTxt, offerNameTxt, expireTxt, uniqueOfferTxt;
        public ImageView offerIcon;
        public RelativeLayout cartRow;
    }
}