package com.example.familymap;

import android.widget.ImageView;

import com.joanzapata.iconify.IconDrawable;

public class ListItemData {
    public String name;
    public String description;
    public IconDrawable image;

    public ListItemData(String name, String description, IconDrawable image) {
        this.name = name;
        this.description = description;
        this.image = image;
    }
}
