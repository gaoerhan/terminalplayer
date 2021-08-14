package com.example.bjb.myapplication.common.observer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 监听管理类,取代广播
 * Created by zr on 2016/10/14.
 * 使用
 *
 */

public class NotifyListenerManager {

    public static NotifyListenerManager manager;
    private List<INotifyListener> listeners = new ArrayList<>();
    private Map<String,INotifyListener> maps = new HashMap<>();


    /**
     * 获得实例
     * @return
     */
    public static NotifyListenerManager getInstance(){
        if(manager == null){
            manager = new NotifyListenerManager();
        }
        return manager;
    }

    /**
     * 注册监听
     * @param lister
     */
    public void registerListener(INotifyListener lister){
        String className = lister.getClass().getSimpleName();
        if(listeners.contains(lister)) return;
        listeners.add(lister);
        maps.put(className,lister); //JDK 8 以下使用
//        maps.putIfAbsent(className,lister);
    }

    /**
     * 去除监听
     * @param lister
     */
    public void unRegisterListener(INotifyListener lister){
        String className = lister.getClass().getSimpleName();
        if(listeners.contains(lister)){
            listeners.remove(lister);
        }
        if(maps.containsKey(className)){
            maps.remove(lister);
        }
    }

    /**
     * 通知全部页面
     * @paramobj
     */
    public void notifyAllListener(){
        for (INotifyListener lister : listeners) {
            lister.notifyUpdate(new NotifyObject());
        }
    }


    /**
     * 通知某个页面
     * @paramobj
     * tag 为context 的 tag
     */
    public void notifyListener(Class c){
        this.notifyListener(new NotifyObject(),c);
    }

    /**
     * 带参数
     * @param obj
     * @param c
     */
    public void notifyListener(NotifyObject obj, Class c){
        INotifyListener lister = maps.get(c.getSimpleName());
        if(lister != null){
            lister.notifyUpdate(obj);
        }
    }

    /**
     * 去除所有监听,建议系统退出时
     */
    public void removeAllListener(){
        listeners.clear();
        maps.clear();
    }

}
