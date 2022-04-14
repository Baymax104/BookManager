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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookmanager.R;
import com.example.bookmanager.domain.Book;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView leftMenu;
    FloatingActionButton floatingAdd;
    RecyclerView bookList;
    List<Book> data;
    BookAdapter adapter;

    private ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    List<Book> newData = (List<Book>) result.getData().getSerializableExtra("BookData");
                    data = newData;
                    adapter.setData(newData);
                    adapter.notifyDataSetChanged();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        data = new ArrayList<>();
        testData();

        // RecyclerView绑定
        adapter = new BookAdapter(this, data, false);
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
        data.add(new Book("第一本书", "2022-4-13", "佚名", 20, 100));
        data.add(new Book("第二本书", "2022-4-13", "佚名",20, 100));
        data.add(new Book("第三本书", "2022-4-13", "佚名",20, 100));
        data.add(new Book("第四本书", "2022-4-13", "佚名",20, 100));
        data.add(new Book("第五本书", "2022-4-13", "佚名",20, 100));
        data.add(new Book("第六本书", "2022-4-13", "佚名",20, 100));
        data.add(new Book("第七本书", "2022-4-13", "佚名",20, 100));
        data.add(new Book("第八本书", "2022-4-13", "佚名",20, 100));
        data.add(new Book("第九本书", "2022-4-13", "佚名",20, 100));
        data.add(new Book("第十本书", "2022-4-13", "佚名",20, 100));
        data.add(new Book("第十一本书", "2022-4-13", "佚名",20, 100));
        data.add(new Book("第十二本书", "2022-4-13", "佚名",20, 100));
        data.add(new Book("第十三本书", "2022-4-13", "佚名",20, 100));
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
            intent.putExtra("BookData", (Serializable) data);
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
}