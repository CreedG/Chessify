package com.creedg.chessify.core;

import java.util.Arrays;

/**
 * Created by Creed on 1/28/2017.
 */

public class Utils {

    public static int[] indicesofLargest(int[] orig, int nummax) {
        int[] copy = Arrays.copyOf(orig,orig.length);
        Arrays.sort(copy);
        int[] honey = Arrays.copyOfRange(copy,copy.length - nummax, copy.length);
        int[] result = new int[nummax+1];
        int resultPos = 0;
        for(int i = 0; i < orig.length; i++) {
            int onTrial = orig[i];
            int index = Arrays.binarySearch(honey,onTrial);
            if(index < 0) continue;
            result[resultPos++] = i;
        }
        return result;
    }
}
