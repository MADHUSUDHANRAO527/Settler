package app.mobile.settler.ui.adapters;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import app.mobile.settler.R;
import app.mobile.settler.models.MapStoresModel;
import app.mobile.settler.ui.activity.BaseActivity;
import app.mobile.settler.ui.fragments.CartDetailFragment;
import app.mobile.settler.utilities.SettlerSingleton;
import app.mobile.settler.utilities.UImsgs;


/**
 * Created by madhu on 21/6/17.
 */

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ServicesViewHolder> {
    private Context mContext;
    private ArrayList<MapStoresModel> cartModelList;
    private UImsgs uImsgs;
    private MapStoresModel cartModel;

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
    public void onBindViewHolder(final ServicesViewHolder holder, final int position) {
        holder.merchantNameTxt.setText(cartModelList.get(position).getMerchantName());
        holder.offerNameTxt.setText(cartModelList.get(position).getOfferName());

        holder.offerDescTxt.setText(cartModelList.get(position).getOfferDesc());
        // holder.expireTxt.setText("Expires in " + cartModelList.get(position).getActiveHours());
        holder.uniqueOfferTxt.setText("Offer Code " + cartModelList.get(position).getOTP());


        if (holder.timer != null) {
            holder.timer.cancel();
        }
        long timer = Long.parseLong(String.valueOf(1200000));

        timer = timer * 1000;

        holder.timer = new CountDownTimer(timer, 1000) {
            public void onTick(long millisUntilFinished) {
//              holder.expireTxt.setText("" + millisUntilFinished/1000 + " Sec");


                int seconds = (int) (millisUntilFinished / 1000) % 60;
                int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
                int hours = (int) ((millisUntilFinished / (1000 * 60 * 60)) % 24);
                String newtime = hours + ":" + minutes + ":" + seconds;

                if (newtime.equals("0:0:0")) {
                    holder.expireTxt.setText("00:00:00");
                } else if ((String.valueOf(hours).length() == 1) && (String.valueOf(minutes).length() == 1) && (String.valueOf(seconds).length() == 1)) {
                    holder.expireTxt.setText("0" + hours + ":0" + minutes + ":0" + seconds);
                } else if ((String.valueOf(hours).length() == 1) && (String.valueOf(minutes).length() == 1)) {
                    holder.expireTxt.setText("0" + hours + ":0" + minutes + ":" + seconds);
                } else if ((String.valueOf(hours).length() == 1) && (String.valueOf(seconds).length() == 1)) {
                    holder.expireTxt.setText("0" + hours + ":" + minutes + ":0" + seconds);
                } else if ((String.valueOf(minutes).length() == 1) && (String.valueOf(seconds).length() == 1)) {
                    holder.expireTxt.setText(hours + ":0" + minutes + ":0" + seconds);
                } else if (String.valueOf(hours).length() == 1) {
                    holder.expireTxt.setText("0" + hours + ":" + minutes + ":" + seconds);
                } else if (String.valueOf(minutes).length() == 1) {
                    holder.expireTxt.setText(hours + ":0" + minutes + ":" + seconds);
                } else if (String.valueOf(seconds).length() == 1) {
                    holder.expireTxt.setText(hours + ":" + minutes + ":0" + seconds);
                } else {
                    holder.expireTxt.setText(hours + ":" + minutes + ":" + seconds);
                }

            }

            public void onFinish() {
                holder.expireTxt.setText("00:00:00");
            }
        }.start();
        holder.cartRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettlerSingleton.getInstance().setMapStoresModel(cartModelList.get(position));
                ((BaseActivity) mContext).addFragment(new CartDetailFragment());
            }
        });


     /*   Glide.with(mContext).load(cartModelList.get(position).getImageUrl())
                .into(holder.servicesIcon);*/
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
        public TextView offerDescTxt, merchantNameTxt, offerNameTxt, expireTxt, uniqueOfferTxt;
        public ImageView servicesIcon;
        public RelativeLayout cartRow;
        CountDownTimer timer;

        public ServicesViewHolder(View itemView) {
            super(itemView);
            expireTxt = (TextView) itemView.findViewById(R.id.active_hrs_txt);
            offerDescTxt = (TextView) itemView.findViewById(R.id.offer_desc_txt);
            merchantNameTxt = (TextView) itemView.findViewById(R.id.merchant_name_txt);
            offerNameTxt = (TextView) itemView.findViewById(R.id.offer_name_txt);
            uniqueOfferTxt = (TextView) itemView.findViewById(R.id.uniques_offer_txt);

            servicesIcon = (ImageView) itemView.findViewById(R.id.services_icon);
            cartRow = (RelativeLayout) itemView.findViewById(R.id.row);
        }
    }
}
