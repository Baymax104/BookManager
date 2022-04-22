package com.example.bookmanager.controller;

import static android.app.Activity.RESULT_OK;
import static android.view.View.INVISIBLE;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmanager.R;
import com.example.bookmanager.controller.callbacks.IDialogCallback;
import com.example.bookmanager.controller.callbacks.IRequestCallback;
import com.example.bookmanager.domain.Book;
import com.example.bookmanager.domain.FinishBook;
import com.example.bookmanager.domain.ProgressBook;
import com.example.bookmanager.domain.ProgressRequestBook;
import com.example.bookmanager.model.BookException;
import com.example.bookmanager.model.BookType;
import com.example.bookmanager.model.BookOperatorListener;
import com.example.bookmanager.model.ProgressOperator;
import com.example.bookmanager.model.DialogsHelper;
import com.example.bookmanager.model.RequestHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.king.zxing.CameraScan;
import com.lxj.xpopup.impl.LoadingPopupView;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * @Description
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/17 21:02
 * @Version
 */
public class ProgressFragment extends Fragment implements BookOperatorListener, IDialogCallback, IRequestCallback {

    private DrawerLayout drawerLayout;
    private FloatingActionButton floatingAdd;
    private RecyclerView bookList;
    private TextView noDataTip;
    private LoadingPopupView loadingView;
    private List<ProgressBook> data = new ArrayList<>();
    private ProgressAdapter adapter;
    private ProgressOperator operator;
    private View view;
    private Uri coverUri = null;

    private ActivityResultLauncher<Intent> editLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Book[] passData = (Book[]) result.getData().getSerializableExtra("BookData");
                    List<ProgressBook> list = new ArrayList<>();
                    for (Book b : passData) {
                        list.add((ProgressBook) b);
                    }
                    data = list;
                    adapter.setData(data);
                    adapter.notifyDataSetChanged();
                    if (data.size() == 0) {
                        noDataTip.setVisibility(View.VISIBLE);
                    } else {
                        noDataTip.setVisibility(INVISIBLE);
                    }
                }
            });

    private ActivityResultLauncher<Intent> scanLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String isbnCode = CameraScan.parseScanResult(result.getData());
                    loadingView = DialogsHelper.showLoadingDialog(getContext());
                    RequestHelper.sendRequest(isbnCode, this);
                }
            });

    private ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent intent = new Intent("com.example.bookmanager.RETURN_URI");
                    String uri = coverUri.toString();
                    intent.putExtra("uri", uri);
                    assert getContext() != null;
                    getContext().sendOrderedBroadcast(intent, null);
                }
            });

    private ActivityResultLauncher<String> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> {
                if (result) {
                    takePhoto();
                } else {
                    Toast.makeText(getContext(), "请到权限中心开启权限！", Toast.LENGTH_SHORT).show();
                }
            });

    private BroadcastReceiver addTimeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ProgressBook book = (ProgressBook) intent.getSerializableExtra("book");
            operator.update(book, ProgressFragment.this);
        }
    };

    private BroadcastReceiver cameraReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            assert getActivity() != null && getContext() != null;
            if(ContextCompat.checkSelfPermission( getContext(), Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.CAMERA);
            } else {
                takePhoto();
            }
            abortBroadcast();
        }
    };

    private BroadcastReceiver progressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ProgressBook book = (ProgressBook) intent.getSerializableExtra("book");
            operator.update(book, ProgressFragment.this);
            abortBroadcast();
        }
    };

    private BroadcastReceiver scanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent intent1 = new Intent(getContext(), BookCaptureActivity.class);
            scanLauncher.launch(intent1);
            abortBroadcast();
        }
    };

    private BroadcastReceiver finishReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ProgressBook book = (ProgressBook) intent.getSerializableExtra("book");
            String endTime = intent.getStringExtra("endTime");
            FinishBook finishBook = new FinishBook(
                    book.getName(),book.getAuthor(),book.getPage(),book.getAddTime(),
                    endTime,book.getHistory(),book.getCover(),book.getCoverUrl()
            );
            int position = data.indexOf(book);
            operator.delete(book, position, ProgressFragment.this);
            Bundle finish = new Bundle();
            finish.putSerializable("book", finishBook);
            getParentFragmentManager().setFragmentResult("Finish", finish);
        }
    };

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

        // 注册接收器
        IntentFilter addTimeFilter = new IntentFilter();
        addTimeFilter.addAction("com.example.bookmanager.UPDATE_ADD_TIME");
        IntentFilter cameraFilter = new IntentFilter();
        cameraFilter.addAction("com.example.bookmanager.START_CAMERA");
        IntentFilter progressFilter = new IntentFilter();
        progressFilter.addAction("com.example.bookmanager.UPDATE_PROGRESS");
        IntentFilter scanFilter = new IntentFilter();
        scanFilter.addAction("com.example.bookmanager.START_SCAN");
        IntentFilter finishFilter = new IntentFilter();
        finishFilter.addAction("com.example.bookmanager.DELETE_FINISH_BOOK");
        assert getContext() != null;
        getContext().registerReceiver(addTimeReceiver, addTimeFilter);
        getContext().registerReceiver(cameraReceiver, cameraFilter);
        getContext().registerReceiver(progressReceiver, progressFilter);
        getContext().registerReceiver(scanReceiver, scanFilter);
        getContext().registerReceiver(finishReceiver, finishFilter);


        adapter = new ProgressAdapter(getContext(), data, false);
        operator = new ProgressOperator(getContext());
        // RecyclerView绑定
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        bookList.setLayoutManager(layoutManager);
        bookList.setAdapter(adapter);

        // 监听重新开始结果
        getParentFragmentManager().setFragmentResultListener("Restart",this,(requestKey, result) -> {
            ProgressBook book = (ProgressBook) result.getSerializable("Book");
            operator.insert(book, this);
        });

        // 查询数据库获取数据
        operator.query(this);

        // FloatingActionButton监听
        floatingAdd.setOnClickListener(v -> DialogsHelper.showAddDialog(getContext(),this));

        // RecyclerView子项点击监听
        adapter.setOnItemClickListener(position -> {
            ProgressBook book = data.get(position);
            HistoryActivity.actionStart(getContext(),book, false);
        });
    }

    private void initView() {
        drawerLayout = requireActivity().findViewById(R.id.drawer_layout);
        floatingAdd = view.findViewById(R.id.floating_add);
        bookList = view.findViewById(R.id.book_list);
        noDataTip = view.findViewById(R.id.no_data_tip);
    }

    @Override
    public void onDestroy() {
        assert getContext() != null;
        getContext().unregisterReceiver(addTimeReceiver);
        getContext().unregisterReceiver(cameraReceiver);
        getContext().unregisterReceiver(progressReceiver);
        getContext().unregisterReceiver(scanReceiver);
        getContext().unregisterReceiver(finishReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.edit_list) {
            Intent intent = new Intent(getContext(), EditActivity.class);
            ProgressBook[] passData = data.toArray(new ProgressBook[0]);
            intent.putExtra("BookData", passData);
            intent.putExtra("Type", BookType.ProgressBook);
            editLauncher.launch(intent);
        } else if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void insertBook(Book book1) {
        ProgressBook book = (ProgressBook) book1;
        int size = data.size() + 1;
        String history = "h_" + size;
        book.setHistory(history);
        operator.insert(book, this);
    }

    @Override
    public void updateBook(Book progressBook) {
        operator.update(progressBook, this);
    }

    @Override
    public void deleteBook(Book book1, int position, boolean... isRestart) {
        ProgressBook book = (ProgressBook) book1;
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String endTime = format.format(date);
        FinishBook finishBook = new FinishBook(
                book.getName(),book.getAuthor(),book.getPage(),book.getAddTime(),
                endTime,book.getHistory(),book.getCover(),book.getCoverUrl()
        );
        Bundle finish = new Bundle();
        finish.putSerializable("Book", finishBook);
        getParentFragmentManager().setFragmentResult("Finish", finish);
        operator.delete(book,position,this);
    }


    private void takePhoto() {
        // 创建图片文件对象，父路径为当前程序的cache目录
        assert getContext() != null;
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_hhmmss",Locale.getDefault());
        String filename = format.format(date);
        File outputImage = new File(getContext().getExternalCacheDir(), filename+".jpg");
        if (outputImage.exists()) {
            outputImage.delete();
        }
        try {
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 判断版本，将File转换为Uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            coverUri = FileProvider.getUriForFile(getContext(), "com.example.bookmanager.fileprovider", outputImage);
        } else {
            coverUri = Uri.fromFile(outputImage);
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,coverUri);
        cameraLauncher.launch(intent);
    }


    @Override
    public void onSuccess(List<Book> data, int... position) {
        List<ProgressBook> list = new ArrayList<>();
        for (Book b : data) {
            list.add((ProgressBook) b);
        }
        this.data = list;
        adapter.setData(data);
        adapter.notifyDataSetChanged();
        if (data.size() == 0) {
            noDataTip.setVisibility(View.VISIBLE);
        } else {
            noDataTip.setVisibility(INVISIBLE);
        }
    }

    @Override
    public void onError(BookException e) {
        switch (e.errorType) {
            case INSERT_ERROR:
                if (Objects.equals(e.getMessage(), "记录已存在")) {
                    Toast.makeText(getContext(), "存在相同记录", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "添加:"+ e, Toast.LENGTH_SHORT).show();
                }
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
    public void getRequestBook(ProgressRequestBook requestBook) {
        assert getActivity() != null;
        getActivity().runOnUiThread(() ->
                loadingView.dismissWith(() -> DialogsHelper.showInfoDialog(
                getContext(),requestBook,this
        )));
    }

    @Override
    public void getRequestError(Exception e) {
        assert getActivity() != null;
        getActivity().runOnUiThread(() -> loadingView.smartDismiss());
        Looper.prepare();
        if (e instanceof IOException) {
            if (Objects.equals(e.getMessage(), "无数据")) {
                Toast.makeText(getContext(), "无搜索结果，试试手动录入~", Toast.LENGTH_SHORT).show();
            } else {
                if (e instanceof SocketTimeoutException) {
                    Toast.makeText(getContext(), "连接超时："+ e, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "连接失败:" + e, Toast.LENGTH_SHORT).show();
                }
            }
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
