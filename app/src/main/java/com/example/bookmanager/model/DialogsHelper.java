package com.example.bookmanager.model;

import android.content.Context;

import com.example.bookmanager.controller.InfoChangeDialog;
import com.example.bookmanager.controller.callbacks.InfoChangeCallback;
import com.example.bookmanager.domain.Book;
import com.example.bookmanager.domain.RequestBook;
import com.example.bookmanager.controller.AddDialog;
import com.example.bookmanager.controller.callbacks.DialogCallback;
import com.example.bookmanager.controller.InfoDisplayDialog;
import com.example.bookmanager.controller.ManualAddDialog;
import com.example.bookmanager.controller.UpdateDialog;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.enums.PopupAnimation;

import java.util.List;

/**
 * @Description
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/17 15:25
 * @Version
 */
public class DialogsHelper {

    public static void showAddDialog(Context context, DialogCallback callback) {
        new XPopup.Builder(context)
                .isDestroyOnDismiss(true)
                .asCustom(new AddDialog(context, callback))
                .show();
    }

    public static void showManualAddDialog(Context context, DialogCallback callback) {
        new XPopup.Builder(context)
                .isDestroyOnDismiss(true)
                .dismissOnTouchOutside(false)
                .asCustom(new ManualAddDialog(context, callback))
                .show();
    }

    public static void showUpdateDialog(Context context, int position, List<Book> data, DialogCallback callback) {
        new XPopup.Builder(context)
                .dismissOnTouchOutside(true)
                .dismissOnTouchOutside(false)
                .asCustom(new UpdateDialog(context,position,data,callback))
                .show();
    }

    public static void showInfoDialog(Context context, RequestBook requestBook, DialogCallback callback) {
        new XPopup.Builder(context)
                .asCustom(new InfoDisplayDialog(context,requestBook,callback))
                .show();
    }

    public static void showInfoChangeDialog(Context context, RequestBook requestBook, InfoChangeCallback callback) {
        new XPopup.Builder(context)
                .isDestroyOnDismiss(true)
                .asCustom(new InfoChangeDialog(context,requestBook,callback))
                .show();
    }
}
