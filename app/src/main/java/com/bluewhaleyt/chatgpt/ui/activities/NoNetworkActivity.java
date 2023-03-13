package com.bluewhaleyt.chatgpt.ui.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bluewhaleyt.chatgpt.R;
import com.bluewhaleyt.chatgpt.databinding.ActivityMyFilesBinding;
import com.bluewhaleyt.chatgpt.databinding.ActivityNoNetworkBinding;
import com.bluewhaleyt.common.CommonUtil;
import com.bluewhaleyt.common.IntentUtil;
import com.bluewhaleyt.component.snackbar.SnackbarUtil;
import com.bluewhaleyt.network.NetworkUtil;

public class NoNetworkActivity extends AppCompatActivity {

    private ActivityNoNetworkBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoNetworkBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initialize();
    }

    private void initialize() {
        CommonUtil.setStatusBarColorWithSurface(this, CommonUtil.SURFACE_FOLLOW_WINDOW_BACKGROUND);
        CommonUtil.setToolBarColorWithSurface(this, CommonUtil.SURFACE_FOLLOW_WINDOW_BACKGROUND);
        CommonUtil.setNavigationBarColorWithSurface(this, CommonUtil.SURFACE_FOLLOW_WINDOW_BACKGROUND);

        getSupportActionBar().setTitle("");
        binding.btnRefresh.setOnClickListener(v -> {
            if (NetworkUtil.isNetworkAvailable(this)) IntentUtil.intent(this, MainActivity.class);
            else SnackbarUtil.makeErrorSnackbar(this, "No network available");
        });
    }

}
