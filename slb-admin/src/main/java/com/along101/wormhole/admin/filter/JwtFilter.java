package com.along101.wormhole.admin.filter;

import com.along.wormhole.admin.common.exception.SlbServiceException;
import com.along.wormhole.admin.config.EnvProperty;
import com.along.wormhole.admin.dto.Response;
import com.along101.wormhole.admin.common.exception.SlbServiceException;
import com.along101.wormhole.admin.config.EnvProperty;
import com.along101.wormhole.admin.dto.Response;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.along101.wormhole.admin.common.exception.SlbServiceException;
import com.along101.wormhole.admin.config.EnvProperty;
import com.along101.wormhole.admin.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by huangyinhuang on 7/20/2017.
 */
@Component
@Slf4j
public class JwtFilter implements Filter {

    @Autowired
    private EnvProperty envProperty;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String httpMethod = httpRequest.getMethod();
        if (!envProperty.JWT_CHECK_ENABLE || httpMethod.equals("GET")) {
            // skip jwt check
            chain.doFilter(request, response);
        } else {
            try {
                doAuthentication(request);
                chain.doFilter(request, response);
            } catch (SlbServiceException e) {
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                httpResponse.setCharacterEncoding("UTF-8");
                httpResponse.setContentType("application/json; charset=utf-8");
                httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);

                Response result = Response.error(e);
                ObjectMapper mapper = new ObjectMapper();
                httpResponse.getWriter().write(mapper.writeValueAsString(result));
            }
        }
    }

    @Override
    public void destroy() {
    }

    private void doAuthentication(ServletRequest request) throws SlbServiceException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String jwtToken = httpRequest.getHeader("jwt-token");
        if (jwtToken == null) {
            throw SlbServiceException.newException("用户令牌为空，请登录系统后进行操作。");
        }

        String userName = null;
        try {
            JWT jwt = decodeToken(jwtToken);
            userName = jwt.getSubject();
        } catch (Exception ex) {
            log.info("failed to decode the user info from jwt token: " + jwtToken);
        }

        if (userName == null || userName.equals("")) {
            throw SlbServiceException.newException("用户令牌解析失败，无法确认当前操作的用户，请尝试重新登录。");
        }

        // set the user name into request attribute, which will be used by Spring JPA　Auditor　Aware
        httpRequest.setAttribute("slb.audit.username", userName);
    }

    private JWT decodeToken(String jwtToken) {
        JWT jwt = null;

        try {
            // verify jwt signature
            JWT.require(Algorithm.HMAC256("slb-secret"))
                    .withIssuer("slb-front")
                    .build()
                    .verify(jwtToken);

            // decode jwt token
            jwt = JWT.decode(jwtToken);
        } catch (JWTVerificationException e) {
            log.warn("invalid jwt sign is found.");
        } catch (UnsupportedEncodingException e) {
            log.warn("unsuppoed jwt encodking is found.");
        }

        return jwt;
    }

}
