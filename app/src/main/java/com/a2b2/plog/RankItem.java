package com.a2b2.plog;

public class RankItem {
    private String username;
    private int rank,score;
    private int badge;

    public RankItem(int badge, int rank, String username, int score) {
        this.badge = badge;
        this.rank = rank;
        this.username = username;
        this.score = score;
    }
    public int getBadge(){return badge;}
    public int getRank() {return rank;}

    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }
}
