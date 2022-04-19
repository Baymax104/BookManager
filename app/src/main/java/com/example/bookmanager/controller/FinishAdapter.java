package com.example.bookmanager.controller;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.bookmanager.R;
import com.example.bookmanager.controller.callbacks.OnItemClickListener;
import com.example.bookmanager.domain.Book;
import com.example.bookmanager.domain.FinishBook;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/18 17:00
 * @Version
 */
public class FinishAdapter extends BookAdapter {
    private Context context;
    private View view;

    public FinishAdapter(Context context, List<? extends Book> data, boolean isEdit) {
        super(data,isEdit);
        this.context = context;
    }
    @Override
    public void setData(List<? extends Book> data) {
        this.data = data;
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public FinishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_finish_item,parent,false);
        return new FinishViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookAdapter.ViewHolder holder, int position) {
        FinishViewHolder viewHolder = (FinishViewHolder) holder;
        FinishBook book = (FinishBook) data.get(position);
        Resources resources = view.getResources();
        viewHolder.name.setText(book.getName());
        viewHolder.author.setText(book.getAuthor());
        viewHolder.time.setText(String.format(resources.getString(R.string.book_time),book.getAddTime(),book.getEndTime()));
        viewHolder.page.setText(String.format(resources.getString(R.string.info_page),book.getPage()));
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

    public static class FinishViewHolder extends BookAdapter.ViewHolder {
        private TextView name;
        private TextView author;
        private ImageView sortImg;
        private TextView time;
        private TextView page;
        public FinishViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.book_name);
            author = itemView.findViewById(R.id.book_author);
            sortImg = itemView.findViewById(R.id.sort_img);
            time = itemView.findViewById(R.id.book_time);
            page = itemView.findViewById(R.id.book_page);
        }
    }
}
