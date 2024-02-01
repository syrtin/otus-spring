package ru.otus.hw.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import ru.otus.hw.services.UserService;

import java.util.ArrayList;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfiguration {

    private final UserService userService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        auth -> auth.requestMatchers(HttpMethod.GET, "/", "/books", "/api/books",
                                        "/api/books/{id}", "/api/books/{bookId}/comments", "/api/authors", "api/genres")
                                .hasAnyRole("USER", "ADMIN")
                                .requestMatchers(new RegexRequestMatcher("/books/\\d+", null))
                                .hasAnyRole("USER", "ADMIN")
                                .requestMatchers("/books/{id}/edit", "/books/new", "/api/**")
                                .hasRole("ADMIN")
                                .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .permitAll()
                )
                .rememberMe(rememberMe -> rememberMe.key("AbraCadabra").tokenValiditySeconds(60 * 30))
                .logout(logout -> logout.logoutUrl("/signout").permitAll());
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        var users = new ArrayList<UserDetails>();
        var dbUsers = userService.findAll();
        dbUsers.forEach(u -> users.add(
                User.builder().username(u.getUsername()).password(u.getPassword()).roles(u.getRole().name()).build())
        );

        return new InMemoryUserDetailsManager(users);
    }

    @SuppressWarnings("deprecation")
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
