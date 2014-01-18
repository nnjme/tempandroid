package com.changlianxi.data;

import com.changlianxi.data.enums.ChatType;

/**
 * Abstract Chat data, can be extended by CircleChat and PersonChat
 * 
 * @author nnjme
 * 
 */
public class AbstractChat extends AbstractData {

	protected int chatId = 0;
	protected ChatType type = ChatType.TYPE_TEXT;
	protected String content = "";
	protected String time = "";

	public AbstractChat(int chatId) {
		this.chatId = chatId;
	}

	public AbstractChat(int chatId, ChatType type, String content, String time) {
		super();
		this.chatId = chatId;
		this.type = type;
		this.content = content;
		this.time = time;
	}

	public int getChatId() {
		return chatId;
	}

	public void setChatId(int chatId) {
		this.chatId = chatId;
	}

	public ChatType getType() {
		return type;
	}

	public void setType(ChatType type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
