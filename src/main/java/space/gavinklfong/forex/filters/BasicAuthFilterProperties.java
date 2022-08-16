package space.gavinklfong.forex.filters;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Value
@Component
@ConfigurationProperties(prefix = "app.filters.basic-auth-filter")
public class BasicAuthFilterProperties {

    private Map<String, String> apiCredentials;
}
