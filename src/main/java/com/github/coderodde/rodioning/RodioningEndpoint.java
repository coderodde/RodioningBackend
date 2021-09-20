package com.github.coderodde.rodioning;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;

/**
 * This class implements the web socket endpoint for pushing program text to the
 * web browser of the client.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Sep 16, 2021)
 * @since 1.6 (Sep 16, 2021)
 */
public class RodioningEndpoint extends Endpoint {

    private static final Logger LOGGER = 
            Logger.getLogger(RodioningEndpoint.class.getSimpleName());
    
    @Override
    public void onOpen(Session session, EndpointConfig config) {
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
    
    @Override
    public void onClose(Session session, CloseReason closeReason) {
        LOGGER.log(
                Level.INFO, 
                "Session [{0}] closed. Reason: {1}", 
                objs(session, closeReason));
    }
    
    @Override
    public void onError(Session session, Throwable throwable) {
        LOGGER.log(
                Level.SEVERE, 
                "Error on session [{0}]. {1}, caused by: {2}", 
                objs(session,
                     throwable.getMessage(), 
                     throwable.getCause()));
    }
    
    private static Object[] objs(Object... objects) {
        return objects;
    }
}
