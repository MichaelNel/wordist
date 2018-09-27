package com.mike.WordistHangman;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


public class Home extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button button = (Button) findViewById(R.id.button);
        button.getBackground().setColorFilter(0xFFFFFFFF, PorterDuff.Mode.MULTIPLY);

    }

    /* Executes when the user clicks on challenge */
    public void start_message(View view) {
        Intent intent = new Intent(this, DisplayMessage.class);
        startActivity(intent);
    }

    public void start_two_player_mode(View view) {
        Intent intent = new Intent(this, TwoPlayerHangman.class);
        startActivity(intent);
    }

}
