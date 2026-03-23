package net.ooder.sdk.a2a.loadbalance.impl;

import lombok.extern.slf4j.Slf4j;
import net.ooder.sdk.a2a.AgentInfo;
import net.ooder.sdk.a2a.loadbalance.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 负载均衡服务实现
 *
 * @version 2.3.1
 * @since 2.3.1
 */
@Slf4j
public class LoadBalancerImpl implements LoadBalancer {

    private final Map<String, LoadInfo> loadInfoMap = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> roundRobinCounters = new ConcurrentHashMap<>();
    private final String strategy;
    private final Random random = new Random();

    public LoadBalancerImpl() {
        this("least-load");
    }

    public LoadBalancerImpl(String strategy) {
        this.strategy = strategy;
    }

    @Override
    public AgentInfo selectAgent(List<AgentInfo> agents, SelectionContext context) {
        if (agents == null || agents.isEmpty()) {
            return null;
        }

        List<AgentInfo> availableAgents = filterAvailableAgents(agents);
        if (availableAgents.isEmpty()) {
            log.warn("No available agents for selection");
            return null;
        }

        switch (strategy.toLowerCase()) {
            case "round-robin":
                return selectByRoundRobin(availableAgents, context);
            case "random":
                return selectByRandom(availableAgents);
            case "least-load":
            default:
                return selectByLeastLoad(availableAgents);
        }
    }

    @Override
    public void updateLoad(String agentId, int loadDelta) {
        loadInfoMap.compute(agentId, (id, info) -> {
            if (info == null) {
                info = LoadInfo.builder()
                        .agentId(agentId)
                        .maxLoad(100)
                        .build();
            }
            info.setCurrentLoad(Math.max(0, info.getCurrentLoad() + loadDelta));
            info.setLastUpdateTime(System.currentTimeMillis());
            return info;
        });
    }

    @Override
    public LoadInfo getLoadInfo(String agentId) {
        return loadInfoMap.getOrDefault(agentId, LoadInfo.builder()
                .agentId(agentId)
                .currentLoad(0)
                .maxLoad(100)
                .build());
    }

    @Override
    public void resetLoad(String agentId) {
        loadInfoMap.remove(agentId);
    }

    @Override
    public String getStrategy() {
        return strategy;
    }

    private List<AgentInfo> filterAvailableAgents(List<AgentInfo> agents) {
        List<AgentInfo> available = new ArrayList<>();
        for (AgentInfo agent : agents) {
            if (agent.getStatus() == AgentInfo.AgentStatus.ONLINE) {
                LoadInfo loadInfo = getLoadInfo(agent.getAgentId());
                if (loadInfo.isAvailable()) {
                    available.add(agent);
                }
            }
        }
        return available;
    }

    private AgentInfo selectByRoundRobin(List<AgentInfo> agents, SelectionContext context) {
        String key = context.getSceneId() != null ? context.getSceneId() : "default";
        AtomicInteger counter = roundRobinCounters.computeIfAbsent(key, k -> new AtomicInteger(0));
        int index = Math.abs(counter.getAndIncrement() % agents.size());
        return agents.get(index);
    }

    private AgentInfo selectByRandom(List<AgentInfo> agents) {
        int index = random.nextInt(agents.size());
        return agents.get(index);
    }

    private AgentInfo selectByLeastLoad(List<AgentInfo> agents) {
        AgentInfo selected = null;
        int minLoad = Integer.MAX_VALUE;

        for (AgentInfo agent : agents) {
            LoadInfo loadInfo = getLoadInfo(agent.getAgentId());
            if (loadInfo.getCurrentLoad() < minLoad) {
                minLoad = loadInfo.getCurrentLoad();
                selected = agent;
            }
        }

        return selected != null ? selected : agents.get(0);
    }
}
