package com.example.lee.adaptersample;

/**
 * Created by Lee on 2016-09-17.
 */
public class data {
    public String a;
    public String b;

    public data (String a, String b){
        this.a = a;
        this.b = b;
    }

    @Override
    public String toString() {
        return a.toString();
    }
}
