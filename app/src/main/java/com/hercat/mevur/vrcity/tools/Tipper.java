package com.hercat.mevur.vrcity.tools;

import android.content.Context;
import android.widget.Toast;

public class Tipper {

    private static boolean inited = false;
    private static Context context;

    public static void initialize(Context context) {
        if (!inited) {
            if (null != context) {
                Tipper.context = context;
                inited = true;
                System.out.println("Tipper initialized.");
            } else {
                System.err.println("context pass to Tipper cannot be null.");
            }
        } else {
            System.err.println("the Tipper has been initialized.");
        }
    }

    public static void tip(String content) {
        if (inited) {
            Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
        } else {
            System.err.println("Tipper need to be initialized before use.");
        }
    }
}
