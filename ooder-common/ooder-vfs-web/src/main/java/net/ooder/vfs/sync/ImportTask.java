package net.ooder.vfs.sync;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;

public class ImportTask<T extends Path> implements Callable<Path> {

    private final File file;
    private final List<UPLoadTask<TaskResult<String>>> tasks;
    private final int maxTaskSize;

    ImportTask(File file, int maxTaskSize, List<UPLoadTask<TaskResult<String>>> tasks) {
        this.tasks = tasks;
        this.maxTaskSize = maxTaskSize;
        this.file = file;
    }

    @Override
    public Path call() {
        Path vpath = null;
        try {
            // LocalFileVisitor visitor = new LocalFileVisitor("root/" + file.getName(), file.toPath(), 0, maxTaskSize, tasks);
            LocalFileVisitor visitor = new LocalFileVisitor(file.getName(), file.toPath(), 0, maxTaskSize, tasks);
            vpath = Files.walkFileTree(file.toPath(), visitor);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vpath;
    }
}