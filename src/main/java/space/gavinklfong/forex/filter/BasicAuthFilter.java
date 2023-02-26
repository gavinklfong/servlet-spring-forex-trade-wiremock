package space.gavinklfong.forex.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
public class BasicAuthFilter implements Filter {

    private final static String BASIC_AUTH_HEADER = "Authorization";

    @Autowired
    private BasicAuthFilterProperties properties;

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
