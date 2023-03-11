package com.bluewhaleyt.chatgpt.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bluewhaleyt.chatgpt.R;
import com.bluewhaleyt.chatgpt.adapters.MessageAdapter;
import com.bluewhaleyt.chatgpt.databinding.ActivityMainBinding;
import com.bluewhaleyt.chatgpt.models.Message;
import com.bluewhaleyt.chatgpt.utils.NotificationUtil;
import com.bluewhaleyt.chatgpt.utils.OpenAIClient;
import com.bluewhaleyt.chatgpt.utils.PreferencesManager;
import com.bluewhaleyt.common.CommonUtil;
import com.bluewhaleyt.common.DynamicColorsUtil;
import com.bluewhaleyt.common.IntentUtil;
import com.bluewhaleyt.common.PermissionUtil;
import com.bluewhaleyt.common.SDKUtil;
import com.bluewhaleyt.component.dialog.DialogUtil;
import com.bluewhaleyt.component.snackbar.SnackbarUtil;
import com.bluewhaleyt.crashdebugger.CrashDebugger;
import com.bluewhaleyt.filemanagement.FileUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends BaseActivity {

    private static ActivityMainBinding binding;
    private static OpenAIClient openAIClient;

    private List<Message> messages;
    private static MessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initialize();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupChatGPT();
        requestNotificationPermission();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_my_files:
                IntentUtil.intent(this, MyFilesActivity.class);
                break;
            case R.id.menu_settings:
                IntentUtil.intent(this, SettingsActivity.class);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initialize() {

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        requestPermission();

        setupChatList();
        binding.btnSend.setOnClickListener(v -> sendMessage());
        binding.btnClear.setOnClickListener(v -> clearAllChats());

        var gd = new GradientDrawable();
        var dynamic = new DynamicColorsUtil(this);
        gd.setColor(dynamic.getColorPrimaryContainer());
        gd.setAlpha(100);
        gd.setCornerRadius(100);
        binding.etMessage.setBackground(gd);

        updateIsShowSpacer();
        if (!adapter.isEmpty()) updateIsRequesting(true);
    }

    private void requestPermission() {
        if (PermissionUtil.isAlreadyGrantedExternalStorageAccess()) {
            FileUtil.makeDirectory(Message.MESSAGES_SAVE_PATH);
        } else PermissionUtil.requestAllFileAccess(this);
    }

    private void setupChatList() {
        var rvList = binding.rvChatList;
        messages = new ArrayList<>();
        adapter = new MessageAdapter(messages, this);

        CommonUtil.waitForTimeThenDo(3000, () -> {
            binding.layoutRvContainer.setVisibility(View.VISIBLE);
            binding.rvChatList.smoothScrollToPosition(messages.size() - 1);
            updateIsRequesting(false);
            return null;
        });

        var layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvList.setLayoutManager(layoutManager);
        rvList.setAdapter(adapter);

        // 從SharedPreferences中讀取已儲存的項目數據
        var json = PreferencesManager.getChatContext();
        var gson = new Gson();
        var type = new TypeToken<ArrayList<Message>>() {}.getType();
        messages = gson.fromJson(json, type);

        // 如果SharedPreferences中不存在已儲存的項目數據，則創建一個空的列表
        if (messages == null) messages = new ArrayList<>();

        // 設置Adapter的數據
        adapter.setMessageList(this, messages);

        rvList.setSectionIndexer(position -> {
            var message = messages.get(position);
            String str;
            if (message.isSentByUser()) {
                str = getString(R.string.you);
                int lineBreakIndex = message.getMessage().indexOf("\n");
                var firstLine = (lineBreakIndex != -1) ? message.getMessage().substring(0, lineBreakIndex) : message.getMessage();

                return "[" + str + "]" + ": " + firstLine;
            } else return null;
        });
    }

    private void setupChatGPT() {
        if (PreferencesManager.getOpenAIAPIKey().equals("")) {
            SnackbarUtil.makeSnackbar(this, getString(R.string.api_key_missing));
            binding.etMessage.setVisibility(View.GONE);
            binding.btnSend.setVisibility(View.GONE);
            binding.rvChatList.setVisibility(View.GONE);
        } else {
            binding.etMessage.setVisibility(View.VISIBLE);
            binding.btnSend.setVisibility(View.VISIBLE);
            binding.rvChatList.setVisibility(View.VISIBLE);

            openAIClient = new OpenAIClient();
            openAIClient.setModel(PreferencesManager.getOpenAIModel());
            openAIClient.setApiUrl(getAPIURL());
            openAIClient.setApiKey(PreferencesManager.getOpenAIAPIKey());
            var enable = openAIClient.getModel().equals(OpenAIClient.TEXT_DAVINCI_003);
            openAIClient.setMaxTokensEnabled(enable);
            openAIClient.setMaxTokens(Double.parseDouble(PreferencesManager.getOpenAIMaxTokens()));
            openAIClient.setTemperature(Double.parseDouble(PreferencesManager.getOpenAITemperature()));
            openAIClient.setEcho(PreferencesManager.isOpenAIEcho());
        }
    }

    public void sendMessage() {
        var userInput = binding.etMessage.getText().toString();
        if (!TextUtils.isEmpty(userInput)) {
            addMessage(userInput, true);
            binding.etMessage.setText("");
            updateIsRequesting(true);
            try {
                openAIClient.setPrompt(Message.getContext() + "\n" + userInput);
                openAIClient.generateResponse(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        showError(e.getMessage());
                    }
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        var responseBody = response.body().string();
                        try {
                            addMessage(openAIClient.parseResponse(responseBody), false);
                        } catch (JSONException e) {
                            try {
                                showError(openAIClient.parseError(responseBody));
                                removeMessage(messages.size() - 1);
                            } catch (JSONException ex) {
                                showError(ex.getMessage());
                            }
                        }
                    }
                });
            } catch (Exception e) {
                showError(e.getMessage());
            }
        }
    }

    private void addMessage(String message, boolean isSentByUser) {
        var chatMessage = new Message(message, isSentByUser, Calendar.getInstance().getTimeInMillis());
        runOnUiThread(() -> {
            messages.add(chatMessage);
            adapter.notifyItemInserted(messages.size() - 1);
            adapter.saveData(this);
            update();
            updateIsShowSpacer();
        });

//        if (!isSentByUser) {
//            var notify = new NotificationUtil(this);
//            var intent = new Intent(this, MainActivity.class);
//            notify.showNotification(getString(R.string.bot), message, intent);
//        }
    }

    private void removeMessage(int position) {
        runOnUiThread(() -> {
            messages.remove(position);
            adapter.notifyDataSetChanged();
            update();
        });
    }

    private void update() {
        binding.rvChatList.smoothScrollToPosition(messages.size() - 1);
        updateIsRequesting(false);
    }

    private void updateIsRequesting(boolean bool) {
        binding.progressIndicator.setVisibility(bool ? View.VISIBLE : View.INVISIBLE);
    }

    private void updateIsShowSpacer() {
        binding.spacer.setVisibility(adapter.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void showError(String error) {
        SnackbarUtil.makeErrorSnackbar(this, error);
    }

    public static OpenAIClient getOpenAIClient() {
        return openAIClient;
    }

    private void clearAllChats() {
        var dialog = new DialogUtil(this, getString(R.string.clear));
        dialog.setMessage(getString(R.string.clear_desc));
        dialog.setPositiveButton(android.R.string.ok, (d, i) -> {
            adapter.removeAllMessages(this);
            adapter = new MessageAdapter(new ArrayList<>(), this);
            binding.rvChatList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            initialize();
        });
        dialog.setNegativeButton(android.R.string.cancel, null);
        dialog.build();
    }

    private void requestNotificationPermission() {
        if (!NotificationUtil.isNotificationEnabled(this)) {
            Intent intent = new Intent();
            if (SDKUtil.isAtLeastSDK26()) {
                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
            } else if (SDKUtil.isAtLeastSDK21()) {
                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                intent.putExtra("app_package", getPackageName());
                intent.putExtra("app_uid", getApplicationInfo().uid);
            } else {
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + getPackageName()));
            }
            startActivity(intent);
        }
    }

    private String getAPIURL() {
        var url = "";
        switch (openAIClient.getModel()) {
            case OpenAIClient.TEXT_DAVINCI_003:
                url = OpenAIClient.COMPLETION_URL;
                break;
            case OpenAIClient.GPT_3_5_TURBO:
                url = OpenAIClient.CHAT_COMPLETION_URL;
                break;
        }
        return url;
    }

    public void tellMeMore() {
        binding.etMessage.setText(R.string.tell_me_more);
        sendMessage();
    }

}