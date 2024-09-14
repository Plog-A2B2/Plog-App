package com.a2b2.plog;

public class TrashcountItem {

    private String trashtype;
    private int paper;
    private int glass;
    private int garbage;
    private int plastic;
    private int can;
    private int plasticbag;
    private int cnt;
    private int total;
    private String[] trashTypes = {"종이류", "유리류", "일반쓰레기", "플라스틱", "캔/고철류", "비닐류"};
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
    public void setTotal(int total){this.total = total;}
    public int getTotal(){return total;}
    public int getCnt() {
        return cnt;
    }
    public void setPaper(int paper){this.paper = paper;}
    public int getPaper(){return paper;}
    public void setGlass(int glass){this.glass = glass;}
    public int getGlass(){return glass;}
    public void setGarbage(int garbage){this.garbage = garbage; }
    public int getGarbage(){return garbage;}
    public void setPlastic(int plastic){this.plastic =plastic;}
    public int getPlastic() {return plastic;}
    public void setCan(int can) {this.can = can;}
    public int getCan() {return can;}
    public void setPlasticbag(int plasticbag) {this.plasticbag = plasticbag;}
    public int getPlasticbag() {return plasticbag;}
}
