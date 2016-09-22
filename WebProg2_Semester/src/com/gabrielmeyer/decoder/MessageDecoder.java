package com.gabrielmeyer.decoder;

import java.io.IOException;

import javax.websocket.DecodeException;
import javax.websocket.Decoder.Text;
import javax.websocket.EndpointConfig;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.gabrielmeyer.websocket.WebSocket.TextNode;

public class MessageDecoder implements Text<TextNode> {

	@Override
	public void destroy() {

	}

	@Override
	public void init(EndpointConfig config) {

	}

	@Override
	public TextNode decode(String jsonMessage) throws DecodeException {
		ObjectMapper mapper = new ObjectMapper();
		try {
			System.out.println("json: " + jsonMessage);
			return mapper.readValue(jsonMessage, TextNode.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean willDecode(String jsonMessage) {
		return jsonMessage != null;
	}

}
