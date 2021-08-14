package com.example.bjb.myapplication.entity;

import org.dom4j.Element;

/**
 * 可层叠，有坐标元素base
 * @author Ethan.li
 *
 */
public abstract class CommonPageElement extends BasePageElement {

	private int left;
	private int top;
	private int width;
	private int height;
	private int index; //层叠级别
	
	private String jumpLink; //跳转链接,不是所有的元素都有
	private int boxPlayTime; //组合元素时，播放时长
	private int boxSort; //组合元素排序ID
	
	@Override
	protected void readData(Element element) {
    	this.left = getIntValue(element, "Left");
    	this.top = getIntValue(element, "Top");
    	this.width = getIntValue(element, "Width");
    	this.height = getIntValue(element, "Height");
    	this.index = getIntValue(element, "ZIndex");
    	
    	readOtherData(element);
	}
	
	protected abstract void readOtherData(Element element);
	
	public int getLeft() {
		return left;
	}

	public int getTop() {
		return top;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getIndex() {
		return index;
	}
	
	public void setJumpLink(String jumpLink) {
		this.jumpLink = jumpLink;
	}

	public String getJumpLink() {
		return jumpLink;
	}
	
	public int getBoxPlayTime() {
		return boxPlayTime;
	}

	public void setBoxPlayTime(int boxPlayTime) {
		this.boxPlayTime = boxPlayTime;
	}

	public int getBoxSort() {
		return boxSort;
	}

	public void setBoxSort(int boxSort) {
		this.boxSort = boxSort;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("index:" + index);
		sb.append('\n');
		sb.append("left:" + left);
		sb.append('\n');
		sb.append("top:" + top);
		sb.append('\n');
		sb.append("width:" + width);
		sb.append('\n');
		sb.append("height:" + height);
		sb.append('\n');
		return sb.toString();
	}

}
