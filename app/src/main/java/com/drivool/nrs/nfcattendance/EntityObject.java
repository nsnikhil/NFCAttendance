package com.drivool.nrs.nfcattendance;

/**
 * Created by sid on 6/3/17.
 */

public class EntityObject {

    String name,picture;
    int id;

    EntityObject(String name,String picture,int id){
        this.name = name;
        this.picture = picture;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getPicture() {
        return picture;
    }

    public int getId() {
        return id;
    }
}
