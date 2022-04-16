package com.example.bookmanager.view;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.telecom.StatusHints;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.example.bookmanager.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.oned.MultiFormatOneDReader;
import com.king.zxing.CaptureActivity;
import com.king.zxing.DecodeConfig;
import com.king.zxing.DecodeFormatManager;
import com.king.zxing.analyze.BarcodeFormatAnalyzer;
import com.king.zxing.analyze.MultiFormatAnalyzer;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/15 19:08
 * @Version
 */
public class BookCapture extends CaptureActivity {
    private static Map<DecodeHintType,Object> EAN_13_HINTS = new EnumMap<>(DecodeHintType.class);

    static { // 设置EAN13格式
        List<BarcodeFormat> formats = new ArrayList<>();
        formats.add(BarcodeFormat.EAN_13);
        EAN_13_HINTS.put(DecodeHintType.POSSIBLE_FORMATS, formats);
        EAN_13_HINTS.put(DecodeHintType.TRY_HARDER, BarcodeFormat.EAN_13);
        EAN_13_HINTS.put(DecodeHintType.CHARACTER_SET, "UTF-8");
    }

    @Override
    public void initCameraScan() {
        super.initCameraScan();

        DecodeConfig config = new DecodeConfig();
        config.setAreaRectRatio(0.5f)
                .setHints(EAN_13_HINTS);

        getCameraScan()
                .setAnalyzer(new MultiFormatAnalyzer(config))
                .setOnScanResultCallback(this)
                .setNeedAutoZoom(true)
                .setDarkLightLux(35f)
                .setBrightLightLux(40f)
                .bindFlashlightView(ivFlashlight);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back);
            actionBar.setDisplayShowTitleEnabled(false);
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_book_capture;
    }

    @Override
    public boolean onScanResultCallback(Result result) {
        String resultCode = result.getText();
        // 中国的ISBN编号共13位，978为固定，其他十位的第一位为中国的标识7
        // 之后是标识出版社的2-6位数字，之后是出版社内刊物的流水，最后一位是校验位0-9和X
        // 使用正则表达式校验
        String ISBNFormat = "^(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$|97[89][0-9]{10}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)(?:97[89][- ]?)?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$";
        if (resultCode.matches(ISBNFormat)) {
            getCameraScan().setAnalyzeImage(false);
            return false;
        }
        getCameraScan().setAnalyzeImage(true);
        return true;
    }
}
