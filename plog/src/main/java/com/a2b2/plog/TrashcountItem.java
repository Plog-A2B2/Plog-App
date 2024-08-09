package com.a2b2.plog;

public class TrashcountItem {

    private String trashtype;
    private int cnt;

    public TrashcountItem(String trashtype, int cnt) {
        this.trashtype = trashtype;
        this.cnt = cnt;
    }

    public String getTrashtype() {
        return trashtype;
    }
    public void setCnt(int cnt){
        this.cnt = cnt;
    }


    public int getCnt() {
        return cnt;
    }
}
