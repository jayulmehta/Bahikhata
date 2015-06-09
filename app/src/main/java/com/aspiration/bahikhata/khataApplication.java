package com.aspiration.bahikhata;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.net.ParseException;
import android.util.Log;

import com.helpshift.Helpshift;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.PushService;
import com.shamanland.fonticon.FontIconTypefaceHolder;
import com.parse.Parse;
import com.parse.ParsePush;
import com.parse.SaveCallback;

import java.lang.reflect.Field;

public class khataApplication extends Application {

    private static Context mContext;

    public static Context getmContext() {
        return mContext;
    }

    public static void setmContext(Context mContext) {
        khataApplication.mContext = mContext;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        FontIconTypefaceHolder.init(getAssets(), "icons.ttf");
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, getResources().getString(R.string.ParseAppId), getResources().getString(R.string.ParseClientKey));

        Helpshift.install(this,
                getResources().getString(R.string.helpshiftApiKey),
                getResources().getString(R.string.helpshiftDomain),
                getResources().getString(R.string.helpshiftAppId));
    }
}

