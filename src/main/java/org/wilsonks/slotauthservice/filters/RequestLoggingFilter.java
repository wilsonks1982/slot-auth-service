package org.wilsonks.slotauthservice.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    public static final String MDC_KEY = "correlationId";

    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String MDC_USER_ID_KEY = "userId";

    public static final String USER_ROLE_HEADER = "X-User-Role";
    public static final String MDC_USER_ROLE_KEY = "role";

    public static final String USER_TYPE_HEADER = "X-User-Type";
    public static final String MDC_USER_TYPE_KEY = "type";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws java.io.IOException, ServletException {

        long start = System.currentTimeMillis();

        MDC.put(MDC_KEY, request.getHeader(CORRELATION_ID_HEADER) != null ? request.getHeader(CORRELATION_ID_HEADER) : "N/A");
        MDC.put(MDC_USER_ID_KEY, request.getHeader(USER_ID_HEADER) != null ? request.getHeader(USER_ID_HEADER) : "");
        MDC.put(MDC_USER_ROLE_KEY, request.getHeader(USER_ROLE_HEADER) != null ? request.getHeader(USER_ROLE_HEADER) : "");
        MDC.put(MDC_USER_TYPE_KEY, request.getHeader(USER_TYPE_HEADER) != null ? request.getHeader(USER_TYPE_HEADER) : "");

        try {
            log.info(
                    "Incoming Request method={} uri={} ip={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    request.getRemoteAddr());

            filterChain.doFilter(request, response);
        } finally {
            long duration =
                    System.currentTimeMillis() - start;

            log.info(
                    "Completed Request method={} uri={} status={} duration={}ms",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration);

            MDC.clear();
        }
    }

}
