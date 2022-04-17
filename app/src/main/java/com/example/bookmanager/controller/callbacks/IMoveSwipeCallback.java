package com.example.bookmanager.controller.callbacks;

/**
 * @Description
 * @Author Jake
 * @email wzy1048168235@163.com
 * @Date 2022/4/13 18:42
 * @Version
 */
public interface IMoveSwipeCallback {
    void onMove(int fromPosition, int toPosition);
    void onSwiped(int position);
}
