package com.niucong.punchcardclient;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.niucong.punchcardclient.databinding.ActivityMainBinding;

public class MainActivity extends BasicActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
//        setContentView(R.layout.activity_main);
//        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        binding.setHandlers(new MainClickHandlers());
    }

    public class MainClickHandlers {
        public void onClickName(View v) {
            Log.d("MainActivity", "MainClickHandlers");
            switch (v.getId()) {
                case R.id.main_sign:

                    break;
                case R.id.main_sync:

                    break;
            }
        }
    }

}
