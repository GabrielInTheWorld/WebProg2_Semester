package com.gabrielmeyer.websocket;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.gabrielmeyer.decoder.MessageDecoder;
import com.gabrielmeyer.encoder.MessageEncoder;

@ServerEndpoint(value = "/websocket", encoders = { MessageEncoder.class }, decoders = { MessageDecoder.class })
public class WebSocket {

	public class TextNode {
		String username, message;

		public TextNode(String username, String message) {
			this.username = username;
			this.message = message;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getUsername() {
			return username;
		}

		public void setMesssage(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}

		public String asText() {
			return "{\"username\" : \"" + username + "\", \"message\" : \"" + message + "\"}";
		}
	}

	static Set<Session> users = Collections.synchronizedSet(new HashSet<Session>());

	@OnMessage
	public void onMessage(String message, Session session) throws InterruptedException, IOException, EncodeException {
		System.out.println("User input: " + message);

		String username = (String) session.getUserProperties().get("username");
		if (username == null) {
			session.getUserProperties().put("username", message);
			session.getBasicRemote().sendObject(buildJsonData("System", "you are now connected as " + message));
		} else {
			Iterator<Session> iterator = users.iterator();
			while (iterator.hasNext()) {
				iterator.next().getBasicRemote().sendObject(buildJsonData(username, message));
			}
		}
	}

	@OnOpen
	public void onOpen(Session session) {
		System.out.println("Client connected.");
		users.add(session);
	}

	@OnClose
	public void onClose(Session session) {
		System.out.println("Client closed");
		users.remove(session);
	}

	private Object buildJsonData(String username, String message) {
		ObjectMapper mapper = new ObjectMapper();

		TextNode node = new TextNode(username, message);
		JsonNode json = null;
		try {
			json = mapper.valueToTree(node);
			System.out.println("json: " + json);

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return json;

	}

}
