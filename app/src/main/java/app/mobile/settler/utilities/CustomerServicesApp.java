package app.mobile.settler.utilities;

import android.app.Application;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.android.volley.ExecutorDelivery;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;

import java.util.concurrent.ThreadPoolExecutor;

import static com.android.volley.VolleyLog.TAG;

/**
 * Created by Madhu on 19/06/17.
 */

public class CustomerServicesApp extends Application {
    private static CustomerServicesApp customerServicesAppInstance;
    RequestQueue mRequestQueue;
    private static final String DEFAULT_CACHE_DIR = "volley";
    private static final int DEFAULT_NETWORK_THREAD_POOL_SIZE = 4;
    private ImageLoader mImageLoader;

    @Override
    public void onCreate() {
        super.onCreate();
        customerServicesAppInstance = this;
    }
    public static synchronized CustomerServicesApp getInstance() {
        return customerServicesAppInstance;
    }
    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            java.io.File cacheDir = new java.io.File(getCacheDir(), DEFAULT_CACHE_DIR);
            Network network = new BasicNetwork(new HurlStack());
            ThreadPoolExecutor tp = (ThreadPoolExecutor) AsyncTask.THREAD_POOL_EXECUTOR;
            tp.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
            ExecutorDelivery exec = new ExecutorDelivery(tp);
            mRequestQueue = new RequestQueue(new DiskBasedCache(cacheDir), network,
                    DEFAULT_NETWORK_THREAD_POOL_SIZE, exec);
            mRequestQueue.start();
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }
    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }
}
