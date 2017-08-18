package app.mobile.settler.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import app.mobile.settler.R;
import app.mobile.settler.models.MapStoresModel;
import app.mobile.settler.utilities.UImsgs;


/**
 * Created by madhu on 21/6/17.
 */

public class ServicesListAdapter extends RecyclerView.Adapter<ServicesListAdapter.ServicesViewHolder> {
    private Context mContext;
    public ArrayList<MapStoresModel> mapsStoreModel;
    private UImsgs uImsgs;

    public ServicesListAdapter(Context context, ArrayList<MapStoresModel> model) {
        this.mapsStoreModel = model;
        this.mContext = context;
        uImsgs = new UImsgs(context);
    }

    @Override
    public ServicesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.services_row, parent, false);
        return new ServicesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ServicesViewHolder holder, final int position) {
        holder.storeTitleTxt.setText(mapsStoreModel.get(position).getStorName());
        holder.prodcutNameTxt.setText(mapsStoreModel.get(position).getProductName());
        holder.offerTxt.setText(mapsStoreModel.get(position).getOfferName());
        //holder.statusTxt.setText(mapsStoreModel.get(position).getStorName());

        Glide.with(mContext).load(mapsStoreModel.get(position).getImageUrl())
                .into(holder.servicesIcon);
       /* holder.historyRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, HistoryActiveDetailActivity.class);
                Log.d("booking_id", mapsStoreModel.get(position).getId());
                i.putExtra("booking_id", mapsStoreModel.get(position).getId());
                
                mContext.startActivity(i);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return mapsStoreModel.size();
    }

    public class ServicesViewHolder extends RecyclerView.ViewHolder {
        public TextView storeTitleTxt, prodcutNameTxt, offerTxt;
        public ImageView servicesIcon;
        public RelativeLayout historyRow;

        public ServicesViewHolder(View itemView) {
            super(itemView);
           // storeTitleTxt = (TextView) itemView.findViewById(R.id.store_txt);
           // prodcutNameTxt = (TextView) itemView.findViewById(R.id.product_name_txt);
            offerTxt = (TextView) itemView.findViewById(R.id.offer_txt);
            servicesIcon = (ImageView) itemView.findViewById(R.id.services_icon);
            historyRow = (RelativeLayout) itemView.findViewById(R.id.history_row);
        }
    }
}
