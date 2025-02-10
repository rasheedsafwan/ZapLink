package com.url.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtTokenProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // get jwt token
            String jwt = jwtTokenProvider.getJwtFromHeader(request);
            if (jwt!=null && jwtTokenProvider.validateToken(jwt)){
                String username = jwtTokenProvider.getUsernameFromJwtToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (userDetails != null){
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        filterChain.doFilter(request,response);
    }
}

/*Notes for self
Create an Authentication Token
UsernamePasswordAuthenticationToken authentication =
    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

Purpose: This object represents an authenticated user in Spring Security.

2. Set Request Details in Authentication

authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
WebAuthenticationDetailsSource().buildDetails(request) extracts additional details from the request, such as:
IP address
Session ID
Other request metadata
These details are useful for security audits and tracking user activities.

3. Store Authentication in the Security Context
SecurityContextHolder.getContext().setAuthentication(authentication);
SecurityContextHolder is a Spring Security component that holds authentication details for the current request.
setAuthentication(authentication) sets the authenticated user in the security context.
 */
