package com.anish.calabashbros;

import java.util.Random;

public class getRandom {
    private int[] source;
    private int size;

    public getRandom(int[] source, int size){
        this.source = source;
        this.size = size;
    }

    public int[] getRandomRes(){
        if (source == null && size > source.length) {
            return null;
        }
        int[] result = new int[size];
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            int randomIndex = random.nextInt(source.length - i);
            int randomRes = source[randomIndex];
            result[i] = randomRes;
            int temp = source[randomIndex];
            source[randomIndex] = source[source.length - 1 - i];
            source[source.length - 1 - i] = temp;
        }
        return result;
    }
}