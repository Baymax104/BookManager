package com.example.bookmanager.view;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.admin.SecurityLog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.bookmanager.R;
import com.example.bookmanager.domain.Book;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class BookEditActivity extends AppCompatActivity implements IMoveAndSwipeCallback {

    private RecyclerView bookList;
    private BookAdapter adapter;
    private List<Book> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_edit);
        initView();

        // 绑定RecyclerView和关联拖动接口
        adapter = new BookAdapter(this, data, true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        bookList.setLayoutManager(layoutManager);
        bookList.setAdapter(adapter);
        BookTouchCallback callback = new BookTouchCallback(this);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(bookList);
    }

    private void initView() {
        bookList = findViewById(R.id.book_list);
        Toolbar toolbar = findViewById(R.id.tool_bar);


        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        Intent intent = getIntent();
        data = (List<Book>) intent.getSerializableExtra("BookData");
        Toast.makeText(this, "左滑删除，拖动排序哦~", Toast.LENGTH_SHORT).show();
        // 修改状态栏为亮色主题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    @Override
    public void onMove(int fromPosition, int toPosition) {
        Collections.swap(data, fromPosition, toPosition);
        adapter.notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onSwiped(int position) {
        Book deletedBook = data.get(position);
        Snackbar snackbar = Snackbar.make(bookList, "已删除", Snackbar.LENGTH_LONG)
                                    .setBackgroundTint(Color.parseColor("#1e90ff"))
                                    .setTextColor(Color.WHITE)
                                    .setAction("撤销", view -> {
                                        data.add(position, deletedBook);
                                        adapter.notifyItemInserted(position);
                                    });
        snackbar.show();
        data.remove(position);
        adapter.notifyItemRemoved(position);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK, new Intent().putExtra("BookData", (Serializable) data));
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            setResult(RESULT_OK, new Intent().putExtra("BookData", (Serializable) data));
            finish();
        }
        return true;
    }

}