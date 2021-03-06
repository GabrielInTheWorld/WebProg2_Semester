package com.gabrielmeyer.encoder;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import org.codehaus.jackson.map.ObjectMapper;

import com.gabrielmeyer.websocket.WebSocket.MessageNode;

public class MessageEncoder implements Encoder.Text<MessageNode> {

	@Override
	public void destroy() {
		System.out.println("MessageEncoder - destroy method called");
	}

	@Override
	public void init(EndpointConfig config) {
		System.out.println("MessageEncoder - init method called");
	}

	@Override
	public String encode(MessageNode text) throws EncodeException {
		System.out.println("MessageEncoder");
		ObjectMapper mapper = new ObjectMapper();
		String json = null;

		try {
			json = mapper.writeValueAsString(text);
		} catch (Exception e) {
			System.out.println("Exception in MessageEncoder");
			e.printStackTrace();
		}

		return json;
	}

}
