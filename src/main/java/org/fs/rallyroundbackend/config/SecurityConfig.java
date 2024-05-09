package org.fs.rallyroundbackend.config;

import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.fs.rallyroundbackend.filter.JwtAuthenticationFilter;
import org.fs.rallyroundbackend.filter.MercadoPagoAccountLinkFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final MercadoPagoAccountLinkFilter mercadoPagoAccountLinkFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        return http
                .csrf(csrf ->
                        csrf.disable())
                .authorizeHttpRequests(authRequest ->
                        authRequest
                                .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
                                .requestMatchers("/rr/api/v1/auth/participant/validate/jwt").hasRole("PARTICIPANT")
                                .requestMatchers("/rr/api/v1/auth/participant/**").permitAll()

                                .requestMatchers("/rr/api/v1/activities/matches/{name}").permitAll()

                                .requestMatchers("/rr/api/v1/participant/**").hasRole("PARTICIPANT")

                                .requestMatchers("/rr/api/v1/mp/auth/url/").hasRole("PARTICIPANT")
                                .requestMatchers("/rr/api/v1/mp/auth/account/linked/").hasRole("PARTICIPANT")
                                .requestMatchers("/rr/api/v1/mp/auth/authorize/").permitAll()

                                .requestMatchers("/rr/api/v1/mp/payment/done/").permitAll()

                                .requestMatchers("/rr/api/v1/events/**").hasRole("PARTICIPANT")

                                .requestMatchers("/rr/api/v1/location/autosuggest/places/{query}").permitAll()
                                .requestMatchers("/rr/api/v1/location/autosuggest/addresses/{query}")
                                .hasRole("PARTICIPANT")

                                .anyRequest().authenticated()
                )
                .sessionManagement(sessionManager->
                        sessionManager
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(mercadoPagoAccountLinkFilter, JwtAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception
    {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider()
    {
        DaoAuthenticationProvider authenticationProvider= new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
