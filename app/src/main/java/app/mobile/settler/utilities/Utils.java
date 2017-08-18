package app.mobile.settler.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import app.mobile.settler.R;


/**
 * Created by madhu on 10/7/17.
 */

public class Utils {

    public static String convertToDateFromUTC(String createdAt) {
        if (createdAt.length() > 10) {
            String datee = createdAt.substring(0, 10);
            String fromYear = datee.substring(0, 4);
            try {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                Date dat = df.parse(datee);
                System.out.println(createdAt);
                createdAt = dat.toString();
                System.out.println(createdAt.length());
                createdAt = createdAt.substring(3, 10);
                System.out.println(createdAt);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return createdAt + "," + fromYear;
        } else {
            return createdAt;
        }
    }

    public static Bitmap setMarkerSize(Context mContext) {
        int height = 95;
        int width = 95;
        BitmapDrawable bitmapdraw = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.marker);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        return smallMarker;
    }
    public static Bitmap getResizedBitmap(Bitmap bm) {
        int newWidth = 80,newHeight = 80;

        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    //july 14th 2017
    public static String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    //2009-12-31
    public static String getCurrentDateFormat() {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    //CompareDates
    public static boolean comapareDates(String fromDate, String toDate) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); //edit here
        Date todayDate = sdf.parse(fromDate);
        Date toDatee = sdf.parse(toDate);
        if (todayDate.before(toDatee) || todayDate.equals(toDatee)) {
            return true;
        }
        return false;
    }

}
