package com.example.bookmanager.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bookmanager.R;
import com.example.bookmanager.controller.callbacks.IDialogCallback;
import com.example.bookmanager.domain.Book;
import com.example.bookmanager.domain.FinishBook;
import com.example.bookmanager.domain.History;
import com.example.bookmanager.domain.ProgressBook;
import com.example.bookmanager.model.BookException;
import com.example.bookmanager.model.DialogsHelper;
import com.example.bookmanager.model.HistoryOperator;
import com.example.bookmanager.model.HistoryOperatorListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.List;

public class HistoryActivity extends AppCompatActivity implements HistoryOperatorListener, IDialogCallback {

    private RecyclerView historyList;
    private ProgressBar progressBar;
    private TextView updateProgress;
    private TextView progressNum;
    private ImageButton restartButton;
    private Book book;
    private boolean isFinish;
    private HistoryAdapter adapter;
    private HistoryOperator operator;
    private List<History> data;
    private BroadcastReceiver finishReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String endTime = intent.getStringExtra("endTime");
            Intent intent1 = new Intent("com.example.bookmanager.DELETE_FINISH_BOOK");
            ProgressBook progressBook = (ProgressBook) book;
            intent1.putExtra("book", progressBook);
            intent1.putExtra("endTime", endTime);
            sendBroadcast(intent1);
        }
    };
    private BroadcastReceiver restartReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent intent1 = new Intent("com.example.bookmanager.DELETE_RESTART_BOOK");
            FinishBook finishBook = (FinishBook) book;
            intent1.putExtra("book", finishBook);
            sendBroadcast(intent1);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        IntentFilter finishFilter = new IntentFilter();
        finishFilter.addAction("com.example.bookmanager.FINISH_BOOK");
        IntentFilter restartFilter = new IntentFilter();
        restartFilter.addAction("com.example.bookmanager.RESTART");
        registerReceiver(finishReceiver,finishFilter);
        registerReceiver(restartReceiver, restartFilter);

        Intent intent = getIntent();
        book = (Book) intent.getSerializableExtra("book");
        isFinish = intent.getBooleanExtra("isFinish", true);

        adapter = new HistoryAdapter(data, this);
        operator = new HistoryOperator(this, book);
        operator.query(this);
        initView();

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        manager.setReverseLayout(true);
        historyList.setLayoutManager(manager);
        historyList.setAdapter(adapter);

        if (!isFinish) {
            adapter.setOnItemClickListener(position -> DialogsHelper.showHistoryEditDialog(this,position,this));
            updateProgress.setOnClickListener(view -> {
                    History history = data.get(data.size() - 1);
                    if (history.getEndPage() != history.getPage()) {
                        DialogsHelper.showUpdateDialog(this, data, this);
                    }
            });
        } else {
            restartButton.setOnClickListener(view -> DialogsHelper.showRestartConfirmDialog(this));
        }
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.tool_bar);
        CollapsingToolbarLayout collapsingLayout = findViewById(R.id.collapse_bar);
        ImageView imageBar = findViewById(R.id.book_img_bar);
        FrameLayout progressFrameLayout = findViewById(R.id.progress_frame_layout);
        restartButton = findViewById(R.id.restart_button);
        historyList = findViewById(R.id.history_list);
        updateProgress = findViewById(R.id.update_progress);
        progressNum = findViewById(R.id.history_progress);
        progressBar = findViewById(R.id.history_progress_bar);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back);
        }
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        collapsingLayout.setTitle(book.getName());
        collapsingLayout.setCollapsedTitleTextColor(Color.BLACK);
        if (book.getCover() != null) {
            Glide.with(this).load(book.getCover()).into(imageBar);
        } else if (book.getCoverUrl() != null) {
            Glide.with(this).load(book.getCoverUrl()).into(imageBar);
        } else {
            Glide.with(this).load(R.drawable.no_cover).into(imageBar);
        }

        if (isFinish) {
            progressFrameLayout.setVisibility(View.GONE);
            restartButton.setVisibility(View.VISIBLE);
        } else {
            ProgressBook book1 = (ProgressBook) book;
            Resources resources = getResources();
            progressNum.setText(String.format(resources.getString(R.string.progress_display),book1.getProgress(),book1.getPage()));
            double pro = book1.getProgress() * 1.0 / book1.getPage();
            int progress = (int) (pro * 100);
            progressBar.setProgress(progress);
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(finishReceiver);
        unregisterReceiver(restartReceiver);
        super.onDestroy();
    }

    public static void actionStart(Context context, Book book, boolean isFinish) {
        Intent intent = new Intent(context, HistoryActivity.class);
        intent.putExtra("book", book);
        intent.putExtra("isFinish", isFinish);
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
    public void insertHistory(History history) {
        Resources resources = getResources();
        progressNum.setText(String.format(resources.getString(R.string.progress_display),history.getEndPage(),history.getPage()));
        double pro = history.getEndPage() * 1.0 / history.getPage();
        int progress = (int) (pro * 100);
        progressBar.setProgress(progress);
        operator.insert(history,this);
        ProgressBook book1 = (ProgressBook) book;
        ProgressBook updateBook = new ProgressBook(
                book1.getName(),book1.getAuthor(),book1.getAddTime(),
                history.getEndPage(),book1.getPage(),book1.getHistory(),
                book1.getCover(),book1.getCoverUrl()
        );
        Intent intent = new Intent("com.example.bookmanager.UPDATE_PROGRESS");
        intent.putExtra("book", updateBook);
        sendBroadcast(intent);
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

    @Override
    public void deleteHistory(int position) {
        History history = data.get(position);
        if (history.getId() == 1) { // 第一条历史
            Toast.makeText(this, "该记录不可删除", Toast.LENGTH_SHORT).show();
        } else if (position == data.size() - 1) { // 最后一条历史，需要更新进度
            Resources resources = getResources();
            progressNum.setText(String.format(resources.getString(R.string.progress_display),history.getStartPage(),history.getPage()));
            double pro = history.getStartPage() * 1.0 / history.getPage();
            int progress = (int) (pro * 100);
            progressBar.setProgress(progress);
            ProgressBook book1 = (ProgressBook) book;
            ProgressBook updateBook = new ProgressBook(
                    book1.getName(),book1.getAuthor(),book1.getAddTime(),
                    history.getStartPage(),book1.getPage(),book1.getHistory(),
                    book1.getCover(),book1.getCoverUrl()
            );
            operator.delete(history, position, this);
            Intent intent = new Intent("com.example.bookmanager.UPDATE_PROGRESS");
            intent.putExtra("book", updateBook);
            sendBroadcast(intent);
        } else { // 中途历史，不需要更改进度
            History preHistory = data.get(position - 1);
            History postHistory = data.get(position + 1);
            postHistory.setStartPage(preHistory.getEndPage());
            operator.update(postHistory, this);
            operator.delete(history, position, this);
        }
    }

    @Override
    public void startSelectTime(int position) {
        DialogsHelper.showDatePicker(this, position, this);
    }

    @Override
    public void updateHistory(String updateTime, int position) {
        History history = data.get(position);
        history.setUpdateTime(updateTime);
        // 若修改开始阅读时间，则通知ProgressActivity修改
        if (history.getId() == 1) {
            Intent intent = new Intent("com.example.bookmanager.UPDATE_ADD_TIME");
            ProgressBook book1 = (ProgressBook) book;
            book1.setAddTime(updateTime);
            intent.putExtra("book", book1);
            sendBroadcast(intent);
        }
        operator.update(history, this);
    }

}