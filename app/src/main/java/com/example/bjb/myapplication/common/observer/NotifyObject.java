package com.example.bjb.myapplication.common.observer;

import java.util.ArrayList;

/**
 * INotifyListener用实体类
 * Created by zr on 2017/2/20.
 */

public class NotifyObject {
    public String str;
    public int what;
//    public List<ShareResponseEntity> list;
    public int num;
    public String video_id;
    public String video_title;
    public ArrayList list;
    public Object object;
    public ArrayList hotList;
    public ArrayList otherList;
//    public LatLng latLng;//高德坐标
    public int update;
    public String downLoad;
    public int videoPosition;


    public NotifyObject (){

    }
    //一个string的
    public NotifyObject (String _str){
        this.str = _str;
    }

    //两个int的
    public NotifyObject (int _what,int num){
        this.what = _what;
        this.num = num;
    }
    //一个ArrayList的
    public NotifyObject (ArrayList _list){
        this.list = _list;
    }

    //一个int的
    public NotifyObject (int what){
        this.what = what;
    }

    //一个int，两个string的
    public NotifyObject (int what, String video_id, String video_title){
        this.what = what;
        this.video_id = video_id;
        this.video_title = video_title;
    }

    //一个int，一个对象的
    public NotifyObject (int what,Object object){
        this.object = object;
        this.what = what;
    }
    //一个int，一个string的
    public NotifyObject(int what,String str){
        this.what = what;
        this.str = str;
    }

    //两个int，两个string的
    public NotifyObject(int what, String str, String download, int update){
        this.what = what;
        this.str = str;
        this.update = update;
        this.downLoad = download;
    }
    //两个int，两个string的
    public NotifyObject(int what,int what1,int what2){
        this.what = what;
        this.videoPosition = what1;
        this.update = what2;
    }
}
