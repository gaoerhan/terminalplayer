package com.example.bjb.myapplication.common;

public class EnumConst {
	public enum 	LoginState {
		FailConnected(1L), Connecting(2L), Logined(3L);
		
		private final Long value;
		
		private LoginState(Long value) {
			this.value = value;			
		}

		public Long getValue() {
			return value;
		}		
	}
	
	public enum Flag {
		TRUE(1L, "是"), FALSE(0L, "否");
		
		private final Long value;
		private final String name;
		
		private Flag(Long value, String name) {
			this.value = value;
			this.name = name;
		}

		public Long getValue() {
			return value;
		}

		public String getName() {
			return name;
		}
	}

	public enum PageElement {
		BgPicture, Text, Picture, Audio, Video, Flash, Time, Weather, WebPage, ThirdPartyData, CallNum, PictureVolume, Pdf, Navigation, Word, Excel, PPT, BoxControl, News, ManualInputData,StreamMedia,CountDown
	}

	public enum StringDateFormat{
		YYYYMMDDHHMMSS("yyyy-MM-dd HH:mm:ss"),
		HHMMSS("HH:mm:ss");

		private String value;

		private StringDateFormat(String value){
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	public enum DeviceType {
		LINGYI(1, "凌壹"),
		SHIXIN(2, "视新");

		private final int value;
		private final String name;

		private DeviceType(int value, String name) {
			this.value = value;
			this.name = name;
		}

		public int getValue() {
			return value;
		}

		public String getName() {
			return name;
		}
	}
}
