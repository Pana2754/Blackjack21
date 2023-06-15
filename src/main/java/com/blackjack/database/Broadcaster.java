package com.blackjack.database;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class Broadcaster {
    static Executor executor = Executors.newSingleThreadExecutor();

    static List<Consumer<List<Player>>> listeners = new CopyOnWriteArrayList<>();

    public static synchronized void register(Consumer<List<Player>> listener) {
        listeners.add(listener);
    }

    public static synchronized void unregister(Consumer<List<Player>> listener) {
        listeners.remove(listener);
    }

    public static synchronized void broadcast(List<Player> players) {
        for (Consumer<List<Player>> listener : listeners) {
            executor.execute(() -> listener.accept(players));
        }
    }
}
