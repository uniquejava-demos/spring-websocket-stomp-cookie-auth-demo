package demo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public InMemoryUserDetailsManager users() {
        return new InMemoryUserDetailsManager(
                User.withUsername("cyper")
                        .password("{noop}password")
                        .roles("admin")
                        .build(),
                User.withUsername("david")
                        .password("{noop}password")
                        .roles("user")
                        .build()
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests(auth -> auth
                .anyRequest().authenticated()
        );

        http.exceptionHandling((ex) -> ex
                .authenticationEntryPoint(new MyAuthenticationEntryPoint())
                .accessDeniedHandler(new MyAccessDeniedHandler())
        );

        http.formLogin(login -> login
                .permitAll()
                .successHandler(new MyAuthenticationSuccessHandler())
                .failureHandler(new MyAuthenticationFailureHandler())
        );

        http.logout(logout -> logout
                .permitAll()
                .logoutSuccessHandler(new MyLogoutSuccessHandler()));

        // by default uses a Bean by the name of corsConfigurationSource
        http.cors(withDefaults());

        http.csrf(csrf -> csrf
                .ignoringAntMatchers("/login")
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));

        return http.build();
    }

    record R(String code, String message, Object data) {
        public R(String code, String message) {
            this(code, message, null);
        }
    }

    class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            String message = NestedExceptionUtils.getMostSpecificCause(authException).getMessage();
            R r = new R("unauthorized", "MyAuthenticationEntryPoint: " + message);
            response.getWriter().println(new ObjectMapper().writeValueAsString(r));
        }
    }

    class MyAccessDeniedHandler implements AccessDeniedHandler {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            String message = NestedExceptionUtils.getMostSpecificCause(accessDeniedException).getMessage();
            R r = new R("forbidden", "MyAccessDeniedHandler: " + message);
            response.getWriter().println(new ObjectMapper().writeValueAsString(r));
        }
    }

    class MyAuthenticationSuccessHandler implements org.springframework.security.web.authentication.AuthenticationSuccessHandler {
        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            R r = new R("success", "success", UUID.randomUUID().toString());
            response.getWriter().println(new ObjectMapper().writeValueAsString(r));
        }
    }

    class MyAuthenticationFailureHandler implements AuthenticationFailureHandler {
        @Override
        public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            String message = NestedExceptionUtils.getMostSpecificCause(exception).getMessage();
            R r = new R("unauthorized", "MyAuthenticationFailureHandler: " + message);
            response.getWriter().println(new ObjectMapper().writeValueAsString(r));
        }
    }

    class MyLogoutSuccessHandler implements LogoutSuccessHandler {
        @Override
        public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            R r = new R("success", "success");
            response.getWriter().println(new ObjectMapper().writeValueAsString(r));
        }
    }
}
