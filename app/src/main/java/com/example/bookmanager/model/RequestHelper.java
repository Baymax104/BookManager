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
import java.util.concurrent.TimeUnit;


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

    private static final int wrong = 1;
    private static final int noData = 2;
    private static final int hasData = 3;
    private static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(5,TimeUnit.SECONDS)
            .readTimeout(5,TimeUnit.SECONDS)
            .build();

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
                        callback.getRequestBook(requestBook);
                    }
                } catch (IOException | JSONException e) {
                    callback.getRequestError(e);
                }
            }
        });
    }

    private static ProgressRequestBook parse(String result) throws JSONException,IOException {
        JSONObject allResult = new JSONObject(result);
        int ret = allResult.getInt("ret");
        String msg = allResult.getString("msg");
        int validResult = isValid(ret,msg,allResult);
        if (validResult == wrong) {
            throw new IOException("请求失败");
        } else if (validResult == noData) {
            throw new IOException("无数据");
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

    private static int isValid(int ret, String msg, JSONObject result) {
        if (ret == 0 && "请求成功".equals(msg)) {
            if (!result.isNull("data")) {
                return hasData;
            } else {
                return noData;
            }
        } else {
            return wrong;
        }
    }

}
