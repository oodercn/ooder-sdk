package net.ooder.vfs.sync.restor;

import  net.ooder.vfs.sync.TaskResult;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;

public class RestorTask<T extends Path> implements Callable<Path> {

    private final File file;
    private final List<RestorObjectTask<TaskResult<String>>> tasks;
    private final int maxTaskSize;

    RestorTask(File file, int maxTaskSize, List<RestorObjectTask<TaskResult<String>>> tasks) {
        this.tasks = tasks;
        this.maxTaskSize = maxTaskSize;
        this.file = file;
    }

    @Override
    public Path call() {
        Path vpath = null;
        try {
            RestorFileVisitor visitor = new RestorFileVisitor(file.toPath(), maxTaskSize, tasks);
            vpath = Files.walkFileTree(file.toPath(), visitor);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return vpath;
    }
}