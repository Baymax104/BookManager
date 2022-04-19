package com.example.bookmanager.controller.Dialogs;

import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.bookmanager.R;
import com.example.bookmanager.controller.callbacks.IDialogCallback;
import com.example.bookmanager.domain.ProgressBook;
import com.lxj.xpopup.core.CenterPopupView;

/**
 * @Description
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/17 20:35
 * @Version
 */
public class FinishConfirmDialog extends CenterPopupView {
    private Context context;
    private IDialogCallback callback;
    private int position;
    private ProgressBook book;

    public FinishConfirmDialog(@NonNull Context context) {
        super(context);
    }

    public FinishConfirmDialog(@NonNull Context context, ProgressBook book, int position, IDialogCallback callback) {
        super(context);
        this.context = context;
        this.book = book;
        this.position = position;
        this.callback = callback;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_finish_confirm;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        TextView confirm = findViewById(R.id.finish_confirm);
        TextView cancel = findViewById(R.id.finish_cancel);
        confirm.setOnClickListener(view -> {
            callback.deleteBook(book,position);
            dismiss();
        });
        cancel.setOnClickListener(view -> dismiss());
    }
}
