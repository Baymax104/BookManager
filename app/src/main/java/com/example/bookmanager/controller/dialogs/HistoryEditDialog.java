package com.example.bookmanager.controller.dialogs;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.bookmanager.R;
import com.example.bookmanager.controller.callbacks.IDialogCallback;
import com.example.bookmanager.domain.History;
import com.lxj.xpopup.core.CenterPopupView;

import java.util.List;

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
    private List<History> data;


    public HistoryEditDialog(@NonNull Context context) {
        super(context);
    }

    public HistoryEditDialog(@NonNull Context context, List<History> data, int position, IDialogCallback callback) {
        super(context);
        this.context = context;
        this.callback = callback;
        this.position = position;
        this.data = data;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        TextView updateDate = findViewById(R.id.history_update);
        TextView deleteHistory = findViewById(R.id.history_delete);
        View separator = findViewById(R.id.separator);

        History finalHistory = data.get(data.size() - 1);
        if (finalHistory.getEndPage() == finalHistory.getPage()) {
            deleteHistory.setVisibility(GONE);
            separator.setVisibility(GONE);
        } else {
            deleteHistory.setOnClickListener(view -> {
                callback.deleteHistory(position);
                dismiss();
            });
        }
        updateDate.setOnClickListener(view -> dismissWith(() -> callback.startSelectTime(position)));
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_history_edit;
    }

}
