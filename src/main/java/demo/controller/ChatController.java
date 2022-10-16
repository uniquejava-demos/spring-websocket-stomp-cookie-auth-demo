package demo.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class ChatController {

    @MessageMapping("/class403")
    public String greetings(String message, Principal principal) {
        System.out.println("message: " + message);

        System.out.println("principal.name: " + principal.getName());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("auth.name: " + auth.getName());

        return message;
    }
}
