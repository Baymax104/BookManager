package com.example.bookmanager.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bookmanager.R;
import com.example.bookmanager.domain.Book;
import com.example.bookmanager.domain.History;
import com.example.bookmanager.domain.ProgressBook;
import com.example.bookmanager.model.BookException;
import com.example.bookmanager.model.DialogsHelper;
import com.example.bookmanager.model.HistoryOperator;
import com.example.bookmanager.model.HistoryOperatorListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.List;
import java.util.Objects;

public class HistoryActivity extends AppCompatActivity implements HistoryOperatorListener {

    private RecyclerView historyList;
    private Book book;
    private HistoryAdapter adapter;
    private HistoryOperator operator;
    private List<History> data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        initView();

        adapter = new HistoryAdapter(data, this);
        operator = new HistoryOperator(this, book);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        historyList.setLayoutManager(manager);
        historyList.setAdapter(adapter);

        operator.query(this);

        adapter.setOnItemClickListener(position -> DialogsHelper.showHistoryEditDialog(this));
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.tool_bar);
        CollapsingToolbarLayout collapsingLayout = findViewById(R.id.collapse_bar);
        ImageView imageBar = findViewById(R.id.book_img_bar);
        historyList = findViewById(R.id.history_list);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back);
        }
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        Intent intent = getIntent();
        book = (Book) intent.getSerializableExtra("book");

        collapsingLayout.setTitle(book.getName());
        collapsingLayout.setCollapsedTitleTextColor(Color.BLACK);
        if (book.getCover() != null) {
            Glide.with(this).load(book.getCover()).into(imageBar);
        } else if (book.getCoverUrl() != null) {
            Glide.with(this).load(book.getCoverUrl()).into(imageBar);
        } else {
            Glide.with(this).load(R.drawable.no_cover).into(imageBar);
        }
    }

    public static void actionStart(Context context, ProgressBook book) {
        Intent intent = new Intent(context, HistoryActivity.class);
        intent.putExtra("book", book);
        context.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public void onSuccess(List<History> data, int... position) {
        this.data = data;
        adapter.setData(data);
        if (position.length != 0) {
            adapter.notifyItemRemoved(position[0]);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onError(BookException e) {
        switch (e.errorType) {
            case INSERT_ERROR:
                Toast.makeText(this, "添加:"+ e, Toast.LENGTH_SHORT).show();
                Log.e("MainActivity",e.toString());
                break;
            case UPDATE_ERROR:
                Toast.makeText(this, "更新:"+e, Toast.LENGTH_SHORT).show();
                Log.e("MainActivity",e.toString());
                break;
            case QUERY_ERROR:
                Toast.makeText(this, "查询:"+e, Toast.LENGTH_SHORT).show();
                Log.e("MainActivity", e.toString());
                break;
            default:
                break;
        }
    }
}