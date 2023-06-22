package com.blackjack.database;

public interface GameEventListener {

    void onGameStart();

    void onGameReset();

    void onStakeRoundStart();

    void playerIsOut(IPlayer player);

    void onGameEnd();
}
