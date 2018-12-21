package com.element.lucy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;
import com.hanks.htextview.HTextView;

import java.util.Locale;

import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIDataService;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import pl.droidsonroids.gif.GifImageView;

import static android.Manifest.permission.RECORD_AUDIO;

/**
 * This code is in no manner open for anyone to use. The use of this code
 * in any manner anywhere is governed by Shreyas Hosmani (yours truly)
 * for all eternity.
 **/

public class MainActivity extends AppCompatActivity {

    // Object Declarations

    TextView usertv, lucytv;
    EditText textLucyInput;
    HTextView voice;
    Button positiveButton, negativeButton, textInputCancelButton, textInputDoneButton;
    FloatingActionButton listen;
    ImageView lucyLogo;
    GifImageView cover;
    Animation fadeInBackground, fadeOutBackground, fadeInQuick, fadeOutQuick, fadeOutQuick2;
    AIConfiguration config;
    AIService aiService;
    String lucy_text, user_text;
    Typeface ubuntu_r;
    TextToSpeech tts;
    Handler handler;
    Vibrator vibrator;

    LinearLayout buttonContainer;

    // Variable Declarations

    int actionCode, USER_TV, LUCY_TV;
    boolean isListening, textInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("");

        // View Bindings

        usertv = (TextView) findViewById(R.id.usertv);
        lucytv = (TextView) findViewById(R.id.lucytv);

        voice = (HTextView) findViewById(R.id.voice);

        positiveButton = (Button) findViewById(R.id.positiveButton);
        negativeButton = (Button) findViewById(R.id.negativeButton);

        listen = (FloatingActionButton) findViewById(R.id.listen);

        cover = (GifImageView) findViewById(R.id.cover);

        lucyLogo = (ImageView) findViewById(R.id.lucyLogo);

        buttonContainer = (LinearLayout) findViewById(R.id.buttonContainer);

        // Object Initialization

        fadeInBackground = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_background);
        fadeOutBackground = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out_background);
        fadeInQuick = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_quick);
        fadeOutQuick = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out_quick);
        fadeOutQuick2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out_quick);

        /* Display Custom Toasts after a 1 second delay */

        final Activity activity = this;

        // Tell user how to use text mode

        voice.animateText("Tap here to use text");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                // Display Custom Toast

                SuperActivityToast.create(activity, new Style(), Style.TYPE_STANDARD)
                        .setText("Long Press the mic button to know about Lucy's developers.")
                        .setDuration(Style.DURATION_SHORT)
                        .setFrame(Style.FRAME_STANDARD)
                        .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_DEEP_ORANGE))
                        .setAnimations(Style.ANIMATIONS_POP).show();
                voice.animateText("Command Me Sir");

            }
        }, 3000);

        config = new AIConfiguration("d06e80cfe9594157b875ad58ab30b31b",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(getApplicationContext(), config);

        ubuntu_r = Typeface.createFromAsset(getApplicationContext().getAssets(), "ubuntu_r.ttf");

        handler = new Handler();

        vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);



        // Variable Initialization

        actionCode = 0;
        USER_TV = 0;
        LUCY_TV = 1;

        lucy_text = null;
        user_text = null;

        isListening = false;
        textInput = false;



        // Set font for TextViews 'usertv' and 'lucytv'

        usertv.setTypeface(ubuntu_r);
        lucytv.setTypeface(ubuntu_r);

        // Greet user

        changeText(getResources().getString(R.string.default_greeting), USER_TV);

        // Manage permissions

        if(ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
        {

            ActivityCompat.requestPermissions(this,
                    new String[]{RECORD_AUDIO}, 0);

        }

        lucyLogo.startAnimation(fadeInBackground);
        fadeInBackground.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                lucyLogo.startAnimation(fadeOutBackground);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fadeOutBackground.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                lucyLogo.startAnimation(fadeInBackground);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        fadeOutQuick.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                lucytv.setText(lucy_text);
                lucytv.startAnimation(fadeInQuick);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        fadeOutQuick2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                usertv.setText(user_text);
                usertv.startAnimation(fadeInQuick);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });




        // AI Listener

        aiService.setListener(new AIListener() {
            @Override
            public void onResult(AIResponse result) {

                if(result.getStatus().getCode()!=200)
                {

                    changeText("Something's not right. Try again in a little bit", LUCY_TV);
                    GlobalClass.logError("API.AI Error: " + result.getStatus().getCode() + ": " + result.getStatus().getErrorType(), getApplicationContext());

                }

                else
                {
                    changeText(result.getResult().getResolvedQuery().toString(), USER_TV);
                    if (!result.getResult().getFulfillment().getSpeech().toString().isEmpty())
                    {

                        changeText(result.getResult().getFulfillment().getSpeech().toString(), LUCY_TV);
                        speak(result.getResult().getFulfillment().getSpeech().toString());

                    }

                }

            }

            @Override
            public void onError(AIError error) {

                GlobalClass.logError(error.toString(), getApplicationContext());
                changeText("Something's not right. Try again in a little bit", LUCY_TV);
                voice.animateText("Could you say that again?");
                isListening = false;

            }

            @Override
            public void onAudioLevel(float level) {

            }

            @Override
            public void onListeningStarted() {

                voice.animateText("Now listening...");
                isListening = true;

            }

            @Override
            public void onListeningCanceled() {

                isListening = false;

                voice.animateText("Could you say that again?");
                isListening = false;

            }

            @Override
            public void onListeningFinished() {

                voice.animateText("Got you!");
                isListening = false;

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        voice.animateText("Say something...");

                    }
                }, 1000);

            }
        });


        /* Click listeners for UI elements */

        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if(isListening)
                    {

                        aiService.cancel();
                        isListening = false;

                    }

                    else
                    {

                        aiService.startListening();
                        isListening = true;

                    }

                }
        });

        listen.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                vibrator.vibrate(100);

                startActivity(new Intent(MainActivity.this, DevelopersActivity.class));
                finish();

                return false;
            }
        });

        voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /* The following chunk of code inflates a custom dialog box to allow
                 * people to text lucy instead of using the default voice feature */

                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                final View textOnyx = inflater.inflate(R.layout.text_input, null);
                AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
                ab.setView(textOnyx);
                ab.setCancelable(false);
                ab.create();
                final AlertDialog show = ab.show();
                show.setCancelable(true);
                textInputCancelButton = (Button) show.findViewById(R.id.textInputCancelButton);
                textInputDoneButton = (Button) show.findViewById(R.id.textInputDoneButton);
                textLucyInput = (EditText) show.findViewById(R.id.textInput);
                textLucyInput.requestFocus();
                textInputCancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // Dismiss dialog box

                        show.dismiss();

                        /* The following code will enable immersive mode for the splash screen
                         * for devices running on Android 3.0 Honeycomb or higher. This will effectively
                         * enable immersive mode for all of the app's instances as the app is only compatible
                         * with devices running on Android 6.0 Marshmallow or higher */

                        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB) {
                            View decorView = getWindow().getDecorView();
                            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                        }

                    }
                });
                textInputDoneButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(!textLucyInput.getText().toString().isEmpty())
                        {

                            // Dismiss the dialog box

                            show.dismiss();

                            /* The following code will enable immersive mode for the splash screen
                             * for devices running on Android 3.0 Honeycomb or higher. This will effectively
                             * enable immersive mode for all of the app's instances as the app is only compatible
                             * with devices running on Android 6.0 Marshmallow or higher */

                            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB) {
                                View decorView = getWindow().getDecorView();
                                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                            }

                            // Initialize api.ai to handle text requests

                            final AIConfiguration textAIconfig = new AIConfiguration("d06e80cfe9594157b875ad58ab30b31b",
                                    AIConfiguration.SupportedLanguages.English,
                                    AIConfiguration.RecognitionEngine.System);
                            final AIDataService textAIDataService = new AIDataService(getApplicationContext(), textAIconfig);
                            final AIRequest textAIRequest = new AIRequest();
                            textAIRequest.setQuery(textLucyInput.getText().toString());



                        }
                        else
                        {

                            // Display Custom Toast

                            SuperActivityToast.create(activity, new Style(), Style.TYPE_STANDARD)
                                    .setText("You have to say something for Lucy to respond.")
                                    .setDuration(Style.DURATION_SHORT)
                                    .setFrame(Style.FRAME_STANDARD)
                                    .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_DEEP_ORANGE))
                                    .setAnimations(Style.ANIMATIONS_POP).show();

                        }

                    }
                });

            }
        });

    }

    @Override
    protected void onResume() {

        /* The following code will enable immersive mode for the splash screen
         * for devices running on Android 3.0 Honeycomb or higher. This will effectively
         * enable immersive mode for all of the app's instances as the app is only compatible
         * with devices running on Android 6.0 Marshmallow or higher */

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

        // Animate activity transition

        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        // Initialize TTS engine

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    int result=tts.setLanguage(Locale.US);
                    if(result==TextToSpeech.LANG_MISSING_DATA ||
                            result==TextToSpeech.LANG_NOT_SUPPORTED){
                        GlobalClass.logError("TTS Error: Unsupported Language", getApplicationContext());
                    }
                }
                else
                {

                    GlobalClass.logError("TTS Error: Initialization Failed", getApplicationContext());

                }
            }
        });

        super.onResume();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub

        // Shut down TTS service

        if(tts != null){

            tts.stop();
            tts.shutdown();
        }
        super.onPause();
    }

    // Permission Handling

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {

            case 0:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // Permission Granted. Do Nothing.
                }
                else
                {

                    Toast.makeText(getApplicationContext(), "Lucy will not function as expected if you " +
                            "do not grant it the necessary permissions",Toast.LENGTH_LONG).show();

                }

        }
    }


    // Private Methods

    /* The following method will change the values of 'usertv' and 'lucytv' where
     * '0' is 'usertv' and
     * '1' is 'lucytv' */

    private void changeText(String t, int ti)
    {

        switch (ti)
        {

            case 0: user_text = t;
                    usertv.startAnimation(fadeOutQuick2);
                    break;
            case 1: lucy_text = t;
                    lucytv.startAnimation(fadeOutQuick);
                    break;

        }
        return;

    }

    /* The following method will use TTS to speak any given string */

    private void speak(String speechText)
    {

        // Format TTS output

        if(speechText.contains("0's")||speechText.contains("1's"))
        {

            speechText.replace("0's", "zeroes");
            speechText.replace("1's", "ones");

        }

        // Speak 'speechText'

        tts.speak(speechText, TextToSpeech.QUEUE_FLUSH, null);
        return;

    }



    /* The following method will reset all API.AI contexts */

    private void resetAllApiAiContexts()
    {

        // Reset all API.AI contexts on a separate thread (network operation)

        new Thread(new Runnable() {
            @Override
            public void run() {

                try
                {

                    aiService.resetContexts();

                }
                catch (Exception e)
                {

                    e.printStackTrace();
                    GlobalClass.logError(e.toString(), getApplicationContext());

                }

            }
        }).start();

    }

}
