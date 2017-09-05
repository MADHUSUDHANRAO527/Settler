package app.mobile.settler.netwrokHelpers;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.mobile.settler.events.AddToCartEvent;
import app.mobile.settler.events.CartListEvent;
import app.mobile.settler.events.MapStoresHomeEvent;
import app.mobile.settler.models.MapStoresModel;
import app.mobile.settler.utilities.Constants;
import app.mobile.settler.utilities.CustomerServicesApp;
import app.mobile.settler.utilities.PreferenceManager;


/**
 * Created by Madhu on 17/06/17.
 */

public class VolleyHelper {
    private static final String TAG = "Volley";
    private Context mContext;
    private PreferenceManager mPreferenceManager;

    public VolleyHelper(Context context) {
        this.mContext = context;
        mPreferenceManager = new PreferenceManager(context);
    }

    public void getStoresList(final JSONObject json) {

        StringRequest jsonObjRequest = new StringRequest(Request.Method.POST,
                Constants.BASE_URL + Constants.STORES_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response.toString());
                        //JSONObject jsonObject = detailJson.getJSONObject("data");

                        Gson gson = new Gson();
                        Type type = new TypeToken<ArrayList<MapStoresModel>>() {
                        }.getType();
                        ArrayList<MapStoresModel> model = gson.fromJson(response.toString(), type);
                        EventBus.getDefault().post(new MapStoresHomeEvent(true, 200, model));

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("volley", "Error: " + error.getMessage());
                error.printStackTrace();
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("MobileNo", "9999999999");
                params.put("SettlerId", "Str100");
                params.put("UserId", "Test100");
                params.put("UserLatitude", "19.286812");
                params.put("UserLongitude", "72.874334");

                return params;
            }

        };
        jsonObjRequest.setShouldCache(false);
        jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, 2));
        CustomerServicesApp.getInstance().addToRequestQueue(jsonObjRequest);
    }

    public void addToCart(final String offerId) {
        StringRequest jsonObjRequest = new StringRequest(Request.Method.POST,
                Constants.BASE_URL + Constants.STORES_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response.toString());
                        //JSONObject jsonObject = detailJson.getJSONObject("data");

                        EventBus.getDefault().post(new AddToCartEvent(true, 200));

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("volley", "Error: " + error.getMessage());
                error.printStackTrace();
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("MobileNo", "9999999999");
                params.put("SettlerId", "Str100");
                params.put("UserId", "Test100");
                params.put("OfferId", offerId);
                return params;
            }

        };
        jsonObjRequest.setShouldCache(false);
        jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, 2));
        CustomerServicesApp.getInstance().addToRequestQueue(jsonObjRequest);
    }

    public void getCartList() {

        StringRequest jsonObjRequest = new StringRequest(Request.Method.POST,
                Constants.BASE_URL + Constants.STORES_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response.toString());
                        //JSONObject jsonObject = detailJson.getJSONObject("data");

                        Gson gson = new Gson();
                        Type type = new TypeToken<ArrayList<MapStoresModel>>() {
                        }.getType();
                        ArrayList<MapStoresModel> model = gson.fromJson(response.toString(), type);
                        EventBus.getDefault().post(new CartListEvent(true, 200, model));

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("volley", "Error: " + error.getMessage());
                error.printStackTrace();
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("MobileNo", "9999999999");
                params.put("SettlerId", "Str100");
                params.put("UserId", "Test100");

                return params;
            }

        };
        jsonObjRequest.setShouldCache(false);
        jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, 2));
        CustomerServicesApp.getInstance().addToRequestQueue(jsonObjRequest);
    }
}
