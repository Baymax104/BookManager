package com.example.bookmanager.controller;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookmanager.R;
import com.example.bookmanager.controller.callbacks.OnItemClickListener;
import com.example.bookmanager.domain.Book;
import com.example.bookmanager.domain.History;

import java.util.List;

/**
 * @Description
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/21 13:46
 * @Version
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<History> data;
    private Context context;
    private OnItemClickListener listener;
    private View view;

    public HistoryAdapter(List<History> data, Context context) {
        this.data = data;
        this.context = context;
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        TextView historyTime;
        TextView historyPage;
        ImageView timeDot;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            historyTime = itemView.findViewById(R.id.history_time);
            historyPage = itemView.findViewById(R.id.history_page);
            timeDot = itemView.findViewById(R.id.time_dot);
        }
    }

    public void setData(List<History> data) {
        this.data = data;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_history_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        History history = data.get(position);
        holder.historyTime.setText(history.getUpdateTime());
        Resources resources = view.getResources();
        if (history.getStartPage() == 1 && history.getEndPage() == 1) {
            holder.historyPage.setText("开始阅读");
            holder.timeDot.setImageResource(R.drawable.time_dot_start);
        } else if (history.getEndPage() == history.getPage()) {
            holder.historyPage.setText("完成阅读");
            holder.timeDot.setImageResource(R.drawable.time_dot_end);
        } else {
            holder.historyPage.setText(String.format(resources.getString(R.string.history_page),history.getStartPage(),history.getEndPage()));
            holder.timeDot.setImageResource(R.drawable.time_dot);
        }
        holder.historyPage.setOnClickListener(view1 -> listener.onItemClick(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
