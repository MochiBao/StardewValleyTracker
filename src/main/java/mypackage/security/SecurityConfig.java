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
                        // Дозволяємо доступ до головної, стилів та картинок для всіх
                        .requestMatchers("/", "/login**", "/error", "/css/**", "/js/**", "/images/**").permitAll()
                        .anyRequest().authenticated() // Все інше - тільки після входу
                )
                // НАЛАШТУВАННЯ ДЛЯ ЗВИЧАЙНОГО ЛОГІНУ (з логіном і паролем)
                .formLogin(form -> form
                        .loginPage("/") // Кажемо Spring: "Моя форма лежить на головній сторінці"
                        .defaultSuccessUrl("/farms", true)
                        .permitAll()
                )
                // НАЛАШТУВАННЯ ДЛЯ GOOGLE ЛОГІНУ
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/") // ГОЛОВНА МАГІЯ ТУТ: забороняємо Spring малювати своє вікно або одразу кидати на Гугл
                        .defaultSuccessUrl("/farms", true)
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }
}