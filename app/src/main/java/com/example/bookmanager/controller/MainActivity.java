package com.example.bookmanager.controller;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.bookmanager.R;
import com.example.bookmanager.controller.callbacks.IRequestCallback;
import com.example.bookmanager.domain.Book;
import com.example.bookmanager.domain.RequestBook;
import com.example.bookmanager.model.BookException;
import com.example.bookmanager.model.BookOperator;
import com.example.bookmanager.model.BookOperateListener;
import com.example.bookmanager.model.RequestHelper;
import com.example.bookmanager.controller.callbacks.DialogCallback;
import com.example.bookmanager.model.DialogsHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.king.zxing.CameraScan;


import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;

public class MainActivity extends AppCompatActivity implements BookOperateListener,DialogCallback, IRequestCallback {
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView leftMenu;
    private FloatingActionButton floatingAdd;
    private RecyclerView bookList;
    private List<Book> data = new ArrayList<>();
    private BookAdapter adapter = new BookAdapter(this, data, false);
    private BookOperator operator = new BookOperator(this);

    private ActivityResultLauncher<Intent> editLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Book[] passData = (Book[]) result.getData().getSerializableExtra("BookData");
                    data = Arrays.asList(passData);
                    adapter.setData(data);
                    adapter.notifyDataSetChanged();
                }
            });

    private ActivityResultLauncher<Intent> scanLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String isbnCode = CameraScan.parseScanResult(result.getData());
                    RequestHelper.sendRequest(isbnCode, this);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SQLiteStudioService.instance().start(this);
        initView();

        // RecyclerView绑定
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        bookList.setLayoutManager(layoutManager);
        bookList.setAdapter(adapter);

        // 查询数据库获取数据
        operator.query(this);

        // FloatingActionButton监听
        floatingAdd.setOnClickListener(view -> DialogsHelper.showAddDialog(this, this));

        // RecyclerView子项点击监听
        adapter.setItemClickListener(position -> DialogsHelper.showUpdateDialog(
                this,position,data,this
        ));

    }

    private void initView() {
        toolbar = findViewById(R.id.tool_bar);
        drawerLayout = findViewById(R.id.drawer_layout);
        leftMenu = findViewById(R.id.nav_view);
        floatingAdd = findViewById(R.id.floating_add);
        bookList = findViewById(R.id.book_list);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // 显示home键，更换图标
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.nav_menu);
            // 隐藏原标题
            actionBar.setDisplayShowTitleEnabled(false);
        }
        // 左滑菜单图标显示颜色
        leftMenu.setItemIconTintList(null);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.read) {
            Toast.makeText(this, "我看过的书", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.settings) {
            Toast.makeText(this, "设置", Toast.LENGTH_SHORT).show();
        } else if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
        } else if (id == R.id.edit_list) {
            Intent intent = new Intent(this, BookEditActivity.class);
            Book[] passData = data.toArray(new Book[0]);
            intent.putExtra("BookData", passData);
            editLauncher.launch(intent);
        }
        return true;
    }


    @Override
    public void scanBarcode() {
        Intent intent = new Intent(this, BookCapture.class);
        scanLauncher.launch(intent);
    }

    @Override
    public void insertBook(Book book) {
        operator.insert(book, this);
    }

    @Override
    public void updateBook(Book book) {
        operator.update(book, this);
    }

    @Override
    public void onSuccess(List<Book> data, int... position) {
        this.data = data;
        adapter.setData(data);
        adapter.notifyDataSetChanged();
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
    public void getRequestBook(RequestBook requestBook) {
        runOnUiThread(() -> DialogsHelper.showInfoDialog(
                this,requestBook,this
        ));
    }

    @Override
    public void getRequestError(Exception e) {
        Looper.prepare();
        if (e instanceof IOException) {
            Toast.makeText(this, "连接失败:" + e, Toast.LENGTH_SHORT).show();
            Log.e("MainActivity", e.toString());
        } else if (e instanceof JSONException) {
            Toast.makeText(this, "解析失败:" + e, Toast.LENGTH_SHORT).show();
            Log.e("MainActivity", e.toString());
        } else {
            Toast.makeText(this, "未知错误:" + e, Toast.LENGTH_SHORT).show();
            Log.e("MainActivity", e.toString());
            e.printStackTrace();
        }
        Looper.loop();
    }
}