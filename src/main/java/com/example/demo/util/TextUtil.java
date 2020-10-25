package com.example.demo.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TextUtil {
    public static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
}
