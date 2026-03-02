package net.ooder.sdk.core.connection;

import net.ooder.sdk.api.connection.CapabilityEndpoint;
import net.ooder.sdk.api.connection.ConnectionTestResult;
import net.ooder.sdk.api.connection.ConnectionTestService;

import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

/**
 * 连接测试服务实现
 * 实现能力端点连接测试功能
 *
 * @author ooder
 * @since 2.3
 */
public class ConnectionTestServiceImpl implements ConnectionTestService {
    
    @Override
    public CompletableFuture<ConnectionTestResult> testCapabilityEndpoint(CapabilityEndpoint endpoint) {
        return CompletableFuture.supplyAsync(() -> {
            if (endpoint == null || endpoint.getEndpoint() == null) {
                return ConnectionTestResult.failure("Endpoint configuration is null");
            }
            
            String url = endpoint.getEndpoint();
            int timeout = endpoint.getTimeout() > 0 ? endpoint.getTimeout() : 5000;
            
            long startTime = System.currentTimeMillis();
            
            try {
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    return testHttpEndpoint(url, timeout, startTime);
                } else if (url.contains(":")) {
                    return testTcpEndpoint(url, timeout, startTime);
                } else {
                    return ConnectionTestResult.failure("Unsupported endpoint format: " + url);
                }
            } catch (Exception e) {
                return ConnectionTestResult.failure(e.getMessage());
            }
        });
    }
    
    /**
     * 测试 HTTP 端点
     * @param url URL地址
     * @param timeout 超时时间
     * @param startTime 开始时间
     * @return 测试结果
     */
    private ConnectionTestResult testHttpEndpoint(String url, int timeout, long startTime) {
        HttpURLConnection connection = null;
        try {
            URL targetUrl = new URL(url);
            connection = (HttpURLConnection) targetUrl.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setInstanceFollowRedirects(true);
            
            int responseCode = connection.getResponseCode();
            int latency = (int) (System.currentTimeMillis() - startTime);
            
            if (responseCode >= 200 && responseCode < 400) {
                ConnectionTestResult result = ConnectionTestResult.success(latency, 
                    "HTTP connection successful, response code: " + responseCode);
                result.setServerVersion(connection.getHeaderField("Server"));
                return result;
            } else {
                return ConnectionTestResult.failure("HTTP error, response code: " + responseCode);
            }
        } catch (Exception e) {
            return ConnectionTestResult.failure("HTTP connection failed: " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    
    /**
     * 测试 TCP 端点
     * @param endpoint 端点地址 (host:port)
     * @param timeout 超时时间
     * @param startTime 开始时间
     * @return 测试结果
     */
    private ConnectionTestResult testTcpEndpoint(String endpoint, int timeout, long startTime) {
        String[] parts = endpoint.split(":");
        if (parts.length != 2) {
            return ConnectionTestResult.failure("Invalid TCP endpoint format, expected host:port");
        }
        
        String host = parts[0];
        int port;
        try {
            port = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            return ConnectionTestResult.failure("Invalid port number: " + parts[1]);
        }
        
        try (Socket socket = new Socket()) {
            socket.connect(new java.net.InetSocketAddress(host, port), timeout);
            int latency = (int) (System.currentTimeMillis() - startTime);
            return ConnectionTestResult.success(latency, 
                "TCP connection successful to " + host + ":" + port);
        } catch (Exception e) {
            return ConnectionTestResult.failure("TCP connection failed: " + e.getMessage());
        }
    }
}
