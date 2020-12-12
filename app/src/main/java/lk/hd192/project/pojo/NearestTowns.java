package lk.hd192.project.pojo;

import java.util.ArrayList;

public class NearestTowns {
    private String townName;


    public NearestTowns(String townName) {
        this.townName = townName;
    }

    public String getTownName() {
        return townName;
    }

    public void setTownName(String townName) {
        this.townName = townName;
    }

    public static ArrayList<NearestTowns> TOWNS;

}
