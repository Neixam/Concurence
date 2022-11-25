package fr.uge.concurrence;

@FunctionalInterface
public interface Task {
    void run() throws InterruptedException;
}
