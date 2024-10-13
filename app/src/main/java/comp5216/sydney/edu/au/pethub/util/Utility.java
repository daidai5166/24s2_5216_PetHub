package comp5216.sydney.edu.au.pethub.util;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Utility {
    // 使用para2分割para1字符串，分割为三部分，返回一个字符串数组
    public static String[] splitLocation(String location, String splitElement) {
        String row1 = location.split(splitElement)[0];

        if (location.equals("") || location.equals(" ")) {
            return new String[]{"", "", ""};
        }
        if(location.split(splitElement).length == 1) {
            return new String[]{row1, "", ""};
        }
        else if(location.split(splitElement).length == 2) {
            return new String[]{row1, location.split(splitElement)[1], ""};
        }
        String row2 = location.split(splitElement)[1];
        // row3 is the left part of the location string
        String row3 = location.substring(row1.length() + row2.length() + 2);

        return new String[]{row1, row2, row3};
    }

    // 将字符串数组合并为一个字符串
    public static String mergeLocation(String[] location, String splitElement) {
        return location[0] + splitElement + location[1] + splitElement + location[2];
    }

}