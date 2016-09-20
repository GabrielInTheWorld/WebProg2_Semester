package com.gabrielmeyer.encoder;

import java.io.IOException;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.gabrielmeyer.websocket.WebSocket.TextNode;

public class MessageEncoder implements Encoder.Text<TextNode> {

	@Override
	public void destroy() {
		System.out.println("MessageEncoder - destroy method called");
	}

	@Override
	public void init(EndpointConfig config) {
		System.out.println("MessageEncoder - init method called");
	}

	@Override
	public String encode(TextNode text) throws EncodeException {
		ObjectMapper mapper = new ObjectMapper();
		String json = null;

		try {
			json = mapper.writeValueAsString(text);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return json;
	}

}
