package com.example.bookmanager.view;

/**
 * @Description
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/13 18:42
 * @Version
 */
public interface IMoveAndSwipeCallback {
    void onMove(int fromPosition, int toPosition);
    void onSwiped(int position);
}
