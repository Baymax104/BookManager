package com.example.bookmanager.controller.Dialogs;

import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.bookmanager.R;
import com.example.bookmanager.domain.ProgressBook;
import com.example.bookmanager.controller.callbacks.IDialogCallback;
import com.lxj.xpopup.core.CenterPopupView;

import java.util.List;


/**
 * @Description
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/17 16:09
 * @Version
 */
public class UpdateDialog extends CenterPopupView {
    private Context context;
    private IDialogCallback callback;
    private int position;
    private List<ProgressBook> data;

    public UpdateDialog(@NonNull Context context) {
        super(context);
    }

    public UpdateDialog(@NonNull Context context, int position, List<ProgressBook> data, IDialogCallback callback) {
        super(context);
        this.context = context;
        this.callback = callback;
        this.position = position;
        this.data = data;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_update_progress;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        TextView confirm = findViewById(R.id.update_confirm);
        TextView cancel = findViewById(R.id.update_cancel);
        confirm.setOnClickListener(view1 -> {
            EditText inputProgress = findViewById(R.id.input_book_progress);
            if (inputProgress != null && !inputProgress.getText().toString().equals("")) {
                int progress = Integer.parseInt(inputProgress.getText().toString());
                ProgressBook progressBook = data.get(position);
                if (progress <= progressBook.getPage()) {
                    progressBook.setProgress(progress);
                    callback.updateBook(progressBook);
                    if (progressBook.getProgress() == progressBook.getPage()) {
                        // TODO 弹出是否完成对话框
                    } else {
                        dismiss();
                    }
                } else {
                    Toast.makeText(context, "超过了最大页数", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "输入不能为空", Toast.LENGTH_SHORT).show();
            }
        });
        cancel.setOnClickListener(view1 -> dismiss());
    }
}
