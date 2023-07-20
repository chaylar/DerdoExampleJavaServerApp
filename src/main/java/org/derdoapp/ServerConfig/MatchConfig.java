package org.derdoapp.ServerConfig;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "match")
public class MatchConfig {

    private int defaultCount = 25;//TODO : FROM CONFIG FILE

    public int GetDefaultCount() {
        return defaultCount;
    }

}
