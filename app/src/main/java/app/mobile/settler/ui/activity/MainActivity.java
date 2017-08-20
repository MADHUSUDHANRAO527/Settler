package app.mobile.settler.ui.activity;

import android.os.Bundle;

import app.mobile.settler.R;
import app.mobile.settler.ui.fragments.HomeMapFragment;

public class MainActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        replaceFragment(new HomeMapFragment());
    }
    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        onBackpressedd();
    }
}