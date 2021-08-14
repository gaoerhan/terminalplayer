package com.example.bjb.myapplication.entity;

import com.example.bjb.myapplication.common.EnumConst;

import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;



/**
 * 图片元素
 * @author Ethan.li
 *
 */
public class Picture extends CommonPageElement {

	private int switchTime; //切换时间 s
	private int switchEffect; //切换效果
    private List<String> rawPathList;
	private int playTime;
	private boolean isBoxPlay;

	public Picture(Element element) {
    	setType(EnumConst.PageElement.Picture);
    	readData(element);
    }
    
    public void readOtherData(Element element) {
    	setJumpLink(getValue(element, "JumpLink"));
    	this.switchTime = getIntValue(element, "SwitchTime");
    	this.switchEffect = getIntValue(element, "SwitchEffect");
    	this.playTime = getIntValue(element, "PlayTime");
    	this.isBoxPlay = getIntValue(element, "Overlap") == 1;
    	
    	List<Element> elements = getElements(getElement(element, "RawPaths"), "RawPath");
		if (elements != null) {
			rawPathList = new ArrayList<String>();
			for (int i = 0; i < elements.size(); i++) {
				rawPathList.add(elements.get(i).getTextTrim());
			}
		}
    }

	public int getSwitchTime() {
		return switchTime;
	}

	public int getSwitchEffect() {
		return switchEffect;
	}

	public List<String> getRawPathList() {
		return rawPathList;
	}

    public int getPlayTime() {
		return playTime;
	}

	public void setPlayTime(int playTime) {
		this.playTime = playTime;
	}

	public boolean isBoxPlay() {
		return isBoxPlay;
	}

	public void setBoxPlay(boolean isBoxPlay) {
		this.isBoxPlay = isBoxPlay;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Picture");
		sb.append('\n');
		sb.append(super.toString());
		sb.append("switchTime:" + switchTime);
		sb.append('\n');
		sb.append("switchEffect:" + switchEffect);
		sb.append('\n');
		sb.append("rawPathList:");
		sb.append('\n');
		for (String s : rawPathList) {
			sb.append(s);
			sb.append('\n');
		}
		return sb.toString();
	}
	
	
}
