package com.flyingeffects.com.ui.model;

import com.flyingeffects.com.utils.LogUtil;

import java.util.ArrayList;
import java.util.Random;

public class repairRandomPaths {

    private static  int lastRandomInt;

    public static String[] randomPaths(String[] paths) {
        ArrayList<String> path = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < paths.length; i++) {
            if (paths[i] == null || paths[i].isEmpty()) {
                if (path.size() > 0) {
                    int getNum = random.nextInt(path.size());
                    while (path.size() > 1 && getNum == lastRandomInt) {
                        getNum = random.nextInt(path.size());
                    }
                    LogUtil.d("random", getNum + "");
                    paths[i] = path.get(getNum);
                    lastRandomInt = getNum;
                } else {
                    lastRandomInt = 0;
                }
            } else {
                lastRandomInt = i;
            }
        }
        return paths;
    }
}
