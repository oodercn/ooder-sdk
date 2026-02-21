/**
 * $RCSfile: TaskEngine.java,v $
 * $Revision: 1.1 $
 * $Date: 2025/07/08 00:25:55 $
 *
 * Copyright (C) 2003 spk, Inc. All rights reserved.
 *
 * This software is the proprietary information of spk, Inc.
 * Use is subject to license terms.
 */
/**
 * $RCSfile: TaskEngine.java,v $
 * $Revision: 1.0 $
 * $Date: 2025/08/25 $
 * <p>
 * Copyright (c) 2025 ooder.net
 * </p>
 * <p>
 * Company: ooder.net
 * </p>
 * <p>
 * License: MIT License
 * </p>
 */
package net.ooder.common.util;

import java.util.Date;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * <p>Title: 常用代码打包</p>
 * <p>Description: 
 * A class to manage the execution of tasks in the Jive system. A TaskEngine
 * object accepts Runnable objects and queues them for execution by
 * worker threads. Optionally, a priority may be assigned to each task. Tasks with a
 * higher priority are taken from the queue first.
 * <p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: raddev.cn</p>
 * @author wenzhang li
 * @version 1.0
 */
public class TaskEngine {

    public static final int HIGH_PRIORITY = 2;
    public static final int MEDIUM_PRIORITY = 1;
    public static final int LOW_PRIORITY = 0;

    /**
     * A queue of tasks to be executed.
     */
    private static PriorityQueue taskQueue = null;

    /**
     * A thread group for all workers.
     */
    private static ThreadGroup threadGroup;

    /**
     * An array of worker threads.
     */
    private static TaskEngineWorker[] workers = null;

    /**
     * A Timer to perform periodic tasks.
     */
    private static Timer taskTimer = null;

    private static Object lock = new Object();

    private static long newWorkerTimestamp = currentTime();
    private static long busyTimestamp = currentTime();

    static {
        // Initialize the task timer and make it a deamon.
        taskTimer = new Timer(true);
        taskQueue = new PriorityQueue();
        threadGroup = new ThreadGroup("Task Engine Workers");
        // Use 5 worker threads by default.
        workers = new TaskEngineWorker[5];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new TaskEngineWorker("Task Engine Worker " + i);
            workers[i].setDaemon(true);
            workers[i].start();
        }
    }

    private TaskEngine() {
        // Not instantiable.
    }

    /**
     * Adds a task to the task queue. The task will be executed immediately
     * provided there is a free worker thread to execute it. Otherwise, it
     * will execute as soon as a worker thread becomes available.<p>
     *
     * @param task the task to execute
     */
    public static void addTask(Runnable task) {
        addTask(MEDIUM_PRIORITY, task);
    }

    /**
     * Adds a task to the task queue. The task will be executed immediately
     * provided there is a free worker thread to execute it. Otherwise, it
     * will execute as soon as a worker thread becomes available.<p>
     *
     * @param priority the priority of the task in the queue.
     * @param task the task to execute
     */
    public static void addTask(int priority, Runnable task) {
        synchronized (lock) {
            taskQueue.enqueue(priority, task);
            // Notify a worker thread of the enqueue.
            lock.notify();
        }
    }

    /**
     * Schedules a task to be run once after a specified delay.
     *
     * @param task task to be scheduled.
     * @param date the date in milliseconds at which the task is to be executed.
     * @return a TimerTask object which can be used to track execution of the
     *      task.
     */
    public static TimerTask scheduleTask(Runnable task, Date date) {
        return scheduleTask(MEDIUM_PRIORITY, task, date);
    }

    /**
     * Schedules a task to be run once after a specified delay.
     *
     * @param priority the priority of the task in the queue.
     * @param task task to be scheduled.
     * @param date the date in milliseconds at which the task is to be executed.
     * @return a TimerTask object which can be used to track execution of the
     *      task.
     */
    public static TimerTask scheduleTask(int priority, Runnable task, Date date) {
        TimerTask timerTask = new ScheduledTask(priority, task);
        taskTimer.schedule(timerTask, date);
        return timerTask;
    }

    /**
     * Schedules a task to periodically run. This is useful for tasks such as
     * updating search indexes, deleting old data at periodic intervals, etc.
     *
     * @param task task to be scheduled.
     * @param delay delay in milliseconds before task is to be executed.
     * @param period time in milliseconds between successive task executions.
     * @return a TimerTask object which can be used to track executions of the
     *      task and to cancel subsequent executions.
     */
    public static TimerTask scheduleTask(Runnable task, long delay, long period) {
        return scheduleTask(MEDIUM_PRIORITY, task, delay, period);
    }

    /**
     * Schedules a task to periodically run. This is useful for tasks such as
     * updating search indexes, deleting old data at periodic intervals, etc.
     *
     * @param priority the priority of the task in the queue.
     * @param task task to be scheduled.
     * @param delay delay in milliseconds before task is to be executed.
     * @param period time in milliseconds between successive task executions.
     * @return a TimerTask object which can be used to track executions of the
     *      task and to cancel subsequent executions.
     */
    public static TimerTask scheduleTask(int priority, Runnable task, long delay, long period) {
        TimerTask timerTask = new ScheduledTask(priority, task);
        taskTimer.scheduleAtFixedRate(timerTask, delay, period);
        return timerTask;
    }

    /**
     * Return the next task in the queue. If no task is available, this method
     * will block until a task is added to the queue.
     *
     * @return a <code>Task</code> object
     */
    private static Runnable nextTask() {
        synchronized (lock) {
            // Block until we have another object in the queue to execute.
            while (taskQueue.isEmpty()) {
                try {
                    lock.wait();
                } catch (InterruptedException ie) {}
            }

            // Now, grow or shrink the worker pool as necessary.
            boolean busy = taskQueue.size() > Math.ceil(workers.length / 2);
            if (busy) {
                // Update the busy timestamp.
                busyTimestamp = currentTime();
                // Attempt to add another worker to handle the load.
                addWorker();
            } else {
                // Attempt to remove a worker.
                removeWorker();
            }

            return (Runnable)taskQueue.dequeue();
        }
    }

    /**
     * Adds a new worker to handle load. New workers are added at most once every two seconds
     * and only up to thirty workers.
     */
    private static void addWorker() {
        // Only add a new worker if it's been at least 2 seconds since the last time.
        if (workers.length < 30 && currentTime() > newWorkerTimestamp + 2000) {
            int newSize = workers.length + 1;
            int lastIndex = newSize - 1;
            TaskEngineWorker[] newWorkers = new TaskEngineWorker[newSize];
            for (int i = 0; i < workers.length; i++) {
                newWorkers[i] = workers[i];
            }
            newWorkers[lastIndex] = new TaskEngineWorker("Task Engine Worker " + lastIndex);
            newWorkers[lastIndex].setDaemon(true);
            newWorkers[lastIndex].start();
            // Finally, switch in new data structure.
            workers = newWorkers;
            newWorkerTimestamp = currentTime();
        }
    }

    /**
     * Removes a worker if load is lower than the necessary number of workers. Workers are removed
     * at once every five seconds, down to a minimum of three workers.
     */
    private static void removeWorker() {
        // Only remove a worker if at least 5 seconds have passed since we were last busy.
        if (workers.length > 3 && currentTime() > busyTimestamp + Constants.SECOND * 5) {
            // First, stop the last worker.
            workers[workers.length - 1].stopWorker();
            // Create a new worker array one elment smaller than the current one.
            int newSize = workers.length - 1;
            TaskEngineWorker[] newWorkers = new TaskEngineWorker[newSize];
            // Copy in elements up to newSize.
            for (int i = 0; i < newSize; i++) {
                newWorkers[i] = workers[i];
            }
            workers = newWorkers;
            // Update the busy timestamp so that another worker won't be removed for a bit.
            busyTimestamp = currentTime();
        }
    }

    private static long currentTime() {
        return System.currentTimeMillis();
    }

    /**
     * A worker thread class which executes <code>Task</code> objects.
     */
    private static class TaskEngineWorker extends Thread {

        private boolean done = false;

        public TaskEngineWorker(String name) {
            super(threadGroup, name);
        }

        /**
         * Stops the worker.
         */
        public void stopWorker() {
            done = true;
        }

        /**
         * Get tasks from the task engine. The call to get another task will
         * block until there is an available task to execute.
         */
        public void run() {
            while (!done) {
                try {
                    nextTask().run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * A subclass of TimerClass that passes along a Runnable to the task engine
     * when the scheduled task is run.
     */
    private static class ScheduledTask extends TimerTask {

        private int priority;
        private Runnable task;

        public ScheduledTask(int priority, Runnable task) {
            this.priority = priority;
            this.task = task;
        }

        public void run() {
            // Put the task into the queue to be run as soon as possible by a
            // worker.
            addTask(priority, task);
        }
    }

    /**
     * A simple priority queue that only allows for elements with one of three priorities:
     * TaskEngine.HIGH_PRIORITY, TaskEngine.MEDIUM_PRIORITY, and TaskEngine.LOW_PRIORITY. A
     * small deviation is made from the standard priority queue behavior to prevent lower
     * priority elements from languishing in the queue forever: during every dequeue operation,
     * one element is moved from the low priority to medium priority, and one item is moved from
     * medium priority to high priority.<p>
     *
     * This class is not thread-safe, so external synchronization should be used.
     */
    private static class PriorityQueue {

        private LinkedList high = new LinkedList();
        private LinkedList medium = new LinkedList();
        private LinkedList low = new LinkedList();

        /**
         * Adds an object to the queue with the specified priority. Valid priority values are:
         * TaskEngine.HIGH_PRIORITY, TaskEngine.MEDIUM_PRIORITY, and TaskEngine.LOW_PRIORITY. Any
         * value higher than TaskEngine.HIGH_PRIORITY will be added as high priority, and any
         * value lower than TaskEngine.LOW_PRIORITY will be added as low priority.
         *
         * @param priority the priority of the object in the queue.
         * @param object the value to add to the queue.
         */
        public void enqueue(int priority, Object object) {
            if (priority > HIGH_PRIORITY) {
                priority = HIGH_PRIORITY;
            } else if (priority < LOW_PRIORITY) {
                priority = LOW_PRIORITY;
            }
            switch (priority) {
                case HIGH_PRIORITY :
                    high.addFirst(object);
                    break;
                case MEDIUM_PRIORITY :
                    medium.addFirst(object);
                    break;
                case LOW_PRIORITY :
                    low.addFirst(object);
                    break;
            }
        }

        /**
         * Returns true if the queue is empty.
         *
         * @return true if the queue is empty.
         */
        public boolean isEmpty() {
            return high.isEmpty() && medium.isEmpty() && low.isEmpty();
        }

        /**
         * Returns the number of elements in the queue.
         *
         * @return the number of elements in the queue.
         */
        public int size() {
            return high.size() + medium.size() + low.size();
        }

        /**
         * Removes and returns the highest priority element from the queue.
         *
         * @return the next element from the queue.
         */
        public Object dequeue() {
            Object object = null;
            if (!high.isEmpty()) {
                object = high.removeLast();
            } else if (!medium.isEmpty()) {
                object = medium.removeLast();
            } else if (!low.isEmpty()) {
                object = low.removeLast();
            } else {
                throw new NoSuchElementException("Queue is empty.");
            }
            // To prevent items from never being run once added to the queue, we move one element
            // from each of the lower priority lists to the higher priority lists.
            if (!low.isEmpty()) {
                medium.addFirst(low.removeLast());
            }
            if (!medium.isEmpty()) {
                high.addFirst(medium.removeLast());
            }
            return object;
        }
    }
}