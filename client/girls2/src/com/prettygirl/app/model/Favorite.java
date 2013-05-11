package com.prettygirl.app.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Favorite {

    public int id;

    public int level;

    public String path;

    public Favorite() {
        super();
    }

    public Favorite(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getInt("id");
        path = jsonObject.getString("path");
        level = jsonObject.getInt("level");
    }

    public Favorite(int imageId, int level, String url) {
        this.id = imageId;
        this.level = level;
        path = url;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject result = new JSONObject();
        result.put("id", id);
        result.put("path", path);
        result.put("level", level);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Favorite) {
            if (id == ((Favorite) o).id && level == ((Favorite) o).level) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        return "{id: " + id + ", level: " + level + "}";
    }

}
