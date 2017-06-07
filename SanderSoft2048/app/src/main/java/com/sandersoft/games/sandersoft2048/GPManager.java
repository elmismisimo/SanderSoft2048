package com.sandersoft.games.sandersoft2048;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.database.CharArrayBuffer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Game;
import com.google.android.gms.games.Games;
import com.sandersoft.games.sandersoft2048.R;
import com.sandersoft.games.sandersoft2048.controllers.BoardController;
import com.sandersoft.games.sandersoft2048.utils.Globals;
import com.sandersoft.games.sandersoft2048.views.ActivityMain;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Sander on 23/05/2017.
 */

public class GPManager implements Game, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public GoogleApiClient mGoogleApiClient;
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;
    Context context;

    //@Override
    //public void onCreate(@Nullable Bundle savedInstanceState) {
    public GPManager(Context context){
        this.context = context;
        // Create a GoogleApiClient instance
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Games.API)
                //.addApi(Plus.API)
                //.addScope(Drive.SCOPE_FILE)
                //.addApi(AppStateManager.API)
                .addScope(Games.SCOPE_GAMES)
                //.addScope(Plus.SCOPE_PLUS_LOGIN)
                //.addScope(AppStateManager.SCOPE_APP_STATE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        //conectamos a google
        connectGP();

        //super.onCreate(savedInstanceState);
    }

    /*@Override
    public void onStart() {
        super.onStart();
        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(Globals.GAMES_CONNECT, true))
            mGoogleApiClient.connect();
    }*/

    /*@Override
    public void onDestroy() {
        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(Globals.GAMES_CONNECT, true)
                && isConnectedGooglePlay())
            mGoogleApiClient.disconnect();
        super.onDestroy();
    }*/

    public void connectGP(){
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Globals.GAMES_CONNECT, true))
            mGoogleApiClient.connect();
    }
    public void disconnectGP(){
        if (isConnectedGooglePlay())
            mGoogleApiClient.disconnect();
    }

    public void unlockAchievement(int id){
        if (mGoogleApiClient.isConnected())
            Games.Achievements.unlock(mGoogleApiClient, context.getResources().getString(id));
    }
    public void unlockIncrementalAchievement(int id, int steps){
        if (mGoogleApiClient.isConnected() && steps > 0)
            Games.Achievements.increment(mGoogleApiClient, context.getResources().getString(id), steps);
    }

    public void seeAchievements(Activity activity){
        if (mGoogleApiClient.isConnected())
            activity.startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient), 1);
    }
    public void seeScoreBoard(Activity activity){
        if (mGoogleApiClient.isConnected())
            activity.startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(mGoogleApiClient), 1);
    }

    public void defineScore(long score){
        if (mGoogleApiClient.isConnected()) {
            String selectedScore = context.getResources().getString(R.string.score_2048);
            PendingResult r = Games.Leaderboards.submitScoreImmediate(mGoogleApiClient, selectedScore , score);
            Games.Leaderboards.submitScore(mGoogleApiClient, selectedScore, score);
            ResultCallback callback = new ResultCallback() {
                @Override
                public void onResult(@NonNull Result result) {
                    Log.v("ResScore", result.getStatus().toString());
                    Log.v("ResScore", result.getStatus().getStatusMessage());
                    Log.v("ResScore", result.getStatus().hasResolution() ? "true" : "false");
                    System.out.println(result.getStatus());
                    //System.out.println(result.getStatus().getStatusMessage());
                    //System.out.println(result.getStatus().hasResolution());
                }
            };
            r.setResultCallback(callback);
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // Connected to Google Play services!
        // The good stuff goes here.
        //Toast.makeText(context, "Conectado a GP", Toast.LENGTH_LONG).show();
        //Games.setViewForPopups(mGoogleApiClient, currentActivity.findViewById(android.R.id.content));
        //screen.game.verificaLogros();
        ActivityMain.This.onGPConnected();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection has been interrupted.
        // Disable any UI components that depend on Google APIs
        // until onConnected() is called.
        //Toast.makeText(getActivity(), "suspendido de GP", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        //Log.v("ConnFail", String.valueOf(result.getErrorCode()));
        //Log.v("ConnFail", result.getErrorMessage());
        //Log.v("ConnFail", result.getResolution().toString());

        // This callback is important for handling errors that
        // may occur while attempting to connect with Google.
        //
        // More about this in the next section.
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(ActivityMain.This, Globals.REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            //showErrorDialog(result.getErrorCode());
            //Toast.makeText(getActivity(), result.toString(), Toast.LENGTH_LONG).show();
            mResolvingError = true;
        }
    }

    //@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Globals.REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient.isConnecting() &&
                        !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
            else if (resultCode == Globals.RESULT_APP_MISCONFIGURED) {
                Toast.makeText(ActivityMain.This, "Error in the configuration", Toast.LENGTH_LONG).show();
            }
        }
    }

    public boolean isConnectedGooglePlay() {
        return mGoogleApiClient.isConnected();
    }

    /*
    From here to the end are stubs of implemented methods that are not used
     */

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getApplicationId() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public void getDisplayName(CharArrayBuffer charArrayBuffer) {

    }

    @Override
    public String getPrimaryCategory() {
        return null;
    }

    @Override
    public String getSecondaryCategory() {
        return null;
    }

    @Override
    public void getDescription(CharArrayBuffer charArrayBuffer) {

    }

    @Override
    public String getDeveloperName() {
        return null;
    }

    @Override
    public void getDeveloperName(CharArrayBuffer charArrayBuffer) {

    }

    @Override
    public Uri getIconImageUri() {
        return null;
    }

    @Override
    public String getIconImageUrl() {
        return null;
    }

    @Override
    public Uri getHiResImageUri() {
        return null;
    }

    @Override
    public String getHiResImageUrl() {
        return null;
    }

    @Override
    public Uri getFeaturedImageUri() {
        return null;
    }

    @Override
    public String getFeaturedImageUrl() {
        return null;
    }

    @Override
    public boolean zzCV() {
        return false;
    }

    @Override
    public boolean isMuted() {
        return false;
    }

    @Override
    public boolean zzCW() {
        return false;
    }

    @Override
    public boolean zzCX() {
        return false;
    }

    @Override
    public String zzCY() {
        return null;
    }

    @Override
    public int getAchievementTotalCount() {
        return 0;
    }

    @Override
    public int getLeaderboardCount() {
        return 0;
    }

    @Override
    public boolean isRealTimeMultiplayerEnabled() {
        return false;
    }

    @Override
    public boolean isTurnBasedMultiplayerEnabled() {
        return false;
    }

    @Override
    public boolean areSnapshotsEnabled() {
        return false;
    }

    @Override
    public String getThemeColor() {
        return null;
    }

    @Override
    public boolean hasGamepadSupport() {
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }

    @Override
    public Game freeze() {
        return null;
    }

    @Override
    public boolean isDataValid() {
        return false;
    }


}
