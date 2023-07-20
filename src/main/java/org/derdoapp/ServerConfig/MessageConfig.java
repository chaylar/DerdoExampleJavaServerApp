package org.derdoapp.ServerConfig;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "message")
public class MessageConfig {

    private int itemsPerPage = 10;//TODO : FROM CONFIG FILE

    public int GetItemsPerPage() {
        return itemsPerPage;
    }

}
