package net.ooder.server.httpproxy.core;

import net.ooder.common.JDSConstants;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ThreadPool {

    private static final Log log = LogFactory.getLog(JDSConstants.CONFIG_KEY, ThreadPool.class);
    private List threads = new ArrayList();
    private LinkedList queue = new LinkedList();

    public ThreadPool( int numberOfThreads ) {
        for( int i = 0; i < numberOfThreads; i++ ) {
            log.debug("Creating thread " + i );
            PooledThread thread = new PooledThread( "Pooled Thread " + i );
            thread.start();
            threads.add( thread );
        }
    }

    public void execute( Runnable runnable ) {
        log.debug("Queueing runnable in thread pool.");
        synchronized( queue ) {
            queue.add( runnable );
            queue.notify();
        }
    }

    public void shutdown() {
        for( int i = 0; i < threads.size(); i++ ) {
            Thread thread = (Thread) threads.get( i );
            thread.interrupt();
        }
    }

    protected class PooledThread extends Thread {
        public PooledThread(String name) {
            super(name);
            setDaemon( true );
        }

        public void run() {
            try {
                while( !isInterrupted() ) {
                    waitForTask();
                    Runnable runnable = retrieveTask();
                    if( runnable != null ) {

                            log.debug("Starting runnable on thread " + Thread.currentThread().getName() );

                        try {
                            runnable.run();
                        } catch( Exception e ) {
                            log.error(  e.toString(), e );
                        }
                    }

                        log.debug("Returning to thread pool " + Thread.currentThread().getName() );

                }
            } catch( InterruptedException e ) {
                log.error(  Thread.currentThread().getName(), e );
            } finally {
                log.info(  Thread.currentThread().getName() + " is shutting down" );
            }
        }

        private void waitForTask() throws InterruptedException {
            synchronized( queue ) {
                if( queue.isEmpty() ) {
                    queue.wait();
                }
            }
        }

        private Runnable retrieveTask() {
            Runnable runnable = null;
            synchronized( queue ) {
                if( !queue.isEmpty() ) {
                    runnable = (Runnable)queue.removeFirst();
                }
            }
            return runnable;
        }
    }
}
