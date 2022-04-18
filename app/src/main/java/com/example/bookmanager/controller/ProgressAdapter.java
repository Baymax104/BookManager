package com.example.bookmanager.controller;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmanager.R;
import com.example.bookmanager.controller.callbacks.OnItemClickListener;
import com.example.bookmanager.domain.Book;
import com.example.bookmanager.domain.ProgressBook;


import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/13 16:26
 * @Version
 */
public class ProgressAdapter extends BookAdapter {
    private Context context;
    private View view;

    public ProgressAdapter(Context context, List<? extends Book> data, boolean isEdit) {
        super(data,isEdit);
        this.context = context;
    }

    @Override
    protected void setData(List<? extends Book> data) {
        this.data = data;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public static class ProgressViewHolder extends BookAdapter.ViewHolder {
        TextView name;
        TextView author;
        TextView addTime;
        ProgressBar readProgress;
        ImageView sortImg;
        TextView progressNumber;
        public ProgressViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.book_name);
            author = itemView.findViewById(R.id.book_author);
            addTime = itemView.findViewById(R.id.add_time);
            readProgress = itemView.findViewById(R.id.read_progress);
            sortImg = itemView.findViewById(R.id.sort_img);
            progressNumber = itemView.findViewById(R.id.num_progress);
        }
    }

    @NonNull
    @Override
    public ProgressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_progress_item, parent, false);
        return new ProgressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProgressViewHolder viewHolder = (ProgressViewHolder) holder;
        ProgressBook book = (ProgressBook) data.get(position);
        Resources resources = view.getResources();
        viewHolder.name.setText(book.getName());
        viewHolder.author.setText(book.getAuthor());
        viewHolder.addTime.setText(book.getAddTime());
        double proportion = book.getProgress() * 1.0 / book.getPage();
        int progress = (int) (proportion * 100);
        viewHolder.readProgress.setProgress(progress);
        viewHolder.progressNumber.setText(String.format(resources.getString(R.string.progress_display),book.getProgress(),book.getPage()));
        if (isEdit) {
            viewHolder.sortImg.setVisibility(View.VISIBLE);
        }
        if (!isEdit) {
            viewHolder.itemView.setOnClickListener(view -> onItemClickListener.onItemClick(position));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}
