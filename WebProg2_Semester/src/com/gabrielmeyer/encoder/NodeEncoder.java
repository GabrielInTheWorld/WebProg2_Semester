package com.gabrielmeyer.encoder;

import java.io.IOException;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

public class NodeEncoder implements Encoder.Text<ObjectNode> {

	@Override
	public void destroy() {
		System.out.println("destroy-method");
	}

	@Override
	public void init(EndpointConfig arg0) {
		System.out.println("init-method");
	}

	@Override
	public String encode(ObjectNode node) throws EncodeException {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(node);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

}
