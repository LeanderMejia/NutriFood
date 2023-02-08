package com.dpio.nutrifood.Class;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

public class ClassJSON {
    public String getJSONFile(Context context) {
        String json;
        try {
            InputStream inputStream = context.getAssets().open("test.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }
}
