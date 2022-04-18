package com.example.bookmanager.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.bookmanager.R;
import com.example.bookmanager.controller.callbacks.BookTouchCallback;
import com.example.bookmanager.controller.callbacks.IMoveSwipeCallback;
import com.example.bookmanager.domain.Book;
import com.example.bookmanager.model.BookException;
import com.example.bookmanager.model.BookOperator;
import com.example.bookmanager.model.BookOperateListener;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BookEditActivity extends AppCompatActivity implements IMoveSwipeCallback, BookOperateListener {

    private RecyclerView bookList;
    private List<Book> data = new ArrayList<>();
    private BookAdapter adapter = new BookAdapter(this, data, true);
    private BookOperator operator = new BookOperator(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_edit);
        initView();

        // 绑定RecyclerView和关联拖动接口
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
        Book[] passData = (Book[]) intent.getSerializableExtra("BookData");
        data = Arrays.asList(passData);
        adapter.setData(data);
        Toast.makeText(this, "滑动删除，拖动排序哦~", Toast.LENGTH_SHORT).show();
        // 修改状态栏为亮色主题
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    @Override
    public void onMove(int fromPosition, int toPosition) {
        Book fromBook = data.get(fromPosition);
        Book toBook = data.get(toPosition);
        operator.swap(fromBook, toBook, fromPosition, toPosition, this);
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
        // TODO 撤销恢复后重复删除
        operator.delete(deletedBook, position, this);
    }

    @Override
    public void onBackPressed() {
        Book[] passData = data.toArray(new Book[0]);
        setResult(RESULT_OK, new Intent().putExtra("BookData", passData));
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Book[] passData = data.toArray(new Book[0]);
            setResult(RESULT_OK, new Intent().putExtra("BookData", passData));
            finish();
        }
        return true;
    }

    @Override
    public void onSuccess(List<Book> data, int... position) {
        this.data = data;
        adapter.setData(data);
        // 删除后返回删除位置，交换后返回两个交换位置
        if (position.length == 1) {
            int deletedPosition = position[0];
            adapter.notifyItemRemoved(deletedPosition);
        } else {
            int fromPosition = position[0];
            int toPosition = position[1];
            adapter.notifyItemMoved(fromPosition, toPosition);
        }
    }

    @Override
    public void onError(BookException e) {
        switch (e.errorType) {
            case DELETE_ERROR:
                Toast.makeText(this, "删除:"+e, Toast.LENGTH_SHORT).show();
                Log.e("BookEditActivity",e.toString());
                break;
            case SORT_ERROR:
                Toast.makeText(this, "排序:"+e, Toast.LENGTH_SHORT).show();
                Log.e("BookEditActivity",e.toString());
                break;
            default:
                break;
        }
    }
}