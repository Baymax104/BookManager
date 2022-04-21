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
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookmanager.R;
import com.example.bookmanager.controller.callbacks.BookTouchCallback;
import com.example.bookmanager.controller.callbacks.IMoveSwipeCallback;
import com.example.bookmanager.domain.Book;
import com.example.bookmanager.model.BookException;
import com.example.bookmanager.model.BookOperator;
import com.example.bookmanager.model.BookType;
import com.example.bookmanager.model.FinishOperator;
import com.example.bookmanager.model.BookOperatorListener;
import com.example.bookmanager.model.ProgressOperator;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;


public class EditActivity extends AppCompatActivity implements IMoveSwipeCallback, BookOperatorListener {

    private RecyclerView bookList;
    private TextView noDataTip;
    private List<Book> data;
    private BookAdapter adapter;
    private BookOperator operator;
    private Stack<DelItem> delStack = new Stack<>();

    private static class DelItem {
        Book delBook;
        int position;
        public DelItem(Book delBook,int position) {
            this.delBook = delBook;
            this.position = position;
        }
    }

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
        noDataTip = findViewById(R.id.no_data_tip);
        Toolbar toolbar = findViewById(R.id.tool_bar);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        // 获取数据，同时根据类型初始化operator和adapter
        Intent intent = getIntent();
        Book[] passData = (Book[]) intent.getSerializableExtra("BookData");
        // 返回值不是util.ArrayList，是Arrays的内部类，直接使用会抛出异常
        data = new ArrayList<>(Arrays.asList(passData));
        BookType type = (BookType) intent.getSerializableExtra("Type");
        switch (type) {
            case ProgressBook:
                operator = new ProgressOperator(this);
                adapter = new ProgressAdapter(this,data,true);
                break;
            case FinishBook:
                operator = new FinishOperator(this);
                adapter = new FinishAdapter(this,data,true);
                break;
            default:
                break;
        }
        adapter.setData(data);
        if (data.size() == 0) {
            noDataTip.setVisibility(View.VISIBLE);
        } else {
            noDataTip.setVisibility(View.INVISIBLE);
        }

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
        Book delBook = data.get(position);
        // 将删除的子项先压栈，撤销时出栈，在退出Activity时删除
        delStack.push(new DelItem(delBook,position));
        data.remove(position);
        adapter.notifyItemRemoved(position);
        Snackbar snackbar = Snackbar.make(bookList, "已删除", Snackbar.LENGTH_LONG)
                .setBackgroundTint(Color.parseColor("#1e90ff"))
                .setTextColor(Color.WHITE)
                .setAction("撤销", view -> {
                    data.add(position,  delBook);
                    delStack.pop();
                    adapter.notifyItemInserted(position);
                });
        snackbar.show();
    }

    @Override
    public void onBackPressed() {
        while (!delStack.empty()) {
            DelItem item = delStack.pop();
            operator.delete(item.delBook,item.position,this);
        }
        Book[] passData = data.toArray(new Book[0]);
        setResult(RESULT_OK, new Intent().putExtra("BookData", passData));
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // 出栈删除
            while (!delStack.empty()) {
                DelItem delItem = delStack.pop();
                operator.delete(delItem.delBook,delItem.position,this);
            }
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
        if (data.size() == 0) {
            noDataTip.setVisibility(View.VISIBLE);
        } else {
            noDataTip.setVisibility(View.INVISIBLE);
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