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

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

//@ServerEndpoint(value = "/websocket", encoders = { MessageEncoder.class, NodeEncoder.class }, decoders = {
//		MessageDecoder.class })
public class WebSocket {

	public static class TextNode {
		private String username, message, imageURL;

		public TextNode() {

		}

		@JsonCreator
		public TextNode(@JsonProperty("username") String username, @JsonProperty("message") String message,
				@JsonProperty("imageURL") String imageURL) {
			this.username = username;
			this.message = message;
			this.imageURL = imageURL;
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

		public void setImageURL(String imageURL) {
			this.imageURL = imageURL;
		}

		public String getImageURL() {
			return imageURL;
		}

		public String asText() {
			return "{\"username\" : \"" + username + "\", \"message\" : \"" + message + "\"}";
		}
	}

	static Set<Session> users = Collections.synchronizedSet(new HashSet<Session>());

	@OnMessage
	public void onMessage(String message, Session session) throws InterruptedException, IOException, EncodeException {
		try {
			JSONObject json = new JSONObject(message);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		System.out.println("User input: " + message);
		ObjectMapper mapper = new ObjectMapper();
		TextNode textNode = mapper.readValue(message, TextNode.class);

		String username = textNode.getUsername();

		if (username == null)
			username = (String) session.getUserProperties().get("username");

		if (username == null) {
			session.getUserProperties().put("username", textNode.getUsername());
			session.getBasicRemote()
					.sendObject(buildJsonData("System", "you are now connected as " + textNode.getUsername(), null));
			username = textNode.getUsername();
			Iterator<Session> iterator = users.iterator();
			while (iterator.hasNext()) {
				iterator.next().getBasicRemote()
						.sendObject(buildJsonData(username, textNode.getMessage(), textNode.getImageURL()));
			}
		} else {
			Iterator<Session> iterator = users.iterator();
			while (iterator.hasNext()) {
				iterator.next().getBasicRemote()
						.sendObject(buildJsonData(username, textNode.getMessage(), textNode.getImageURL()));
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

	private Object buildJsonData(String username, String message, String imageURL) {
		ObjectMapper mapper = new ObjectMapper();

		TextNode node = new TextNode(username, message, imageURL);
		JsonNode json = null;
		try {
			json = mapper.valueToTree(node);
			System.out.println("json: " + json);

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return json;

	}

	// @Override
	// public void onOpen(final Session session, EndpointConfig config) {
	// session.addMessageHandler(new MessageHandler.Whole<String>() {
	//
	// @Override
	// public void onMessage(String text) {
	// System.out.println("Received response in client from endpoint: " + text);
	// }
	//
	// });
	//
	// try {
	// String name = "Duke";
	// System.out.println("Sending message from client -> endpoint: " + name);
	// session.getBasicRemote().sendText(name);
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }

}
