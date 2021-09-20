package com.github.coderodde.rodioning;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.websocket.Endpoint;
import javax.websocket.server.ServerApplicationConfig;
import javax.websocket.server.ServerEndpointConfig;

/**
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 ()
 * @since 1.6 ()
 */
public final class RodioningServerApplicationConfig 
        implements ServerApplicationConfig  {

    public static final class WebsocketNames {
        
        public static final String DOWNLOAD_PROGRAM_TEXT = "/download";
    }
    
    @Override
    public Set<ServerEndpointConfig> 
        getEndpointConfigs(Set<Class<? extends Endpoint>> endpointClasses) {
           Set<ServerEndpointConfig> result = new HashSet<>();
           
           for (Class<? extends Endpoint> cls : endpointClasses) {
               if (cls.equals(RodioningEndpoint.class)) {
                   ServerEndpointConfig config = 
                           ServerEndpointConfig.Builder.create(
                                   cls, 
                                   WebsocketNames.DOWNLOAD_PROGRAM_TEXT)
                                   .build();
                   
                   result.add(config);
               }
           }
           
           return result;  
    }

    @Override
    public Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> scanned) {
        return Collections.<Class<?>>emptySet();
    }
}
