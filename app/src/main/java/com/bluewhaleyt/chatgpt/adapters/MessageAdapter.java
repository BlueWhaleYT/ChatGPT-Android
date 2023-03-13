package com.bluewhaleyt.chatgpt.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import com.bluewhaleyt.chatgpt.ui.activities.MainActivity;
import com.bluewhaleyt.chatgpt.R;
import com.bluewhaleyt.chatgpt.databinding.DialogLayoutMarkdownViewBinding;
import com.bluewhaleyt.chatgpt.databinding.DialogLayoutSaveMessageBinding;
import com.bluewhaleyt.chatgpt.models.Message;
import com.bluewhaleyt.chatgpt.utils.MarkedView;
import com.bluewhaleyt.common.DynamicColorsUtil;
import com.bluewhaleyt.component.dialog.DialogUtil;
import com.bluewhaleyt.component.snackbar.SnackbarUtil;
import com.bluewhaleyt.filemanagement.FileUtil;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private static List<Message> messages;
    private ThreadPoolExecutor executor;

    private MainActivity mainActivity;

    public MessageAdapter(List<Message> messages, MainActivity activity) {
        this.messages = messages;
        this.mainActivity = activity;
        this.
        executor = new ThreadPoolExecutor(
                1,
                1,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>()
        );
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        var context = holder.itemView.getContext();
        var dynamic = new DynamicColorsUtil(context);
        var message = messages.get(position);

        holder.layoutMessageContainer.setVisibility(View.VISIBLE);
        holder.mvMessage.setMDText(message.getMessage());

        var gd = new GradientDrawable();
        gd.setCornerRadius(40);
        if (message.isSentByUser()) {
            holder.layoutFooter.setVisibility(View.GONE);
            holder.ivAvatar.setImageResource(R.drawable.user_avatar);
            gd.setColor(dynamic.getColorPrimaryContainer());
            gd.setAlpha(80);

            holder.tvRequestTime.setVisibility(View.GONE);
        } else {
            holder.layoutFooter.setVisibility(View.VISIBLE);
            holder.ivAvatar.setImageResource(R.drawable.chatgpt_avatar);
            gd.setColor(dynamic.getColorOnSurfaceVariant());
            gd.setAlpha(10);
            setMargin(holder.layoutMessageBox, 0, 20, 0, 0);

            holder.tvRequestTime.setVisibility(View.VISIBLE);
            holder.tvRequestTime.setText(context.getString(R.string.request_time) + ": " + formatTime(context, message.getRequestTime()));
        }

        holder.layoutMessageBox.setBackground(gd);

        holder.btnView.setOnClickListener(v -> viewMarkdown(context, message.getMessage()));
        holder.btnShare.setOnClickListener(v -> shareMessage(context, message.getMessage()));
        holder.btnSave.setOnClickListener(v -> saveMessageAsFile(context, message.getMessage()));
        holder.btnTellMeMore.setOnClickListener(v -> mainActivity.tellMeMore());

        displaySentTime(holder, message);

        itemLongClick(holder, position);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        return R.layout.layout_message_list_item;
    }

    private void itemLongClick(MessageViewHolder holder, int position) {
        var context = holder.itemView.getContext();

        var dialog = new DialogUtil(context);
        dialog.setTitle(context.getString(R.string.delete));
        dialog.setMessage(messages.get(position).getMessage());
        dialog.setPositiveButton(android.R.string.ok, ((d, i) -> {
            removeMessage(context, position);
        }));
        dialog.setNegativeButton(android.R.string.cancel, null);
        dialog.create();

        holder.itemView.setOnLongClickListener(v -> {
            dialog.show();
            return true;
        });
    }

    private void removeMessage(Context context, int position) {
        try {
            messages.remove(position);
            notifyItemRemoved(position);
            saveData(context);
        } catch (Exception e) {
            removeAllMessages(context);
        }
    }

    public void removeAllMessages(Context context) {
        var preferences = PreferenceManager.getDefaultSharedPreferences(context);
        var editor = preferences.edit();
        editor.remove("message_list");
        editor.apply();
    }

    public boolean isEmpty() {
        return messages.isEmpty();
    }

    private void displaySentTime(MessageViewHolder holder, Message message) {
        long timeMillis = (long) message.getSentTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat("d/MM/yyyy - h:mm a");
        String time = dateFormat.format(calendar.getTime());
        holder.tvTime.setText(time);
    }

    private void viewMarkdown(Context context, String text) {
        var inflater = LayoutInflater.from(context);
        var binding = DialogLayoutMarkdownViewBinding.inflate(inflater);
        var botDialog = new BottomSheetDialog(context);
        botDialog.setContentView(binding.getRoot());
        botDialog.create();
        botDialog.show();

        binding.markdownView.setMDText(text);
    }

    private void shareMessage(Context context, String message) {
        var shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);
        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share)));
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutMessageContainer, layoutMessageBox, layoutFooter;
        MarkedView mvMessage;
        ImageView ivAvatar;
        MaterialButton btnView, btnShare, btnSave, btnTellMeMore;
        TextView tvTime, tvRequestTime;
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            var context = itemView.getContext();
            layoutMessageContainer = itemView.findViewById(R.id.layout_message_container);
            layoutMessageBox = itemView.findViewById(R.id.layout_message_box);
            layoutFooter = itemView.findViewById(R.id.layout_footer);
            mvMessage = itemView.findViewById(R.id.mv_message);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            btnView = itemView.findViewById(R.id.btn_view);
            btnShare = itemView.findViewById(R.id.btn_share);
            btnSave = itemView.findViewById(R.id.btn_save);
            btnTellMeMore = itemView.findViewById(R.id.btn_tell_me_more);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvRequestTime = itemView.findViewById(R.id.tv_request_time);
        }
    }

    private String formatTime(Context context, double millis) {
        long seconds = (long) (millis / 1000);
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;

        if (minutes > 0) {
            return String.format(
                    Locale.US,
                    "%d " + context.getString(R.string.min) + " %d " + context.getString(R.string.second),
                    minutes, remainingSeconds);
        } else {
            return String.format(Locale.US, "%d " + context.getString(R.string.second), remainingSeconds);
        }
    }

    public void setMessageList(Context context, List<Message> messageList) {
        messages = messageList;
        notifyDataSetChanged();
        saveData(context);
    }

    public void saveData(Context context) {
        var preferences = PreferenceManager.getDefaultSharedPreferences(context);
        var editor = preferences.edit();
        var gson = new Gson();
        var json = gson.toJson(messages);
        editor.putString("message_list", json);
        editor.apply();
    }

    private void saveMessageAsFile(Context context, String text) {
        var inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        var binding = DialogLayoutSaveMessageBinding.inflate(inflater);
        var dialog = new DialogUtil(context, context.getString(R.string.save));
        dialog.setMessage(context.getString(R.string.save_desc, Message.MESSAGES_SAVE_PATH));
        dialog.setPositiveButton(android.R.string.ok, (d, i) -> {
            var fileName = binding.editText.getText().toString();
            var path = Message.MESSAGES_SAVE_PATH + fileName;
            FileUtil.writeFile(path, text);
            SnackbarUtil.makeSnackbar((Activity) context, context.getString(R.string.save_success, Message.MESSAGES_SAVE_PATH));
        });
        dialog.setNegativeButton(android.R.string.cancel, null);
        dialog.setView(binding.getRoot());
        dialog.build();
    }

    private void setMargin(View v, int left, int top, int right, int bottom) {
        var params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
        params.setMargins(left, top, right, bottom);
        v.setLayoutParams(params);
    }
}
