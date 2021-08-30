package com.example.bjb.myapplication.net;

/**
 * @author: geh
 * @date: 2021/8/30
 */
public interface NetStateChangeObserver {
    void onNetDisconnected();
    void onNetConnected(NetworkType networkType);
}
