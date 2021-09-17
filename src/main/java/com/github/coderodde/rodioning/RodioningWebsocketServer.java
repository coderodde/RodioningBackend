package com.github.coderodde.rodioning;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * This class implements the web socket endpoint for pushing program text to the
 * web browser of the client.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Sep 16, 2021)
 * @since 1.6 (Sep 16, 2021)
 */
@ServerEndpoint(value = "/download")
public final class RodioningWebsocketServer {

    private static final Logger LOGGER = 
            Logger.getLogger(RodioningWebsocketServer.class.getSimpleName());
    
    @OnOpen
    public void open(Session session, EndpointConfig conf) {
        LOGGER.log(Level.INFO, "Session [{0}] opened.", session);
        
        try {
            session.getBasicRemote().sendText("test");
        } catch (IOException ex) {
            LOGGER.log(
                    Level.SEVERE, 
                    "Exception: {0}, caused by: {1}", 
                    objs(ex.getMessage(), 
                         ex.getCause()));
        }
    }
    
    @OnMessage
    public void message(Session session, String message) {
        // Ignore incoming data.
    }
    
    @OnError
    public void error(Session session, Throwable throwable) {
        LOGGER.log(
                Level.SEVERE, 
                "Error on session [{0}]. {1}, caused by: {2}", 
                objs(session,
                     throwable.getMessage(), 
                     throwable.getCause()));
    }
    
    @OnClose
    public void close(Session session, CloseReason closeReason) {
        LOGGER.log(
                Level.INFO, 
                "Session [{0}] closed. Reason: {1}", 
                objs(session, closeReason));
    }
    
    private static Object[] objs(Object... objects) {
        return objects;
    }
}
