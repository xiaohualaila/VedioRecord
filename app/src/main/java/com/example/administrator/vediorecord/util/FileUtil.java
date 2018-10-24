package com.example.administrator.vediorecord.util;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class FileUtil {
    public static List<String> getFilePath(String path){
        List<String> list= new ArrayList<>();
        list.clear();
        File file = new File(path);
        if(file!=null){
            File[] fs =  file.listFiles();
            if(fs!=null&&fs.length>0){
                for(File f:fs) {
                    if (!f.isDirectory()) {
                        list.add(f.getAbsolutePath());
                        Log.i("sss", "f.getAbsolutePath()  >> " + f.getAbsolutePath());
                    }
                }
            }
        }
        return list;
    }


}
