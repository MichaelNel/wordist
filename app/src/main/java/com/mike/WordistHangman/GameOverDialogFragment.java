package com.mike.WordistHangman;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by mike on 22/06/15.
 */
public class GameOverDialogFragment extends DialogFragment {
    String dialog_message = "";
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        super.onCreateDialog(savedInstanceState);

        StringBuilder dialog_message = new StringBuilder();
        dialog_message.append("SCORE: ");
        dialog_message = dialog_message.append(getArguments().getString("score"));
        String message = dialog_message.toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setPositiveButton("Play again", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Intent intent = getActivity().getIntent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        getActivity().overridePendingTransition(0, 0);
                        getActivity().finish();

                        getActivity().overridePendingTransition(0, 0);
                        startActivity(intent);
                    }
                })
        .setNeutralButton("Leaderboards", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ((DisplayMessage) getActivity()).request_leaderboard();
            }
        }).setTitle("GAME OVER")
        .setNegativeButton("Exit to Menu", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = getActivity().getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                getActivity().overridePendingTransition(0, 0);
                getActivity().finish();
            }
        });
        // Create the AlertDialog object and return it
        return builder.create();
    }


}
