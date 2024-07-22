package com.a2b2.plog;

public class QuestItem {
    private String questText;
    private int coinNum;

    public QuestItem(String questText, int coinNum) {
        this.questText = questText;
        this.coinNum = coinNum;
    }

    public String getQuestText() {
        return questText;
    }

    public int getCoinNum() {
        return coinNum;
    }
}
