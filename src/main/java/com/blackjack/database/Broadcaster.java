package com.blackjack.database;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class Broadcaster {
    static Executor executor = Executors.newSingleThreadExecutor();

    static final LinkedList<Runnable> gameListeners = new LinkedList<>();
    static List<Consumer<List<Player>>> listeners = new CopyOnWriteArrayList<>();

    public static synchronized void register(Consumer<List<Player>> listener) {
        listeners.add(listener);
    }

    public static synchronized void register(Runnable listener){
        gameListeners.add(listener);
    }
    public static synchronized void unregister(Runnable listener){gameListeners.remove(listener);}


    public static synchronized void unregister(Consumer<List<Player>> listener) {
        listeners.remove(listener);
    }

    public static synchronized void broadcast(){
        for (final Runnable gameListener : gameListeners) {
            executor.execute(gameListener);
        }
    }
    public static synchronized void broadcast(List<Player> players) {
        for (Consumer<List<Player>> listener : listeners) {
            executor.execute(() -> listener.accept(players));
        }
    }
}
