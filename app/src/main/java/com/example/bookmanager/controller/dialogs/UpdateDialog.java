package com.example.bookmanager.controller.dialogs;

import android.content.Context;
import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.bookmanager.R;
import com.example.bookmanager.domain.History;
import com.example.bookmanager.controller.callbacks.IDialogCallback;
import com.lxj.xpopup.core.CenterPopupView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


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
    private List<History> data;

    public UpdateDialog(@NonNull Context context) {
        super(context);
    }

    public UpdateDialog(@NonNull Context context, List<History> data, IDialogCallback callback) {
        super(context);
        this.context = context;
        this.callback = callback;
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

        History history = data.get(data.size() - 1);
        confirm.setOnClickListener(view1 -> {
            EditText inputProgress = findViewById(R.id.input_book_progress);
            if (inputProgress != null) {
                if (!inputProgress.getText().toString().equals("")) {
                    int progress = Integer.parseInt(inputProgress.getText().toString());
                    if (progress <= history.getPage() && progress >= history.getEndPage()) {
                        Date date = new Date(System.currentTimeMillis());
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        String updateTime = format.format(date);
                        History newHistory = new History(
                                history.getPage(),updateTime,
                                history.getEndPage(),progress
                        );
                        callback.insertHistory(newHistory);
                        if (newHistory.getEndPage() == newHistory.getPage()) {
                            Intent intent = new Intent("com.example.bookmanager.FINISH_BOOK");
                            intent.putExtra("endTime", updateTime);
                            context.sendOrderedBroadcast(intent, null);
                        }
                        dismiss();
                    } else if (progress < history.getEndPage()) {
                        Toast.makeText(context, "不能往回读哦", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "超过了最大页数", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    dismiss();
                }
            }
        });
        cancel.setOnClickListener(view1 -> dismiss());
    }
}
