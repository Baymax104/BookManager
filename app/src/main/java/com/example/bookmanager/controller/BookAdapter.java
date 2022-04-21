package com.example.bookmanager.controller;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmanager.controller.callbacks.OnItemClickListener;
import com.example.bookmanager.domain.Book;
import com.example.bookmanager.domain.FinishBook;

import java.util.List;

/**
 * @Description
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/18 18:10
 * @Version
 */
public abstract class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {
    protected List<? extends Book> data;
    protected boolean isEdit;
    protected OnItemClickListener onItemClickListener;
    protected static abstract class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    protected BookAdapter(List<? extends Book> data, boolean isEdit) {
        this.data = data;
        this.isEdit = isEdit;
    }

    protected abstract void setData(List<? extends Book> data);
    protected abstract void setOnItemClickListener(OnItemClickListener listener);
}
