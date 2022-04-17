package com.example.bookmanager.controller;

import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.bookmanager.R;
import com.example.bookmanager.domain.Book;
import com.example.bookmanager.controller.callbacks.DialogCallback;
import com.lxj.xpopup.core.CenterPopupView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @Description
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/17 16:01
 * @Version
 */
public class ManualAddDialog extends CenterPopupView {
    private DialogCallback callback;
    private Context context;

    public ManualAddDialog(@NonNull Context context, DialogCallback callback) {
        super(context);
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        TextView confirm = findViewById(R.id.insert_confirm);
        TextView cancel = findViewById(R.id.insert_cancel);
        cancel.setOnClickListener(view1 -> dismiss());
        confirm.setOnClickListener(view1 -> {
            EditText inputName = findViewById(R.id.input_book_name);
            EditText inputAuthor = findViewById(R.id.input_book_author);
            EditText inputPage = findViewById(R.id.input_book_page);

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
                callback.insertBook(book);
                dismiss();
            } else {
                Toast.makeText(context, "请输入完整信息", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.manual_add_dialog;
    }
}
