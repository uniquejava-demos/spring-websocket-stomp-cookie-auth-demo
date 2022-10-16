package demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@Slf4j
public class ChatController {
    @GetMapping("/whoami")
    public String whoami() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("thread.name: {}", Thread.currentThread().getName());
        log.info("auth.name: {}", auth.getName());
        return auth.getName();
    }

    @MessageMapping("/class403")
    public String greetings(String message, Principal principal, SimpMessageHeaderAccessor headerAccessor) {
        log.info("thread.id: {}", Thread.currentThread().getId());
        log.info("thread.name: {}", Thread.currentThread().getName());
        log.info("message: {}", message);
        log.info("principal.name: {}", principal.getName());

        // Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // log.info("auth.name: {}", auth != null ? auth.getName() : null);
        log.info("user: {}", headerAccessor.getUser());

        return message;
    }
}
