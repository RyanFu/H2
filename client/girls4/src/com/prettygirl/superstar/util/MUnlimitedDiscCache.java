package com.prettygirl.superstar.util;

import java.io.File;
import java.io.IOException;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;

public class MUnlimitedDiscCache extends UnlimitedDiscCache {

    private final static String NOMEDIA = ".nomedia";

    public MUnlimitedDiscCache(File cacheDir) {
        super(cacheDir);
    }

    public MUnlimitedDiscCache(File cacheDir, FileNameGenerator fileNameGenerator) {
        super(cacheDir, fileNameGenerator);
        if (!StorageUtils.CACHE_DIR_ASSETS.equals(cacheDir.getPath())) {
            File nomedia = new File(cacheDir.getPath(), NOMEDIA);
            if (nomedia.exists()) {
                return;
            }
            try {
                nomedia.getParentFile().mkdirs();
                nomedia.createNewFile();
            } catch (IOException e) {
            }
        }
    }

}
