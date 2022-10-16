package demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stomp").setAllowedOrigins("*").setHandshakeHandler(new MyHandleShakeHandler());
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Set prefix for the endpoint that the client listens for our messages from
        registry.enableSimpleBroker("/topic");

        // Set prefix for endpoints the client will send messages to
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new MyInboundChannelInterceptor());
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.interceptors(new MyOutboundChannelInterceptor());
    }

    private class MyInboundChannelInterceptor implements ChannelInterceptor {
        @Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {
            log.info("================= MyInboundChannelInterceptor =================");
            log.info("thread.id: {}", Thread.currentThread().getId());
            log.info("thread.name: {}", Thread.currentThread().getName());

            final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

            log.info("accessor.user: {}", accessor.getUser());

            if (StompCommand.CONNECT == accessor.getCommand()) {
                log.info("=============== CONNECT =============");
                MessageHeaders headers = message.getHeaders();
                headers.forEach((h, index) -> {
                    log.info("{} -> {}", h, headers.get(h));
                });
            }

            if (StompCommand.SEND == accessor.getCommand()) {
                log.info("=============== SEND =============");
                MessageHeaders headers = message.getHeaders();
                headers.forEach((h, index) -> {
                    log.info("{} -> {}", h, headers.get(h));
                });

                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth == null) {
                    log.info("setting auth ...");
                    SecurityContextHolder.getContext().setAuthentication((UsernamePasswordAuthenticationToken) accessor.getUser());
                }
            }

            return message;
        }
    }

    private class MyOutboundChannelInterceptor implements ChannelInterceptor {
        @Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {
            log.info("============== MyOutboundChannelInterceptor =============");
            log.info("thread.id: {}", Thread.currentThread().getId());
            log.info("thread.name: {}", Thread.currentThread().getName());
            log.info("message: {}", message);

            return message;
        }
    }

    private class MyHandleShakeHandler extends DefaultHandshakeHandler {
        @Override
        protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
            log.info("============== MyHandleShakeHandler =============");
            log.info("thread.id: {}", Thread.currentThread().getId());
            log.info("thread.name: {}", Thread.currentThread().getName());

            Principal principal = super.determineUser(request, wsHandler, attributes);

            return principal;
        }
    }
}
