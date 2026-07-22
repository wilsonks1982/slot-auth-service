package org.wilsonks.slotauthservice.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(1)
public class CorrelationIdFilter extends OncePerRequestFilter {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    public static final String MDC_KEY = "correlationId";

    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String MDC_USER_ID_KEY = "userId";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws java.io.IOException, ServletException {

        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        String userId = request.getHeader(USER_ID_HEADER);

        MDC.put(MDC_KEY, correlationId != null ? correlationId : "");
        MDC.put(MDC_USER_ID_KEY, userId != null ? userId : "unknown");

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_KEY);
        }
    }

}
