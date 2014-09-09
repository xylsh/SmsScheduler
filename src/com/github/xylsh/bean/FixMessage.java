package com.github.xylsh.bean;

/**
 * 定时短信实体类.
 *
 */
public class FixMessage {

	private int id;
	private String phoneNumber;   //号码
	private String msgText;       //短信内容
	private String sendTime;      //发送时间
	private int haveSend;         //是否已发送，0表示未发送，1表示已发送
	
	public FixMessage() {
	}

	public FixMessage(int id, String phoneNumber, String msgText,
			String sendTime, int haveSend) {
		super();
		this.id = id;
		this.phoneNumber = phoneNumber;
		this.msgText = msgText;
		this.sendTime = sendTime;
		this.haveSend = haveSend;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getMsgText() {
		return msgText;
	}
	public void setMsgText(String msgText) {
		this.msgText = msgText;
	}
	public String getSendTime() {
		return sendTime;
	}
	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}
	public int getHaveSend() {
		return haveSend;
	}
	public void setHaveSend(int haveSend) {
		this.haveSend = haveSend;
	}
}
