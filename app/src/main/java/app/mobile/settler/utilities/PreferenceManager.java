package app.mobile.settler.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * Created by madhu on 29/6/17.
 */

public class PreferenceManager {
    // Shared Preferences
    private SharedPreferences mSharedPreferences;

    // Editor for Shared preferences
    private SharedPreferences.Editor editor;

    // Shared mSharedPreferences mode
    private static final int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "usersharedpreferences";

    // Constructor
    @SuppressLint("CommitPrefEdits")
    public PreferenceManager(Context context) {
        mSharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = mSharedPreferences.edit();
    }

    /**
     * Put string value
     */
    public void putString(String key, String value) {
        // Storing in mSharedPreferences
        editor.putString(key, value);

        // commit changes
        editor.apply();
    }

    /**
     * Put integer value
     */
    public void putInt(String key, int value) {
        // Storing in mSharedPreferences
        editor.putInt(key, value);

        // Commit changes
        editor.commit();
    }

    /**
     * Put long value
     */
    public void putLong(String key, Long value) {
        // Storing in mSharedPreferences
        editor.putLong(key, value);

        // Commit changes
        editor.commit();
    }

    /**
     * Put float value
     */
    public void putFloat(String key, float value) {
        // Storing in mSharedPreferences
        editor.putFloat(key, value);

        // Commit changes
        editor.commit();
    }

    /**
     * Put boolean value
     */
    public void putBoolean(String key, boolean value) {
        // Storing in mSharedPreferences
        editor.putBoolean(key, value);
        // Commit changes
        editor.commit();
    }

    /**
     * Put string set value
     */
    public void putStringSet(String key, Set<String> value) {
        // Storing in mSharedPreferences
        editor.putStringSet(key, value);

        // Commit changes
        editor.commit();
    }

    /**
     * Get stored string data
     */
    public String getString(String key) {
        return mSharedPreferences.getString(key, null);
    }

    /**
     * Get stored boolean data
     */
    public boolean getBoolean(String key) {

        return mSharedPreferences.getBoolean(key, false);

    }

    /**
     * Get stored int data
     */
    public int getInt(String key) {

        return mSharedPreferences.getInt(key, 0);

    }

    /**
     * Get stored float data
     */
    public float getFloat(String key) {

        return mSharedPreferences.getFloat(key, 0f);

    }

    /**
     * Get stored long data
     */
    public long getLong(String key) {

        return mSharedPreferences.getLong(key, 0);

    }

    /**
     * Get stored string set data
     */
    public Set<String> getStringSet(String key) {

        return mSharedPreferences.getStringSet(key, null);

    }

    /**
     * Clearing all data from Shared Preferences
     */
    public void clear() {

        editor.clear();
        editor.commit();

    }

}
