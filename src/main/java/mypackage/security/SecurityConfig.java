package mypackage.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login**", "/error", "/css/**", "/js/**").permitAll() // Сюди пускаємо всіх
                        .anyRequest().authenticated() // На інші сторінки - тільки після логіну
                )
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/farms", true) // Куди перекидати після успішного входу (зміни на свою сторінку ферми)
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/") // Куди перекидати після виходу
                        .permitAll()
                );

        return http.build();
    }
}