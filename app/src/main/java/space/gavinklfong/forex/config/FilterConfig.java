package space.gavinklfong.forex.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import space.gavinklfong.forex.filter.IpAddressFilter;
import space.gavinklfong.forex.filter.RequestLogFilter;

import java.util.List;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@Configuration
public class FilterConfig {

    @Value("${app.ip-filter.allowed-ip-range:}")
    private List<String> allowedIpRanges;

    @Value("${app.ip-filter.enabled:false}")
    private Boolean ipFilterEnabled;

    @Bean
    public FilterRegistrationBean ipAddressFilter() {
        FilterRegistrationBean filterReg = new FilterRegistrationBean();
        filterReg.setFilter(new IpAddressFilter(allowedIpRanges, ipFilterEnabled));
        filterReg.addUrlPatterns("/deals");
        filterReg.setOrder(HIGHEST_PRECEDENCE + 1);
        return filterReg;
    }

    @Bean
    public FilterRegistrationBean requestLogFilter() {
        FilterRegistrationBean filterReg = new FilterRegistrationBean();
        filterReg.setFilter(new RequestLogFilter());
        filterReg.addUrlPatterns("/*");
        filterReg.setOrder(HIGHEST_PRECEDENCE);
        return filterReg;
    }
}
