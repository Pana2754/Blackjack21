package com.blackjack.database;

public interface GameEventListener {

    void onGameStart();

    void onGameReset();

    void onDealerEnd();
}
