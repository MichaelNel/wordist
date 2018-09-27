package com.mike.WordistHangman;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.mike.WordistHangman.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

public class DisplayMessage extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener  {
    TextView guess_word;
    TextView guessed_letters;
    TextView display_score;
    TextView display_life;
    String[] rand_word;
    String current_word;
    int score;
    int lives;
    Boolean keyboard = true;
    int final_score;

    // Client used to interact with Google APIs
    private GoogleApiClient mGoogleApiClient;

    // Are we currently resolving a connection failure?
    private boolean mResolvingConnectionFailure = false;

    // Has the user clicked the sign-in button?
    private boolean mSignInClicked = false;

    // Automatically start the sign-in flow when the Activity starts
    private boolean mAutoStartSignInFlow = true;

    // request codes we use when invoking an external activity
    private static final int RC_RESOLVE = 5000;
    private static final int RC_UNUSED = 5001;
    private static final int RC_SIGN_IN = 9001;

    // tag for debug logging
    final boolean ENABLE_DEBUG = true;
    final String TAG = "TanC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        // Create the Google Api Client with access to the Play Games services
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                        // add other APIs and scopes here as needed
                .build();

        guess_word = (TextView) findViewById(R.id.letters_view);
        guessed_letters = (TextView) findViewById(R.id.guessed_letters_view);
        display_score = (TextView) findViewById(R.id.score_view);
        display_life = (TextView) findViewById(R.id.life_view);

        Button next_button = (Button) findViewById(R.id.next_word);
        next_button.setVisibility(View.INVISIBLE);


        score = 0;
        lives = 15;

        rand_word = get_rand_word();
        hide_word(rand_word[0]);

        display_life.setTextSize(30);
        display_life.setText(String.valueOf(lives));

        display_score.setTextSize(30);
        display_score.setText(String.valueOf(score));

        InputMethodManager keyboard_manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard_manager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        hideSoftKeyboard(DisplayMessage.this);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.i("key pressed", String.valueOf(event.getKeyCode()));

        int key_action = event.getAction();

        if(lives > 0) {
            if (key_action == KeyEvent.ACTION_DOWN) {
                int key_no = event.getKeyCode();
                int key_unicode = event.getUnicodeChar(event.getMetaState());
                char key_character = (char) key_unicode;
                key_character = Character.toLowerCase(key_character);

                if ((int) key_character > 96 && (int) key_character < 123) {
                    // If the key pressed is a letter of the alphabet

                    if (!guessed_letters.getText().toString().contains(Character.toString(key_character))) {
                        if (!rand_word[0].contains(Character.toString(key_character))) {
                            lives -= 1;
                            display_life.setText(String.valueOf(lives));
                            guessed_letters.append(" " + key_character);
                        }
                        if (!current_word.contains(Character.toString(key_character))
                                && rand_word[0].contains(Character.toString(key_character))) {
                            score += 20;
                            display_score.setText(String.valueOf(score));
                        }
                        guess_letter(key_character);

                        if (lives == 0) {
                            game_over();
                        }
                        System.out.println("DEBUG MESSAGE KEY=" + key_character + "KEY CODE=" + key_no);
                    }
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    public void guess_letter(char letter) {
        StringBuilder current_word_builder = new StringBuilder();
        char rand_word_letter;

        int i;
        for (i = 0; i < rand_word[0].length(); i++) {
            rand_word_letter = rand_word[0].charAt(i);

            if (rand_word_letter == letter) {
                current_word_builder.append(letter + " ");
            } else {
                current_word_builder.append(current_word.charAt(i * 2) + " ");
            }
        }
        current_word = current_word_builder.toString();
        guess_word.setText(current_word);

        // Word guessed with lives remaining
        if(!current_word.contains("_")){
            guessed_letters.setText("");

            Button next_button = (Button) findViewById(R.id.next_word);
            next_button.setVisibility(View.VISIBLE);
        }
    }

    public String[] get_rand_word() {
        // Returns a random word as a string
        int rand_int = get_rand_int(1, 998); // In future calculate lines to allow changes to dict.txt

        String[] word = {""};
        System.out.println("halp");
        int lines = 0;

        try {
            // Open the file
            InputStream fstream = getAssets().open("dict.txt");

            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;

            //Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                lines++;
                if (lines == rand_int) {
                    word = get_word_from_line(strLine);
                    break;
                }
                // Print the content on the console
                System.out.println(strLine);
            }

            //Close the input stream
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (word[0].length() > 14) {
            word = get_rand_word();
        }
        return word;
    }

    public int get_rand_int(int x, int y) {
        // Returns a random integer between two given values
        Random rand = new Random();
        int rand_int = rand.nextInt((y - x) + 1) + x;
        return rand_int;
    }

    public String[] get_word_from_line(String line) {
        // Takes in a line and returns the first word
        String arr[] = line.split(" ", 2);
        return arr;
    }

    public void hide_word(String word) {
        StringBuilder current_word_builder = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            current_word_builder.append("_ ");
        }
        guess_word.setTextSize(35);
        current_word = current_word_builder.toString();
        guess_word.setText(current_word);
    }

    public void new_game_onClick(View view){
        new_game();
    }

    public void new_game() {
        Button next = (Button) findViewById(R.id.next_word);
        next.setVisibility(View.INVISIBLE);

        score = 0;
        lives = 15;

        display_score.setText(String.valueOf(score));
        display_life.setText(String.valueOf(lives));

        rand_word = get_rand_word();
        hide_word(rand_word[0]);

        guessed_letters.setText("");
    }

    public void show_definition(View view) {
        // Hint button pressed
        score -= 10;
        display_score.setText(String.valueOf(score));

        DialogFragment new_fragment = new DefinitionDialogFragment();

        Bundle message = new Bundle();
        message.putString("message", rand_word[1]);

        new_fragment.setArguments(message);
        new_fragment.show(getFragmentManager(), "missiles");
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

    public void toggle_soft_keyboard(View view) {
        if (keyboard) {
            hideSoftKeyboard(DisplayMessage.this);
            keyboard = false;
        } else {
            InputMethodManager keyboard_manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard_manager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            keyboard = true;
        }
    }

    public void get_next_word(View view){
        Button next_button = (Button) findViewById(R.id.next_word);
        next_button.setVisibility(View.INVISIBLE);

        rand_word = get_rand_word();
        hide_word(rand_word[0]);
    }

    public void game_over(){

        DialogFragment new_fragment = new GameOverDialogFragment();

        Bundle message = new Bundle();

        String end_score = Integer.toString(score);
        final_score = score;
        message.putString("score", end_score);

        new_fragment.setArguments(message);
        new_fragment.show(getFragmentManager(), "missiles");

        new_game();
    }

    public void request_leaderboard(){
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
      }


    @Override
    protected void onStart() {
        super.onStart();
        //Log.d(TAG, "onStart(): connecting");
        //mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop(): disconnecting");
        if (mGoogleApiClient.isConnected()) {
            Games.signOut(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }
    }

    private boolean isSignedIn() {
        return (mGoogleApiClient != null && mGoogleApiClient.isConnected());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (resultCode == RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                BaseGameUtils.showActivityResultError(this, requestCode, resultCode, R.string.signin_other_error);
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected(): connected to Google APIs");
        //Submit highest score
        Games.Leaderboards.submitScore(mGoogleApiClient, getResources()
                .getString(R.string.leaderboardId), final_score);
        //Request leaderboard
        startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient,
                getResources().getString(R.string.leaderboardId)), 1);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended(): attempting to connect");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed(): attempting to resolve");
        if (mResolvingConnectionFailure) {
            Log.d(TAG, "onConnectionFailed(): already resolving");
            return;
        }

        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;
            if (!BaseGameUtils.resolveConnectionFailure(this, mGoogleApiClient, connectionResult,
                    RC_SIGN_IN, getString(R.string.signin_other_error))) {
                mResolvingConnectionFailure = false;
            }
        }

    }

    public void signInClicked(View view) {
        // start the sign-in flow
        mSignInClicked = true;
        mGoogleApiClient.connect();
    }

    public void signOutClicked(View view) {
        Log.d(TAG, "signOutClicked(View view): signing out");
        mSignInClicked = false;
        if (isSignedIn()) {
            Games.signOut(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }
    }

}
