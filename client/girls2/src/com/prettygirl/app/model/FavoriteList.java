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

public class FavoriteList {

    private ArrayList<Favorite> mFavorites = new ArrayList<Favorite>();

    private int cCount = 0;

    public FavoriteList() {
        super();
    }

    public FavoriteList(String json) throws JSONException {
        super();
        JSONArray result = new JSONArray(json);
        cCount = result.length();
        for (int i = 0; i < cCount; i++) {
            mFavorites.add(new Favorite(result.getJSONObject(i)));
        }
    }

    public static FavoriteList load(String file) {
        FavoriteList result = null;
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
            result = new FavoriteList(json);
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

    public void addFavorite(Favorite cFavorite) {
        mFavorites.add(cFavorite);
    }

    public int getIndex(Favorite cFavorite) {
        return mFavorites.indexOf(cFavorite);
    }

    public boolean isFavorite(Favorite cFavorite) {
        return mFavorites.contains(cFavorite);
    }

    public boolean removeFavorite(Favorite cFavorite) {
        return mFavorites.remove(cFavorite);
    }

    public void addFristFavorite(Favorite cFavorite) {
        mFavorites.add(0, cFavorite);
    }

    public int size() {
        return mFavorites.size();
    }

    public Favorite get(int index) {
        return mFavorites.get(index);
    }

    public JSONArray toJSON() throws JSONException {
        JSONArray result = new JSONArray();
        for (Favorite favorite : mFavorites) {
            result.put(favorite.toJSON());
        }
        return result;
    }

}
