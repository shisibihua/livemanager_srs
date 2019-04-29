package com.honghe.livemanager.common.util;


import java.util.Random;

/**
 * Created by wj on 2014-10-13.
 */
 public final class SaltRandom {
    private SaltRandom(){

    }
    static Random r = new Random();
    static String ssource = "0123456789";
    static char[] src = ssource.toCharArray();

    private static String randString (int length)
    {
        char[] buf = new char[length];
        int rnd;
        for(int i=0;i<length;i++)
        {
            rnd = Math.abs(r.nextInt()) % src.length;

            buf[i] = src[rnd];
        }
        return new String(buf);
    }

    public static String runVerifyCode(int i)
    {
        String VerifyCode = randString(i);
        return VerifyCode;
    }
/*
    public static void main(String[] args) {
        String ww =  SaltRandom.runVerifyCode(6);
        System.out.println(ww);
    }*/
}
