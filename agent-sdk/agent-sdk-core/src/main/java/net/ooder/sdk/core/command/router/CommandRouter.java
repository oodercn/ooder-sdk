package net.ooder.sdk.core.command.router;

import net.ooder.sdk.api.command.Command;
import net.ooder.sdk.api.command.CommandResult;
import net.ooder.sdk.api.agent.Agent;
import net.ooder.sdk.api.agent.SceneAgent;
import net.ooder.sdk.api.agent.WorkerAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

public class CommandRouter {
    
    private static final Logger log = LoggerFactory.getLogger(CommandRouter.class);
    
    private final Map<String, CommandHandler> handlers = new ConcurrentHashMap<>();
    private final Map<String, Agent> agentRegistry = new ConcurrentHashMap<>();
    private final ExecutorService executorService;
    
    public CommandRouter() {
        this.executorService = Executors.newCachedThreadPool();
        registerStandardHandlers();
    }
    
    private void registerStandardHandlers() {
        registerHandler("standard://on", new OnCommandHandler());
        registerHandler("standard://off", new OffCommandHandler());
        registerHandler("standard://toggle", new ToggleCommandHandler());
        registerHandler("standard://set", new SetCommandHandler());
        registerHandler("standard://get", new GetCommandHandler());
    }
    
    public void registerHandler(String commandName, CommandHandler handler) {
        handlers.put(commandName, handler);
        log.info("Registered command handler: {}", commandName);
    }
    
    public void unregisterHandler(String commandName) {
        handlers.remove(commandName);
        log.info("Unregistered command handler: {}", commandName);
    }
    
    public void registerAgent(Agent agent) {
        agentRegistry.put(agent.getAgentId(), agent);
        log.debug("Registered agent: {}", agent.getAgentId());
    }
    
    public void unregisterAgent(String agentId) {
        agentRegistry.remove(agentId);
        log.debug("Unregistered agent: {}", agentId);
    }
    
    public CommandResult execute(Command command) {
        long startTime = System.currentTimeMillis();
        log.debug("Executing command: {}", command.getCommandId());
        
        try {
            CommandHandler handler = handlers.get(command.getFullCommandName());
            if (handler != null) {
                CommandResult result = handler.handle(command);
                log.debug("Command {} executed by handler: {}", 
                    command.getCommandId(), command.getFullCommandName());
                return result;
            }
            
            if (command.getTargetCap() != null) {
                return executeViaAgent(command);
            }
            
            return CommandResult.failure(command.getCommandId(), 
                "NO_HANDLER", "No handler found for command: " + command.getFullCommandName());
                
        } catch (Exception e) {
            log.error("Command execution failed: {}", command.getCommandId(), e);
            return CommandResult.failure(command.getCommandId(), 
                "EXECUTION_ERROR", e.getMessage());
        }
    }
    
    public CompletableFuture<CommandResult> executeAsync(Command command) {
        return CompletableFuture.supplyAsync(() -> execute(command), executorService);
    }
    
    private CommandResult executeViaAgent(Command command) {
        Agent agent = agentRegistry.get(command.getTargetDevice());
        if (agent == null) {
            return CommandResult.failure(command.getCommandId(),
                "AGENT_NOT_FOUND", "Agent not found: " + command.getTargetDevice());
        }
        
        try {
            CompletableFuture<Object> future;
            Map<String, Object> params = new HashMap<>();
            params.put("command", command.getName());
            params.putAll(command.getParams());
            
            if (agent instanceof SceneAgent) {
                future = ((SceneAgent) agent).invokeCapabilityAsync(command.getTargetCap(), params);
            } else if (agent instanceof WorkerAgent) {
                future = ((WorkerAgent) agent).execute(command.getTargetCap(), params);
            } else {
                return CommandResult.failure(command.getCommandId(),
                    "UNSUPPORTED_AGENT", "Unsupported agent type: " + agent.getClass().getName());
            }
            
            Object result = future.get(command.getTimeout(), TimeUnit.MILLISECONDS);
            return CommandResult.success(command.getCommandId(), result);
            
        } catch (TimeoutException e) {
            return CommandResult.failure(command.getCommandId(),
                "TIMEOUT", "Command execution timed out");
        } catch (Exception e) {
            return CommandResult.failure(command.getCommandId(),
                "EXECUTION_ERROR", e.getMessage());
        }
    }
    
    public List<String> getAvailableCommands() {
        return new ArrayList<>(handlers.keySet());
    }
    
    public boolean hasHandler(String commandName) {
        return handlers.containsKey(commandName);
    }
    
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
    
    public interface CommandHandler {
        CommandResult handle(Command command);
    }
    
    private static class OnCommandHandler implements CommandHandler {
        @Override
        public CommandResult handle(Command command) {
            log.debug("Handling ON command: {}", command.getCommandId());
            Map<String, Object> result = new HashMap<>();
            result.put("state", "on");
            result.put("timestamp", System.currentTimeMillis());
            return CommandResult.success(command.getCommandId(), result);
        }
    }
    
    private static class OffCommandHandler implements CommandHandler {
        @Override
        public CommandResult handle(Command command) {
            log.debug("Handling OFF command: {}", command.getCommandId());
            Map<String, Object> result = new HashMap<>();
            result.put("state", "off");
            result.put("timestamp", System.currentTimeMillis());
            return CommandResult.success(command.getCommandId(), result);
        }
    }
    
    private static class ToggleCommandHandler implements CommandHandler {
        @Override
        public CommandResult handle(Command command) {
            log.debug("Handling TOGGLE command: {}", command.getCommandId());
            Map<String, Object> result = new HashMap<>();
            result.put("state", "toggled");
            result.put("timestamp", System.currentTimeMillis());
            return CommandResult.success(command.getCommandId(), result);
        }
    }
    
    private static class SetCommandHandler implements CommandHandler {
        @Override
        public CommandResult handle(Command command) {
            log.debug("Handling SET command: {}", command.getCommandId());
            Map<String, Object> result = new HashMap<>();
            result.put("state", "set");
            result.put("params", command.getParams());
            result.put("timestamp", System.currentTimeMillis());
            return CommandResult.success(command.getCommandId(), result);
        }
    }
    
    private static class GetCommandHandler implements CommandHandler {
        @Override
        public CommandResult handle(Command command) {
            log.debug("Handling GET command: {}", command.getCommandId());
            Map<String, Object> result = new HashMap<>();
            result.put("state", "current");
            result.put("timestamp", System.currentTimeMillis());
            return CommandResult.success(command.getCommandId(), result);
        }
    }
}
