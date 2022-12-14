package com.hcx.hcxPayor.configuration;

import com.hcx.hcxPayor.service.impl.UserServiceImpl;
import com.hcx.hcxPayor.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class SecurityFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserServiceImpl userServiceImpl;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("uri{},", request.getRequestURI());
        String userName = null;
        if (request.getRequestURI().equalsIgnoreCase("/vitrayamockpayor/preauth/submit")) {
            userName = "hcxPayor";
            if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails user = userServiceImpl.loadUserByUsername(userName);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                log.info("authenticationToken{} ", authenticationToken);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            }
            filterChain.doFilter(request, response);
        }
        else {
            String token = request.getHeader("Authorization");
            if (token != null) {
                log.info("token{}", token);
                userName = jwtUtil.getUserName(token);
                log.info("username{} ", userName);
            }
            if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails user = userServiceImpl.loadUserByUsername(userName);
                boolean isValid = jwtUtil.validateToken(token, user);
                log.info("isValid{} ", isValid);
                if (isValid) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    log.info("authenticationToken{} ", authenticationToken);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }

            filterChain.doFilter(request, response);


        }
    }
    }
