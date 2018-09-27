package com.mike.WordistHangman;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.mike.WordistHangman.R;

/**
 * Created by mike on 22/06/15.
 */
public class TwoPlayerHangman extends ActionBarActivity{
    TextView guess_word;
    TextView guessed_letters;
    TextView display_score;
    TextView display_life;
    String user_word;
    String current_word;
    int score;
    int lives;
    Boolean keyboard = false;
    Boolean done = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_player);

        guess_word = (TextView) findViewById(R.id.letters_view);
        guessed_letters = (TextView) findViewById(R.id.guessed_letters_view);
        display_score = (TextView) findViewById(R.id.score_view);
        display_life = (TextView) findViewById(R.id.life_view);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        /* Display the blanked out characters
         * TODO: Get random word
         * DONE: Set dashes or w/e to the length of the word. */
        score = 0;
        lives = 4;

        guess_word.setTextSize(30);
        guess_word.setText("");

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
        hideSoftKeyboard(TwoPlayerHangman.this);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.i("key pressed", String.valueOf(event.getKeyCode()));

        int key_no = event.getKeyCode();
        int key_unicode = event.getUnicodeChar(event.getMetaState());
        char key_character = (char) key_unicode;
        key_character = Character.toLowerCase(key_character);

        int key_action = event.getAction();

        if(lives > 0 && done == true) {
            if (key_action == KeyEvent.ACTION_DOWN) {
                if ((int) key_character > 96 && (int) key_character < 123) {
                    // If the key pressed is a letter of the alphabet

                    if (!guessed_letters.getText().toString().contains(Character.toString(key_character))) {
                        if (!user_word.contains(Character.toString(key_character))) {
                            lives -= 1;
                            display_life.setText(String.valueOf(lives));
                            guessed_letters.append(" " + key_character);
                        }
                        if (!current_word.contains(Character.toString(key_character))
                                && user_word.contains(Character.toString(key_character))) {
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
        } else if(done == false){
            if (key_action == KeyEvent.ACTION_DOWN) {
                if(key_no == KeyEvent.KEYCODE_DEL){
                    String word = removeLastChar(guess_word.getText().toString());
                    guess_word.setText(word);
                }else {
                    guess_word.append("" + key_character);
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    public void guess_letter(char letter) {
        StringBuilder current_word_builder = new StringBuilder();
        char rand_word_letter;

        int i;
        for (i = 0; i < user_word.length(); i++) {
            rand_word_letter = user_word.charAt(i);

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
        }
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

    public void done_onClick(View view){
        done = true;
        user_word = guess_word.getText().toString();
        hide_word(user_word);

        TextView enter_word_message = (TextView) findViewById(R.id.textView4);
        Button done_button = (Button) findViewById(R.id.button3);

        enter_word_message.setVisibility(View.GONE);
        done_button.setVisibility(View.GONE);
    }

    public void new_game_onClick(View view){
        new_game();
    }

    public void new_game() {
        this.recreate();
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

    public void toggle_soft_keyboard(View view) {
        if (keyboard) {
            hideSoftKeyboard(TwoPlayerHangman.this);
            keyboard = false;
        } else {
            InputMethodManager keyboard_manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard_manager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            keyboard = true;
        }
    }

    public void game_over(){
        DialogFragment new_fragment = new GameOverDialogFragment();

        Bundle message = new Bundle();

        String end_score = Integer.toString(score);
        message.putString("score", end_score);

        new_fragment.setArguments(message);
        new_fragment.show(getFragmentManager(), "missiles");

        new_game();
    }
    private static String removeLastChar(String str) {
        return str.substring(0,str.length()-1);
    }
}