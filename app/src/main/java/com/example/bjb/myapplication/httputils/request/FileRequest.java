package com.example.bjb.myapplication.httputils.request;

import okhttp3.Request;

/**
 * 文件下载请求
 * Created by cclej on 2017/2/22.
 */

public class FileRequest extends BaseRequest<FileRequest> {


    @Override
    protected void buildBody(Request.Builder requestbuilder) {
        super.request = requestbuilder.get().url(url).build();
    }

}
