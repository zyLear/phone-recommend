package com.example.administrator.personalizationrecommendation;

import android.app.Activity;
import android.app.Application;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by xiezongyu on 2018/4/1.
 */

public class SystemApplication extends Application {

    private List<Activity> mList = new LinkedList<Activity>();
    private static SystemApplication instance;

    private SystemApplication() {
    }

    public synchronized static SystemApplication getInstance() {
        if (null == instance) {
            instance = new SystemApplication();
        }
        return instance;
    }

    // add Activity
    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    public void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }

}
