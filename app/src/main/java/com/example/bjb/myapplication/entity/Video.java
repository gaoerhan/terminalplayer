package com.example.bjb.myapplication.entity;



import com.example.bjb.myapplication.common.EnumConst;
import com.example.bjb.myapplication.common.EnumConst.PageElement;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;



/**
 * Video元素
 * @author Ethan.li
 *
 */
public class Video extends CommonPageElement {

	private List<String> rawPathList;
	private byte silence; //1 静音 0 不静音
	private String playerType; // "media player" "flash player" "html5 player"
	private byte panel;
	private int playTime;
	private boolean isBoxPlay;
	
	public Video(Element element) {
		setType(PageElement.Video);
		readData(element);
	}
	
	public void readOtherData(Element element) {
		setJumpLink(getValue(element, "JumpLink"));
		List<Element> elements = getElements(getElement(element, "RawPaths"), "RawPath");
		if (elements != null) {
			rawPathList = new ArrayList<String>();
			for (int i = 0; i < elements.size(); i++) {
				rawPathList.add(elements.get(i).getTextTrim());
			}
		}
		
		this.silence = getByteValue(element, "Silence");
		playerType = getValue(element, "PlayerType");
		panel = getByteValue(element, "Panel");
    	this.playTime = getIntValue(element, "PlayTime");
    	this.isBoxPlay = getIntValue(element, "Overlap") == 1;
	}

	public List<String> getRawPathList() {
		return rawPathList;
	}

	public byte getSilence() {
		return silence;
	}
	
	public String getPlayerType() {
		return playerType;
	}
	
	public byte getPanel() {
		return panel;
	}

	public void setPanel(byte panel) {
		this.panel = panel;
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
		sb.append("Video");
		sb.append('\n');
		sb.append(super.toString());
		sb.append("silence:" + silence);
		sb.append('\n');
		sb.append("playerType:" + playerType);
		sb.append('\n');
		sb.append("panel:" + panel);
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
