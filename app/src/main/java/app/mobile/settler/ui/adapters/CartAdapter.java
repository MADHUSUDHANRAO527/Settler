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

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ServicesViewHolder> {
    private Context mContext;
    private ArrayList<MapStoresModel> cartModelList;
    private UImsgs uImsgs;

    public CartAdapter(Context context, ArrayList<MapStoresModel> model) {
        this.cartModelList = model;
        this.mContext = context;
        uImsgs = new UImsgs(context);
    }

    @Override
    public ServicesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_row, parent, false);
        return new ServicesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ServicesViewHolder holder, final int position) {
        holder.offerTxt.setText(cartModelList.get(position).getProductName());
        holder.offerNameTxt.setText(cartModelList.get(position).getOfferName());
        holder.expireTxt.setText(cartModelList.get(position).getActiveHours());

        Glide.with(mContext).load(cartModelList.get(position).getImageUrl())
                .into(holder.servicesIcon);
       /* holder.historyRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, HistoryActiveDetailActivity.class);
                Log.d("booking_id", cartModelList.get(position).getId());
                i.putExtra("booking_id", cartModelList.get(position).getId());
                
                mContext.startActivity(i);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return cartModelList.size();
    }

    public class ServicesViewHolder extends RecyclerView.ViewHolder {
        public TextView offerTxt, offerNameTxt, expireTxt;
        public ImageView servicesIcon;
        public RelativeLayout historyRow;

        public ServicesViewHolder(View itemView) {
            super(itemView);
            expireTxt = (TextView) itemView.findViewById(R.id.active_hrs);
            offerTxt = (TextView) itemView.findViewById(R.id.offer_txt);
            offerNameTxt = (TextView) itemView.findViewById(R.id.offer_name_txt);
            servicesIcon = (ImageView) itemView.findViewById(R.id.services_icon);
            historyRow = (RelativeLayout) itemView.findViewById(R.id.history_row);
        }
    }
}
