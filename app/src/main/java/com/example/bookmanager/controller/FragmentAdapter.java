package com.example.bookmanager.controller;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

/**
 * @Description
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/17 22:06
 * @Version
 */
public class FragmentAdapter extends FragmentStateAdapter {

    private List<Fragment> viewList;
    public FragmentAdapter(@NonNull FragmentActivity fragmentActivity, List<Fragment> viewList) {
        super(fragmentActivity);
        this.viewList = viewList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return viewList.get(position);
    }

    @Override
    public int getItemCount() {
        return viewList.size();
    }
}
