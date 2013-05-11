package com.prettygirl.app;

import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;

public class AlbumCacheFileNameGenerator implements FileNameGenerator {
    private final String packageName;

    public AlbumCacheFileNameGenerator(Context context) {
        packageName = context.getPackageName().replace('.', '_') + "_";
    }

    /**
     * Url is like http://www.zn.westlearn.com/imgs/000/000/000000158/t_27.jpg?a=1&b=2
     * generated string is like 000000158__t_27.jpg
     * 
     * if url is something like : assets://images/47.jpg 
     * will generate string like app_package_name_images_47.jpg
     */
    public String generate(String imageUri) {
        if (imageUri.startsWith("http") == true) {
            int index = imageUri.indexOf('?');
            if (index != -1) {
                imageUri = imageUri.substring(0, index);
            }
            index = imageUri.indexOf('/', imageUri.indexOf("://") + 3);
            return imageUri.substring(index + 1).replace('/', '_');
        } else if (imageUri.startsWith("assets") == true) {
            imageUri = imageUri.substring(9);
            return packageName + imageUri.replace('/', '_');
        }
        throw new RuntimeException("Unsupported uri " + imageUri);
    }
}
