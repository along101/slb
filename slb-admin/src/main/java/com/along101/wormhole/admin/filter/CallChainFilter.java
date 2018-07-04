package com.along101.wormhole.admin.filter;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by yinzuolong on 2017/8/15.
 */
@Component
@Slf4j
public class CallChainFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        UUID uuid = java.util.UUID.randomUUID();
        MDC.put("uuid", uuid.toString());
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
