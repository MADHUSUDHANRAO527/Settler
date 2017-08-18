package app.mobile.settler.utilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import app.mobile.settler.R;


/**
 * Created by madhu on 21/6/17.
 */

public class UImsgs {
    static ProgressDialog progressDialog;
    private Context context;

    public UImsgs(Context mContext) {
        this.context = mContext;
    }


    public static void showToastErrorMessage(Context context, int respCode) {
        if (respCode == 400) {
            showToast(context, R.string.bad_request);
        } else if (respCode == 401 || respCode == 403) {
            showToast(context, R.string.invalid_sign_in);
        } else if (respCode == 404) {
            showToast(context, R.string.no_resource);
        } else if (respCode == 500) {
            showToast(context, R.string.internal_server_error);
        } else if (respCode == 501) {
            showToast(context, R.string.unimplemented);
        } else if (respCode == 502) {
            showToast(context, R.string.bad_gateway);
        } else if (respCode == 503) {
            showToast(context, R.string.service_unavailable);
        } else if (respCode == 504) {
            showToast(context, R.string.server_not_reachable);
        } else if (respCode == -1) {
            showToast(context, R.string.certificate_unavailable);
        }
    }

    public static void showToast(Context context, int msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showProgressDialog(String message, Context mContext) {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("loading");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public static void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public Typeface setRobotoThin() {
        Typeface font = Typeface.createFromAsset(
                context.getAssets(),
                "fonts/androidnation.ttf");
        return font;
    }

    public static void showSnackBar(View view, String msg) {
        Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_SHORT);
        view = snackbar.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public static void showSnackBar(View view, int msg) {
        Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_SHORT);
        view = snackbar.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.show();
    }
}
