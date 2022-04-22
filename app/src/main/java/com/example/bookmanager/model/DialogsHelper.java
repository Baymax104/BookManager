package com.example.bookmanager.model;

import android.content.Context;
import android.graphics.Color;


import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.TimePickerView;
import com.example.bookmanager.R;
import com.example.bookmanager.controller.Dialogs.FinishConfirmDialog;
import com.example.bookmanager.controller.Dialogs.HistoryEditDialog;
import com.example.bookmanager.controller.Dialogs.InfoChangeDialog;
import com.example.bookmanager.controller.Dialogs.RestartConfirmDialog;
import com.example.bookmanager.controller.callbacks.InfoChangeCallback;
import com.example.bookmanager.domain.FinishBook;
import com.example.bookmanager.domain.History;
import com.example.bookmanager.domain.ProgressBook;
import com.example.bookmanager.domain.ProgressRequestBook;
import com.example.bookmanager.controller.Dialogs.AddDialog;
import com.example.bookmanager.controller.callbacks.IDialogCallback;
import com.example.bookmanager.controller.Dialogs.InfoDisplayDialog;
import com.example.bookmanager.controller.Dialogs.ManualAddDialog;
import com.example.bookmanager.controller.Dialogs.UpdateDialog;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.LoadingPopupView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * @Description
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/17 15:25
 * @Version
 */
public class DialogsHelper {

    public static void showAddDialog(Context context, IDialogCallback callback) {
        new XPopup.Builder(context)
                .isDestroyOnDismiss(true)
                .asCustom(new AddDialog(context, callback))
                .show();
    }

    public static void showManualAddDialog(Context context, IDialogCallback callback) {
        new XPopup.Builder(context)
                .isDestroyOnDismiss(true)
                .asCustom(new ManualAddDialog(context, callback))
                .show();
    }

    public static void showUpdateDialog(Context context, List<History> data, IDialogCallback callback) {
        new XPopup.Builder(context)
                .dismissOnTouchOutside(true)
                .asCustom(new UpdateDialog(context,data,callback))
                .show();
    }

    public static void showInfoDialog(Context context, ProgressRequestBook requestBook, IDialogCallback callback) {
        new XPopup.Builder(context)
                .asCustom(new InfoDisplayDialog(context,requestBook,callback))
                .show();
    }

    public static void showInfoChangeDialog(Context context, ProgressRequestBook requestBook, InfoChangeCallback callback) {
        new XPopup.Builder(context)
                .isDestroyOnDismiss(true)
                .asCustom(new InfoChangeDialog(context,requestBook,callback))
                .show();
    }

    public static void showFinishConfirmDialog(Context context, ProgressBook book, int position, IDialogCallback callback) {
        new XPopup.Builder(context)
                .isDestroyOnDismiss(true)
                .asCustom(new FinishConfirmDialog(context,book,position,callback))
                .show();
    }

    public static void showRestartConfirmDialog(Context context, List<FinishBook> data, int position, IDialogCallback callback) {
        new XPopup.Builder(context)
                .isDestroyOnDismiss(true)
                .asCustom(new RestartConfirmDialog(context, data, position, callback))
                .show();
    }

    public static LoadingPopupView showLoadingDialog(Context context) {
        LoadingPopupView view = new XPopup.Builder(context)
                .dismissOnTouchOutside(false)
                .asLoading();
        view.show();
        return view;
    }

    public static void showHistoryEditDialog(Context context, int position, IDialogCallback callback) {
        new XPopup.Builder(context)
                .dismissOnTouchOutside(true)
                .asCustom(new HistoryEditDialog(context,position,callback))
                .show();
    }

    public static void showDatePicker(Context context, int position, IDialogCallback callback) {
        Calendar nowDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        startDate.set(2000,0,1);
        Calendar endDate = Calendar.getInstance();
        endDate.set(2050,11,31);
        TimePickerView picker = new TimePickerBuilder(context, (date, v) -> {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String updateTime = format.format(date);
            callback.updateHistory(updateTime, position);
        })
                .setSubmitColor(Color.parseColor("#1e90ff"))
                .setCancelColor(Color.RED)
                .setDate(nowDate)
                .setTitleText("选择日期")
                .setDividerColor(Color.parseColor("#1e90ff"))
                .setTextColorCenter(Color.parseColor("#1e90ff"))
                .setRangDate(startDate, endDate)
                .build();
        picker.show();
    }
}
