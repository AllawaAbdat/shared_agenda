package com.example.monagendapartage.helper;

import android.app.Activity;

import androidx.test.espresso.IdlingResource;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitor;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import java.util.Collection;

public class WaitActivityIsResumedIdlingResource implements IdlingResource {
    private final ActivityLifecycleMonitor instance;
    private final String activityToWaitClassName;
    boolean resumed = false;
    private volatile ResourceCallback resourceCallback;

    public WaitActivityIsResumedIdlingResource(String activityToWaitClassName) {
        instance = ActivityLifecycleMonitorRegistry.getInstance();
        this.activityToWaitClassName = activityToWaitClassName;
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public boolean isIdleNow() {
        resumed = isActivityLaunched();
        if (resumed && resourceCallback != null) {
            resourceCallback.onTransitionToIdle();
        }

        return resumed;
    }

    private boolean isActivityLaunched() {
        Collection<Activity> activitiesInStage = instance.getActivitiesInStage(Stage.RESUMED);
        for (Activity activity : activitiesInStage) {
            if (activity.getClass().getName().equals(activityToWaitClassName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback resourceCallback) {
        this.resourceCallback = resourceCallback;
    }
}
