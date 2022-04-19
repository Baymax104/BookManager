package com.example.bookmanager.model;

import android.content.Context;

import com.example.bookmanager.controller.Dialogs.FinishConfirmDialog;
import com.example.bookmanager.controller.Dialogs.InfoChangeDialog;
import com.example.bookmanager.controller.Dialogs.RestartConfirmDialog;
import com.example.bookmanager.controller.callbacks.InfoChangeCallback;
import com.example.bookmanager.domain.FinishBook;
import com.example.bookmanager.domain.ProgressBook;
import com.example.bookmanager.domain.ProgressRequestBook;
import com.example.bookmanager.controller.Dialogs.AddDialog;
import com.example.bookmanager.controller.callbacks.IDialogCallback;
import com.example.bookmanager.controller.Dialogs.InfoDisplayDialog;
import com.example.bookmanager.controller.Dialogs.ManualAddDialog;
import com.example.bookmanager.controller.Dialogs.UpdateDialog;
import com.lxj.xpopup.XPopup;

import java.util.List;

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
                .dismissOnTouchOutside(false)
                .asCustom(new ManualAddDialog(context, callback))
                .show();
    }

    public static void showUpdateDialog(Context context, int position, List<ProgressBook> data, IDialogCallback callback) {
        new XPopup.Builder(context)
                .dismissOnTouchOutside(true)
                .dismissOnTouchOutside(false)
                .asCustom(new UpdateDialog(context,position,data,callback))
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
}
