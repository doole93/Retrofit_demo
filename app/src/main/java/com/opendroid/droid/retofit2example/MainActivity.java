package com.opendroid.droid.retofit2example;


import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    protected final String TAG = getClass().getSimpleName();
    private RetainedAppData mRetainedAppData;
    // UX handlers
    private ProgressBar mProgressBar;
    private EditText    mUsername;
    private TextView    mNameSurnameTextView;
    private TextView    mEmailTextView;
    private TextView    mDescriptionTextView;
    private TextView    mCommentsTextView;
    private TextView    mTempTextView;
    private TextView    mSunriseTextView;
    private TextView    mSunsetTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar_id);
        mUsername = (EditText) findViewById(R.id.username_id);
        // Data saved
        if (savedInstanceState != null) {
            if (getLastCustomNonConfigurationInstance() != null) {
                mRetainedAppData = (RetainedAppData) getLastCustomNonConfigurationInstance();
                Log.d(TAG,"onCreate(): Reusing retained data set");
            }
        } else {
            mRetainedAppData = new RetainedAppData();
            Log.d(TAG, "onCreate(): Creating new  data set");
        }

        // Setup activity reference
        // mActivityRef = new WeakReference<>(this);
        mRetainedAppData.setAppContext(this);

        if (mRetainedAppData.mData != null) {
            updateUXWithWeather(mRetainedAppData.mData);
        }
        // Setup the progress bar
        if  (mRetainedAppData.isFetchInProgress()) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // mActivityRef = null;
        mRetainedAppData.setAppContext(null);
        Log.d(TAG,"onDestroy()");
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return mRetainedAppData;
    }

    public void expandUserSync(View v) {
        hideKeyboard(MainActivity.this, mUsername.getWindowToken());
        String username = mUsername.getText().toString();
        if (username.isEmpty()) {
            Toast.makeText(getApplicationContext(),"No username specified.",
                    Toast.LENGTH_LONG).show();
            return;
        }
        mRetainedAppData.runRetrofitTestSync(username);
    }

    public void expandUserAsync(View v) {
        hideKeyboard(MainActivity.this, mUsername.getWindowToken());
        String username = mUsername.getText().toString();
        if (username.isEmpty()) {
            Toast.makeText(getApplicationContext(),"No username specified.",
                    Toast.LENGTH_LONG).show();
            return;
        }
        mRetainedAppData.runRetrofitTestAsync(username);
    }

    /**
     * This method is used to hide a keyboard after a user has
     * finished typing the url.
     */
    public static void hideKeyboard(Activity activity,
                                    IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager) activity.getSystemService
                (Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }

    void updateUXWithWeather (final User data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Setup UX handlers
                // Get the UX handlers every time. This is to avoid a condition
                // when runOnUiThread may not have updated UX handles when screen is rotated.
                // 'mActivityRef.get()'
                mProgressBar = (ProgressBar) findViewById(R.id.progress_bar_id);
                mUsername = (EditText) findViewById(R.id.username_id);
                mNameSurnameTextView = (TextView) findViewById(R.id.name_id);
                mEmailTextView = (TextView) findViewById(R.id.email_id);
                mDescriptionTextView = (TextView) findViewById(R.id.description_id);
//                mCommentsTextView = (TextView) findViewById(R.id.coords_id);
//                mTempTextView = (TextView) findViewById(R.id.temp_id);
//                mSunriseTextView = (TextView) findViewById(R.id.sunrise_id);
//                mSunsetTextView = (TextView) findViewById(R.id.sunset_id);

                // Refresh UX data
                if (mRetainedAppData.isFetchInProgress()) {
                    mProgressBar.setVisibility(View.VISIBLE);
                } else {
                    mProgressBar.setVisibility(View.INVISIBLE);
                }

                // Print data to Android display
                Resources res = getResources();
                String textToPrint = res.getString(R.string.name) + data.getName();
                mNameSurnameTextView.setText(textToPrint);
                textToPrint = res.getString(R.string.mail) + data.getMail();
                mEmailTextView.setText(textToPrint);
                textToPrint = res.getString(R.string.description) + data.getDescription();
                mDescriptionTextView.setText(textToPrint);
//                textToPrint = res.getString(R.string.coordinates) +"(" + data.getLat() + "," + data.getLon() + ")";
//                mCommentsTextView.setText(textToPrint);
//                String tempF = String.format(Locale.UK,"Temperature: %.2f F", (data.getTemp() - 273.15) * 1.8 + 32.00);
//                mTempTextView.setText(tempF);
//                DateFormat dfLocalTz = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.UK);
//                Date sunriseTime = new Date(data.getSunrise() * 1000);
//                Date sunsetTime = new Date(data.getSunset() * 1000);
//                textToPrint = res.getString(R.string.sunrise) + dfLocalTz.format(sunriseTime);
//                mSunriseTextView.setText(textToPrint);
//                textToPrint = res.getString(R.string.sunset) + dfLocalTz.format(sunsetTime);
//                mSunsetTextView.setText(textToPrint);
            }
        });
    }

    /**
     * This is main class object that should save all data upon configuration changes.
     * This object is saved by the 'onRetainCustomNonConfigurationInstance' method.
     *
     * Note: In the video it is referred to as 'private class TestWeather'
     */
    private static class RetainedAppData {
        private  WeakReference<MainActivity> mActivityRef;
        protected final String TAG = "RTD";
        private User mData; // User data received
        private AtomicBoolean mInProgress = new AtomicBoolean(false); // Is a download in progress
        private UserRestAdapter mUserRestAdapter; // REST Adapter
        private Callback<User> mUserCallback = new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                // response.isSuccessful() is true if the response code is 2xx
                if (response.isSuccessful()) {
                    User data = response.body();
                    Log.d(TAG, "Async success: User: Name:" + data.getName() + ", Surname:" + data.getSurname()
                            + ",Email: (" + data.getMail());
                    mData = data;
                    if (mActivityRef.get() != null) {
                        mActivityRef.get().updateUXWithWeather(mData);
                        mActivityRef.get().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mActivityRef.get().mProgressBar = (ProgressBar) mActivityRef.get().
                                        findViewById(R.id.progress_bar_id);
                                mActivityRef.get().mProgressBar.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                    mInProgress.set(false);
                } else {
                    int statusCode = response.code();

                    // handle request errors yourself
                    ResponseBody errorBody = response.errorBody();
                    Log.d(TAG,"Error code:" + statusCode + ", Error:" + errorBody);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                mInProgress.set(false);
                if (mActivityRef.get() != null) {
                    mActivityRef.get().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mActivityRef.get().mProgressBar = (ProgressBar) mActivityRef.get().
                                    findViewById(R.id.progress_bar_id);
                            mActivityRef.get().mProgressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        };
        // Method to test Async. call
        public void runRetrofitTestAsync (final String username) {
            if ( (mActivityRef.get() != null) && (mInProgress.get())) {
                Toast.makeText(mActivityRef.get(),"User fetch in progress.",
                        Toast.LENGTH_LONG).show();
                return;
            }
            // Get the Adapter
            if (mUserRestAdapter == null)
                mUserRestAdapter = new UserRestAdapter();

            if (mActivityRef.get() != null) {
                mActivityRef.get().mProgressBar.setVisibility(View.VISIBLE);
            }

            // Test delay
            try {
                mInProgress.set(true);
                mUserRestAdapter.testUserApi(username, mUserCallback); // Call Async API
            } catch (Exception e) {
                Log.d(TAG, "Thread sleep error" + e);
            }
        }

        // Method to test sync. call
        public void runRetrofitTestSync (final String username) {

            if ((mActivityRef.get() != null) && (mInProgress.get())) {
                Toast.makeText(mActivityRef.get(),"Weather fetch in progress.",
                        Toast.LENGTH_LONG).show();
                return;
            }
            if (mActivityRef.get() != null) {
                mActivityRef.get().mProgressBar.setVisibility(View.VISIBLE);
            }

            if (mUserRestAdapter == null)
                mUserRestAdapter = new UserRestAdapter();

            mInProgress.set(true);

            // Test Sync version -- in a separate thread
            // Not doing this will crash the app. As Retro sync calls can not be made from
            // UI thread.
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Call Async API -- always call in a try block if you dont want app to
                        // crash. You get 'HTTP/1.1 500 Internal Server Error' more often than
                        // you think.
                        Thread.sleep(10000);
                        User data = mUserRestAdapter.testUserApiSync(username);
                        if (data != null) {
                            Log.d(TAG, "Async success: User: Name:" + data.getName() + ", Surname:" + data.getSurname()
                                    + ",Email: (" + data.getMail());
                            mData = data;
                            if (mActivityRef.get() != null) {
                                mActivityRef.get().updateUXWithWeather(mData);
                            }
                        } else {
                            Log.e(TAG, "Sync: no data fetched");
                        }
                    } catch (Exception ex) {
                        Log.e(TAG, "Sync: exception", ex);
                        if (mActivityRef.get() != null) {
                            mActivityRef.get().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mActivityRef.get().mProgressBar = (ProgressBar) mActivityRef.get().
                                            findViewById(R.id.progress_bar_id);
                                    mActivityRef.get().mProgressBar.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                    } finally {
                        mInProgress.set(false);
                    }
                }
            }).start();
        }

        void setAppContext (MainActivity ref) {
            mActivityRef = new WeakReference<>(ref);
        }

        boolean isFetchInProgress() {
            return mInProgress.get();
        }
    }
}
