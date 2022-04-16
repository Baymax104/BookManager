package com.example.bookmanager.view;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookmanager.R;
import com.example.bookmanager.domain.Book;

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

    public void showAddDialog(int layoutResId, DialogCallback callback) {
        View view = LayoutInflater.from(context).inflate(layoutResId, null, false);
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
            showManualAddDialog(R.layout.manual_insert_dialog, callback);
            dialog.dismiss();
        });
        scanAdd.setOnClickListener(v -> {
            callback.scanBarcode();
            dialog.dismiss();
        });
        dialog.show();
    }

    public void showManualAddDialog(int layoutResId, DialogCallback callback) {
        View view = LayoutInflater.from(context).inflate(layoutResId, null, false);
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

    public void showUpdateDialog(int layoutRedId, int updatePosition, List<Book> data, DialogCallback callback) {
        View view = LayoutInflater.from(context).inflate(layoutRedId, null, false);
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
}
