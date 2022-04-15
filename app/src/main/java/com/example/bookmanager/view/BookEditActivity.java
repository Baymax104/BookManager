package com.example.bookmanager.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.example.bookmanager.R;
import com.example.bookmanager.domain.Book;
import com.example.bookmanager.model.BookErrorType;
import com.example.bookmanager.model.BookOperator;
import com.example.bookmanager.model.BookOperatorListener;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BookEditActivity extends AppCompatActivity implements IMoveAndSwipeCallback, BookOperatorListener {

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
        operator.delete(deletedBook, this);
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
    public void onSuccess(List<Book> data) {
        this.data = data;
        adapter.setData(data);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onError(BookErrorType resultType) {
        switch (resultType) {
            case DELETE_ERROR:
                Toast.makeText(this, "删除错误！", Toast.LENGTH_SHORT).show();
                break;
            case SORT_ERROR:
                Toast.makeText(this, "排序失败！", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}