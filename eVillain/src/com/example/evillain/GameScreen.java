package com.example.evillain;

import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameScreen extends Activity {

	public final static String PLAYER1_CHOICE = "com.example.villain.PLAYER1_CHOICE";
	public boolean PLAYER_TURN = true;
	public int DE2_COMMANDS[] = {5,10,0,1,13};
	public static int[] actions = {R.string.a0,
									R.string.a1,
									R.string.a2,
									R.string.a3,
									R.string.a4,
									R.string.a5,
									R.string.a6,
									R.string.a7,
									R.string.a8,
									R.string.a9,
									R.string.a10,
									R.string.a11,
									R.string.a12,
									R.string.a13,
									R.string.a14,
									R.string.a15,
									R.string.a16,
									R.string.a17,
									R.string.a18,
									R.string.a19,
									R.string.a20};
	/*
	 * Kris:
	 * playerChoices has 5 indexes temporarily, to have one index of buffer to allow for smooth transition.
	 */
	public int[] playerChoices = {20,20,20,20};
	public int turn_counter = 0;
	public int player_hp = 5;
//	public TextView cmdConsolep[] = {(TextView) findViewById(R.id.playerChoice),
//									(TextView) findViewById(R.id.otherPlayerChoice1),
//									(TextView) findViewById(R.id.otherPlayerChoice2),
//									};

			
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_screen);
		Intent intent = getIntent();
		String playername = intent.getExtras().getString(StartScreen.PLAYER_NAME);
		
		TextView t1 = (TextView) findViewById(R.id.playerName1);
		TextView t2=(TextView) findViewById(R.id.playerName2);
		TextView player_health=(TextView) findViewById(R.id.textView1);
		player_health.setText("PlayerHealth");

		Log.i(playername, "before TextView edit (GameScreen)"+t1.isInEditMode()+" " + t2.isInEditMode());

		t1.setText(playername);
		
		t2.setText(playername);
		Log.i(playername, "after TextView edit (GameScreen)"+t1.isInEditMode()+" " + t2.isInEditMode());
		
		Button b1 = (Button) findViewById(R.id.button1);
		Button b2 = (Button) findViewById(R.id.button2);
		Button b3 = (Button) findViewById(R.id.button3);
		Button b4 = (Button) findViewById(R.id.button4);
		Button b5 = (Button) findViewById(R.id.button5);
		
		b1.setText(actions[DE2_COMMANDS[0]]);
		b2.setText(actions[DE2_COMMANDS[1]]);
		b3.setText(actions[DE2_COMMANDS[2]]);
		b4.setText(actions[DE2_COMMANDS[3]]);
		b5.setText(actions[DE2_COMMANDS[4]]);
	}

/*	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	*/
	 @Override
    protected void onResume() {
    	super.onResume();
    	Log.i("MY_MESSAGE", "in onResume (GameScreen)");
    }
	    
    @Override
    protected void onPause() {
    	super.onPause();
    	Log.i("MY_MESSAGE", "in onPause (GameScreen)");
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	Log.i("MY_MESSAGE", "in onStop (GameScreen)");
    }	

    public void onClick(View v) {
        // 1) Possibly check for instance of first 
        Button b = (Button)v;
        String buttonText = b.getText().toString();
        TextView p1_choice = (TextView) findViewById(R.id.playerChoice);
        
        //Set the text to one of the actions stored in @string
        p1_choice.setText(buttonText);
    }
    
	public void sendButton(View view) {
		MyApplication app = (MyApplication) getApplication();
		
		// Get the message from the box
		
		Button b = (Button)view;
		int imgID = getResources().getIdentifier("action_button", "drawable", getApplication().getPackageName());
		b.setBackgroundResource(imgID);
		Button b1 = (Button) findViewById(R.id.button1);
		Button b2 = (Button) findViewById(R.id.button2);
		Button b3 = (Button) findViewById(R.id.button3);
		Button b4 = (Button) findViewById(R.id.button4);
		Button b5 = (Button) findViewById(R.id.button5);
        String buttonText = b.getText().toString();
       //int buttonActionChoice = Integer.parseInt(buttonText);
        TextView p1_choice = (TextView) findViewById(R.id.playerChoice);
        TextView p2_choice = (TextView) findViewById(R.id.otherPlayerChoice1);
        TextView p3_choice = (TextView) findViewById(R.id.otherPlayerChoice2);
        TextView p4_choice = (TextView) findViewById(R.id.otherPlayerChoice3);
        
        //TextView player_health=(TextView) findViewById(R.id.textView1);
		//player_health.setText(player_hp);
        
        /*
         * Kris:
         * The following code compares pressed button and records it in playerChoices as a number,
         * and then it updates the command info console (what other people pressed), in order.
         * This is to give a backbone for future turn-based system 
         * 
         */
        if(turn_counter <= 3){
	        
//	        if(buttonActionChoice == actions[DE2_COMMANDS[0]]) p1_choice.setText(actions[DE2_COMMANDS[0]]);
//	        else if(buttonActionChoice == actions[DE2_COMMANDS[1]]) p1_choice.setText(actions[DE2_COMMANDS[1]]);
//	        else if(buttonActionChoice == actions[DE2_COMMANDS[2]]) p1_choice.setText(actions[DE2_COMMANDS[2]]);
//	        else if(buttonActionChoice == actions[DE2_COMMANDS[3]]) p1_choice.setText(actions[DE2_COMMANDS[3]]);
			
			/*
			* Kris: The following set of commands checks user button press and updates 
			* 		the order of commands stored in playerChoices.
			*		Upon pressing any button, the order is shifted as to keep track of who 
			*		pressed the button first
			*/
        	playerChoices[3] = playerChoices[2];
        	playerChoices[2] = playerChoices[1];
        	playerChoices[1] = playerChoices[0];
        	if(buttonText == b1.getText().toString()) playerChoices[0] = DE2_COMMANDS[0];
   	        else if(buttonText == b2.getText().toString()) playerChoices[0] = DE2_COMMANDS[1];
   	        else if(buttonText == b3.getText().toString()) playerChoices[0] = DE2_COMMANDS[2];
   	        else if(buttonText == b4.getText().toString()) playerChoices[0] = DE2_COMMANDS[3];
   	        else if(buttonText == b5.getText().toString()) playerChoices[0] = DE2_COMMANDS[4];
        
        	
        p1_choice.setText(buttonText);
        p2_choice.setText(actions[playerChoices[1]]);
        p3_choice.setText(actions[playerChoices[2]]);
        p4_choice.setText(actions[playerChoices[3]]);
        turn_counter++;
        }
        else
        {
        	p1_choice.setText(buttonText);
        	p2_choice.setText("_");
        	p3_choice.setText("_");
        	p4_choice.setText("_");
        	
        	playerChoices[0] = 20;
        	playerChoices[1] = 20;
        	playerChoices[2] = 20;
        	playerChoices[3] = 20;
        	
        	turn_counter = 0;
        }
        
		// Create an array of bytes.  First byte will be the
		// message length, and the next ones will be the message
		
		byte buf[] = new byte[buttonText.length() + 1];
		buf[0] = (byte) buttonText.length(); 
		System.arraycopy(buttonText.getBytes(), 0, buf, 1, buttonText.length());

		// Now send through the output stream of the socket
		
		OutputStream out;
		try {
			out = app.sock.getOutputStream();
			try {
				out.write(buf, 0, buttonText.length() + 1);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
