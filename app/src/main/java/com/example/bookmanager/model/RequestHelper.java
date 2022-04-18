package com.example.bookmanager.model;

import androidx.annotation.NonNull;

import com.example.bookmanager.domain.ProgressRequestBook;
import com.example.bookmanager.controller.callbacks.IRequestCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class RequestHelper {

    // 申请权限
    private static final String apikey = "12431.f57c485ad2c8efcc50c99060417ef187.70d8cc6fcfeb6213492798f13fd59ee8";
    // 根地址
    private static final String baseUrl = "https://api.jike.xyz/situ/book/isbn/";
    private static OkHttpClient client = new OkHttpClient();

    public RequestHelper() {
    }

    public static void sendRequest(String isbn, IRequestCallback callback) {
        String url = baseUrl+isbn+"?apikey="+apikey;
        Request request = new Request.Builder().get().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.getRequestError(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try {
                    ResponseBody body = response.body();
                    if (body != null) {
                        String result = body.string();
                        ProgressRequestBook requestBook = parse(result);
                        if (requestBook == null) {
                            throw new JSONException("不合法的数据");
                        }
                        callback.getRequestBook(requestBook);
                    }
                } catch (IOException | JSONException e) {
                    callback.getRequestError(e);
                }
            }
        });
    }

    private static ProgressRequestBook parse(String result) throws JSONException {
        JSONObject allResult = new JSONObject(result);
        int ret = allResult.getInt("ret");
        String msg = allResult.getString("msg");
        if (!isValid(ret, msg, allResult)) {
            return null;
        }
        JSONObject data = allResult.getJSONObject("data");
        String name = data.getString("name");
        String author = data.getString("author");
        String pageData = data.getString("pages");
        int page = 0;
        if (!pageData.equals("")) {
            page = Integer.parseInt(data.getString("pages"));
        }
        String isbn = data.getString("code");
        String publishing = data.getString("publishing");
        String photoUrl = data.getString("photoUrl");
        String description = data.getString("description");
        Date nowDate = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String addTime = dateFormat.format(nowDate);
        return new ProgressRequestBook(
                name,author,addTime,
                page,isbn,photoUrl,
                publishing,description
        );
    }

    private static boolean isValid(int ret, String msg, JSONObject result) {
        return  ret == 0 && "请求成功".equals(msg) && !result.isNull("data");
    }

}
