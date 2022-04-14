package com.example.bookmanager.view;

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
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookmanager.R;
import com.example.bookmanager.domain.Book;
import com.example.bookmanager.model.BookErrorType;
import com.example.bookmanager.model.BookOperator;
import com.example.bookmanager.model.BookOperatorListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BookOperatorListener {
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView leftMenu;
    FloatingActionButton floatingAdd;
    RecyclerView bookList;
    List<Book> data = new ArrayList<>();
    BookAdapter adapter = new BookAdapter(this, data, false);
    BookOperator operator = new BookOperator(this);

    private ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Book[] passData = (Book[]) result.getData().getSerializableExtra("BookData");
                    data = Arrays.asList(passData);
                    adapter.setData(data);
                    adapter.notifyDataSetChanged();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        // 查询数据库获取数据
        operator.query(this);

        testData();

        // RecyclerView绑定
        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        bookList.setLayoutManager(linearLayout);
        bookList.setAdapter(adapter);

        // FloatingActionButton监听
        floatingAdd.setOnClickListener(view -> showAddDialog());
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    void testData() {
        operator.insert(new Book("一","无","2022-4-14",20,100), this);
        operator.insert(new Book("二","无","2022-4-14",20,100), this);
        operator.insert(new Book("三","无","2022-4-14",20,100), this);
        operator.insert(new Book("四","无","2022-4-14",20,100), this);
        operator.insert(new Book("五","无","2022-4-14",20,100), this);
        operator.insert(new Book("六","无","2022-4-14",20,100), this);
        operator.insert(new Book("七","无","2022-4-14",20,100), this);
        operator.insert(new Book("八","无","2022-4-14",20,100), this);
        operator.insert(new Book("九","无","2022-4-14",20,100), this);

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
            Book[] passData = (Book[]) data.toArray();
            intent.putExtra("BookData", passData);
            launcher.launch(intent);
        }
        return true;
    }

    private void showAddDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.add_book_dialog, null, false);
        AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();
        // 设置对话框背景为透明
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        // 设置对话框透明度
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.alpha = 0.8f;
        dialog.getWindow().setAttributes(params);

        TextView manualAdd = view.findViewById(R.id.manual_add);
        TextView scanAdd = view.findViewById(R.id.scan_add);
        manualAdd.setOnClickListener(v -> {
            Toast.makeText(this, "手动录入", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        scanAdd.setOnClickListener(v -> {
            Toast.makeText(this, "扫码录入", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        dialog.show();
    }

    @Override
    public void onSuccess(List<Book> data) {
        this.data = data;
        adapter.setData(data);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onError(BookErrorType resultType) {

    }
}