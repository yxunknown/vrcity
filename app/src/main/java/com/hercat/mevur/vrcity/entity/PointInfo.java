package com.hercat.mevur.vrcity.entity;

public class PointInfo {

    public static final int TYPE_TEXT = 0;
    public static final int TYPE_TXT_IMAGE = 1;
    public static final int TYPE_IMAGE = 2;

    private double lat;
    private double lng;
    private double directionAngel;
    private double distance;
    private String name;
    private int logoUrl;
    private String infoUrl;
    private String city;
    private String district;
    private String street;
    private int type;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getDirectionAngel() {
        return directionAngel;
    }

    public void setDirectionAngel(double directionAngel) {
        this.directionAngel = directionAngel;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(int logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getInfoUrl() {
        return infoUrl;
    }

    public void setInfoUrl(String infoUrl) {
        this.infoUrl = infoUrl;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
