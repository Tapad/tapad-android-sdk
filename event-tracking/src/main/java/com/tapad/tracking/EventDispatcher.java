package com.tapad.tracking;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A dispatcher that maintains the event queue and starts / stops
 * an executor service for doing the actual network IO on demand.
 *
 * This first version has no queue persistence and no network detection.
 * If the network is not available at the time events are submitted, they
 * will be lost.
 *
 */
class EventDispatcher {

    private EventResource resource;
    private ConcurrentLinkedQueue<Event> queue = new ConcurrentLinkedQueue<Event>();
    private ExecutorService executor = null;

    EventDispatcher(EventResource resource) {
        this.resource = resource;
    }

    protected void dispatch(Event e) {
        queue.add(e);
        startExecutor();
    }

    /**
     * Creates and starts a new executor if there is no existing one
     * or if the existing one is shut down.
     */
    private synchronized void startExecutor() {
        if (executor == null || executor.isShutdown()) {
            executor = Executors.newSingleThreadExecutor();
            executor.submit(new DispatchWorker());
        }
    }

    private class DispatchWorker implements Runnable {
        private static final String TAG = "DispatchWorker";

        public void run() {
            Event event;
            while ((event = queue.poll()) != null) {
                try {
                    Logging.info(TAG, "Posting event " + event);

                    resource.post(event);
                } catch (Exception e)  {
                    Logging.warn(TAG, "Error posting event: " + e.getClass() + ", " + e.getMessage());
                }
            }

            // We're done. Shut down the executor service for now.
            synchronized (EventDispatcher.this) {
                executor.shutdown();
            }
        }
    }
}
