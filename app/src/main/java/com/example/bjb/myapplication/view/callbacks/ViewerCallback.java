package com.example.bjb.myapplication.view.callbacks;

/**
 * 供PlayerActivity调用的接口
 * 
 * @author zhuweiwei
 *
 */
public interface ViewerCallback {
	
	void viewerOnPause(boolean isFinishing);

	void viewerOnResume();

	void viewerOnDestroy();
}
