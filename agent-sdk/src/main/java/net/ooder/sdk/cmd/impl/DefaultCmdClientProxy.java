package net.ooder.sdk.cmd.impl;

import net.ooder.sdk.cmd.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class DefaultCmdClientProxy implements CmdClientProxy {
    
    private static final Logger log = LoggerFactory.getLogger(DefaultCmdClientProxy.class);
    
    private final String clientId;
    private final CmdClientConfig config;
    private final AtomicBoolean connected = new AtomicBoolean(false);
    private final AtomicLong commandIdCounter = new AtomicLong(0);
    
    private final Map<String, CmdRecord> commandHistory = new ConcurrentHashMap<>();
    private final ExecutorService executor;
    
    public DefaultCmdClientProxy() {
        this.config = new CmdClientConfig();
        this.clientId = "cmd-" + UUID.randomUUID().toString().substring(0, 8);
        this.executor = Executors.newCachedThreadPool();
    }
    
    public DefaultCmdClientProxy(CmdClientConfig config) {
        this.config = config != null ? config : new CmdClientConfig();
        this.clientId = this.config.getClientId() != null ? 
            this.config.getClientId() : "cmd-" + UUID.randomUUID().toString().substring(0, 8);
        this.executor = Executors.newCachedThreadPool();
    }
    
    @Override
    public void init(CmdClientConfig config) {
        log.info("Initializing CmdClientProxy: {}", clientId);
        
        if (config != null) {
            this.config.setEndpoint(config.getEndpoint());
            this.config.setUsername(config.getUsername());
            this.config.setPassword(config.getPassword());
            this.config.setDefaultTimeout(config.getDefaultTimeout());
        }
        
        connected.set(true);
        
        log.info("CmdClientProxy initialized: {}", clientId);
    }
    
    @Override
    public CommandBuilder newCommand() {
        return new DefaultCommandBuilder();
    }
    
    @Override
    public CmdResponse send(Command command) {
        if (!connected.get()) {
            return CmdResponse.failure(command.getCommandId(), "NOT_CONNECTED", "Client not connected");
        }
        
        String commandId = command.getCommandId();
        if (commandId == null || commandId.isEmpty()) {
            commandId = generateCommandId();
            command.setCommandId(commandId);
        }
        
        command.setTimestamp(System.currentTimeMillis());
        
        CmdRecord record = new CmdRecord();
        record.setCommandId(commandId);
        record.setCommandType(command.getCommandType());
        record.setSourceId(command.getSourceId());
        record.setTargetId(command.getTargetId());
        record.setStatus(CmdStatus.EXECUTING);
        record.setStartTime(System.currentTimeMillis());
        
        commandHistory.put(commandId, record);
        
        try {
            Object result = executeCommand(command);
            
            record.setStatus(CmdStatus.SUCCESS);
            record.setResult(result);
            record.setEndTime(System.currentTimeMillis());
            record.setDuration(record.getEndTime() - record.getStartTime());
            
            log.debug("Command executed successfully: {}", commandId);
            
            return CmdResponse.success(commandId, result);
            
        } catch (Exception e) {
            record.setStatus(CmdStatus.FAILED);
            record.setErrorCode("EXECUTION_ERROR");
            record.setErrorMessage(e.getMessage());
            record.setEndTime(System.currentTimeMillis());
            record.setDuration(record.getEndTime() - record.getStartTime());
            
            log.error("Command execution failed: {}", commandId, e);
            
            return CmdResponse.failure(commandId, "EXECUTION_ERROR", e.getMessage());
        }
    }
    
    @Override
    public void sendAsync(Command command, CmdCallback callback) {
        executor.submit(() -> {
            try {
                CmdResponse response = send(command);
                if (callback != null) {
                    if (response.isSuccess()) {
                        callback.onSuccess(response);
                    } else {
                        callback.onError(new RuntimeException(response.getErrorMessage()));
                    }
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError(e);
                }
            }
        });
    }
    
    @Override
    public boolean cancel(String commandId) {
        CmdRecord record = commandHistory.get(commandId);
        if (record != null && record.getStatus() == CmdStatus.EXECUTING) {
            record.setStatus(CmdStatus.CANCELLED);
            record.setEndTime(System.currentTimeMillis());
            log.info("Command cancelled: {}", commandId);
            return true;
        }
        return false;
    }
    
    @Override
    public CmdStatus getStatus(String commandId) {
        CmdRecord record = commandHistory.get(commandId);
        return record != null ? record.getStatus() : null;
    }
    
    @Override
    public List<CmdRecord> getHistory(long startTime, long endTime) {
        List<CmdRecord> result = new ArrayList<>();
        for (CmdRecord record : commandHistory.values()) {
            if (record.getStartTime() >= startTime && record.getStartTime() <= endTime) {
                result.add(record);
            }
        }
        result.sort((a, b) -> Long.compare(b.getStartTime(), a.getStartTime()));
        return result;
    }
    
    @Override
    public void shutdown() {
        log.info("Shutting down CmdClientProxy: {}", clientId);
        connected.set(false);
        commandHistory.clear();
        executor.shutdown();
        log.info("CmdClientProxy shutdown complete: {}", clientId);
    }
    
    @Override
    public boolean isConnected() {
        return connected.get();
    }
    
    @Override
    public String getClientId() {
        return clientId;
    }
    
    private String generateCommandId() {
        return clientId + "-" + System.currentTimeMillis() + "-" + commandIdCounter.incrementAndGet();
    }
    
    private Object executeCommand(Command command) {
        log.debug("Executing command: type={}, direction={}", 
            command.getCommandType(), command.getDirection());
        
        Map<String, Object> result = new HashMap<>();
        result.put("commandType", command.getCommandType());
        result.put("direction", command.getDirection() != null ? command.getDirection().getCode() : "unknown");
        result.put("executedAt", System.currentTimeMillis());
        
        return result;
    }
    
    private class DefaultCommandBuilder implements CommandBuilder {
        
        private final Command command = new Command();
        
        @Override
        public CommandBuilder direction(CommandDirection direction) {
            command.setDirection(direction);
            return this;
        }
        
        @Override
        public CommandBuilder protocol(String protocolType) {
            command.setProtocolType(protocolType);
            return this;
        }
        
        @Override
        public CommandBuilder type(String commandType) {
            command.setCommandType(commandType);
            return this;
        }
        
        @Override
        public CommandBuilder source(String sourceId) {
            command.setSourceId(sourceId);
            return this;
        }
        
        @Override
        public CommandBuilder target(String targetId) {
            command.setTargetId(targetId);
            return this;
        }
        
        @Override
        public CommandBuilder payload(Map<String, Object> payload) {
            command.setPayload(payload);
            return this;
        }
        
        @Override
        public CommandBuilder priority(int priority) {
            command.setPriority(priority);
            return this;
        }
        
        @Override
        public CommandBuilder timeout(long timeout) {
            command.setTimeout(timeout);
            return this;
        }
        
        @Override
        public CommandBuilder header(String key, Object value) {
            command.getHeaders().put(key, value);
            return this;
        }
        
        @Override
        public Command build() {
            if (command.getCommandId() == null || command.getCommandId().isEmpty()) {
                command.setCommandId(generateCommandId());
            }
            return command;
        }
    }
}
