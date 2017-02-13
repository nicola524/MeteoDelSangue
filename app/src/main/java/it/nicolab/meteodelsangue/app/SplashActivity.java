package it.nicolab.meteodelsangue.app;

import android.content.Intent;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.os.Handler;

import java.lang.ref.WeakReference;


public class SplashActivity extends ActionBarActivity  {


    private static final String TAG_LOG = SplashActivity.class.getName();
    private static final long MIN_WAIT_INTERVAL = 1500L;
    private static final long MAX_WAIT_INTERVAL = 3000L;

    /**
     * The What to use into the Handler to go to the next Activity
     */
    private static final int GO_AHEAD_WHAT = 1;

    private long mStartTime;
    private boolean mIsDone;

    /**
     * This is a static class that doesn't retain the reference to the container class thought
     * the this reference. The reference to the container Activity is make through the
     * WeakReference that is not counted as a valid reference by the GC
     */
    private static class UiHandler extends Handler {

        /**
         * The WeakReference to the Activity that uses it
         */
        private WeakReference<SplashActivity> mActivityRef;

        /**
         * Constructor with the sourceActivity reference
         * @param srcActivity The Activity we need a reference of.
         */
        public UiHandler(final SplashActivity srcActivity) {
            // We just save the reference to the src Activity
            this.mActivityRef = new WeakReference<SplashActivity>(srcActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            // We get the reference to the sourceActivity
            final SplashActivity srcActivity = this.mActivityRef.get();
            if (srcActivity == null) {
                // In this case the reference to the Activity is lost. It should not be
                // the case.
                //Log.d(TAG_LOG, "Reference to NoLeakSplashActivity lost!");
                return;
            }
            switch (msg.what) {
                case GO_AHEAD_WHAT:
                    // We calculate le elapsed time to prevent the early launch of the next
                    // Activity
                    long elapsedTime = SystemClock.uptimeMillis() - srcActivity.mStartTime;
                    // We go ahead if not closed. We could use isDestroyed() but is available only
                    // from API Level 17. Then we need to use an instance variable.
                    if (elapsedTime >= MIN_WAIT_INTERVAL &&  !srcActivity.mIsDone) {
                        srcActivity.mIsDone = true;
                        srcActivity.goAhead();
                    }
                    break;
            }
        }

    }

    /**
     * This is the Handler we use to manage time interval
     */
    private UiHandler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalsh);
        // We initialize the Handler
        mHandler = new UiHandler(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // We set the time to consider as the start
        mStartTime = SystemClock.uptimeMillis();
        // We set the time for going ahead automatically
        final Message goAheadMessage = mHandler.obtainMessage(GO_AHEAD_WHAT);
        mHandler.sendMessageAtTime(goAheadMessage, mStartTime + MAX_WAIT_INTERVAL);
        //Log.d(TAG_LOG, "Handler message sent!");
    }

    /**
     * Utility method that manages the transition to the FirstAccessActivity
     */
    private void goAhead() {
        // We create the explicit Intent
        final Intent intent = new Intent(this, MainActivity.class);
        // Launch the Intent
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        // We finish the current Activity
        finish();
    }
}