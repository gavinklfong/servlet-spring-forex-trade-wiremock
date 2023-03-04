package space.gavinklfong.forex.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Map;

@Slf4j
public class BasicAuthFilter implements Filter {

    private static final String BASIC_AUTH_HEADER = "Authorization";

    @Autowired
    private Map<String, String> apiCredentials;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        // Validate authentication code
        validateBasicAuth(servletRequest);

        // Pass on the request
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private void validateBasicAuth(ServletRequest servletRequest) {

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        String basicAuth = req.getHeader(BASIC_AUTH_HEADER);

    }
}
