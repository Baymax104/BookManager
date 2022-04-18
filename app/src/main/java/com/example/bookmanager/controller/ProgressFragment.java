package com.example.bookmanager.controller;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmanager.R;
import com.example.bookmanager.controller.BookAdapter;
import com.example.bookmanager.controller.BookCapture;
import com.example.bookmanager.controller.BookEditActivity;
import com.example.bookmanager.controller.callbacks.DialogCallback;
import com.example.bookmanager.controller.callbacks.IRequestCallback;
import com.example.bookmanager.domain.Book;
import com.example.bookmanager.domain.RequestBook;
import com.example.bookmanager.model.BookException;
import com.example.bookmanager.model.BookOperateListener;
import com.example.bookmanager.model.BookOperator;
import com.example.bookmanager.model.DialogsHelper;
import com.example.bookmanager.model.RequestHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.king.zxing.CameraScan;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;

/**
 * @Description
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/17 21:02
 * @Version
 */
public class ProgressFragment extends Fragment implements BookOperateListener, DialogCallback, IRequestCallback {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView leftMenu;
    private FloatingActionButton floatingAdd;
    private RecyclerView bookList;
    private List<Book> data = new ArrayList<>();
    private BookAdapter adapter;
    private BookOperator operator;
    private View view;

    private ActivityResultLauncher<Intent> editLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_progress,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        initView();

        adapter = new BookAdapter(getContext(), data, false);
        operator = new BookOperator(getContext());
        // RecyclerView绑定
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        bookList.setLayoutManager(layoutManager);
        bookList.setAdapter(adapter);

        // 查询数据库获取数据
        operator.query(this);

        // FloatingActionButton监听
        floatingAdd.setOnClickListener(v -> DialogsHelper.showAddDialog(getContext(), this));

        // RecyclerView子项点击监听
        adapter.setItemClickListener(position -> DialogsHelper.showUpdateDialog(
                getContext(),position,data,this
        ));

    }

    private void initView() {
        drawerLayout = requireActivity().findViewById(R.id.drawer_layout);
        toolbar = view.findViewById(R.id.tool_bar);
        leftMenu = view.findViewById(R.id.nav_view);
        floatingAdd = view.findViewById(R.id.floating_add);
        bookList = view.findViewById(R.id.book_list);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.edit_list) {
            Intent intent = new Intent(getContext(), BookEditActivity.class);
            Book[] passData = data.toArray(new Book[0]);
            intent.putExtra("BookData", passData);
            editLauncher.launch(intent);
        } else if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void scanBarcode() {
        Intent intent = new Intent(getContext(), BookCapture.class);
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
                Toast.makeText(getContext(), "添加:"+ e, Toast.LENGTH_SHORT).show();
                Log.e("MainActivity",e.toString());
                break;
            case UPDATE_ERROR:
                Toast.makeText(getContext(), "更新:"+e, Toast.LENGTH_SHORT).show();
                Log.e("MainActivity",e.toString());
                break;
            case QUERY_ERROR:
                Toast.makeText(getContext(), "查询:"+e, Toast.LENGTH_SHORT).show();
                Log.e("MainActivity", e.toString());
                break;
            default:
                break;
        }
    }

    @Override
    public void getRequestBook(RequestBook requestBook) {
        assert getActivity() != null;
        getActivity().runOnUiThread(() -> DialogsHelper.showInfoDialog(
                getContext(),requestBook,this
        ));
    }

    @Override
    public void getRequestError(Exception e) {
        Looper.prepare();
        if (e instanceof IOException) {
            Toast.makeText(getContext(), "连接失败:" + e, Toast.LENGTH_SHORT).show();
            Log.e("MainActivity", e.toString());
        } else if (e instanceof JSONException) {
            Toast.makeText(getContext(), "解析失败:" + e, Toast.LENGTH_SHORT).show();
            Log.e("MainActivity", e.toString());
        } else {
            Toast.makeText(getContext(), "未知错误:" + e, Toast.LENGTH_SHORT).show();
            Log.e("MainActivity", e.toString());
            e.printStackTrace();
        }
        Looper.loop();
    }
}
