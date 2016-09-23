package com.gabrielmeyer.websocket;

import java.util.Arrays;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;

import com.gabrielmeyer.decoder.CircleDecoder;
import com.gabrielmeyer.decoder.FreeHandDecoder;
import com.gabrielmeyer.decoder.LineDecoder;
import com.gabrielmeyer.decoder.MessageDecoder;
import com.gabrielmeyer.decoder.PolygonDecoder;
import com.gabrielmeyer.decoder.RectangleDecoder;
import com.gabrielmeyer.encoder.CircleEncoder;
import com.gabrielmeyer.encoder.FreeHandEncoder;
import com.gabrielmeyer.encoder.LineEncoder;
import com.gabrielmeyer.encoder.MessageEncoder;
import com.gabrielmeyer.encoder.NodeEncoder;
import com.gabrielmeyer.encoder.PolygonEncoder;
import com.gabrielmeyer.encoder.RectangleEncoder;
import com.gabrielmeyer.encoder.TextNodeEncoder;

public class StartUpListener implements ServletContextListener {

	private final static String uri = "/websocket";

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		// TODO auto-generated

	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		ServletContext servletContext = event.getServletContext();
		ServerContainer container = (ServerContainer) servletContext.getAttribute(ServerContainer.class.getName());

		try {
			ServerEndpointConfig config = ServerEndpointConfig.Builder.create(WebSocket.class, uri)
					.decoders(Arrays.asList( //
							MessageDecoder.class, //
							LineDecoder.class, //
							RectangleDecoder.class, //
							CircleDecoder.class, //
							PolygonDecoder.class, //
							FreeHandDecoder.class))
					.encoders(Arrays.asList( //
							MessageEncoder.class, //
							LineEncoder.class, //
							RectangleEncoder.class, //
							CircleEncoder.class, //
							PolygonEncoder.class, //
							FreeHandEncoder.class, //
							TextNodeEncoder.class, //
							NodeEncoder.class))
					.build();

			container.addEndpoint(config);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
