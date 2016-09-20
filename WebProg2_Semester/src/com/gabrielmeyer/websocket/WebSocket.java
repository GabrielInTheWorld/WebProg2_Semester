package com.gabrielmeyer.websocket;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/websocket")
public class WebSocket {

	static Set<Session> users = Collections.synchronizedSet(new HashSet<Session>());

	@OnMessage
	public void onMessage(String message, Session session) throws InterruptedException, IOException {
		System.out.println("User input: " + message);
		session.getBasicRemote().sendText("Hello world Mr. " + message);
		Thread.sleep(100);

		String username = (String) session.getUserProperties().get("username");
		if (username == null) {
			session.getUserProperties().put("username", message);
			session.getBasicRemote().sendText(buildJsonData("System", "you are now connected as " + message));
		} else {
			Iterator<Session> iterator = users.iterator();
			while (iterator.hasNext()) {
				iterator.next().getBasicRemote().sendText(buildJsonData(username, message));
			}
		}
		// for (int i = 0; i <= 25; ++i) {
		// session.getBasicRemote().sendText(i + " Message from ...");
		// Thread.sleep(1000);
		// }
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

	private String buildJsonData(String username, String message) {
		JsonObject json = Json.createObjectBuilder().add("message", username + ": " + message).build();
		StringWriter writer = new StringWriter();
		try (JsonWriter jsonWriter = Json.createWriter(writer)) {
			jsonWriter.write(json);
			return writer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
