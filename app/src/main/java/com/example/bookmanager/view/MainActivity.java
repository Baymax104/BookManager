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
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookmanager.R;
import com.example.bookmanager.domain.Book;
import com.example.bookmanager.model.BookException;
import com.example.bookmanager.model.BookOperator;
import com.example.bookmanager.model.BookOperatorListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.king.zxing.CameraScan;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;

public class MainActivity extends AppCompatActivity implements BookOperatorListener {
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView leftMenu;
    FloatingActionButton floatingAdd;
    RecyclerView bookList;
    List<Book> data = new ArrayList<>();
    BookAdapter adapter = new BookAdapter(this, data, false);
    BookOperator operator = new BookOperator(this);

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
                    String scanResult = CameraScan.parseScanResult(result.getData());
                    Toast.makeText(this, scanResult, Toast.LENGTH_SHORT).show();
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
        floatingAdd.setOnClickListener(view -> showAddDialog());

        adapter.setItemClickListener(this::showUpdateDialog);
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
            showManualAddDialog();
            dialog.dismiss();
        });
        scanAdd.setOnClickListener(v -> {
            scanBarcode();
            dialog.dismiss();
        });
        dialog.show();
    }

    private void showManualAddDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.manual_insert_dialog, null, false);
        AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();

        // 设置点击空白部分对话框不消失
        dialog.setCanceledOnTouchOutside(false);
        // 设置对话框背景
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView confirm = view.findViewById(R.id.insert_confirm);
        TextView cancel = view.findViewById(R.id.insert_cancel);
        cancel.setOnClickListener(view1 -> dialog.dismiss());
        confirm.setOnClickListener(view1 -> {
            EditText inputName = view.findViewById(R.id.input_book_name);
            EditText inputAuthor = view.findViewById(R.id.input_book_author);
            EditText inputPage = view.findViewById(R.id.input_book_page);

            if (inputName != null && !inputName.getText().toString().equals("") &&
                inputAuthor != null && !inputAuthor.getText().toString().equals("") &&
                inputPage != null && !inputPage.getText().toString().equals("")) {

                String name = inputName.getText().toString();
                String author = inputAuthor.getText().toString();
                int page = Integer.parseInt(inputPage.getText().toString());
                Date nowTime = new Date(System.currentTimeMillis());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String addTime = dateFormat.format(nowTime);
                Book book = new Book(name, author, addTime, 0, page);
                operator.insert(book, this);
                dialog.dismiss();
            } else {
                Toast.makeText(this, "请输入完整信息", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    private void showUpdateDialog(int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.update_progress_dialog, null, false);
        AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView confirm = view.findViewById(R.id.update_confirm);
        TextView cancel = view.findViewById(R.id.update_cancel);
        confirm.setOnClickListener(view1 -> {
            EditText inputProgress = view.findViewById(R.id.input_book_progress);
            if (inputProgress != null && !inputProgress.getText().toString().equals("")) {
                int progress = Integer.parseInt(inputProgress.getText().toString());
                Book book = data.get(position);
                if (progress <= book.getPage()) {
                    book.setProgress(progress);
                    operator.update(book, this);
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "超过了最大页数", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "输入不能为空", Toast.LENGTH_SHORT).show();
            }
        });
        cancel.setOnClickListener(view1 -> dialog.dismiss());
        dialog.show();
    }

    private void scanBarcode() {
        Intent intent = new Intent(this, BookCapture.class);
        scanLauncher.launch(intent);
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
                Toast.makeText(this, "添加："+ e, Toast.LENGTH_SHORT).show();
                Log.e("MainActivity",e.toString());
                break;
            case UPDATE_ERROR:
                Toast.makeText(this, "更新："+e, Toast.LENGTH_SHORT).show();
                Log.e("MainActivity",e.toString());
                break;
            case QUERY_ERROR:
                Toast.makeText(this, "查询："+e, Toast.LENGTH_SHORT).show();
                Log.e("MainActivity", e.toString());
                break;
            default:
                break;
        }
    }
}