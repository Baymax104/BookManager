package com.example.bookmanager.controller.Dialogs;

import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.bookmanager.R;
import com.example.bookmanager.controller.callbacks.IDialogCallback;
import com.example.bookmanager.domain.FinishBook;
import com.lxj.xpopup.core.CenterPopupView;

import java.util.List;

/**
 * @Description
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/19 9:28
 * @Version
 */
public class RestartConfirmDialog extends CenterPopupView {
    private Context context;
    private IDialogCallback callback;
    private int position;
    private List<FinishBook> data;

    public RestartConfirmDialog(@NonNull Context context) {
        super(context);
    }

    public RestartConfirmDialog(@NonNull Context context, List<FinishBook> data, int position, IDialogCallback callback) {
        super(context);
        this.context = context;
        this.data = data;
        this.position = position;
        this.callback = callback;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_restart_confirm;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        TextView confirm = findViewById(R.id.restart_confirm);
        TextView cancel = findViewById(R.id.restart_cancel);
        confirm.setOnClickListener(view -> {
            FinishBook book = data.get(position);
            callback.deleteBook(book, position, true);
            dismiss();
        });
        cancel.setOnClickListener(view -> dismiss());
    }
}
