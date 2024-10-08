package org.fs.rallyroundbackend.config;

import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.fs.rallyroundbackend.filter.JwtAuthenticationFilter;
import org.fs.rallyroundbackend.filter.MercadoPagoAccountLinkFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
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
    static GrantedAuthorityDefaults grantedAuthorityDefaults(@Value("${spring.security.user.roles.prefix}")
                                                                 String rolePrefix) {
        return new GrantedAuthorityDefaults(rolePrefix);
    }

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
                                .requestMatchers("/rr/api/v1/auth/password/change/").hasRole("PARTICIPANT")
                                .requestMatchers("/rr/api/v1/auth/participant/**").permitAll()
                                .requestMatchers("/rr/api/v1/auth/login").permitAll()

                                .requestMatchers("/rr/api/v1/admin/auth/**").hasRole("ADMIN")
                                .requestMatchers("/rr/api/v1/admin/register").hasAuthority("REGISTER_ADMIN")
                                .requestMatchers("/rr/api/v1/admin/**").hasRole("ADMIN")
                                .requestMatchers("/rr/api/v1/admin/disable/{adminId}").hasAuthority("DELETE_ADMIN")
                                .requestMatchers("/rr/api/v1/admin/enable/{adminId}").hasAuthority("DELETE_ADMIN")
                                .requestMatchers("/rr/api/v1/admin/modify").hasAuthority("MODIFY_ADMIN")
                                .requestMatchers("/rr/api/v1/admin/find/").hasAuthority("READ_ADMINS")
                                .requestMatchers("/rr/api/v1/admin/find/{adminId}").hasAuthority("READ_ADMINS")

                                .requestMatchers("/rr/api/v1/departments").hasRole("ADMIN")

                                .requestMatchers("/rr/api/v1/activities/matches/{name}").permitAll()
                                .requestMatchers("/rr/api/v1/activities/event-counts").permitAll()
                                .requestMatchers("/rr/api/v1/activities").hasRole("ADMIN")
                                .requestMatchers("/rr/api/v1/activities/{id}/disable").hasRole("ADMIN")
                                .requestMatchers("/rr/api/v1/activities/{id}/enable").hasRole("ADMIN")

                                .requestMatchers("/rr/api/v1/participant/**").hasRole("PARTICIPANT")
                                .requestMatchers("/rr/api/v1/participant/report/participants/").hasRole("ADMIN")
                                .requestMatchers("/rr/api/v1/participant/reports/{participantId}").hasRole("ADMIN")
                                .requestMatchers("/rr/api/v1/participant/report/delete/{reportId}").hasRole("ADMIN")
                                .requestMatchers("/rr/api/v1/participant/reports/count").hasRole("ADMIN")

                                .requestMatchers("/rr/api/v1/mp/auth/url/").hasRole("PARTICIPANT")
                                .requestMatchers("/rr/api/v1/mp/auth/account/linked/").hasRole("PARTICIPANT")
                                .requestMatchers("/rr/api/v1/mp/auth/authorize/").permitAll()

                                .requestMatchers("/rr/api/v1/mp/payment/done/").permitAll()

                                .requestMatchers("/rr/api/v1/events/**").hasRole("PARTICIPANT")
                                .requestMatchers("/rr/api/v1/events/fee-stats").hasRole("ADMIN")
                                .requestMatchers("/rr/api/v1/events/inscription-trend/{year}").hasRole("ADMIN")

                                .requestMatchers("/rr/api/v1/location/autosuggest/places/{query}").permitAll()
                                .requestMatchers("/rr/api/v1/location/autosuggest/addresses/{query}")
                                .hasRole("PARTICIPANT")

                                .requestMatchers("/rr/api/v1/chats/**").hasRole("PARTICIPANT")
                                .requestMatchers("/chat").permitAll()
                                .requestMatchers("/notification").permitAll()

                                .requestMatchers("/rr/api/v1/documents/terms-and-conditions").permitAll()
                                .requestMatchers("/rr/api/v1/faq").permitAll()

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
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        String hierarchy = "ROLE_ADMIN > ROLE_PARTICIPANT";
        roleHierarchy.setHierarchy(hierarchy);
        return roleHierarchy;
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
