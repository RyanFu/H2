package com.prettygirl.app.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.prettygirl.app.utils.MiscUtil;

public class ThumbUp {

    public int id;

    public int level;

    public int up;

    public int down;

    public int count;

    /**
     * method 表示 1 表示赞， 2 表示 鄙视
     */
    public int method;

    public ThumbUp() {
        super();
    }

    public ThumbUp(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getInt("id");
        level = jsonObject.getInt("level");
        if (jsonObject.has("up")) {
            try {
                up = jsonObject.getInt("up");
            } catch (JSONException e) {
                if (MiscUtil.IS_DEBUG) {
                    e.printStackTrace();
                }
                up = 0;
            }
        }
        if (jsonObject.has("down")) {
            try {
                down = Math.abs(jsonObject.getInt("down"));
            } catch (JSONException e) {
                if (MiscUtil.IS_DEBUG) {
                    e.printStackTrace();
                }
                down = 0;
            }
        }
        if (jsonObject.has("favorited")) {
            count = jsonObject.getInt("favorited");
        }
    }

    public ThumbUp(int imageId, int level, int method) {
        this.id = imageId;
        this.level = level;
        this.method = method;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject result = new JSONObject();
        result.put("id", id);
        result.put("level", level);
        result.put("method", method);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ThumbUp) {
            if (id == ((ThumbUp) o).id && level == ((ThumbUp) o).level && method == ((ThumbUp) o).method) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        return "{id: " + id + ", level: " + level + "}";
    }

}
