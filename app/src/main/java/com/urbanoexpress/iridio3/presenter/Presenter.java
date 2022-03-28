package com.urbanoexpress.iridio3.presenter;

/**
 * Created by mick on 13/05/16.
 */
public interface Presenter<T> {
    void addView(T view);
    void removeView();
}
