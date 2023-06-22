package com.blackjack.database;
import com.vaadin.flow.component.UI;

import java.util.ArrayList;
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
    private static List<GameEventListener> gameEventListeners = new ArrayList<>();

    public static void addGameEventListener(GameEventListener listener) {
        gameEventListeners.add(listener);
    }

    public static void removeGameEventListener(GameEventListener listener){gameEventListeners.remove(listener);}
    public static void startGame() {
        for (GameEventListener listener : gameEventListeners) {
            listener.onGameStart();
        }
    }

    public static synchronized void register(Consumer<List<Player>> listener) {
        listeners.add(listener);
    }

    public static synchronized void register(Runnable listener){
        if(!gameListeners.contains(listener)){
            gameListeners.add(listener);
        }

    }
    public static synchronized void stakeRound(){
        for (GameEventListener listener: gameEventListeners){
            listener.onStakeRoundStart();
        }
    }
    public static synchronized void playRound(){

    }
    public static synchronized void playerIsOut(IPlayer player){
        for (GameEventListener listener: gameEventListeners){
            listener.playerIsOut(player);
        }
    }
    public static synchronized void dealerRound(){

    }

    public static synchronized void gameEnd(){
        for(GameEventListener listener: gameEventListeners){
            listener.onGameEnd();
        }
    }
    public static synchronized void resetGame(){
        for(GameEventListener listener : gameEventListeners){
            listener.onGameReset();
        }
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
