package com.example.bookmanager.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.bookmanager.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;

public class StartActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private NavigationView leftMenu;
    private ViewPager2 viewPager;
    private BottomNavigationView navigationView;
    private List<Fragment> viewList = new ArrayList<>();
    private FragmentAdapter adapter = new FragmentAdapter(this, viewList);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        SQLiteStudioService.instance().start(this);
        initView();

        navigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.bottom_nav_progress) {
                viewPager.setCurrentItem(0);
                return true;
            } else if (id == R.id.bottom_nav_finish) {
                viewPager.setCurrentItem(1);
                return true;
            }
            return false;
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                navigationView.getMenu().getItem(position).setChecked(true);
            }
        });
    }

    private void initView() {
        toolbar = findViewById(R.id.tool_bar);
        viewPager = findViewById(R.id.view_pager);
        leftMenu = findViewById(R.id.nav_view);
        navigationView = findViewById(R.id.nav_tab);

        viewList.add(new ProgressFragment());
        viewList.add(new FinishFragment());
        viewPager.setAdapter(adapter);

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
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }
}