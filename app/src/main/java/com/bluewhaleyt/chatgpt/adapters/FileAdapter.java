package com.bluewhaleyt.chatgpt.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bluewhaleyt.chatgpt.R;
import com.bluewhaleyt.component.dialog.DialogUtil;
import com.bluewhaleyt.filemanagement.FileIconUtil;
import com.bluewhaleyt.filemanagement.FileUtil;

import java.io.File;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {

    private List<?> fileList;

    public FileAdapter(List<?> fileList) {
        this.fileList = fileList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_file_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        var context = holder.itemView.getContext();
        var file = new File((String) fileList.get(position));
        holder.tvFileName.setText(file.getName());
        holder.tvFilePath.setText(file.getAbsolutePath());

        var fileIcon = new FileIconUtil(file.getAbsolutePath(), "");
        fileIcon.bindFileIcon(holder.ivFileIcon);

        var dialog = new DialogUtil(context, file.getName());
        dialog.setMessage(FileUtil.readFile(file.getAbsolutePath()));
        dialog.setNeutralButton(android.R.string.ok, null);
        dialog.create();
        holder.itemView.setOnClickListener(v -> dialog.show());
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFileName, tvFilePath;
        ImageView ivFileIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            tvFileName = itemView.findViewById(R.id.tv_file_name);
            tvFilePath = itemView.findViewById(R.id.tv_file_path);
            ivFileIcon = itemView.findViewById(R.id.iv_file_icon);
        }
    }
}