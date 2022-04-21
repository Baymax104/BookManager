package com.example.bookmanager.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;

import com.example.bookmanager.R;
import com.google.android.material.appbar.CollapsingToolbarLayout;

public class HistoryActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingLayout;
    private ImageView background;
    private RecyclerView historyList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
    }
}