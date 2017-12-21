package edu.usc.csci571.searchonfb;

import java.util.ArrayList;
import java.util.List;

public class Album {
    private String name;
    private List<String> imageSources= new ArrayList<String>();;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getImageSources() {
        return imageSources;
    }
}
