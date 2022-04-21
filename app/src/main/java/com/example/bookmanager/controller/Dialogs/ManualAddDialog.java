package com.example.bookmanager.controller.Dialogs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.bookmanager.R;
import com.example.bookmanager.controller.callbacks.IUriCallback;
import com.example.bookmanager.domain.ProgressBook;
import com.example.bookmanager.controller.callbacks.IDialogCallback;
import com.lxj.xpopup.core.BottomPopupView;

import java.io.FileNotFoundException;
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
public class ManualAddDialog extends BottomPopupView implements IUriCallback {
    private IDialogCallback callback;
    private Context context;
    private String coverUri;
    private ImageView takePhoto;
    private ImageView takeCover;

    public ManualAddDialog(@NonNull Context context) {
        super(context);
    }

    public ManualAddDialog(@NonNull Context context, IDialogCallback callback) {
        super(context);
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        TextView confirm = findViewById(R.id.insert_confirm);
        TextView cancel = findViewById(R.id.insert_cancel);
        takePhoto = findViewById(R.id.take_photo);
        takeCover = findViewById(R.id.take_book_cover);
        takeCover.setOnClickListener(view -> callback.startCamera(takePhoto,takeCover,this));
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
                ProgressBook progressBook = new ProgressBook(name, author, addTime, 0, page, coverUri);
                callback.insertBook(progressBook);
                dismiss();
            } else {
                Toast.makeText(context, "请输入完整信息", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_manual_add;
    }

    @Override
    public void returnPhotoUri(Uri uri) {
        Glide.with(context).load(uri).into(takeCover);
        takePhoto.setVisibility(INVISIBLE);
        this.coverUri = uri.toString();
    }
}
