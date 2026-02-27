package net.ooder.scene.skill;

import java.util.Map;

/**
 * HttpClient Provider 接口
 *
 * <p>定义 HTTP 客户端能力接口，由 Skills Team 实现</p>
 * <p>实现类通过 ServiceLoader 注册</p>
 */
public interface HttpClientProvider {

    /**
     * 获取提供者类型
     * @return 如 "okhttp", "apache", "java", "mock"
     */
    String getProviderType();

    /**
     * GET 请求
     * @param url URL
     * @param headers 请求头
     * @param options 可选参数
     * @return 响应
     */
    HttpResponse get(String url, Map<String, String> headers, Map<String, Object> options);

    /**
     * POST 请求
     * @param url URL
     * @param body 请求体
     * @param headers 请求头
     * @param options 可选参数
     * @return 响应
     */
    HttpResponse post(String url, Object body, Map<String, String> headers, Map<String, Object> options);

    /**
     * PUT 请求
     * @param url URL
     * @param body 请求体
     * @param headers 请求头
     * @param options 可选参数
     * @return 响应
     */
    HttpResponse put(String url, Object body, Map<String, String> headers, Map<String, Object> options);

    /**
     * DELETE 请求
     * @param url URL
     * @param headers 请求头
     * @param options 可选参数
     * @return 响应
     */
    HttpResponse delete(String url, Map<String, String> headers, Map<String, Object> options);

    /**
     * HEAD 请求
     * @param url URL
     * @param headers 请求头
     * @param options 可选参数
     * @return 响应
     */
    HttpResponse head(String url, Map<String, String> headers, Map<String, Object> options);

    /**
     * PATCH 请求
     * @param url URL
     * @param body 请求体
     * @param headers 请求头
     * @param options 可选参数
     * @return 响应
     */
    HttpResponse patch(String url, Object body, Map<String, String> headers, Map<String, Object> options);

    /**
     * HTTP 响应
     */
    class HttpResponse {
        private int statusCode;
        private Map<String, String> headers;
        private Object body;
        private long duration;

        public HttpResponse() {}

        public HttpResponse(int statusCode, Map<String, String> headers, Object body, long duration) {
            this.statusCode = statusCode;
            this.headers = headers;
            this.body = body;
            this.duration = duration;
        }

        public int getStatusCode() { return statusCode; }
        public void setStatusCode(int statusCode) { this.statusCode = statusCode; }
        public Map<String, String> getHeaders() { return headers; }
        public void setHeaders(Map<String, String> headers) { this.headers = headers; }
        public Object getBody() { return body; }
        public void setBody(Object body) { this.body = body; }
        public long getDuration() { return duration; }
        public void setDuration(long duration) { this.duration = duration; }

        public boolean isSuccess() {
            return statusCode >= 200 && statusCode < 300;
        }
    }
}
