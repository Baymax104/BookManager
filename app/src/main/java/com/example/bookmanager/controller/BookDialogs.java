package com.example.bookmanager.controller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bookmanager.R;
import com.example.bookmanager.domain.Book;
import com.example.bookmanager.domain.RequestBook;
import com.example.bookmanager.controller.callbacks.DialogCallback;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @Description
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/16 16:10
 * @Version
 */
public class BookDialogs {
    private Context context;

    public BookDialogs() {
    }

    public BookDialogs(Context context) {
        this.context = context;
    }

    public void showAddDialog(DialogCallback callback) {
        View view = LayoutInflater.from(context).inflate(R.layout.add_book_dialog, null, false);
        AlertDialog dialog = new AlertDialog.Builder(context).setView(view).create();

        // 设置对话框背景为透明
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        // 设置对话框透明度
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.alpha = 0.8f;
        dialog.getWindow().setAttributes(params);

        TextView manualAdd = view.findViewById(R.id.manual_add);
        TextView scanAdd = view.findViewById(R.id.scan_add);
        manualAdd.setOnClickListener(v -> {
            Log.d("Dialogs", "监听");
            showManualAddDialog(callback);
            dialog.dismiss();
        });
        scanAdd.setOnClickListener(v -> {
            callback.scanBarcode();
            dialog.dismiss();
        });
        dialog.show();
    }

    public void showManualAddDialog(DialogCallback callback) {
        View view = LayoutInflater.from(context).inflate(R.layout.manual_add_dialog, null, false);
        AlertDialog dialog = new AlertDialog.Builder(context).setView(view).create();

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
                callback.insertBook(book);
                dialog.dismiss();
            } else {
                Toast.makeText(context, "请输入完整信息", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    public void showUpdateDialog(int updatePosition, List<Book> data, DialogCallback callback) {
        View view = LayoutInflater.from(context).inflate(R.layout.update_progress_dialog, null, false);
        AlertDialog dialog = new AlertDialog.Builder(context).setView(view).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView confirm = view.findViewById(R.id.update_confirm);
        TextView cancel = view.findViewById(R.id.update_cancel);
        confirm.setOnClickListener(view1 -> {
            EditText inputProgress = view.findViewById(R.id.input_book_progress);
            if (inputProgress != null && !inputProgress.getText().toString().equals("")) {
                int progress = Integer.parseInt(inputProgress.getText().toString());
                Book book = data.get(updatePosition);
                if (progress <= book.getPage()) {
                    book.setProgress(progress);
                    callback.updateBook(book);
                    dialog.dismiss();
                } else {
                    Toast.makeText(context, "超过了最大页数", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "输入不能为空", Toast.LENGTH_SHORT).show();
            }
        });
        cancel.setOnClickListener(view1 -> dialog.dismiss());
        dialog.show();
    }

    public void showBottomSheetDialog(RequestBook book, DialogCallback callback) {
        BottomSheetDialog dialog = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        View view = LayoutInflater.from(context).inflate(R.layout.book_info_dialog,null,false);
        dialog.setContentView(view);
        // 设置固定高度为最高
        BottomSheetBehavior<FrameLayout> behavior = dialog.getBehavior();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        manager.getDefaultDisplay().getSize(point);
        behavior.setPeekHeight(point.y);

        ImageView bookCover = view.findViewById(R.id.book_cover);
        TextView infoName = view.findViewById(R.id.book_info_name);
        TextView infoAuthor = view.findViewById(R.id.book_info_author);
        TextView infoPublishing = view.findViewById(R.id.book_info_publishing);
        TextView infoPage = view.findViewById(R.id.book_info_page);
        TextView infoIsbn = view.findViewById(R.id.book_info_isbn);
        TextView infoDescription = view.findViewById(R.id.book_info_description);

        Glide.with(view).load(book.getPhotoUrl()).into(bookCover);
        Resources resources = view.getResources();
        infoPublishing.setText(String.format(resources.getString(R.string.info_publishing),book.getPublishing()));
        infoIsbn.setText(String.format(resources.getString(R.string.info_isbn), book.getIsbn()));
        infoDescription.setText(String.format(resources.getString(R.string.info_description),book.getDescription()));
        changeInfo(view,infoName,infoAuthor,infoPage,book);

        LinearLayout infoCard = view.findViewById(R.id.book_info);
        infoCard.setOnClickListener(view1 -> {
            showInfoChangeDialog(book);
            changeInfo(view,infoName,infoAuthor,infoPage,book);
        });
        TextView confirm = view.findViewById(R.id.insert_confirm);
        TextView cancel = view.findViewById(R.id.insert_cancel);
        confirm.setOnClickListener(view1 -> {
            callback.insertBook(book);
            dialog.dismiss();
        });
        cancel.setOnClickListener(view1 -> dialog.dismiss());
        dialog.show();
    }
    private void changeInfo(View view, TextView infoName, TextView infoAuthor, TextView infoPage, RequestBook book) {
        Resources resources = view.getResources();
        infoName.setText(String.format(resources.getString(R.string.info_name),book.getName()));
        infoAuthor.setText(String.format(resources.getString(R.string.info_author),book.getAuthor()));
        infoPage.setText(String.format(resources.getString(R.string.info_page),book.getPage()));
    }

    private void showInfoChangeDialog(RequestBook book) {
        View view = LayoutInflater.from(context).inflate(R.layout.info_change_dialog,null,false);
        AlertDialog dialog = new AlertDialog.Builder(context).setView(view).create();

        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.NoAnimationDialog);
//        BottomSheetDialog dialog = new BottomSheetDialog(context, R.style.BottomSheetDialog);
//        dialog.setContentView(view);
//        // 设置固定高度为最高
//        BottomSheetBehavior<FrameLayout> behavior = dialog.getBehavior();
//        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        Point point = new Point();
//        manager.getDefaultDisplay().getSize(point);
//        behavior.setPeekHeight(point.y);
//        AlphaAnimation alphaAnim = new AlphaAnimation(0, 0);
//        alphaAnim.setDuration(3000);
//        alphaAnim.setFillAfter(true);




        TextView confirm = view.findViewById(R.id.change_confirm);
        TextView cancel = view.findViewById(R.id.change_cancel);
        cancel.setOnClickListener(view1 -> dialog.dismiss());
        confirm.setOnClickListener(view1 -> {
            EditText inputName = view.findViewById(R.id.input_book_name);
            EditText inputAuthor = view.findViewById(R.id.input_book_author);
            EditText inputPage = view.findViewById(R.id.input_book_page);

            if (inputName != null && !inputName.getText().toString().equals("")) {
                String name = inputName.getText().toString();
                book.setName(name);
            }
            if (inputAuthor != null && !inputAuthor.getText().toString().equals("")) {
                String author = inputAuthor.getText().toString();
                book.setAuthor(author);
            }
            if (inputPage != null && !inputPage.getText().toString().equals("")) {
                int page = Integer.parseInt(inputPage.getText().toString());
                book.setPage(page);
            }
            dialog.dismiss();
        });
        dialog.show();
    }
}
