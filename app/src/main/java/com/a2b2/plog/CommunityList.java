package com.a2b2.plog;

public class CommunityList {
    private int postId;
    private int badge;
    private String title;
    private String time;
    private String userNickname;
    public CommunityList(int postId, int badge, String title, String time, String userNickname){
        this.postId = postId;
        this.badge = badge;
        this.title = title;
        this.time = time;
        this.userNickname = userNickname;
    }
    public int getPostId(){ return postId;}
    public int getBadge() {return badge;}
    public String getTitle(){return title;}
    public  String getTime(){return  time;}
    public  String getUserNickname(){return userNickname;}
}
