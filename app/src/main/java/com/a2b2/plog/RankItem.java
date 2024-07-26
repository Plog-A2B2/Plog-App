package com.a2b2.plog;

public class RankItem {
    private String username;
    private int rank,score;

    public RankItem(int rank, String username, int score) {
        this.rank = rank;
        this.username = username;
        this.score = score;
    }

    public int getRank() {return rank;}

    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }
}
