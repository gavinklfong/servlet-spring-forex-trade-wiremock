package space.gavinklfong.forex.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.util.SubnetUtils;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class IpAddressFilter implements Filter {

    private static final String LOCALHOST_V6 = "0:0:0:0:0:0:0:1";
    private static final String LOCALHOST_V4 = "127.0.0.1";

    private final List<String> allowedIpRanges;

    private final boolean enabled;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (enabled) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            String clientIpAddress = getClientIpAddress(request);
            boolean matched = allowedIpRanges.stream()
                    .anyMatch(ipRange -> matchIpRange(ipRange, clientIpAddress));
            if (matched) {
                chain.doFilter(request, response);
            } else {
                httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    private boolean matchIpRange(String ipRange, String clientIpAddress) {
        if (ipRange.endsWith("/32")) {
            return ipRange.substring(0, ipRange.length() - 3).equals(clientIpAddress);
        } else if (!ipRange.contains("/")) {
            return ipRange.equals(clientIpAddress);
        } else {
            return new SubnetUtils(ipRange).getInfo().isInRange(clientIpAddress);
        }
    }

    private String getClientIpAddress(ServletRequest request) {
        if (LOCALHOST_V6.equals(request.getRemoteAddr())) {
            return LOCALHOST_V4;
        } else {
            return request.getRemoteAddr();
        }
    }
}
