package com.bluewhaleyt.chatgpt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.bluewhaleyt.chatgpt.adapters.FileAdapter;
import com.bluewhaleyt.chatgpt.databinding.ActivityMainBinding;
import com.bluewhaleyt.chatgpt.databinding.ActivityMyFilesBinding;
import com.bluewhaleyt.chatgpt.models.Message;
import com.bluewhaleyt.common.CommonUtil;
import com.bluewhaleyt.crashdebugger.CrashDebugger;
import com.bluewhaleyt.filemanagement.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MyFilesActivity extends AppCompatActivity {

    private ActivityMyFilesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CrashDebugger.init(this);
        binding = ActivityMyFilesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initialize();
    }

    private void initialize() {
        CommonUtil.setStatusBarColorWithSurface(this, CommonUtil.SURFACE_FOLLOW_WINDOW_BACKGROUND);
        CommonUtil.setToolBarColorWithSurface(this, CommonUtil.SURFACE_FOLLOW_WINDOW_BACKGROUND);
        CommonUtil.setNavigationBarColorWithSurface(this, CommonUtil.SURFACE_FOLLOW_WINDOW_BACKGROUND);

        setupFileList();
    }

    private void setupFileList() {
        var layoutManager = new LinearLayoutManager(this);
        binding.rvFileList.setLayoutManager(layoutManager);

        var list = new ArrayList<String>();
        FileUtil.listOnlyFilesSubDirFiles(Message.MESSAGES_SAVE_PATH, list);
        var adapter = new FileAdapter(list);
        binding.rvFileList.setAdapter(adapter);
    }

}