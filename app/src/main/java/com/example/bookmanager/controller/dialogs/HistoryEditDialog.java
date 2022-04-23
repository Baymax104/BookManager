package com.example.bookmanager.controller.dialogs;

import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.bookmanager.R;
import com.example.bookmanager.controller.callbacks.IDialogCallback;
import com.lxj.xpopup.core.CenterPopupView;

/**
 * @Description
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/21 19:31
 * @Version
 */
public class HistoryEditDialog extends CenterPopupView {
    private Context context;
    private IDialogCallback callback;
    private int position;


    public HistoryEditDialog(@NonNull Context context) {
        super(context);
    }

    public HistoryEditDialog(@NonNull Context context, int position, IDialogCallback callback) {
        super(context);
        this.context = context;
        this.callback = callback;
        this.position = position;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        TextView updateDate = findViewById(R.id.history_update);
        TextView deleteHistory = findViewById(R.id.history_delete);

        updateDate.setOnClickListener(view -> dismissWith(() -> callback.startSelectTime(position)));
        deleteHistory.setOnClickListener(view -> {
            callback.deleteHistory(position);
            dismiss();
        });
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_history_edit;
    }

}
