package com.newland.tianyan.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.annotation.Order;
import org.springframework.security.web.savedrequest.Enumerator;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Component
@Order(value = Integer.MIN_VALUE)
public class JsonToUrlEncodedAuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String servletPath = ((HttpServletRequest) request).getServletPath();
        String contentType = request.getContentType();
        if ("/oauth/token".equals(servletPath) && contentType.contains("application/json")) {
            InputStream is = request.getInputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[16384];

            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            byte[] json = buffer.toByteArray();

            if (json.length > 0) {
                HashMap result = new ObjectMapper().readValue(json, HashMap.class);
                HashMap<String, String[]> parameters = new HashMap<>();
                for (Object key : result.keySet()) {
                    String[] val = new String[1];
                    val[0] = (String) result.get(key);
                    parameters.put((String) key, val);
                }

                HttpServletRequest requestWrapper = new RequestWrapper((HttpServletRequest) request, parameters);
                chain.doFilter(requestWrapper, response);
            }else {
                chain.doFilter(request, response);
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }

    private class RequestWrapper extends HttpServletRequestWrapper {

        private final Map<String, String[]> params;

        RequestWrapper(HttpServletRequest request, Map<String, String[]> params) {
            super(request);
            this.params = params;
        }

        @Override
        public String getParameter(String name) {
            if (this.params.containsKey(name)) {
                return this.params.get(name)[0];
            }
            return "";
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            return this.params;
        }

        @Override
        public Enumeration<String> getParameterNames() {
            return new Enumerator<>(params.keySet());
        }

        @Override
        public String[] getParameterValues(String name) {
            return params.get(name);
        }
    }

}

