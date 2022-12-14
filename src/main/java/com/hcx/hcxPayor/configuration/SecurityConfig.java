package com.hcx.hcxPayor.configuration;

import com.hcx.hcxPayor.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserServiceImpl userServiceImpl;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private InvalidUserAuthEntryPoint authenticationEntryPoint;

    @Autowired
    private SecurityFilter securityFilter;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
      auth.userDetailsService(userServiceImpl);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception{
        return super.authenticationManagerBean();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
               http.csrf().disable().
               authorizeRequests().antMatchers("/","/payor/user/addUser","/payor/user/login").permitAll()
               .anyRequest().authenticated().and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

               http.addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);

    }
}
