package com.example.bookmanager.controller.Dialogs;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.bookmanager.R;
import com.lxj.xpopup.core.CenterPopupView;

/**
 * @Description
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/21 19:31
 * @Version
 */
public class HistoryEditDialog extends CenterPopupView {

    public HistoryEditDialog(@NonNull Context context) {
        super(context);


    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_history_edit;
    }
}
