package net.ooder.sdk.llm.token.impl;

import lombok.extern.slf4j.Slf4j;
import net.ooder.sdk.llm.token.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Token 配额服务实现
 *
 * @version 2.3.1
 * @since 2.3.1
 */
@Slf4j
public class TokenQuotaServiceImpl implements TokenQuotaService {

    private final Map<String, Integer> quotaLimits = new ConcurrentHashMap<>();
    private final Map<String, Integer> quotaUsed = new ConcurrentHashMap<>();
    private final Map<String, Reservation> reservations = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Integer>> usageByModel = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Integer>> usageByOperation = new ConcurrentHashMap<>();
    private final Map<String, Integer> requestCounts = new ConcurrentHashMap<>();

    private final int defaultQuota;

    public TokenQuotaServiceImpl() {
        this(10000);
    }

    public TokenQuotaServiceImpl(int defaultQuota) {
        this.defaultQuota = defaultQuota;
    }

    @Override
    public QuotaCheckResult checkQuota(QuotaRequest quotaRequest) {
        if (quotaRequest == null || quotaRequest.getScope() == null) {
            return QuotaCheckResult.denied("Invalid request");
        }

        String scopeKey = getScopeKey(quotaRequest.getScope());
        int limit = quotaLimits.getOrDefault(scopeKey, defaultQuota);
        int used = quotaUsed.getOrDefault(scopeKey, 0);
        int reserved = getReservedAmount(scopeKey);
        int available = limit - used - reserved;

        if (available >= quotaRequest.getRequestedTokens()) {
            return QuotaCheckResult.allowed(available, limit, used);
        }

        return QuotaCheckResult.denied("Insufficient quota. Available: " + available + 
                ", Requested: " + quotaRequest.getRequestedTokens());
    }

    @Override
    public int consumeQuota(TokenConsumption consumption) {
        if (consumption == null || consumption.getScope() == null) {
            return 0;
        }

        String scopeKey = getScopeKey(consumption.getScope());
        int tokens = consumption.getTotalTokens();

        quotaUsed.merge(scopeKey, tokens, Integer::sum);

        recordModelUsage(scopeKey, consumption.getModel(), tokens);
        recordOperationUsage(scopeKey, consumption.getOperationId(), tokens);
        requestCounts.merge(scopeKey, 1, Integer::sum);

        log.debug("Consumed {} tokens for scope {}", tokens, scopeKey);
        return tokens;
    }

    @Override
    public QuotaUsageStats getUsageStats(QuotaScope scope) {
        if (scope == null) {
            return QuotaUsageStats.builder().build();
        }

        String scopeKey = getScopeKey(scope);
        int limit = quotaLimits.getOrDefault(scopeKey, defaultQuota);
        int used = quotaUsed.getOrDefault(scopeKey, 0);

        return QuotaUsageStats.builder()
                .scope(scope)
                .totalQuota(limit)
                .usedQuota(used)
                .remainingQuota(limit - used)
                .usagePercentage(limit > 0 ? (double) used / limit * 100 : 0)
                .usageByModel(usageByModel.getOrDefault(scopeKey, new HashMap<>()))
                .usageByOperation(usageByOperation.getOrDefault(scopeKey, new HashMap<>()))
                .requestCount(requestCounts.getOrDefault(scopeKey, 0))
                .build();
    }

    @Override
    public void setQuota(QuotaScope scope, int quota) {
        if (scope != null) {
            String scopeKey = getScopeKey(scope);
            quotaLimits.put(scopeKey, quota);
            log.info("Quota set to {} for scope {}", quota, scopeKey);
        }
    }

    @Override
    public void resetQuota(QuotaScope scope) {
        if (scope != null) {
            String scopeKey = getScopeKey(scope);
            quotaUsed.remove(scopeKey);
            usageByModel.remove(scopeKey);
            usageByOperation.remove(scopeKey);
            requestCounts.remove(scopeKey);
            log.info("Quota reset for scope {}", scopeKey);
        }
    }

    @Override
    public String reserveQuota(QuotaRequest quotaRequest) {
        QuotaCheckResult checkResult = checkQuota(quotaRequest);
        if (!checkResult.isAllowed()) {
            return null;
        }

        String reservationId = UUID.randomUUID().toString();
        String scopeKey = getScopeKey(quotaRequest.getScope());

        reservations.put(reservationId, new Reservation(
                scopeKey,
                quotaRequest.getRequestedTokens(),
                System.currentTimeMillis()
        ));

        return reservationId;
    }

    @Override
    public void releaseReservation(String reservationId) {
        Reservation reservation = reservations.remove(reservationId);
        if (reservation != null) {
            log.debug("Released reservation {} for {} tokens", reservationId, reservation.tokens);
        }
    }

    @Override
    public void confirmReservation(String reservationId, int actualConsumption) {
        Reservation reservation = reservations.remove(reservationId);
        if (reservation != null) {
            quotaUsed.merge(reservation.scopeKey, actualConsumption, Integer::sum);
            log.debug("Confirmed reservation {} with {} tokens", reservationId, actualConsumption);
        }
    }

    private String getScopeKey(QuotaScope scope) {
        StringBuilder sb = new StringBuilder();
        if (scope.getCompanyId() != null) sb.append("c:").append(scope.getCompanyId());
        if (scope.getDepartmentId() != null) sb.append(":d:").append(scope.getDepartmentId());
        if (scope.getUserId() != null) sb.append(":u:").append(scope.getUserId());
        if (scope.getSceneId() != null) sb.append(":s:").append(scope.getSceneId());
        return sb.length() > 0 ? sb.toString() : "default";
    }

    private int getReservedAmount(String scopeKey) {
        return reservations.values().stream()
                .filter(r -> r.scopeKey.equals(scopeKey))
                .mapToInt(r -> r.tokens)
                .sum();
    }

    private void recordModelUsage(String scopeKey, String model, int tokens) {
        if (model != null) {
            usageByModel.computeIfAbsent(scopeKey, k -> new ConcurrentHashMap<>())
                    .merge(model, tokens, Integer::sum);
        }
    }

    private void recordOperationUsage(String scopeKey, String operation, int tokens) {
        if (operation != null) {
            usageByOperation.computeIfAbsent(scopeKey, k -> new ConcurrentHashMap<>())
                    .merge(operation, tokens, Integer::sum);
        }
    }

    private static class Reservation {
        final String scopeKey;
        final int tokens;
        final long timestamp;

        Reservation(String scopeKey, int tokens, long timestamp) {
            this.scopeKey = scopeKey;
            this.tokens = tokens;
            this.timestamp = timestamp;
        }
    }
}
