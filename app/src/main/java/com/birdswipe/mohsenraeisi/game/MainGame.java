package com.birdswipe.mohsenraeisi.game;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseObject;

import java.lang.reflect.Field;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

public class MainGame extends Activity {

    ImageView imageView;
    ImageView imageViewAnswer;
    TextView timeText;
    TextView scoreText;
    TextView scoreBonus;
    TextView finalScore;
    ImageView playButton;
    AbsoluteLayout gameLayout;
    RelativeLayout menuLayout;
    RelativeLayout restartLayout;
    RelativeLayout bgLayout;
    RelativeLayout settingsLayout;
    RelativeLayout animateBG ;
    Button restartButton;
    MediaPlayer mediaPlayer ;
    ImageView pauseButton;
    ImageView settingsButtonMenu;
    ImageView settingsButtonRestart;
    TextView pauseText;
    ImageView backButton;
    TextView bestScoreMenu;

    ImageView imageViewBird ;

    TextView bestScoreRestart;
    TextView bestTextView;

    CheckBox soundCheckBox;
    CheckBox vibrationCheckBox;
    TextView gamesPlayedTextView;
    TextView bestScoreTextViewSettings;

    InterstitialAd mInterstitialAd;
    Handler adHandler;
    AdView mAdView;
    AdRequest adRequest;

    MediaPlayer backMusic;

    CountDownTimer timer;
    int Width;
    int maxWidth;
    int Height;
    int maxHeight;
    int sizeMax;
    int sizeMin;
    int[] imagesResources;
    int score = 0;
    long timeToFinish =0 ;

    int corrects = 0;
    int coef = 1;

    boolean shouldInit = false;
    Bird currentBird;
    ArrayList<Bird> birdList;

    boolean settingsBack = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        setContentView(R.layout.activity_main_game);

        prepareParse();

        imageView = (ImageView) findViewById(R.id.ImageView);
        imageViewAnswer = (ImageView) findViewById(R.id.imageViewAnswer);
        timeText = ( TextView ) findViewById(R.id.textViewTime);
        scoreText = (TextView) findViewById(R.id.textViewScore);
        scoreBonus = (TextView) findViewById(R.id.textViewBonus);
        playButton = (ImageView) findViewById(R.id.imageViewPlay);
        menuLayout =  (RelativeLayout) findViewById(R.id.menuLayout);
        restartLayout = (RelativeLayout) findViewById(R.id.rastartLayout);
        restartButton = (Button) findViewById(R.id.buttonRestart);
        bgLayout = (RelativeLayout) findViewById(R.id.bgLayout);
        finalScore = (TextView) findViewById(R.id.textViewFinalScore);
        pauseButton = (ImageView) findViewById(R.id.imageViewPause);
        pauseText = (TextView) findViewById(R.id.textViewPause);
        settingsLayout = (RelativeLayout) findViewById(R.id.settingsLayout);
        settingsButtonMenu = (ImageView) findViewById(R.id.imageViewSettings);
        backButton = (ImageView) findViewById(R.id.imageViewBack);
        settingsButtonRestart = (ImageView)  findViewById(R.id.imageViewSettingsRestart);
        bestScoreRestart = (TextView) findViewById(R.id.textViewBestValue);
        bestTextView = (TextView) findViewById(R.id.textViewBest);
        bestScoreMenu = (TextView) findViewById(R.id.textViewBestMenu);
        animateBG = (RelativeLayout) findViewById(R.id.animateLayout);
        imageViewBird = (ImageView) findViewById(R.id.ImageViewBird);
        //settings
         soundCheckBox = (CheckBox)  findViewById(R.id.checkBoxSound);
         vibrationCheckBox =(CheckBox)  findViewById(R.id.checkBoxVibration);
         gamesPlayedTextView = (TextView) findViewById(R.id.textViewGamesPlayed);
         bestScoreTextViewSettings = (TextView) findViewById(R.id.textViewBestScoreSettings);

        addNewImage(R.anim.slide_out_left);
        getSettings();

        gameLayout= (AbsoluteLayout) findViewById(R.id.gameLayout);
        gameLayout.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeTop() {

                swiped(2, R.anim.slide_out_up);
            }

            public void onSwipeRight() {
                swiped(3, R.anim.slide_out_right);

            }

            public void onSwipeLeft() {
                swiped(1, R.anim.slide_out_left);
            }

            public void onSwipeBottom() {
                swiped(4, R.anim.slide_out_down);
            }

            public boolean onTouch(View v, MotionEvent event) {

                return gestureDetector.onTouchEvent(event);
            }

        });


        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeBG(false);
                showGame(getApplicationContext());
                startTimer(Constants.time);
                initGame();
                pauseButton.setVisibility(View.VISIBLE);

                //updating nbr played
                SharedPreferences prefs = getSharedPreferences("BEST_SCORE", MODE_PRIVATE);
                int nbr = prefs.getInt("NBR_PLAYED", 0);
                nbr++;
                SharedPreferences.Editor editor = getSharedPreferences("BEST_SCORE", MODE_PRIVATE).edit();
                editor.putInt("NBR_PLAYED", nbr);
                editor.apply();

                if(Constants.parse)
                     saveInParse();


            }
        });

        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGameFromRestart(getApplicationContext());
                startTimer(Constants.time);
                initGame();
                changeBG(false);
                pauseButton.setVisibility(View.VISIBLE);

                //updating nbr played
                SharedPreferences prefs = getSharedPreferences("BEST_SCORE", MODE_PRIVATE);
                int nbr = prefs.getInt("NBR_PLAYED", 0);
                nbr++;

                SharedPreferences.Editor editor = getSharedPreferences("BEST_SCORE", MODE_PRIVATE).edit();
                editor.putInt("NBR_PLAYED", nbr);
                editor.apply();

            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPause();
            }
        });

        settingsButtonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettings(getApplicationContext(), menuLayout);
                settingsBack = false;

                getSettings();
            }
        });

        settingsButtonRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettings(getApplicationContext(), restartLayout);
                settingsBack = true;

                getSettings();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!settingsBack)
                    showMenu(getApplicationContext(), menuLayout);
                else
                    showMenu(getApplicationContext(), restartLayout);


            }
        });

        vibrationCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getSharedPreferences("SETTINGS", MODE_PRIVATE).edit();
                editor.putBoolean("VIBRATION", isChecked);
                editor.apply();

                Constants.vibration = isChecked;
            }
        });

        soundCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getSharedPreferences("SETTINGS", MODE_PRIVATE).edit();
                editor.putBoolean("SOUND", isChecked);
                editor.apply();

                Constants.sound = isChecked;
            }
        });


        if(Constants.ads){
            showAd();
            prepareFullAd();
        }

        //showing best score in menu
        SharedPreferences prefs = getSharedPreferences("BEST_SCORE", MODE_PRIVATE);
        int bestScore = prefs.getInt("BEST", 0);
        bestScoreMenu.setText(bestScore + "");

/*
        try {
            backMusic = MediaPlayer.create(MainGame.this,R.raw.back1);
            backMusic.setLooping(true);
            backMusic.start();

        }catch (Exception e){
            e.printStackTrace();
        }
*/



        animateBackground();

        animateBird();
    }

    private void animateBird() {

        final Animation anim_trans  = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate);

        anim_trans.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Random rand = new Random();
                int y = rand.nextInt(maxHeight) + 1;
                int size = rand.nextInt(sizeMax) + sizeMin;
                AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
                        size,
                        size, 0, y
                );
                imageViewBird.setLayoutParams(params);
                Log.e("repeated", "REPEATED");
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                Log.e("repeated","REPEATED");

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                Random rand = new Random();
                int y = rand.nextInt(maxHeight) + 1;
                int size = rand.nextInt(((Width*10)/100)) + sizeMin;
                AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
                        size,
                        size, 0, y
                );
                imageViewBird.setLayoutParams(params);
                Log.e("repeated", "REPEATED");

            }

        });
        imageViewBird.startAnimation(anim_trans);


    }

    public void prepareParse(){

        try {
            Parse.enableLocalDatastore(this);
            Parse.initialize(this, "8Lzk1kR5yBZLDiqMvJ5v7IFbqC5ci7HgNyI9PKjz", "6u7EFOmVo7QppSm1ZnHx6IXDt93E8VN6rq5GZbzZ");
            ParseAnalytics.trackAppOpenedInBackground(getIntent());
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public void saveInParse(){
        String locale = this.getResources().getConfiguration().locale.getDisplayCountry();

        String model = Build.MODEL + " " + Build.DEVICE;

        SharedPreferences prefs = getSharedPreferences("BEST_SCORE", MODE_PRIVATE);

        int bestScore = prefs.getInt("BEST", 0);
        int nbrPlayed = prefs.getInt("NBR_PLAYED", 0);


        ParseObject gameScore = new ParseObject("Played");
        gameScore.put("country", locale);
        gameScore.put("nbrPlayed", nbrPlayed);
        gameScore.put("bestScore", bestScore);
        gameScore.put("phone",model);

        gameScore.saveInBackground();

    }

    public void prepareFullAd(){
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-4795862040225505/6837740272");

        //test ca-app-pub-3940256099942544/1033173712
       // ca-app-pub-4795862040225505/6837740272
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                shouldInit = true;
            }
        });

        requestNewInterstitial();
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
              // .addTestDevice("29C1E8B75B372E206D5241015E1FF4B4")
                .build();


        mInterstitialAd.loadAd(adRequest);
    }

    public void showAd(){

    adHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            mAdView = (AdView) findViewById(R.id.adView);
            adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
            return true;
        }
    });
        adHandler.sendEmptyMessageDelayed(0, 1000);

    }

    public void getSettings (){
      //  SharedPreferences.Editor editor = getSharedPreferences("SETTINGS", MODE_PRIVATE).edit();

        SharedPreferences prefs = getSharedPreferences("SETTINGS", MODE_PRIVATE);
        Constants.vibration = prefs.getBoolean("VIBRATION",true);
        Constants.sound = prefs.getBoolean("SOUND",true);

        vibrationCheckBox.setChecked( Constants.vibration);
        soundCheckBox.setChecked( Constants.sound);

         prefs = getSharedPreferences("BEST_SCORE", MODE_PRIVATE);

        bestScoreTextViewSettings.setText(prefs.getInt("BEST",0)+"");
        gamesPlayedTextView.setText(prefs.getInt("NBR_PLAYED", 0) + "");

    }

    boolean pauseClicked = false;
    private void showPause() {
        if(!pauseClicked) {
            gameLayout.setVisibility(View.GONE);
            timer.cancel();

            pauseClicked = true;
            changeBG(true);

            final Animation anim_in  = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_in);

            anim_in.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    pauseText.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            pauseText.startAnimation(anim_in);

        }else {


            pauseClicked = false;
            changeBG(false);

            final Animation anim_out  = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_out_big);

            anim_out.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    gameLayout.setVisibility(View.VISIBLE);
                    pauseText.setVisibility(View.GONE);
                    startTimer((int) timeToFinish);

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            pauseText.startAnimation(anim_out);
        }



    }

    public void startTimer(int time){

        final int[] min = {0};
        final int[] sec = {0};


        timer = new CountDownTimer(time*1000, 1000) {

            public void onTick(long millisUntilFinished) {
                timeToFinish = millisUntilFinished/1000;

                min[0] = (int)millisUntilFinished/60000;
                sec[0] = (int) (millisUntilFinished/1000) -(min[0]*60);
                if(sec[0]<10)
                    timeText.setText(String.format("%s0%d:0%d", getString(R.string.timetext), min[0], sec[0]));
                else
                    timeText.setText(String.format("%s0%d:%d", getString(R.string.timetext), min[0], sec[0]));

              //  timeText.setText(String.format("%s%d", getString(R.string.timetext), millisUntilFinished / 1000));

            }

            public void onFinish() {

                /*
                restartLayout.setVisibility(View.VISIBLE);
                gameLayout.setVisibility(View.GONE);
*/

                changeBG(true);
                showRestart(getApplicationContext());
                finalScore.setText(score + "");
                timeText.setText("FINISHED");
                pauseButton.setVisibility(View.GONE);
                pauseText.setVisibility(View.GONE);


                SharedPreferences prefs = getSharedPreferences("BEST_SCORE", MODE_PRIVATE);
                int bestScore = prefs.getInt("BEST" , 0);

                if(bestScore<score) {
                    bestTextView.setText(R.string.newhighscore);
                    SharedPreferences.Editor editor = getSharedPreferences("BEST_SCORE", MODE_PRIVATE).edit();
                    editor.putInt("BEST", score);
                    editor.apply();

                    bestScoreRestart.setText(""+score);
                }
                else{
                    bestScoreRestart.setText(""+bestScore);
                    bestTextView.setText("BEST");
                }



            }
        };

        timer.start();



    }
    public void swiped( int direction,int animation) {

        int answer = currentBird.getAnswer();

        if( direction == answer ){
            updateScore();
           showCorrectOrFlase(this, imageViewAnswer, true);
            addNewImage(animation);


        }else{

            corrects = 0;
            coef = 1;
            if(Constants.vibration) {
                Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(200);
            }

            showCorrectOrFlase(this, imageViewAnswer, false);
            addNewImage(animation);

            if(Constants.sound)
                 soundEffect(false,false);


        }

        if(shouldInit){
            init();
            shouldInit = false;
        }


    }

    public void soundEffect(boolean answer , boolean bonus){

        if(answer && !bonus)
            mediaPlayer = MediaPlayer.create(MainGame.this,R.raw.swipe);
        else if(answer && bonus)
            mediaPlayer = MediaPlayer.create(MainGame.this,R.raw.bonus);
        else if( !answer)
            mediaPlayer = MediaPlayer.create(MainGame.this,R.raw.wrong);

        mediaPlayer.setVolume(0.5f, 0.5f);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
                mediaPlayer = null;
            }
        });

        mediaPlayer.start();

    }


    Handler mHandler;
    public void animateBackground(){


        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {


                Random random = new Random();

                ColorDrawable d =(ColorDrawable) animateBG.getBackground();

                int color =d.getColor();

                ObjectAnimator objectAnimator = ObjectAnimator.ofObject(animateBG,
                        "backgroundColor",
                        new ArgbEvaluator(),
                        color,
                        Color.argb(100, random.nextInt(256), random.nextInt(256), random.nextInt(256)));



                objectAnimator.setDuration(Constants.changeColorDuration);
                objectAnimator.start();


                // absoluteLayout.setBackgroundColor(Color.rgb(random.nextInt(256),random.nextInt(256),random.nextInt(256)));
                mHandler = new Handler();
                mHandler.postDelayed(this, Constants.changeColorDuration);


            }

        });
    }
    public void updateScore(){


        score+= Constants.score * coef;
        scoreText.setText(String.format("%s%d", getString(R.string.scoretext), score));
        corrects++;

       // saveScore(score);

        if(corrects==4){
            corrects =0;
            coef++;
            showBonus(this, scoreBonus, coef);

            if(Constants.sound)
              soundEffect(true,true);
        }else{
            showCorrectOrFlase(this, imageViewAnswer, true);
            if(Constants.sound)
             soundEffect(true, false);

        }
    }

    public ArrayList<Bird> getAllImages(){

        ArrayList<Bird> list = new ArrayList<>();
       // imagesResources = new int[]{R.drawable.flesh1left, R.drawable.flesh2right};


        for(int i = 0 ; i<imagesResources.length ; i++){

            if(imagesResources[i] > 0) {
                Bird b = new Bird(imagesResources[i], this);
                list.add(b);
            }
        }

        return list;

    }

    public void init(){

        WindowManager mWinMgr = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Width = mWinMgr.getDefaultDisplay().getWidth();
        maxWidth = Width -((Width*55)/100);
        Height = mWinMgr.getDefaultDisplay().getHeight();
        maxHeight = Height -((Height*55)/100);
        sizeMax =((Width*55)/100);
        sizeMin = ((Width*7)/100);

        imagesResources = getImgIds();

        birdList = getAllImages();

// Hide the status bar.


            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

            View decorView = getWindow().getDecorView();
            int uiOptions =  View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);

        getImgIds();

    }

    public int[] getImgIds(){
        String name ;
        Field[] ID_Fields = R.drawable.class.getFields();
        int[] resArray = new int[ID_Fields.length];
        for(int i = 0; i < ID_Fields.length; i++) {
            try {
                name = ID_Fields[i].getName();
                if(!name.contains("_") && (name.contains("right") || name.contains("left") || name.contains("up") || name.contains("down") )) {

                     resArray[i] = ID_Fields[i].getInt(null);
                    Log.e("files", resArray[i]+"");
                }
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return resArray;
    }

    public void addNewImage(int animation){

        Random rand = new Random();

        int x = rand.nextInt(maxWidth) + 1;
        int y = rand.nextInt(maxHeight) + 1;

        Bird bird = birdList.get(rand.nextInt(birdList.size()));
        currentBird = bird;

        //int size = rand.nextInt(sizeMax) + sizeMin;

        int size = sizeMax;
        AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
                size,
                size, x, y
        );

       // imageView.setImageResource(bird.getId());


        ImageViewAnimatedChange(this, imageView, bird.getId(), params, animation);

    }

    public static void ImageViewAnimatedChange(Context c, final ImageView v, final int id ,final AbsoluteLayout.LayoutParams params,int animation) {
        final Animation anim_out = AnimationUtils.loadAnimation(c, animation);
        final Animation anim_in  = AnimationUtils.loadAnimation(c, android.R.anim.slide_in_left);

        //android.R.anim.slide_in_left
        anim_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                v.setImageResource(id);
                v.setLayoutParams(params);
                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                    }
                });
                v.startAnimation(anim_in);
            }
        });
        v.startAnimation(anim_out);
    }

    public static void showCorrectOrFlase(Context c, final ImageView v, final boolean answer) {
        final Animation anim_out = AnimationUtils.loadAnimation(c, R.anim.scale_out);
        final Animation anim_in  = AnimationUtils.loadAnimation(c, R.anim.scale_in);

        //android.R.anim.slide_in_left
        anim_in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                if(answer)
                    v.setImageResource(R.drawable.correct);
                else
                    v.setImageResource(R.drawable.wrong);

                v.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {


                // v.setLayoutParams(params);
                anim_out.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        v.setVisibility(View.INVISIBLE);
                    }
                });



                v.startAnimation(anim_out);
            }
        });
        v.setVisibility(View.VISIBLE);
        v.startAnimation(anim_in);
    }


    public static void showBonus(Context c, final TextView v, final int number) {

        final Animation anim_out = AnimationUtils.loadAnimation(c, R.anim.scale_out);
        final Animation anim_in  = AnimationUtils.loadAnimation(c, R.anim.scale_in);


        anim_in.setDuration( c.getResources().getInteger(R.integer.animationSpeedLow));
        anim_out.setDuration( c.getResources().getInteger(R.integer.animationSpeedLow));
        //android.R.anim.slide_in_left
        anim_in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

               v.setText("X"+number);
                v.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {


                // v.setLayoutParams(params);
                anim_out.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        v.setVisibility(View.INVISIBLE);
                    }
                });



                v.startAnimation(anim_out);
            }
        });
        v.setVisibility(View.VISIBLE);
        v.startAnimation(anim_in);
    }

    public void showGame(Context c){
        final Animation anim_out = AnimationUtils.loadAnimation(c, R.anim.slide_out_left);
        final Animation anim_in  = AnimationUtils.loadAnimation(c, R.anim.slide_in_right);


        anim_in.setDuration( c.getResources().getInteger(R.integer.animationSpeedLow));
        anim_out.setDuration( c.getResources().getInteger(R.integer.animationSpeedLow));

        anim_in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                gameLayout.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {


                // v.setLayoutParams(params);

            }
        });

        anim_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                menuLayout.setVisibility(View.GONE);
            }
        });



        menuLayout.startAnimation(anim_out);

        gameLayout.setVisibility(View.VISIBLE);
        gameLayout.startAnimation(anim_in);

    }


    public void showRestart(Context c){
        final Animation anim_out = AnimationUtils.loadAnimation(c, R.anim.slide_out_left);
        final Animation anim_in  = AnimationUtils.loadAnimation(c, R.anim.slide_in_right);

        anim_in.setDuration( c.getResources().getInteger(R.integer.animationSpeedLow));
        anim_out.setDuration( c.getResources().getInteger(R.integer.animationSpeedLow));

        anim_in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                restartLayout.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {


                // v.setLayoutParams(params);

            }
        });

        anim_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                gameLayout.setVisibility(View.GONE);


                Random rand = new Random();
                int ok = rand.nextInt(9) + 1;

                if(Constants.ads) {
                    try {
                        if (mInterstitialAd != null && mInterstitialAd.isLoaded() && (ok > 5)) {
                            mInterstitialAd.show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        gameLayout.startAnimation(anim_out);

        restartLayout.setVisibility(View.VISIBLE);
        restartLayout.startAnimation(anim_in);

    }

    public void showGameFromRestart(Context c){
        final Animation anim_out = AnimationUtils.loadAnimation(c, R.anim.slide_out_right);
        final Animation anim_in  = AnimationUtils.loadAnimation(c, android.R.anim.slide_in_left);


        anim_in.setDuration( c.getResources().getInteger(R.integer.animationSpeedLow));
        anim_out.setDuration( c.getResources().getInteger(R.integer.animationSpeedLow));

        anim_in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                gameLayout.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {


                // v.setLayoutParams(params);

            }
        });

        anim_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                restartLayout.setVisibility(View.GONE);
            }
        });



        restartLayout.startAnimation(anim_out);

        gameLayout.setVisibility(View.VISIBLE);
        gameLayout.startAnimation(anim_in);

    }


    public void showSettings(Context c, final RelativeLayout layout){

        final Animation anim_out = AnimationUtils.loadAnimation(c, R.anim.slide_out_left);
        final Animation anim_in  = AnimationUtils.loadAnimation(c, R.anim.slide_in_right);


        anim_in.setDuration( c.getResources().getInteger(R.integer.animationSpeedLow));
        anim_out.setDuration( c.getResources().getInteger(R.integer.animationSpeedLow));


        anim_in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                settingsLayout.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }
        });

        anim_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                layout.setVisibility(View.GONE);
            }
        });


        layout.startAnimation(anim_out);

        settingsLayout.setVisibility(View.VISIBLE);
        settingsLayout.startAnimation(anim_in);

    }


    public void showMenu(Context c, final RelativeLayout layout){

        final Animation anim_out = AnimationUtils.loadAnimation(c, R.anim.slide_out_right);
        final Animation anim_in  = AnimationUtils.loadAnimation(c, android.R.anim.slide_in_left);


        anim_in.setDuration( c.getResources().getInteger(R.integer.animationSpeedLow));
        anim_out.setDuration( c.getResources().getInteger(R.integer.animationSpeedLow));

        anim_in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                layout.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }
        });

        anim_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                settingsLayout.setVisibility(View.GONE);
            }
        });



        settingsLayout.startAnimation(anim_out);

        layout.setVisibility(View.VISIBLE);
        layout.startAnimation(anim_in);

    }

   public void initGame(){

       score = 0;
       corrects = 0;
       coef = 1;

       scoreText.setText(String.format("%s%d", getString(R.string.scoretext), score));


   }


    @Override
    protected void onResume() {
        super.onResume();
        init();

        if(mAdView != null)
            mAdView.resume();


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
    }


    public void  changeBG ( boolean reverse){

        TransitionDrawable transition = (TransitionDrawable) bgLayout.getBackground();
        if(!reverse)
             transition.startTransition(Constants.blurAnimationSpeed);
        else
            transition.reverseTransition(Constants.blurAnimationSpeed);

    }


    @Override
    protected void onPause() {
        super.onPause();
        shouldInit = true;
        if(mediaPlayer!=null) {
            mediaPlayer.release();

            mediaPlayer = null;
        }

        if(mAdView != null)
            mAdView.pause();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mediaPlayer!=null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        if(adHandler!=null)
            adHandler.removeCallbacksAndMessages(null);
        if(mAdView != null)
            mAdView.destroy();
    }


    public void saveScore(int score){
        SortedList<Integer> scoreList = new SortedList<>();




        int max = 0 ;
        int min = 0 ;
        //getting
        SharedPreferences prefs = getSharedPreferences("BEST_SCORES", MODE_PRIVATE);
        for(int i = 0 ; i<5 ; i++) {
            int value = prefs.getInt(""+(i+1),0);


            scoreList.add(value);
        }
        scoreList.add(800);
        scoreList.add(500);
        max = Collections.max(scoreList);
        min = Collections.min(scoreList);

        scoreList.add(score);

/*
        for(int i = 0 ; i<5 ; i++) {
            int value = scoreList.get(i);
            if( score<min)
                break;

            if(value > max){
                scoreList.add(score , 4);
            }


            if(value>)
        }
*/

        // saving
        SharedPreferences.Editor editor = getSharedPreferences("BEST_SCORES", MODE_PRIVATE).edit();
        int i = 0;
        for (int value : scoreList) {
            editor.putInt(""+(i+1), value);
            i++;
        }
        editor.apply();



    }

}
