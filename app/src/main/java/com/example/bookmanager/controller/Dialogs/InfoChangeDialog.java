package com.example.bookmanager.controller.Dialogs;

import android.content.Context;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.bookmanager.R;
import com.example.bookmanager.controller.callbacks.InfoChangeCallback;
import com.example.bookmanager.domain.ProgressRequestBook;
import com.lxj.xpopup.core.BottomPopupView;

/**
 * @Description
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/17 17:24
 * @Version
 */
public class InfoChangeDialog extends BottomPopupView {
    private ProgressRequestBook requestBook;
    private InfoChangeCallback callback;

    public InfoChangeDialog(@NonNull Context context) {
        super(context);
    }

    public InfoChangeDialog(@NonNull Context context, ProgressRequestBook requestBook, InfoChangeCallback callback) {
        super(context);
        this.requestBook = requestBook;
        this.callback = callback;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_info_change;
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        TextView confirm = findViewById(R.id.change_confirm);
        TextView cancel = findViewById(R.id.change_cancel);
        EditText inputName = findViewById(R.id.input_book_name);
        EditText inputAuthor = findViewById(R.id.input_book_author);
        EditText inputPage = findViewById(R.id.input_book_page);
        ImageView bookCover = findViewById(R.id.book_cover);

        if (bookCover != null) {
            Glide.with(this).load(requestBook.getPhotoUrl()).into(bookCover);
        }
        if (inputName != null) {
            inputName.setText(requestBook.getName());
        }
        if (inputAuthor != null) {
            inputAuthor.setText(requestBook.getAuthor());
        }
        if (inputPage != null) {
            inputPage.setText(String.valueOf(requestBook.getPage()));
        }
        cancel.setOnClickListener(view1 -> dismiss());
        confirm.setOnClickListener(view1 -> {

            if (inputName != null && !inputName.getText().toString().equals("")) {
                String name = inputName.getText().toString();
                requestBook.setName(name);
            }
            if (inputAuthor != null && !inputAuthor.getText().toString().equals("")) {
                String author = inputAuthor.getText().toString();
                requestBook.setAuthor(author);
            }
            if (inputPage != null && !inputPage.getText().toString().equals("")) {
                int page = Integer.parseInt(inputPage.getText().toString());
                requestBook.setPage(page);
            }
            callback.refreshInfo(requestBook);
            dismiss();
        });
    }
}
