package hr.algebra.cugomat.configuration;

import hr.algebra.cugomat.filter.CertificateAuthFilter;
import hr.algebra.cugomat.filter.JwtAuthFilter;
import hr.algebra.cugomat.filter.LogFilter;
import hr.algebra.cugomat.service.UserDetailsServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfiguration {
    JwtAuthFilter jwtAuthFilter;
    LogFilter logFilter;
    CertificateAuthFilter certificateAuthFilter;

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/refreshToken",
                                "/api/auth/token/expiry",
                                "/api/auth/ping",
                                "/api/user/register",
                                "api/user/checkExist",
                                "/api/stripe/create-checkout-session"
                        ).permitAll()
                        .requestMatchers(
                                "/api/product/list",
                                "/api/category/list",
                                "/api/order/create",
                                "/api/order/byUser",
                                "/api/client",
                                "/api/user/check").hasAnyRole("USER", "WORKER", "ADMIN")
                        .requestMatchers("/api/worker",
                                "/api/order/setStatus",
                                "/api/order/active",
                                "/api/order/byFilter").hasAnyRole("WORKER", "ADMIN")
                        .requestMatchers(
                                "/api/category",
                                "/api/category/create",
                                "/api/category/update",
                                "/api/category/delete/**",
                                "/api/product",
                                "/api/product/create",
                                "/api/product/update",
                                "/api/product/delete/**",
                                "/h2-console/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(logFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(certificateAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
