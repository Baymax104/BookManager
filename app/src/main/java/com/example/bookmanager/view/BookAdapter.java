package com.example.bookmanager.view;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmanager.R;
import com.example.bookmanager.domain.Book;
import com.google.android.material.card.MaterialCardView;


import java.util.List;

/**
 * @Description
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/13 16:26
 * @Version
 */
public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {
    private List<Book> data;
    private Context context;
    private boolean isEdit;
    private OnItemClickListener itemClickListener;

    public BookAdapter() {}
    public BookAdapter(Context context, List<Book> data, boolean isEdit) {
        this.context = context;
        this.data = data;
        this.isEdit = isEdit;
    }

    public void setData(List<Book> data) {
        this.data = data;
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView bookName;
        TextView author;
        TextView addTime;
        ProgressBar readProgress;
        MaterialCardView cardView;
        ImageView sortImg;
        TextView progressNumber;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bookName = itemView.findViewById(R.id.book_name);
            addTime = itemView.findViewById(R.id.add_time);
            readProgress = itemView.findViewById(R.id.read_progress);
            cardView = itemView.findViewById(R.id.card_view);
            sortImg = itemView.findViewById(R.id.sort_img);
            progressNumber = itemView.findViewById(R.id.num_progress);
            author = itemView.findViewById(R.id.book_author);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book book = data.get(position);
        holder.bookName.setText(book.getName());
        holder.author.setText(book.getAuthor());
        holder.addTime.setText(book.getAddTime());
        double proportion = book.getProgress() * 1.0 / book.getPage();
        int progress = (int) (proportion * 100);
        holder.readProgress.setProgress(progress);
        holder.progressNumber.setText(book.getProgress()+"/"+ book.getPage());
        if (isEdit) {
            holder.sortImg.setVisibility(View.VISIBLE);
        }
        if (!isEdit) {
            holder.itemView.setOnClickListener(view -> itemClickListener.onItemClick(position));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}
