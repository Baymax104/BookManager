package com.example.bookmanager.controller.Dialogs;

import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.bookmanager.R;
import com.example.bookmanager.model.DialogsHelper;
import com.example.bookmanager.controller.callbacks.IDialogCallback;
import com.lxj.xpopup.core.CenterPopupView;

/**
 * @Description
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/17 15:27
 * @Version
 */
public class AddDialog extends CenterPopupView {
    private IDialogCallback callback;
    private Context context;

    public AddDialog(@NonNull Context context) {
        super(context);
    }

    public AddDialog(@NonNull Context context, IDialogCallback callback) {
        super(context);
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        TextView manualAdd = findViewById(R.id.manual_add);
        TextView scanAdd = findViewById(R.id.scan_add);
        manualAdd.setOnClickListener(v -> dismissWith(() -> DialogsHelper.showManualAddDialog(context, callback)));
        scanAdd.setOnClickListener(v -> dismissWith(() -> callback.scanBarcode()));
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_add_book;
    }
}
