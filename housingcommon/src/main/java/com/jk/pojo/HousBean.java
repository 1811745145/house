package com.jk.pojo;


import java.io.Serializable;

public class HousBean implements Serializable {
    private static final long serialVersionUID = -3729613519665696954L;
    private Integer id;
    private String housName;
    private String housImg;
    private String housArea;
    private String housInfo;
    private String housTitle;
    private String housGuanzhu;
    private String housDate;
    private String ditie;
    private String housTime;
    private String housKanfang;
    private long housprice;
    private long housDanjia;
    private String housDizhi;
    private String housDaxiao;
    private String housCeng;
    private String housFangwei;
    private String housZhuangxiu;
    private String housPingmi;
    private String housCaiZhi;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHousName() {
        return housName;
    }

    public void setHousName(String housName) {
        this.housName = housName;
    }

    public String getHousImg() {
        return housImg;
    }

    public void setHousImg(String housImg) {
        this.housImg = housImg;
    }

    public String getHousArea() {
        return housArea;
    }

    public void setHousArea(String housArea) {
        this.housArea = housArea;
    }

    public String getHousInfo() {
        return housInfo;
    }

    public void setHousInfo(String housInfo) {
        this.housInfo = housInfo;
    }

    public String getHousTitle() {
        return housTitle;
    }

    public void setHousTitle(String housTitle) {
        this.housTitle = housTitle;
    }

    public String getHousGuanzhu() {
        return housGuanzhu;
    }

    public void setHousGuanzhu(String housGuanzhu) {
        this.housGuanzhu = housGuanzhu;
    }

    public String getHousDate() {
        return housDate;
    }

    public void setHousDate(String housDate) {
        this.housDate = housDate;
    }

    public String getDitie() {
        return ditie;
    }

    public void setDitie(String ditie) {
        this.ditie = ditie;
    }

    public String getHousTime() {
        return housTime;
    }

    public void setHousTime(String housTime) {
        this.housTime = housTime;
    }

    public String getHousKanfang() {
        return housKanfang;
    }

    public void setHousKanfang(String housKanfang) {
        this.housKanfang = housKanfang;
    }

    public long getHousprice() {
        return housprice;
    }

    public void setHousprice(long housprice) {
        this.housprice = housprice;
    }

    public long getHousDanjia() {
        return housDanjia;
    }

    public void setHousDanjia(long housDanjia) {
        this.housDanjia = housDanjia;
    }

    public String getHousDizhi() {
        return housDizhi;
    }

    public void setHousDizhi(String housDizhi) {
        this.housDizhi = housDizhi;
    }

    public String getHousDaxiao() {
        return housDaxiao;
    }

    public void setHousDaxiao(String housDaxiao) {
        this.housDaxiao = housDaxiao;
    }

    public String getHousCeng() {
        return housCeng;
    }

    public void setHousCeng(String housCeng) {
        this.housCeng = housCeng;
    }

    public String getHousFangwei() {
        return housFangwei;
    }

    public void setHousFangwei(String housFangwei) {
        this.housFangwei = housFangwei;
    }

    public String getHousZhuangxiu() {
        return housZhuangxiu;
    }

    public void setHousZhuangxiu(String housZhuangxiu) {
        this.housZhuangxiu = housZhuangxiu;
    }

    public String getHousPingmi() {
        return housPingmi;
    }

    public void setHousPingmi(String housPingmi) {
        this.housPingmi = housPingmi;
    }

    public String getHousCaiZhi() {
        return housCaiZhi;
    }

    public void setHousCaiZhi(String housCaiZhi) {
        this.housCaiZhi = housCaiZhi;
    }

    @Override
    public String toString() {
        return "HousBean{" +
                "id=" + id +
                ", housName='" + housName + '\'' +
                ", housImg='" + housImg + '\'' +
                ", housArea='" + housArea + '\'' +
                ", housInfo='" + housInfo + '\'' +
                ", housTitle='" + housTitle + '\'' +
                ", housGuanzhu='" + housGuanzhu + '\'' +
                ", housDate='" + housDate + '\'' +
                ", ditie='" + ditie + '\'' +
                ", housTime='" + housTime + '\'' +
                ", housKanfang='" + housKanfang + '\'' +
                ", housprice=" + housprice +
                ", housDanjia=" + housDanjia +
                ", housDizhi='" + housDizhi + '\'' +
                ", housDaxiao='" + housDaxiao + '\'' +
                ", housCeng='" + housCeng + '\'' +
                ", housFangwei='" + housFangwei + '\'' +
                ", housZhuangxiu='" + housZhuangxiu + '\'' +
                ", housPingmi='" + housPingmi + '\'' +
                ", housCaiZhi='" + housCaiZhi + '\'' +
                '}';
    }
}
