package com.example.demo.config;


import com.example.demo.config.WebSecurityConfiguration;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfiguration{

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private UserRepository employeeRepo;

    @Bean
    public UserDetailsService userDetailsService(){
        UserDetailsService service = username ->  employeeRepo.findByEmail(username)
                .orElseThrow(()->new UsernameNotFoundException("User not found"));
        return service;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
        return security.csrf(csrf->csrf.disable())
                .authorizeHttpRequests(
                        auth->auth.requestMatchers("/signup/addUser","/user/login").permitAll()
                ).authorizeHttpRequests(auth->auth.requestMatchers(HttpMethod.POST,"/user/getByToken").authenticated())
                .authorizeHttpRequests(auth->auth.requestMatchers("/admin/**").hasRole("ADMIN"))
                .authorizeHttpRequests(auth->auth.requestMatchers("/employee/**").hasRole("EMPLOYEE"))
                .authorizeHttpRequests(auth->auth.requestMatchers("/hr/**").hasRole("HR"))
                .authorizeHttpRequests(auth->auth.anyRequest().permitAll())
                .sessionManagement(sm->sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return  new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return  configuration.getAuthenticationManager();
    }
}
