package com.prettygirl.app.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import com.prettygirl.app.utils.MiscUtil;

public class ThumbUpList {

    private ArrayList<ThumbUp> mThumbUps = new ArrayList<ThumbUp>();

    private int cCount = 0;

    public ThumbUpList() {
        super();
    }

    public ThumbUpList(String json) throws JSONException {
        super();
        JSONArray result = new JSONArray(json);
        cCount = result.length();
        for (int i = 0; i < cCount; i++) {
            mThumbUps.add(new ThumbUp(result.getJSONObject(i)));
        }
    }

    public ThumbUpList(JSONArray result) {
        super();
        cCount = result.length();
        for (int i = 0; i < cCount; i++) {
            try {
                mThumbUps.add(new ThumbUp(result.getJSONObject(i)));
            } catch (Exception e) {
                if (MiscUtil.IS_DEBUG) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static ThumbUpList load(String file) {
        ThumbUpList result = null;
        File path = new File(file);
        if (!path.exists()) {
            return result;
        }
        try {
            FileInputStream inputStream = new FileInputStream(file);
            StringWriter reader = new StringWriter();
            int chaz = -1;
            while ((chaz = inputStream.read()) != -1) {
                reader.write(chaz);
            }
            inputStream.close();
            String json = reader.toString();
            result = new ThumbUpList(json);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void save(String file) {
        File path = new File(file);
        if (!path.getParentFile().exists()) {
            path.getParentFile().mkdirs();
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            StringReader reader = new StringReader(toJSON().toString());
            int chaz = -1;
            while ((chaz = reader.read()) != -1) {
                outputStream.write(chaz);
            }
            reader.close();
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addThumbUp(ThumbUp cThumbUp) {
        mThumbUps.add(cThumbUp);
    }

    public int getIndex(ThumbUp cThumbUp) {
        return mThumbUps.indexOf(cThumbUp);
    }

    public boolean hasThumbUp(ThumbUp cThumbUp) {
        return mThumbUps.contains(cThumbUp);
    }

    public boolean removeThumbUp(ThumbUp cThumbUp) {
        return mThumbUps.remove(cThumbUp);
    }

    public void addFristThumbUp(ThumbUp cThumbUp) {
        mThumbUps.add(0, cThumbUp);
    }

    public int size() {
        return mThumbUps.size();
    }

    public ThumbUp get(int index) {
        return mThumbUps.get(index);
    }

    public JSONArray toJSON() throws JSONException {
        JSONArray result = new JSONArray();
        for (ThumbUp cThumbUp : mThumbUps) {
            result.put(cThumbUp.toJSON());
        }
        return result;
    }

}
