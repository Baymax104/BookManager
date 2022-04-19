package com.example.bookmanager.controller;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmanager.R;
import com.example.bookmanager.controller.callbacks.IDialogCallback;
import com.example.bookmanager.domain.Book;
import com.example.bookmanager.domain.FinishBook;
import com.example.bookmanager.domain.ProgressBook;
import com.example.bookmanager.model.BookException;
import com.example.bookmanager.model.BookType;
import com.example.bookmanager.model.DialogsHelper;
import com.example.bookmanager.model.FinishOperator;
import com.example.bookmanager.model.OperatorListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/18 10:32
 * @Version
 */
public class FinishFragment extends Fragment implements OperatorListener, IDialogCallback {
    private View view;
    private List<FinishBook> data = new ArrayList<>();
    private DrawerLayout drawerLayout;
    private RecyclerView bookList;
    private TextView noDataTip;
    private FinishOperator operator;
    private FinishAdapter adapter;

    private ActivityResultLauncher<Intent> editLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Book[] passData = (Book[]) result.getData().getSerializableExtra("BookData");
                    List<FinishBook> list = new ArrayList<>();
                    for (Book b : passData) {
                        list.add((FinishBook) b);
                    }
                    data = list;
                    adapter.setData(data);
                    adapter.notifyDataSetChanged();
                    if (data.size() == 0) {
                        noDataTip.setVisibility(View.VISIBLE);
                    } else {
                        noDataTip.setVisibility(View.INVISIBLE);
                    }
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_finish,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        initView();

        adapter = new FinishAdapter(getContext(), data, false);
        operator = new FinishOperator(getContext());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        bookList.setLayoutManager(layoutManager);
        bookList.setAdapter(adapter);

        getParentFragmentManager().setFragmentResultListener("Finish",this,(requestKey, result) -> {
            FinishBook book = (FinishBook) result.getSerializable("Book");
            operator.insert(book, this);
        });

        operator.query(this);

        adapter.setOnItemClickListener(position -> DialogsHelper.showRestartConfirmDialog(
                getContext(),data,position,this
        ));
    }

    private void initView() {
        drawerLayout = requireActivity().findViewById(R.id.drawer_layout);
        bookList = view.findViewById(R.id.book_list);
        noDataTip = view.findViewById(R.id.no_data_tip);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.edit_list) {
            Intent intent = new Intent(getContext(), EditActivity.class);
            FinishBook[] passData = data.toArray(new FinishBook[0]);
            intent.putExtra("BookData", passData);
            intent.putExtra("Type", BookType.FinishBook);
            editLauncher.launch(intent);
        } else if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void onSuccess(List<Book> data, int... position) {
        List<FinishBook> list = new ArrayList<>();
        for (Book b : data) {
            list.add((FinishBook) b);
        }
        this.data = list;
        adapter.setData(data);
        adapter.notifyDataSetChanged();
        if (data.size() == 0) {
            noDataTip.setVisibility(View.VISIBLE);
        } else {
            noDataTip.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onError(BookException e) {

    }

    // 该页面不需要此功能
    @Override
    public void scanBarcode() {
    }

    // 该页面不需要此功能
    @Override
    public void insertBook(Book book) {
    }

    // 该页面不需要此功能
    @Override
    public void updateBook(Book book) {
    }

    @Override
    public void deleteBook(Book book1, int position, boolean... isRestart) {
        // 若删除来源为重新开始对话框
        if (isRestart.length == 1 && isRestart[0]) {
            FinishBook book = (FinishBook) book1;
            ProgressBook progressBook = new ProgressBook(
                    book.getName(),book.getAuthor(),book.getAddTime(),
                    0,book.getPage()
            );
            Bundle restart = new Bundle();
            restart.putSerializable("Book",progressBook);
            getParentFragmentManager().setFragmentResult("Restart",restart);
        }
        operator.delete(book1,position,this);
    }
}
