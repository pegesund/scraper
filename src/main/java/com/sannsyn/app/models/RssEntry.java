package com.sannsyn.app.models;

import org.bson.types.ObjectId;

import java.io.Serializable;
import java.net.URI;

/**
 * Created by petter on 12.04.14.
 */
public class RssEntry implements Serializable {
    private ObjectId _id;
    public String url;
    public String nickname;
    public String category;
    public String subCategory;
}
