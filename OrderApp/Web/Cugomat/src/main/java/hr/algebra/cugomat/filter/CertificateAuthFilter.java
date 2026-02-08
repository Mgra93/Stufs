package hr.algebra.cugomat.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class CertificateAuthFilter extends OncePerRequestFilter {

    private static final List<String> DESKTOP_ONLY_ENDPOINTS = Arrays.asList(
            "/api/worker", "/api/worker/list", "/api/worker/create",  "/api/worker/update", "/api/worker/delete",
            "/api/order/active", "/api/order/byFilter", "/api/order/setStatus", "/api/category/create", "/api/category/update", "/api/category/delete",
            "/api/product", "/api/product/create", "/api/product/update", "/api/product/delete",
            "/api/user/check"
    );

    private static final List<String> CLIENTS_WITH_CERT = Arrays.asList(
            "CBS",
            "KBS"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        boolean needsCertificate = DESKTOP_ONLY_ENDPOINTS.stream()
                .anyMatch(endpoint -> uri.equals(endpoint));

        if (needsCertificate) {
            if (!hasValidCertificate(request)) {
                log.warn("Access denied - No certificate for: {}", uri);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Desktop certificate required\"}");
                return;
            }
            log.info("Valid certificate for: {}", uri);
        }

        filterChain.doFilter(request, response);
    }

    private boolean hasValidCertificate(HttpServletRequest request) {
        X509Certificate[] certs = (X509Certificate[])
                request.getAttribute("jakarta.servlet.request.X509Certificate");

        if (certs == null || certs.length == 0) {
            return false;
        }

        try {
            String subjectDN = certs[0].getSubjectX500Principal().getName();
            String[] itemList = subjectDN.split(",");

            for (String item : itemList) {
                String trimm = item.trim();
                if (trimm.startsWith("CN=")) {
                    String cn = trimm.substring(3).trim();
                    return CLIENTS_WITH_CERT.contains(cn);
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
