package org.derdoapp.ServerConfig;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "auth")
public class ConfigAuth {

    private String defaultAuth;

    public String GetDefaultAuthToken() {
        return defaultAuth;
    }

}
