package com.gabrielmeyer.websocket;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class WebSocket {

	public static class MessageNode {
		private String username, message, imageURL;
		private String type;

		public MessageNode() {

		}

		@JsonCreator
		public MessageNode(@JsonProperty("username") String username, @JsonProperty("message") String message,
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

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String asText() {
			return "{\"username\" : \"" + username + "\", \"message\" : \"" + message + "\"}";
		}
	}

	public static class Line {
		private String type;
		private int startX, startY, endX, endY;

		@JsonCreator
		public Line( //
				@JsonProperty("startX") int startX, //
				@JsonProperty("startY") int startY, //
				@JsonProperty("endX") int endX, //
				@JsonProperty("endY") int endY) {
			this.startX = startX;
			this.startY = startY;
			this.endX = endX;
			this.endY = endY;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public int getStartX() {
			return startX;
		}

		public void setStartX(int startX) {
			this.startX = startX;
		}

		public int getStartY() {
			return startY;
		}

		public void setStartY(int startY) {
			this.startY = startY;
		}

		public int getEndX() {
			return endX;
		}

		public void setEndX(int endX) {
			this.endX = endX;
		}

		public int getEndY() {
			return endY;
		}

		public void setEndY(int endY) {
			this.endY = endY;
		}

	}

	public static class Rectangle {
		private String type;
		private int x, y, width, height;

		@JsonCreator
		public Rectangle( //
				@JsonProperty("x") int x, //
				@JsonProperty("y") int y, //
				@JsonProperty("width") int width, //
				@JsonProperty("height") int height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}

		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}

	}

	public static class Circle {
		private int x, y;
		private float radius, arc;
		private String type;

		@JsonCreator
		public Circle( //
				@JsonProperty("x") int x, //
				@JsonProperty("y") int y, //
				@JsonProperty("radius") float radius, //
				@JsonProperty("arc") float arc) {
			this.x = x;
			this.y = y;
			this.radius = radius;
			this.arc = arc;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}

		public float getRadius() {
			return radius;
		}

		public void setRadius(float radius) {
			this.radius = radius;
		}

		public float getArc() {
			return arc;
		}

		public void setArc(float arc) {
			this.arc = arc;
		}

	}

	public static class Polygon {
		private String type;
		private List<Point> polygonPoints;

		@JsonCreator
		public Polygon(@JsonProperty("polygonPoints") List<Point> polygonPoints) {
			this.polygonPoints = polygonPoints;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public List<Point> getPolygonPoints() {
			return polygonPoints;
		}

		public void setPolygonPoints(List<Point> polygonPoints) {
			this.polygonPoints = polygonPoints;
		}

	}

	public static class FreeHand {
		private String type;
		private List<Point> freeStylePoints;
		private int size;

		@JsonCreator
		public FreeHand( //
				@JsonProperty("freeStylePoints") List<Point> freeStylePoints, //
				@JsonProperty("size") int size) {
			this.freeStylePoints = freeStylePoints;
			this.size = size;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public List<Point> getFreeStylePoints() {
			return freeStylePoints;
		}

		public void setFreeStylePoints(List<Point> freeStylePoints) {
			this.freeStylePoints = freeStylePoints;
		}

		public int getSize() {
			return size;
		}

		public void setSize(int size) {
			this.size = size;
		}

	}

	static Set<Session> users = Collections.synchronizedSet(new HashSet<Session>());

	@OnMessage
	public void onMessage(String message, Session session) throws InterruptedException, IOException, EncodeException {
		String toParse = null;
		String type = null;
		Iterator<Session> iterator = users.iterator();
		try {
			JSONObject json = new JSONObject(message);
			System.out.println(String.valueOf(json.get("type")));
			System.out.println(String.valueOf(json.get("content")));
			type = String.valueOf(json.get("type"));
			toParse = String.valueOf(json.get("content"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		ObjectMapper mapper = new ObjectMapper();

		if ("chat".equals(type)) {
			System.out.println("User input: " + toParse);

			MessageNode messageNode = mapper.readValue(toParse, MessageNode.class);

			String username = messageNode.getUsername();

			if (username == null)
				username = (String) session.getUserProperties().get("username");

			if (username == null) {
				session.getUserProperties().put("username", messageNode.getUsername());
				session.getBasicRemote().sendObject(
						buildJsonData("System", "you are now connected as " + messageNode.getUsername(), null));
				username = messageNode.getUsername();
				// Iterator<Session> iterator = users.iterator();
				while (iterator.hasNext()) {
					iterator.next().getBasicRemote()
							.sendObject(buildJsonData(username, messageNode.getMessage(), messageNode.getImageURL()));
				}
			} else {
				// Iterator<Session> iterator = users.iterator();
				while (iterator.hasNext()) {
					iterator.next().getBasicRemote()
							.sendObject(buildJsonData(username, messageNode.getMessage(), messageNode.getImageURL()));
				}
			}
		} else {
			// Object objectToDraw = null;
			if ("line".equals(type)) {
				Line line = mapper.readValue(toParse, Line.class);

				line.setType(type);
				while (iterator.hasNext()) {
					iterator.next().getBasicRemote().sendObject(buildJsonData(line));
				}
			} else if ("rectangle".equals(type)) {
				Rectangle rect = mapper.readValue(toParse, Rectangle.class);

				rect.setType(type);
				while (iterator.hasNext()) {
					iterator.next().getBasicRemote().sendObject(buildJsonData(rect));
				}
			} else if ("circle".equals(type)) {
				Circle circle = mapper.readValue(toParse, Circle.class);

				circle.setType(type);
				while (iterator.hasNext()) {
					iterator.next().getBasicRemote().sendObject(buildJsonData(circle));
				}
			} else if ("polygon".equals(type)) {
				List<Point> polygonPoints = new ArrayList<>();
				try {
					JSONArray array = new JSONArray(toParse);
					for (int i = 0; i < array.length(); ++i) {
						JSONObject object = array.getJSONObject(i);
						int x = object.getInt("x");
						int y = object.getInt("y");
						polygonPoints.add(new Point(x, y));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				// Polygon polygon = mapper.readValue(toParse, Polygon.class);
				Polygon polygon = new Polygon(polygonPoints);

				polygon.setType(type);

				try {
					while (iterator.hasNext()) {
						iterator.next().getBasicRemote().sendObject(buildJsonData(polygon));
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("etwas ist schief gegangen");
				}
			} else if ("freeHand".equals(type)) {
				FreeHand freeHand = mapper.readValue(toParse, FreeHand.class);

				freeHand.setType(type);
				while (iterator.hasNext()) {
					iterator.next().getBasicRemote().sendObject(buildJsonData(freeHand));
				}
			}

			// // Iterator<Session> iterator = users.iterator();
			// while (iterator.hasNext()) {
			// iterator.next().getBasicRemote().sendObject(objectToDraw);
			// }
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

	/**
	 * 
	 * @param username
	 * @param message
	 * @param imageURL
	 * @return
	 */
	private Object buildJsonData(String username, String message, String imageURL) {
		ObjectMapper mapper = new ObjectMapper();

		MessageNode node = new MessageNode(username, message, imageURL);
		node.setType("chat");
		JsonNode json = null;
		try {
			json = mapper.valueToTree(node);
			System.out.println("json: " + json);

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return json;

	}

	/**
	 *
	 * @param type
	 * @param objectToDraw
	 * @return JSON object for JavaScript
	 */
	private Object buildJsonData(Line objectToDraw) {
		ObjectMapper mapper = new ObjectMapper();

		JsonNode json = null;
		try {
			String jsonString = mapper.writeValueAsString(objectToDraw);
			System.out.println("jsonString: " + jsonString);

			json = mapper.valueToTree(objectToDraw);

			// json = mapper.valueToTree(jsonString);
			// json = jsonString;
			System.out.println("json when send: " + json);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return json;
	}

	private Object buildJsonData(Rectangle objectToDraw) {
		ObjectMapper mapper = new ObjectMapper();

		JsonNode json = null;
		try {
			json = mapper.valueToTree(objectToDraw);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return json;
	}

	private Object buildJsonData(Circle objectToDraw) {
		ObjectMapper mapper = new ObjectMapper();

		JsonNode json = null;
		try {
			json = mapper.valueToTree(objectToDraw);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return json;
	}

	private Object buildJsonData(Polygon objectToDraw) {
		// ObjectMapper mapper = new ObjectMapper();
		List<Point> list = objectToDraw.getPolygonPoints();
		JSONArray array = new JSONArray();
		JSONObject o = new JSONObject();
		try {
			o.put("type", objectToDraw.getType());
			// array.put(o);
			for (Point p : list) {
				JSONObject object = new JSONObject();
				object.put("x", p.getX());
				object.put("y", p.getY());

				array.put(object);
				// JsonNode node = mapper.valueToTree(p);
				System.out.println("Node: " + object);
			}
			o.put("content", array);
			System.out.println("Array: " + array);
			System.out.println("Object o: " + o);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// OutputStream out = new ByteArrayOutputStream();
		// ObjectMapper mapper = new ObjectMapper();
		//
		// try {
		// mapper.writeValue(out, list);
		// } catch (IOException e1) {
		// e1.printStackTrace();
		// }

		// JsonNode json = null;
		// // final byte[] data = out.toByteArray();
		// try {
		// json = mapper.valueToTree(objectToDraw);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		//
		// return json;
		return o;
	}

	private Object buildJsonData(FreeHand objectToDraw) {
		ObjectMapper mapper = new ObjectMapper();

		JsonNode json = null;
		try {
			json = mapper.valueToTree(objectToDraw);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return json;
	}

}
