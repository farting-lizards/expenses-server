package com.shareexpenses.server.config;

import lombok.SneakyThrows;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@Profile("local")
public class WebSecurityConfigFake extends WebSecurityConfigurerAdapter {

    @Override
    @SneakyThrows
    protected void configure(HttpSecurity http) {
        http
            .authorizeRequests()
            .anyRequest()
            .permitAll()
            .and().csrf().disable();
    }
}
