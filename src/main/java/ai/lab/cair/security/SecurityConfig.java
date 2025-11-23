package ai.lab.cair.security;

import ai.lab.cair.filter.JwtAuthenticationFilter;
import ai.lab.cair.filter.TokenBlacklistFilter;
import ai.lab.cair.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final TokenBlacklistFilter tokenBlacklistFilter;
    private final CustomUserDetailsService userDetailsService;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        http
                .sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                        .accessDeniedHandler(new CustomAccessDeniedHandler())
                );

        http.addFilterBefore(tokenBlacklistFilter, LogoutFilter.class);
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.authorizeHttpRequests(auth -> {
            if (!"prod".equals(activeProfile)) {
                auth.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll();
            } else {
                auth.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").denyAll();
            }

            auth
                    .requestMatchers("/error").permitAll()
                    .requestMatchers(HttpMethod.POST, "/auth/login", "/auth/refresh-token").permitAll()
                    .requestMatchers("/auth/logout").authenticated()

                    .requestMatchers(HttpMethod.GET, "/team-members/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/team-members/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/team-members/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/team-members/**").hasRole("ADMIN")

                    .requestMatchers(HttpMethod.GET, "/projects/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/projects/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/projects/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/projects/**").hasRole("ADMIN")

                    .anyRequest().authenticated();
        });

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());

        return authenticationManagerBuilder.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        if ("prod".equals(activeProfile)) {
            configuration.setAllowedOriginPatterns(Arrays.asList(
                    "https://admin.di-clinic.kz",
                    "https://di-clinic.kz"
            ));
        } else {
            configuration.setAllowedOriginPatterns(Arrays.asList(
                    "http://localhost:3000",
                    "http://localhost:5173"
            ));
        }

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin")); // "Cache-Control", "xsrf-token",
        configuration.setExposedHeaders(Arrays.asList("Set-Cookie", "Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
