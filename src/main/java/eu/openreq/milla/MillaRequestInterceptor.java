package eu.openreq.milla;

import eu.openreq.milla.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MillaRequestInterceptor
        extends HandlerInterceptorAdapter {

    @Autowired
    FileService service;

    @Value("${milla.logActivity}")
    private boolean logActivity;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) {
        if (logActivity && !request.getParameterMap().isEmpty()) {
            service.logRequests(request);
        }
        return true;
    }


}
