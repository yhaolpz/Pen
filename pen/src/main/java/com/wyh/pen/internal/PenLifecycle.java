package com.wyh.pen.internal;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.RestrictTo;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import android.view.View;

import com.wyh.pen.core.Pen;
import com.wyh.pen.core.PenTag;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class PenLifecycle {

    public static void init(Context context) {
        ((Application) context.getApplicationContext()).registerActivityLifecycleCallbacks(
                new Application.ActivityLifecycleCallbacks() {
                    @Override
                    public void onActivityCreated(Activity activity, Bundle bundle) {
                        Pen.d(PenTag.ACTIVITY_LIFE_TAG, "Create -> " + activity.getClass().getSimpleName() + "@" + activity.hashCode());
                        if (activity instanceof FragmentActivity) {
                            FragmentActivity fActivity = (FragmentActivity) activity;
                            FragmentManager fm = fActivity.getSupportFragmentManager();
                            fm.registerFragmentLifecycleCallbacks(sFragmentLifecycleCallbacks, true);
                        }
                    }

                    @Override
                    public void onActivityStarted(Activity activity) {
                        Pen.d(PenTag.ACTIVITY_LIFE_TAG, "onStart -> " + activity.getClass().getSimpleName() + "@" + activity.hashCode());
                    }

                    @Override
                    public void onActivityResumed(Activity activity) {
                        Pen.d(PenTag.ACTIVITY_LIFE_TAG, "Resume -> " + activity.getClass().getSimpleName() + "@" + activity.hashCode());
                    }

                    @Override
                    public void onActivityPaused(Activity activity) {
                        Pen.d(PenTag.ACTIVITY_LIFE_TAG, "Pause -> " + activity.getClass().getSimpleName() + "@" + activity.hashCode());
                    }

                    @Override
                    public void onActivityStopped(Activity activity) {
                        Pen.d(PenTag.ACTIVITY_LIFE_TAG, "Stop -> " + activity.getClass().getSimpleName() + "@" + activity.hashCode());
                    }

                    @Override
                    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
                        Pen.d(PenTag.ACTIVITY_LIFE_TAG, "onSaveInstanceState -> " + activity.getClass().getSimpleName() + "@" + activity.hashCode());
                    }

                    @Override
                    public void onActivityDestroyed(Activity activity) {
                        Pen.d(PenTag.ACTIVITY_LIFE_TAG, "Destroy -> " + activity.getClass().getSimpleName() + "@" + activity.hashCode());
                    }
                }
        );
    }

    private static FragmentManager.FragmentLifecycleCallbacks sFragmentLifecycleCallbacks = new FragmentManager.FragmentLifecycleCallbacks() {
        @Override
        public void onFragmentPreAttached(FragmentManager fm, Fragment f, Context context) {
            super.onFragmentPreAttached(fm, f, context);
        }

        @Override
        public void onFragmentAttached(FragmentManager fm, Fragment f, Context context) {
            super.onFragmentAttached(fm, f, context);
        }

        @Override
        public void onFragmentPreCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
            super.onFragmentPreCreated(fm, f, savedInstanceState);
        }

        @Override
        public void onFragmentCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
            super.onFragmentCreated(fm, f, savedInstanceState);
            String log = "Created -> " + f.getClass().getSimpleName() + "@" + f.hashCode();
            Pen.d(PenTag.FRAGMENT_LIFE_TAG, log);
        }

        @Override
        public void onFragmentActivityCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
            super.onFragmentActivityCreated(fm, f, savedInstanceState);
        }

        @Override
        public void onFragmentViewCreated(FragmentManager fm, Fragment f, View v, Bundle savedInstanceState) {
            super.onFragmentViewCreated(fm, f, v, savedInstanceState);
        }

        @Override
        public void onFragmentStarted(FragmentManager fm, Fragment f) {
            super.onFragmentStarted(fm, f);
        }

        @Override
        public void onFragmentResumed(FragmentManager fm, Fragment f) {
            super.onFragmentResumed(fm, f);
            String log = "Resume -> " + f.getClass().getSimpleName() + "@" + f.hashCode();
            Pen.d(PenTag.FRAGMENT_LIFE_TAG, log);
        }

        @Override
        public void onFragmentPaused(FragmentManager fm, Fragment f) {
            super.onFragmentPaused(fm, f);
            String log = "Pause -> " + f.getClass().getSimpleName() + "@" + f.hashCode();
            Pen.d(PenTag.FRAGMENT_LIFE_TAG, log);
        }

        @Override
        public void onFragmentStopped(FragmentManager fm, Fragment f) {
            super.onFragmentStopped(fm, f);
            String log = "Stop -> " + f.getClass().getSimpleName() + "@" + f.hashCode();
            Pen.d(PenTag.FRAGMENT_LIFE_TAG, log);
        }

        @Override
        public void onFragmentSaveInstanceState(FragmentManager fm, Fragment f, Bundle outState) {
            super.onFragmentSaveInstanceState(fm, f, outState);
        }

        @Override
        public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
            super.onFragmentViewDestroyed(fm, f);
        }

        @Override
        public void onFragmentDestroyed(FragmentManager fm, Fragment f) {
            super.onFragmentDestroyed(fm, f);
            String log = "Destroyed -> " + f.getClass().getSimpleName() + "@" + f.hashCode();
            Pen.d(PenTag.FRAGMENT_LIFE_TAG, log);
        }

        @Override
        public void onFragmentDetached(FragmentManager fm, Fragment f) {
            super.onFragmentDetached(fm, f);
        }
    };
}
