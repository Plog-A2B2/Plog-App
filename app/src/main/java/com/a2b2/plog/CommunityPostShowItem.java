package com.a2b2.plog;

public class CommunityPostShowItem {
    private int postId;
    private String title;
    private String content;
    private String plogPlace;
    private String meetPlace;
    private String time;
    private String schedule;
    private String userNickname;
    private int badge;
    private boolean joined;
    private boolean liked;
    public CommunityPostShowItem(int postId, String title, String content, String plogPlace, String meetPlace, String time, String schedule, String userNickname, int badge, boolean joined, boolean liked){
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.plogPlace = plogPlace;
        this.meetPlace = meetPlace;
        this.time = time;
        this.schedule = schedule;
        this.userNickname = userNickname;
        this.badge = badge;
        this.joined = joined;
        this.liked =liked;

    }

    public int getPostId() {return postId;}
    public String getTitle(){return title;}
    public String getContent(){return content;}
    public String getPlogPlace(){return plogPlace;}
    public String getMeetPlace(){return meetPlace;}
    public String getTime(){return time;}
    public String getSchedule(){return schedule;}
    public String getUserNickname(){return userNickname;}
    public int getBadge(){return badge;}
    public boolean getJoined(){return joined;}
    public boolean getLiked(){return liked;}

}
