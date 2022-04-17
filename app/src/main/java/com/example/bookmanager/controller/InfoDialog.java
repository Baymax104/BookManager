package com.example.bookmanager.controller;

import android.content.Context;
import android.content.res.Resources;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.bookmanager.R;
import com.example.bookmanager.domain.RequestBook;
import com.example.bookmanager.controller.callbacks.DialogCallback;
import com.lxj.xpopup.core.BottomPopupView;

/**
 * @Description
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/17 16:21
 * @Version
 */
public class InfoDialog extends BottomPopupView {

    private Context context;
    private DialogCallback callback;
    private RequestBook requestBook;
    public InfoDialog(@NonNull Context context, RequestBook requestBook, DialogCallback callback) {
        super(context);
        this.context = context;
        this.callback = callback;
        this.requestBook = requestBook;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.book_info_dialog;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        ImageView bookCover = findViewById(R.id.book_cover);
        TextView infoName = findViewById(R.id.book_info_name);
        TextView infoAuthor = findViewById(R.id.book_info_author);
        TextView infoPublishing = findViewById(R.id.book_info_publishing);
        TextView infoPage = findViewById(R.id.book_info_page);
        TextView infoIsbn = findViewById(R.id.book_info_isbn);
        TextView infoDescription = findViewById(R.id.book_info_description);

        Glide.with(context).load(requestBook.getPhotoUrl()).into(bookCover);
        Resources resources = getResources();
        infoPublishing.setText(String.format(resources.getString(R.string.info_publishing),requestBook.getPublishing()));
        infoIsbn.setText(String.format(resources.getString(R.string.info_isbn), requestBook.getIsbn()));
        infoDescription.setText(String.format(resources.getString(R.string.info_description),requestBook.getDescription()));
        changeInfo(infoName,infoAuthor,infoPage,requestBook);

        LinearLayout infoCard = findViewById(R.id.book_info);
        infoCard.setOnClickListener(view1 -> {
//            showInfoChangeDialog(requestBook);
//            changeInfo(view,infoName,infoAuthor,infoPage,requestBook);
        });
        TextView confirm = findViewById(R.id.insert_confirm);
        TextView cancel = findViewById(R.id.insert_cancel);
        confirm.setOnClickListener(view1 -> {
            callback.insertBook(requestBook);
            dismiss();
        });
        cancel.setOnClickListener(view1 -> dismiss());
    }

    private void changeInfo(TextView infoName, TextView infoAuthor, TextView infoPage, RequestBook book) {
        Resources resources = getResources();
        infoName.setText(String.format(resources.getString(R.string.info_name),book.getName()));
        infoAuthor.setText(String.format(resources.getString(R.string.info_author),book.getAuthor()));
        infoPage.setText(String.format(resources.getString(R.string.info_page),book.getPage()));
    }

}
