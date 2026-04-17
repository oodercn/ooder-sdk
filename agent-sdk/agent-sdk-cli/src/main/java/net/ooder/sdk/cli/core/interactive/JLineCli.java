package net.ooder.sdk.cli.core.interactive;

import net.ooder.sdk.cli.api.InteractiveCli;
import org.jline.reader.*;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * JLine3交互式CLI实现
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class JLineCli implements InteractiveCli {

    private static final Logger log = LoggerFactory.getLogger(JLineCli.class);

    private Terminal terminal;
    private LineReader lineReader;
    private volatile boolean running = false;
    private Completer completer;
    private HistoryManager historyManager;

    public JLineCli() {
        try {
            this.terminal = TerminalBuilder.builder()
                    .system(true)
                    .build();
        } catch (IOException e) {
            log.error("Failed to create terminal", e);
            throw new RuntimeException("Failed to create terminal", e);
        }
    }

    @Override
    public void start() {
        if (running) {
            return;
        }

        LineReaderBuilder builder = LineReaderBuilder.builder()
                .terminal(terminal)
                .parser(new DefaultParser());

        if (completer != null) {
            builder.completer(new JLineCompleter(completer));
        }

        this.lineReader = builder.build();

        if (historyManager != null) {
            historyManager.load();
        }

        running = true;
        log.info("Interactive CLI started");
    }

    @Override
    public void stop() {
        running = false;

        if (historyManager != null) {
            historyManager.save();
        }

        if (terminal != null) {
            try {
                terminal.close();
            } catch (Exception e) {
                log.error("Error closing terminal", e);
            }
        }

        log.info("Interactive CLI stopped");
    }

    @Override
    public String readLine(String prompt) {
        if (lineReader == null) {
            throw new IllegalStateException("CLI not started");
        }

        try {
            String line = lineReader.readLine(prompt);

            if (historyManager != null && line != null && !line.trim().isEmpty()) {
                historyManager.add(line);
            }

            return line;
        } catch (UserInterruptException e) {
            return null; // 用户中断
        } catch (EndOfFileException e) {
            stop();
            return null;
        }
    }

    @Override
    public String readPassword(String prompt) {
        if (lineReader == null) {
            throw new IllegalStateException("CLI not started");
        }

        try {
            return lineReader.readLine(prompt, (char) 0); // 隐藏输入
        } catch (Exception e) {
            log.error("Error reading password", e);
            return null;
        }
    }

    @Override
    public void print(String message) {
        terminal.writer().print(message);
        terminal.writer().flush();
    }

    @Override
    public void println(String message) {
        terminal.writer().println(message);
        terminal.writer().flush();
    }

    @Override
    public void setCompleter(Completer completer) {
        this.completer = completer;
    }

    @Override
    public void setHistoryManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    /**
     * JLine补全器包装
     */
    private static class JLineCompleter implements org.jline.reader.Completer {
        private final Completer completer;

        public JLineCompleter(Completer completer) {
            this.completer = completer;
        }

        @Override
        public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
            List<String> suggestions = completer.complete(line.line(), line.cursor());
            for (String suggestion : suggestions) {
                candidates.add(new Candidate(suggestion));
            }
        }
    }
}
