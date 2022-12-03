package com.shareexpenses.server.config;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Setter;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix="accounts-config")
@Setter
public class AccountsConfig {
   public List<Map<String, String>> accounts;
}
