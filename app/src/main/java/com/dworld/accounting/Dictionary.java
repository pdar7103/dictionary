package com.dworld.accounting;

import android.content.Context;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;


/**
 * Created by darshan on 8/20/2017.
 */

public class Dictionary extends MultiDexApplication {



    @Override
    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(base);
        MultiDex.install(Dictionary.this);
    }
}
