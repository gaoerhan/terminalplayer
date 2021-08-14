package com.example.bjb.myapplication.entity;

import com.example.bjb.myapplication.common.CommonXmlSupport;
import com.example.bjb.myapplication.common.EnumConst;
import com.example.bjb.myapplication.common.EnumConst.PageElement;
import org.dom4j.Element;



/**
 * 节目元素base
 * @author Ethan.li
 *
 */
public abstract class BasePageElement extends CommonXmlSupport {

	private String version = "1.0";
    
	private PageElement type;
	
	protected abstract void readData(Element element);
	
	public String getVersion() {
		return version;
	}

	public PageElement getType() {
		return type;
	}

	public void setType(PageElement type) {
		this.type = type;
	}
}
