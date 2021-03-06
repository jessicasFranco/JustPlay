package com.example.filip.justplay;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.filip.justplay.Fragments.MainPage;
import com.example.filip.justplay.Fragments.MyMusic;
import com.example.filip.justplay.Fragments.PlayList;
import com.example.filip.justplay.Utility.Utility;
import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    private static final Object PERMISSION_REQUEST_CODE = "100" ;
    //Static variable to get a reference of our application context
    public static Context contextOfApplication;
    public static Context getContextOfApplication() {
        return contextOfApplication;
    }


    Intent _serviceIntent;
    SensorBroadcastReceiver _receiver;


    //User Information
    private ImageView photoImageView;
    private TextView nameTextView;
    private TextView emailTextView;
    private GoogleApiClient googleApiClient;

    private String nameText;
    private String emailText;
    private Uri photoUri;

    private Activity mainActivity = this;

    //Player
    public ImageView play;
    public ImageView forward;
    public ImageView rewind;
    public ImageView favorite;
    public ImageView block;
    public TextView currentTime, songDuration, song;


    SeekBar seekbar;
    public MediaPlayer mediaPlayer;

    Handler handler;
    Runnable runnable;

    private double startTime = 0;
    private double finalTime = 0;

    private Handler myHandler = new Handler();
    private int forwardTime = 5000;
    private int backwardTime = 5000;


    private String folder = "";
    private String previousfolder = "";

    public static int oneTimeOnly = 0;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Service
        _serviceIntent = new Intent(this,SensorService.class);
        startService(_serviceIntent);

        IntentFilter filter = new IntentFilter();
        filter.addAction("PLAYLIST");
        _receiver = new SensorBroadcastReceiver();
        registerReceiver(_receiver, filter);

        MainPage mainPage = new MainPage();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(
                R.id.layoutCM,
                mainPage,
                mainPage.getTag()).commit();

        //Silent Log in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        contextOfApplication = getApplicationContext();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            //Called when a drawer has settled in a completely open state
            public void onDrawerOpened(View view) {
                super.onDrawerOpened(view);
                photoImageView = (ImageView) findViewById(R.id.photoImageView);
                nameTextView = (TextView) findViewById(R.id.nameTextView);
                emailTextView = (TextView) findViewById(R.id.emailTextView);

                nameTextView.setText(nameText);
                emailTextView.setText(emailText);
                Glide.with(mainActivity).load(photoUri).into(photoImageView);
            }

        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private class SensorBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String str = intent.getStringExtra("activity");

            if(str != null){
                folder = str;
                Log.i("onReceive", str);
            }

            Log.i("MainActivity", "onReceive");
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //TODO: buttons behaviour

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Navigation of Side Bar Menu
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_mainPage) {
            MainPage mainPage = new MainPage();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(
                    R.id.layoutCM,
                    mainPage,
                    mainPage.getTag()).commit();
        } else if (id == R.id.nav_MyMusic) {
            MyMusic myMusic = new MyMusic();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(
                    R.id.layoutCM,
                    myMusic,
                    myMusic.getTag()).commit();

        } else if (id == R.id.nav_favorites) {

        } else if (id == R.id.nav_definitions) {

        } else if (id == R.id.nav_logout) {
            logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout(){
        LoginManager.getInstance().logOut();
        goLogInScreen();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v("LoginActivity", "onConnectionFailed");
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(AccessToken.getCurrentAccessToken() == null){
            checkLoginWithGoogle();
        }
        else{
            checkLoginWithFacebook();
        }
    }

    private void checkLoginWithGoogle(){
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (opr.isDone()) {
            GoogleSignInResult result = opr.get();
            handleGoogleSignInResult(result);
        }
        else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    handleGoogleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(_serviceIntent);
        unregisterReceiver(_receiver);
    }

    private void checkLoginWithFacebook(){
        Profile user = Profile.getCurrentProfile();
        if(user != null){
            nameText = user.getName();
            photoUri = user.getProfilePictureUri(200,200);
        }
    }

    private void handleGoogleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            nameText = account.getDisplayName();
            emailText = account.getEmail();
            photoUri = account.getPhotoUrl();

            Log.d("APP", account.getPhotoUrl().toString());
        } else {
            goLogInScreen();
        }
    }

    private void goLogInScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void playerStart(final String path, final Song currentSong, final int value) {

        handler = new Handler();
        seekbar = (SeekBar) findViewById(R.id.seekbar);

        //all the buttons/images
        play = (ImageView) findViewById(R.id.play);
        forward = (ImageView) findViewById(R.id.forward);
        rewind = (ImageView) findViewById(R.id.rewind);

        //Bottom with name of music (Playbar textview)
        TextView durationTime = (TextView) findViewById(R.id.songDuration);
        final TextView currentTime = (TextView) findViewById(R.id.currentTime);
        durationTime.setText(Utility.convertDuration(currentSong.getDuration()));

        handler = new Handler();
        seekbar = (SeekBar) findViewById(R.id.seekbar);
        //seekbar.setMax(mediaPlayer.getDuration());

        mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(path));
        mediaPlayer.start();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                seekbar.setMax(mediaPlayer.getDuration());
                playCycle();
                play.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
                Toast.makeText(getApplicationContext(), "Playing", Toast.LENGTH_SHORT).show();

                play.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mediaPlayer != null) {
                                    if (mediaPlayer.isPlaying()) {
                                        mediaPlayer.pause();
                                        Toast.makeText(getApplicationContext(), "Pausing", Toast.LENGTH_SHORT).show();
                                        play.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));

                                    } else {
                                        mediaPlayer.start();
                                        Toast.makeText(getApplicationContext(), "Playing", Toast.LENGTH_SHORT).show();
                                        play.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));

                                    }
                                }}});
                forward.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mediaPlayer.isPlaying()){
                            mediaPlayer.stop();
                            if(value == 0){
                                MyMusic myMusic = new MyMusic();
                                myMusic.getNext(currentSong);
                            }
                            else{
                                PlayList playList = new PlayList();
                                playList.getNext(currentSong);
                            }
                            Toast.makeText(getApplicationContext(),"Next", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                rewind.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mediaPlayer.isPlaying()){
                            mediaPlayer.stop();
                            if(value == 0){
                                MyMusic myMusic = new MyMusic();
                                myMusic.getPrevious(currentSong);
                            }
                            else{
                                PlayList playList = new PlayList();
                                playList.getPrevious(currentSong);
                            }
                            Toast.makeText(getApplicationContext(),"Previous", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        });

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String prog =  Utility.convertDuration(progress);
                currentTime.setText(prog);
                seekbar.setProgress(progress);
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    currentTime.setText(Utility.convertDuration(progress));
                }
                Log.i("LOG", progress + " " + currentSong.getDuration());
                if(progress >= currentSong.getDuration()){
                    if(folder != null && previousfolder != folder){
                        PlayList playList = new PlayList();
                        Bundle bundle = new Bundle();
                        bundle.putString("folder", folder);
                        playList.setArguments(bundle);
                        FragmentManager manager = getSupportFragmentManager();
                        manager.beginTransaction().replace(
                                R.id.layoutCM,
                                playList,
                                playList.getTag()).commit();

                        previousfolder = folder;
                    }
                    else if(folder != null && previousfolder == folder){
                        PlayList playList = new PlayList();
                        playList.getNext(currentSong);
                    }
                    else{
                        MyMusic myMusic = new MyMusic();
                        myMusic.getNext(currentSong);
                    }

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }


        });
    }

    public void playCycle(){
        seekbar.setProgress(mediaPlayer.getCurrentPosition());

        if(mediaPlayer.isPlaying()){
            runnable = new Runnable(){
                @Override
                public void run() {
                    playCycle();
                }
            };
            myHandler.postDelayed(runnable, 1000);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
       // mediaPlayer.start();
    }

}