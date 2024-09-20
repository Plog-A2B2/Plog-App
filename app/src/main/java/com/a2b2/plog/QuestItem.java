package com.a2b2.plog;

public class QuestItem {
    private int questId;
    private String questText;
    private int coinNum;
    private boolean isFinish = false;

    public QuestItem(int questId, String questText, int coinNum) {
        this.questId = questId;
        this.questText = questText;
        this.coinNum = coinNum;
    }

    public String getQuestText() {
        return questText;
    }

    public int getCoinNum() {
        return coinNum;
    }

    public int getQuestId() {
        return questId;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
    }

    public boolean isFinish() {
        return isFinish;
    }
}
