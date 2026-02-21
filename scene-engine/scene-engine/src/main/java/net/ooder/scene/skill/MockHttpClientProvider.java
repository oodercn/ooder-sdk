package net.ooder.scene.skill;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock HTTP Client Provider
 *
 * <p>默认的 Mock 实现，用于测试和开发</p>
 */
public class MockHttpClientProvider implements HttpClientProvider {

    @Override
    public String getProviderType() {
        return "mock";
    }

    @Override
    public HttpResponse get(String url, Map<String, String> headers, Map<String, Object> options) {
        Map<String, String> responseHeaders = new HashMap<String, String>();
        responseHeaders.put("Content-Type", "application/json");
        return new HttpResponse(200, responseHeaders, "{\"message\": \"Mock GET response\"}", 100);
    }

    @Override
    public HttpResponse post(String url, Object body, Map<String, String> headers, Map<String, Object> options) {
        Map<String, String> responseHeaders = new HashMap<String, String>();
        responseHeaders.put("Content-Type", "application/json");
        return new HttpResponse(201, responseHeaders, "{\"message\": \"Mock POST response\"}", 150);
    }

    @Override
    public HttpResponse put(String url, Object body, Map<String, String> headers, Map<String, Object> options) {
        Map<String, String> responseHeaders = new HashMap<String, String>();
        responseHeaders.put("Content-Type", "application/json");
        return new HttpResponse(200, responseHeaders, "{\"message\": \"Mock PUT response\"}", 120);
    }

    @Override
    public HttpResponse delete(String url, Map<String, String> headers, Map<String, Object> options) {
        Map<String, String> responseHeaders = new HashMap<String, String>();
        return new HttpResponse(204, responseHeaders, null, 80);
    }

    @Override
    public HttpResponse head(String url, Map<String, String> headers, Map<String, Object> options) {
        Map<String, String> responseHeaders = new HashMap<String, String>();
        responseHeaders.put("Content-Type", "application/json");
        responseHeaders.put("Content-Length", "20");
        return new HttpResponse(200, responseHeaders, null, 50);
    }

    @Override
    public HttpResponse patch(String url, Object body, Map<String, String> headers, Map<String, Object> options) {
        Map<String, String> responseHeaders = new HashMap<String, String>();
        responseHeaders.put("Content-Type", "application/json");
        return new HttpResponse(200, responseHeaders, "{\"message\": \"Mock PATCH response\"}", 100);
    }
}
