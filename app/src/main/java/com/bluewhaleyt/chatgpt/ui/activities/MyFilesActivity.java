package com.bluewhaleyt.chatgpt.ui.activities;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.bluewhaleyt.chatgpt.R;
import com.bluewhaleyt.chatgpt.adapters.FileAdapter;
import com.bluewhaleyt.chatgpt.databinding.ActivityMyFilesBinding;
import com.bluewhaleyt.chatgpt.models.Message;
import com.bluewhaleyt.crashdebugger.CrashDebugger;
import com.bluewhaleyt.filemanagement.FileUtil;

import java.util.ArrayList;

public class MyFilesActivity extends BaseActivity {

    private ActivityMyFilesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyFilesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initialize();
    }

    private void initialize() {
        getSupportActionBar().setTitle(R.string.my_files);

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