package com.example.gym.websocket;

import jakarta.enterprise.inject.spi.CDI;
import jakarta.websocket.server.ServerEndpointConfig;

/**
 * Custom WebSocket configurator that enables CDI injection in WebSocket endpoints.
 */
public class ChatEndpointConfigurator extends ServerEndpointConfig.Configurator {

    @Override
    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
        // Use CDI to create the endpoint instance, enabling @Inject to work
        return CDI.current().select(endpointClass).get();
    }
}
