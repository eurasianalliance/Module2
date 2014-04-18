package com.example.evillain;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class GameScreen extends Activity {

	int action_count=0;
	boolean set=false;
	boolean received_life = true;
	String choice;
	boolean button_enabled_flag = true;
	
	//sound effects
	SoundPool sp;
	MediaPlayer mp;
	int kick, punch, slap, backhand, headbutt, 
	meditate, heal, help, laugh, taunt, snipe, 
	suppress, melee, yell, bite, napalm, 
	electrocute, cry, kamehameha, debate=0;

	public final static String PLAYER1_CHOICE = "com.example.villain.PLAYER1_CHOICE";
	
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

	// *************************

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MyApplication app = (MyApplication) getApplication();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_screen);
		
		Typeface typeFace =  Typeface.createFromAsset(getAssets(),"fonts/gamefont.ttf");
       
		
		
		//EditText et = (EditText) findViewById(R.id.RecvdMessage2);
		//et.setKeyListener(null);
		Intent intent=getIntent();
		String playername=intent.getExtras().getString(StartScreen.PLAYER_NAME);
		app.player.setPlayerName(playername);
			
		
		TextView t1 = (TextView) findViewById(R.id.playerName1);
				
		//TextView t2=(TextView) findViewById(R.id.playerName2);

		//Log.i(app.player.getPlayerName(), "before TextView edit (GameScreen)"+t1.isInEditMode()+" " + t2.isInEditMode());

		t1.setText(app.player.getPlayerName());
		t1.setTypeface(typeFace);

		//t2.setText(app.player.getPlayerName());
		//Log.i(app.player.getPlayerName(), "after TextView edit (GameScreen)"+t1.isInEditMode()+" " + t2.isInEditMode());
		
		// --- Set buttons ---
		Button set_button = (Button)findViewById(R.id.button1);
		set_button.setText(actions[app.DE2_COMMANDS[0]]);
		
		Button set_button2 = (Button)findViewById(R.id.button2);
		set_button2.setText(actions[app.DE2_COMMANDS[1]]);
		
		Button set_button3 = (Button)findViewById(R.id.button3);
		set_button3.setText(actions[app.DE2_COMMANDS[2]]);
		
		Button set_button4 = (Button)findViewById(R.id.button4);
		set_button4.setText(actions[app.DE2_COMMANDS[3]]);
	
		Button set_button5 = (Button)findViewById(R.id.button5);
		set_button5.setText(actions[app.DE2_COMMANDS[4]]);
		
		Button set_button6 = (Button)findViewById(R.id.button6);
		
		 set_button.setTypeface(typeFace);
		 set_button2.setTypeface(typeFace);
		 set_button3.setTypeface(typeFace);
		 set_button4.setTypeface(typeFace);
		 set_button5.setTypeface(typeFace);
		 set_button6.setTypeface(typeFace);
		//sound effects
		sp=new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		//butt=sp.load(this, R.raw.butt, 1);
		kick=sp.load(this, R.raw.kick, 1);
		punch=sp.load(this, R.raw.punch, 1); 
		slap=sp.load(this, R.raw.slap, 1);
		backhand=sp.load(this, R.raw.backhand_slap, 1);
		headbutt=sp.load(this, R.raw.headbutt, 1);
		meditate=sp.load(this, R.raw.meditate, 1);
		heal=sp.load(this, R.raw.heal, 1);
		help=sp.load(this, R.raw.help, 1);
		laugh=sp.load(this, R.raw.laugh, 1);
		taunt=sp.load(this, R.raw.taunt, 1);
		snipe=sp.load(this, R.raw.snipe, 1);
		suppress=sp.load(this, R.raw.suppress, 1);
		melee=sp.load(this, R.raw.melee, 1);
		yell=sp.load(this, R.raw.yell, 1);
		bite=sp.load(this, R.raw.bite, 1);
		napalm=sp.load(this, R.raw.napalm, 1);
		electrocute=sp.load(this, R.raw.electrocute, 1);
		cry=sp.load(this, R.raw.cry, 1);
		kamehameha=sp.load(this, R.raw.kamehameha, 1);
		debate=sp.load(this, R.raw.debate, 1);

		// --- Timer ---
		TCPReadTimerTask tcp_task = new TCPReadTimerTask();
		Timer tcp_timer = new Timer();
		tcp_timer.schedule(tcp_task, 3000, 500);

		Log.i("MY_MESSAGE", "in onCreate (GameScreen)");

	}		

/*
 * Send the chosen action to the server.
 * The value to be sent is stored in "choice".
 */
	public void set_action(View view) {
		
		if(button_enabled_flag){
		Log.i("MY_MESSAGE", "sending choice (GameScreen)");
		//System.out.println("2. In set_action, choice is " + choice);
		
		Vibrator v = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(150);
		
		MyApplication app = (MyApplication) getApplication();
		
		// -----------Layout changes-----------
		final Button b = (Button) view;
		final int buttonPressed = getResources().getIdentifier("gamebuttonglasspressed", "drawable", getApplication().getPackageName());
		final int buttonDefault = getResources().getIdentifier("gamebuttonglass", "drawable", getApplication().getPackageName());
		final int buttonDisabled = getResources().getIdentifier("gamebuttonglassdisabled", "drawable", getApplication().getPackageName());
		b.setBackgroundResource(buttonPressed);
		

		String buttonText = choice;
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

		received_life=false;
		button_enabled_flag = false;
		b.setEnabled(false);
		b.setBackgroundResource(buttonDisabled);
		//Toast t = Toast.makeText(getApplicationContext(),"Your action sent", Toast.LENGTH_LONG);
		//t.show();
		
		//update_life();
		new Thread(new Runnable() {
		    @Override
		    public void run() {
		        try {
		            Thread.sleep(5000);
		        } catch (InterruptedException e) {
		            e.printStackTrace();
		        }
		        runOnUiThread(new Runnable() {
		            @Override
		            public void run() {
		                button_enabled_flag = true;
		                b.setEnabled(true);
		                b.setBackgroundResource(buttonDefault);
		              Toast t = Toast.makeText(getApplicationContext(),"Ready for next action", Toast.LENGTH_SHORT);
		        	t.show();
		                
		                
		            }
		        });
		    }
		}).start();
			
		}
		
	}

	
	public void update_life(){
		
		Log.i("MY_MESSAGE", "updating life (GameScreen)");
		
		
		MyApplication app = (MyApplication) getApplication();
		
		while(received_life == false){		
			//sending for actions
			String buttonText = "W";
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
			try{
				Thread.sleep(3500L);
			}catch(Exception e){}
		}
		
	}


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

	
	// --------------------------------------------------------
	
	

	public void chooseAction_b1(View view) {
		
		Log.i("MY_MESSAGE", "choosing Button1 action(GameScreen)");
		
		Vibrator v = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(150);
        
        // ---------Layout changes----------
        //Routine for clearing other buttons
		Button b2 = (Button) findViewById(R.id.button2);
		Button b3 = (Button) findViewById(R.id.button3);
		Button b4 = (Button) findViewById(R.id.button4);
		Button b5 = (Button) findViewById(R.id.button5);
		int highlightButton = getResources().getIdentifier("gamebuttonglasspressed", "drawable", getApplication().getPackageName());
		int defaultButton = getResources().getIdentifier("gamebuttonglass", "drawable", getApplication().getPackageName());
		b3.setBackgroundResource(defaultButton);
		b4.setBackgroundResource(defaultButton);
		b5.setBackgroundResource(defaultButton);
		b2.setBackgroundResource(defaultButton);
		// -------------------
		
		MyApplication app = (MyApplication) getApplication();
		
		// Get the message from the box
		Button b=(Button) view;
		b.setBackgroundResource(highlightButton);
		String buttonText = b.getText().toString();
		
		//TextView p1_choice = (TextView) findViewById(R.id.playerChoice);
		//p1_choice.setText(buttonText);
		
		//System.out.println("B1 Action # " + app.DE2_COMMANDS[0]);
		
		if(app.DE2_COMMANDS[0] == 0 ) {
			if(kick!=0)
			{
				sp.play(kick, 1, 1, 0, 0, 1);
			}
			
			choice = "A";
		}
		else if(app.DE2_COMMANDS[0] == 1) {
			if(punch!=0)
			{
				sp.play(punch, 1, 1, 0, 0, 1);
			}
			
			choice = "B";	
		}
		else if(app.DE2_COMMANDS[0] == 2) {
			if(slap!=0)
			{
				sp.play(slap, 1, 1, 0, 0, 1);
			}
			
			choice = "C";		
		}
		else if(app.DE2_COMMANDS[0] == 3) {
			if(backhand!=0)
			{
				sp.play(backhand, 1, 1, 0, 0, 1);
			}
			
			choice = "D";		
		}
		else if(app.DE2_COMMANDS[0] == 4) {
			if(headbutt!=0)
			{
				sp.play(headbutt, 1, 1, 0, 0, 1);
			}
			
			choice = "E";	
		}
		else if(app.DE2_COMMANDS[0] == 5) {
			if(meditate!=0)
			{
				sp.play(meditate, 1, 1, 0, 0, 1);
			}
			
			choice = "F";	
		}
		else if(app.DE2_COMMANDS[0] == 6) {
			if(heal!=0)
			{
				sp.play(heal, 1, 1, 0, 0, 1);
			}
			
			choice = "G";		
		}
		else if(app.DE2_COMMANDS[0] == 7) {
			if(help!=0)
			{
				sp.play(help, 1, 1, 0, 0, 1);
			}
			
			choice = "H";
		}
		else if(app.DE2_COMMANDS[0] == 8) {
			if(laugh!=0)
			{
				sp.play(laugh, 1, 1, 0, 0, 1);
			}
			
			choice = "I";		
		}
		else if(app.DE2_COMMANDS[0] == 9) {
			if(taunt!=0)
			{
				sp.play(taunt, 1, 1, 0, 0, 1);
			}
			
			choice = "J";	
		}
		else if(app.DE2_COMMANDS[0] == 10) {
			if(snipe!=0)
			{
				sp.play(snipe, 1, 1, 0, 0, 1);
			}
			
			choice = "K";		
		}
		else if(app.DE2_COMMANDS[0] == 11) {
			if(suppress!=0)
			{
				sp.play(suppress, 1, 1, 0, 0, 1);
			}
			
			choice = "L";		
		}
		else if(app.DE2_COMMANDS[0] == 12) {
			if(melee!=0)
			{
				sp.play(melee, 1, 1, 0, 0, 1);
			}
			
			choice = "M";		
		}
		else if(app.DE2_COMMANDS[0] == 13) {
			if(yell!=0)
			{
				sp.play(yell, 1, 1, 0, 0, 1);
			}
			
			choice = "N";		
		}
		else if(app.DE2_COMMANDS[0] == 14) {
			if(bite!=0)
			{
				sp.play(bite, 1, 1, 0, 0, 1);
			}
			
			choice = "O";		
		}
		else if(app.DE2_COMMANDS[0] == 15) {
			if(napalm!=0)
			{
				sp.play(napalm, 1, 1, 0, 0, 1);
			}
			
			choice = "P";			
		}
		else if(app.DE2_COMMANDS[0] == 16) {
			if(electrocute!=0)
			{
				sp.play(electrocute, 1, 1, 0, 0, 1);
			}
			
			choice = "Q";			
		}	
		else if(app.DE2_COMMANDS[0] == 17) {
			if(cry!=0)
			{
				sp.play(cry, 1, 1, 0, 0, 1);
			}
			
			choice = "R";			
		}
		else if(app.DE2_COMMANDS[0] == 18) {
			if(kamehameha!=0)
			{
				sp.play(kamehameha, 1, 1, 0, 0, 1);
			}
			
			choice = "S";		
		}
		else if(app.DE2_COMMANDS[0] == 19) {
			if(debate!=0)
			{
				sp.play(debate, 1, 1, 0, 0, 1);
			}
			
			choice = "T";	
		}
		
		//System.out.println("1. B1 choice is" + choice);
	}
	
public void chooseAction_b2(View view) {
		
		Log.i("MY_MESSAGE", "choosing Button2 action(GameScreen)");
		
		Vibrator v = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(150);
        
        // ----------Layout changes-----------
		//Routine for clearing other buttons
		Button b1 = (Button) findViewById(R.id.button1);
		Button b3 = (Button) findViewById(R.id.button3);
		Button b4 = (Button) findViewById(R.id.button4);
		Button b5 = (Button) findViewById(R.id.button5);
		int highlightButton = getResources().getIdentifier("gamebuttonglasspressed", "drawable", getApplication().getPackageName());
		int defaultButton = getResources().getIdentifier("gamebuttonglass", "drawable", getApplication().getPackageName());
		b3.setBackgroundResource(defaultButton);
		b4.setBackgroundResource(defaultButton);
		b5.setBackgroundResource(defaultButton);
		b1.setBackgroundResource(defaultButton);
        // --------------------
        		
		MyApplication app = (MyApplication) getApplication();
		
		// Get the message from the box
		Button b=(Button) view;
		b.setBackgroundResource(highlightButton);
		String buttonText = b.getText().toString();
		//TextView p1_choice = (TextView) findViewById(R.id.playerChoice);
		//p1_choice.setText(buttonText);
		
		//System.out.println("B2 Action # " + app.DE2_COMMANDS[1]);
		
		if(app.DE2_COMMANDS[1] == 0 ) {
			if(kick!=0)
			{
				sp.play(kick, 1, 1, 0, 0, 1);
			}
			
			choice = "A";
		}
		else if(app.DE2_COMMANDS[1] == 1) {
			if(punch!=0)
			{
				sp.play(punch, 1, 1, 0, 0, 1);
			}
			
			choice = "B";	
		}
		else if(app.DE2_COMMANDS[1] == 2) {
			if(slap!=0)
			{
				sp.play(slap, 1, 1, 0, 0, 1);
			}
			
			choice = "C";		
		}
		else if(app.DE2_COMMANDS[1] == 3) {
			if(backhand!=0)
			{
				sp.play(backhand, 1, 1, 0, 0, 1);
			}
			
			choice = "D";		
		}
		else if(app.DE2_COMMANDS[1] == 4) {
			if(headbutt!=0)
			{
				sp.play(headbutt, 1, 1, 0, 0, 1);
			}
			
			choice = "E";	
		}
		else if(app.DE2_COMMANDS[1] == 5) {
			if(meditate!=0)
			{
				sp.play(meditate, 1, 1, 0, 0, 1);
			}
			
			choice = "F";	
		}
		else if(app.DE2_COMMANDS[1] == 6) {
			if(heal!=0)
			{
				sp.play(heal, 1, 1, 0, 0, 1);
			}
			
			choice = "G";		
		}
		else if(app.DE2_COMMANDS[1] == 7) {
			if(help!=0)
			{
				sp.play(help, 1, 1, 0, 0, 1);
			}
			
			choice = "H";
		}
		else if(app.DE2_COMMANDS[1] == 8) {
			if(laugh!=0)
			{
				sp.play(laugh, 1, 1, 0, 0, 1);
			}
			
			choice = "I";		
		}
		else if(app.DE2_COMMANDS[1] == 9) {
			if(taunt!=0)
			{
				sp.play(taunt, 1, 1, 0, 0, 1);
			}
			
			choice = "J";	
		}
		else if(app.DE2_COMMANDS[1] == 10) {
			if(snipe!=0)
			{
				sp.play(snipe, 1, 1, 0, 0, 1);
			}
			
			choice = "K";		
		}
		else if(app.DE2_COMMANDS[1] == 11) {
			if(suppress!=0)
			{
				sp.play(suppress, 1, 1, 0, 0, 1);
			}
			
			choice = "L";		
		}
		else if(app.DE2_COMMANDS[1] == 12) {
			if(melee!=0)
			{
				sp.play(melee, 1, 1, 0, 0, 1);
			}
			
			choice = "M";		
		}
		else if(app.DE2_COMMANDS[1] == 13) {
			if(yell!=0)
			{
				sp.play(yell, 1, 1, 0, 0, 1);
			}
			
			choice = "N";		
		}
		else if(app.DE2_COMMANDS[1] == 14) {
			if(bite!=0)
			{
				sp.play(bite, 1, 1, 0, 0, 1);
			}
			
			choice = "O";		
		}
		else if(app.DE2_COMMANDS[1] == 15) {
			if(napalm!=0)
			{
				sp.play(napalm, 1, 1, 0, 0, 1);
			}
			
			choice = "P";			
		}
		else if(app.DE2_COMMANDS[1] == 16) {
			if(electrocute!=0)
			{
				sp.play(electrocute, 1, 1, 0, 0, 1);
			}
			
			choice = "Q";			
		}	
		else if(app.DE2_COMMANDS[1] == 17) {
			if(cry!=0)
			{
				sp.play(cry, 1, 1, 0, 0, 1);
			}
			
			choice = "R";			
		}
		else if(app.DE2_COMMANDS[1] == 18) {
			if(kamehameha!=0)
			{
				sp.play(kamehameha, 1, 1, 0, 0, 1);
			}
			
			choice = "S";		
		}
		else if(app.DE2_COMMANDS[1] == 19) {
			if(debate!=0)
			{
				sp.play(debate, 1, 1, 0, 0, 1);
			}
			
			choice = "T";	
		}
		
		
		//System.out.println("1. Choice is" + choice);
	}
	
	
public void chooseAction_b3(View view) {
	
	Log.i("MY_MESSAGE", "choosing Button3 action(GameScreen)");
	
	Vibrator v = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
    v.vibrate(150);
    
    // ------Layout changes------
	//Routine for clearing other buttons
	Button b2 = (Button) findViewById(R.id.button2);
	Button b1 = (Button) findViewById(R.id.button1);
	Button b4 = (Button) findViewById(R.id.button4);
	Button b5 = (Button) findViewById(R.id.button5);
	int highlightButton = getResources().getIdentifier("gamebuttonglasspressed", "drawable", getApplication().getPackageName());
	int defaultButton = getResources().getIdentifier("gamebuttonglass", "drawable", getApplication().getPackageName());
	b1.setBackgroundResource(defaultButton);
	b4.setBackgroundResource(defaultButton);
	b5.setBackgroundResource(defaultButton);
	b2.setBackgroundResource(defaultButton);
    // --------------------
	
	MyApplication app = (MyApplication) getApplication();
	
	// Get the message from the box
	Button b=(Button) view;
	b.setBackgroundResource(highlightButton);
	String buttonText = b.getText().toString();
	//TextView p1_choice = (TextView) findViewById(R.id.playerChoice);
	//p1_choice.setText(buttonText);
	
	//System.out.println("B3 Action # " + app.DE2_COMMANDS[2]);
	
	if(app.DE2_COMMANDS[2] == 0 ) {
		if(kick!=0)
		{
			sp.play(kick, 1, 1, 0, 0, 1);
		}
		
		choice = "A";
	}
	else if(app.DE2_COMMANDS[2] == 1) {
		if(punch!=0)
		{
			sp.play(punch, 1, 1, 0, 0, 1);
		}
		
		choice = "B";	
	}
	else if(app.DE2_COMMANDS[2] == 2) {
		if(slap!=0)
		{
			sp.play(slap, 1, 1, 0, 0, 1);
		}
		
		choice = "C";		
	}
	else if(app.DE2_COMMANDS[2] == 3) {
		if(backhand!=0)
		{
			sp.play(backhand, 1, 1, 0, 0, 1);
		}
		
		choice = "D";		
	}
	else if(app.DE2_COMMANDS[2] == 4) {
		if(headbutt!=0)
		{
			sp.play(headbutt, 1, 1, 0, 0, 1);
		}
		
		choice = "E";	
	}
	else if(app.DE2_COMMANDS[2] == 5) {
		if(meditate!=0)
		{
			sp.play(meditate, 1, 1, 0, 0, 1);
		}
		
		choice = "F";	
	}
	else if(app.DE2_COMMANDS[2] == 6) {
		if(heal!=0)
		{
			sp.play(heal, 1, 1, 0, 0, 1);
		}
		
		choice = "G";		
	}
	else if(app.DE2_COMMANDS[2] == 7) {
		if(help!=0)
		{
			sp.play(help, 1, 1, 0, 0, 1);
		}
		
		choice = "H";
	}
	else if(app.DE2_COMMANDS[2] == 8) {
		if(laugh!=0)
		{
			sp.play(laugh, 1, 1, 0, 0, 1);
		}
		
		choice = "I";		
	}
	else if(app.DE2_COMMANDS[2] == 9) {
		if(taunt!=0)
		{
			sp.play(taunt, 1, 1, 0, 0, 1);
		}
		
		choice = "J";	
	}
	else if(app.DE2_COMMANDS[2] == 10) {
		if(snipe!=0)
		{
			sp.play(snipe, 1, 1, 0, 0, 1);
		}
		
		choice = "K";		
	}
	else if(app.DE2_COMMANDS[2] == 11) {
		if(suppress!=0)
		{
			sp.play(suppress, 1, 1, 0, 0, 1);
		}
		
		choice = "L";		
	}
	else if(app.DE2_COMMANDS[2] == 12) {
		if(melee!=0)
		{
			sp.play(melee, 1, 1, 0, 0, 1);
		}
		
		choice = "M";		
	}
	else if(app.DE2_COMMANDS[2] == 13) {
		if(yell!=0)
		{
			sp.play(yell, 1, 1, 0, 0, 1);
		}
		
		choice = "N";		
	}
	else if(app.DE2_COMMANDS[2] == 14) {
		if(bite!=0)
		{
			sp.play(bite, 1, 1, 0, 0, 1);
		}
		
		choice = "O";		
	}
	else if(app.DE2_COMMANDS[2] == 15) {
		if(napalm!=0)
		{
			sp.play(napalm, 1, 1, 0, 0, 1);
		}
		
		choice = "P";			
	}
	else if(app.DE2_COMMANDS[2] == 16) {
		if(electrocute!=0)
		{
			sp.play(electrocute, 1, 1, 0, 0, 1);
		}
		
		choice = "Q";			
	}	
	else if(app.DE2_COMMANDS[2] == 17) {
		if(cry!=0)
		{
			sp.play(cry, 1, 1, 0, 0, 1);
		}
		
		choice = "R";			
	}
	else if(app.DE2_COMMANDS[2] == 18) {
		if(kamehameha!=0)
		{
			sp.play(kamehameha, 1, 1, 0, 0, 1);
		}
		
		choice = "S";		
	}
	else if(app.DE2_COMMANDS[2] == 19) {
		if(debate!=0)
		{
			sp.play(debate, 1, 1, 0, 0, 1);
		}
		
		choice = "T";	
	}
	
	//System.out.println("1. Choice is" + choice);
}


public void chooseAction_b4(View view) {
	
	Log.i("MY_MESSAGE", "choosing Button4 action(GameScreen)");
	
	Vibrator v = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
    v.vibrate(150);
    
    // ------Layout changes -------------
	//Routine for clearing other buttons
	Button b2 = (Button) findViewById(R.id.button2);
	Button b3 = (Button) findViewById(R.id.button3);
	Button b1 = (Button) findViewById(R.id.button1);
	Button b5 = (Button) findViewById(R.id.button5);
	int highlightButton = getResources().getIdentifier("gamebuttonglasspressed", "drawable", getApplication().getPackageName());
	int defaultButton = getResources().getIdentifier("gamebuttonglass", "drawable", getApplication().getPackageName());
	b3.setBackgroundResource(defaultButton);
	b1.setBackgroundResource(defaultButton);
	b5.setBackgroundResource(defaultButton);
	b2.setBackgroundResource(defaultButton);
    
    // ----------------------------
	
	MyApplication app = (MyApplication) getApplication();
	
	// Get the message from the box
	Button b=(Button) view;
	b.setBackgroundResource(highlightButton);
	String buttonText = b.getText().toString();
	//TextView p1_choice = (TextView) findViewById(R.id.playerChoice);
	//p1_choice.setText(buttonText);
	
	//System.out.println("B4 Action # " + app.DE2_COMMANDS[3]);
	
	if(app.DE2_COMMANDS[3] == 0 ) {
		if(kick!=0)
		{
			sp.play(kick, 1, 1, 0, 0, 1);
		}
		
		choice = "A";
	}
	else if(app.DE2_COMMANDS[3] == 1) {
		if(punch!=0)
		{
			sp.play(punch, 1, 1, 0, 0, 1);
		}
		
		choice = "B";	
	}
	else if(app.DE2_COMMANDS[3] == 2) {
		if(slap!=0)
		{
			sp.play(slap, 1, 1, 0, 0, 1);
		}
		
		choice = "C";		
	}
	else if(app.DE2_COMMANDS[3] == 3) {
		if(backhand!=0)
		{
			sp.play(backhand, 1, 1, 0, 0, 1);
		}
		
		choice = "D";		
	}
	else if(app.DE2_COMMANDS[3] == 4) {
		if(headbutt!=0)
		{
			sp.play(headbutt, 1, 1, 0, 0, 1);
		}
		
		choice = "E";	
	}
	else if(app.DE2_COMMANDS[3] == 5) {
		if(meditate!=0)
		{
			sp.play(meditate, 1, 1, 0, 0, 1);
		}
		
		choice = "F";	
	}
	else if(app.DE2_COMMANDS[3] == 6) {
		if(heal!=0)
		{
			sp.play(heal, 1, 1, 0, 0, 1);
		}
		
		choice = "G";		
	}
	else if(app.DE2_COMMANDS[3] == 7) {
		if(help!=0)
		{
			sp.play(help, 1, 1, 0, 0, 1);
		}
		
		choice = "H";
	}
	else if(app.DE2_COMMANDS[3] == 8) {
		if(laugh!=0)
		{
			sp.play(laugh, 1, 1, 0, 0, 1);
		}
		
		choice = "I";		
	}
	else if(app.DE2_COMMANDS[3] == 9) {
		if(taunt!=0)
		{
			sp.play(taunt, 1, 1, 0, 0, 1);
		}
		
		choice = "J";	
	}
	else if(app.DE2_COMMANDS[3] == 10) {
		if(snipe!=0)
		{
			sp.play(snipe, 1, 1, 0, 0, 1);
		}
		
		choice = "K";		
	}
	else if(app.DE2_COMMANDS[3] == 11) {
		if(suppress!=0)
		{
			sp.play(suppress, 1, 1, 0, 0, 1);
		}
		
		choice = "L";		
	}
	else if(app.DE2_COMMANDS[3] == 12) {
		if(melee!=0)
		{
			sp.play(melee, 1, 1, 0, 0, 1);
		}
		
		choice = "M";		
	}
	else if(app.DE2_COMMANDS[3] == 13) {
		if(yell!=0)
		{
			sp.play(yell, 1, 1, 0, 0, 1);
		}
		
		choice = "N";		
	}
	else if(app.DE2_COMMANDS[3] == 14) {
		if(bite!=0)
		{
			sp.play(bite, 1, 1, 0, 0, 1);
		}
		
		choice = "O";		
	}
	else if(app.DE2_COMMANDS[3] == 15) {
		if(napalm!=0)
		{
			sp.play(napalm, 1, 1, 0, 0, 1);
		}
		
		choice = "P";			
	}
	else if(app.DE2_COMMANDS[3] == 16) {
		if(electrocute!=0)
		{
			sp.play(electrocute, 1, 1, 0, 0, 1);
		}
		
		choice = "Q";			
	}	
	else if(app.DE2_COMMANDS[3] == 17) {
		if(cry!=0)
		{
			sp.play(cry, 1, 1, 0, 0, 1);
		}
		
		choice = "R";			
	}
	else if(app.DE2_COMMANDS[3] == 18) {
		if(kamehameha!=0)
		{
			sp.play(kamehameha, 1, 1, 0, 0, 1);
		}
		
		choice = "S";		
	}
	else if(app.DE2_COMMANDS[3] == 19) {
		if(debate!=0)
		{
			sp.play(debate, 1, 1, 0, 0, 1);
		}
		
		choice = "T";	
	}
	
	//System.out.println("1. Choice is" + choice);
}

public void chooseAction_b5(View view) {
	
	Log.i("MY_MESSAGE", "choosing Button5 action(GameScreen)");
	
	Vibrator v = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
    v.vibrate(150);
    
    // -----Layout changes ---------------
	//Routine for clearing other buttons
	Button b2 = (Button) findViewById(R.id.button2);
	Button b3 = (Button) findViewById(R.id.button3);
	Button b4 = (Button) findViewById(R.id.button4);
	Button b1 = (Button) findViewById(R.id.button1);
	int highlightButton = getResources().getIdentifier("gamebuttonglasspressed", "drawable", getApplication().getPackageName());
	int defaultButton = getResources().getIdentifier("gamebuttonglass", "drawable", getApplication().getPackageName());
	b3.setBackgroundResource(defaultButton);
	b4.setBackgroundResource(defaultButton);
	b1.setBackgroundResource(defaultButton);
	b2.setBackgroundResource(defaultButton);
	// --------------------------------
	
	MyApplication app = (MyApplication) getApplication();
	
	// Get the message from the box
	Button b=(Button) view;
	b.setBackgroundResource(highlightButton);
	String buttonText = b.getText().toString();
	//TextView p1_choice = (TextView) findViewById(R.id.playerChoice);
	//p1_choice.setText(buttonText);
	
	//System.out.println("B5 Action # " + app.DE2_COMMANDS[4]);
	
	if(app.DE2_COMMANDS[4] == 0 ) {
		if(kick!=0)
		{
			sp.play(kick, 1, 1, 0, 0, 1);
		}
		
		choice = "A";
	}
	else if(app.DE2_COMMANDS[4] == 1) {
		if(punch!=0)
		{
			sp.play(punch, 1, 1, 0, 0, 1);
		}
		
		choice = "B";	
	}
	else if(app.DE2_COMMANDS[4] == 2) {
		if(slap!=0)
		{
			sp.play(slap, 1, 1, 0, 0, 1);
		}
		
		choice = "C";		
	}
	else if(app.DE2_COMMANDS[4] == 3) {
		if(backhand!=0)
		{
			sp.play(backhand, 1, 1, 0, 0, 1);
		}
		
		choice = "D";		
	}
	else if(app.DE2_COMMANDS[4] == 4) {
		if(headbutt!=0)
		{
			sp.play(headbutt, 1, 1, 0, 0, 1);
		}
		
		choice = "E";	
	}
	else if(app.DE2_COMMANDS[4] == 5) {
		if(meditate!=0)
		{
			sp.play(meditate, 1, 1, 0, 0, 1);
		}
		
		choice = "F";	
	}
	else if(app.DE2_COMMANDS[4] == 6) {
		if(heal!=0)
		{
			sp.play(heal, 1, 1, 0, 0, 1);
		}
		
		choice = "G";		
	}
	else if(app.DE2_COMMANDS[4] == 7) {
		if(help!=0)
		{
			sp.play(help, 1, 1, 0, 0, 1);
		}
		
		choice = "H";
	}
	else if(app.DE2_COMMANDS[4] == 8) {
		if(laugh!=0)
		{
			sp.play(laugh, 1, 1, 0, 0, 1);
		}
		
		choice = "I";		
	}
	else if(app.DE2_COMMANDS[4] == 9) {
		if(taunt!=0)
		{
			sp.play(taunt, 1, 1, 0, 0, 1);
		}
		
		choice = "J";	
	}
	else if(app.DE2_COMMANDS[4] == 10) {
		if(snipe!=0)
		{
			sp.play(snipe, 1, 1, 0, 0, 1);
		}
		
		choice = "K";		
	}
	else if(app.DE2_COMMANDS[4] == 11) {
		if(suppress!=0)
		{
			sp.play(suppress, 1, 1, 0, 0, 1);
		}
		
		choice = "L";		
	}
	else if(app.DE2_COMMANDS[4] == 12) {
		if(melee!=0)
		{
			sp.play(melee, 1, 1, 0, 0, 1);
		}
		
		choice = "M";		
	}
	else if(app.DE2_COMMANDS[4] == 13) {
		if(yell!=0)
		{
			sp.play(yell, 1, 1, 0, 0, 1);
		}
		
		choice = "N";		
	}
	else if(app.DE2_COMMANDS[4] == 14) {
		if(bite!=0)
		{
			sp.play(bite, 1, 1, 0, 0, 1);
		}
		
		choice = "O";		
	}
	else if(app.DE2_COMMANDS[4] == 15) {
		if(napalm!=0)
		{
			sp.play(napalm, 1, 1, 0, 0, 1);
		}
		
		choice = "P";			
	}
	else if(app.DE2_COMMANDS[4] == 16) {
		if(electrocute!=0)
		{
			sp.play(electrocute, 1, 1, 0, 0, 1);
		}
		
		choice = "Q";			
	}	
	else if(app.DE2_COMMANDS[4] == 17) {
		if(cry!=0)
		{
			sp.play(cry, 1, 1, 0, 0, 1);
		}
		
		choice = "R";			
	}
	else if(app.DE2_COMMANDS[4] == 18) {
		if(kamehameha!=0)
		{
			sp.play(kamehameha, 1, 1, 0, 0, 1);
		}
		
		choice = "S";		
	}
	else if(app.DE2_COMMANDS[4] == 19) {
		if(debate!=0)
		{
			sp.play(debate, 1, 1, 0, 0, 1);
		}
		
		choice = "T";	
	}
	
	//System.out.println("1. Choice is" + choice);
}
	

	public class TCPReadTimerTask extends TimerTask {
		//Intent intentVibrate =new Intent(getApplicationContext(),VibrateService.class);
		public void run() {
			MyApplication app = (MyApplication) getApplication();
			if(received_life == false){
				if (app.sock != null && app.sock.isConnected()
						&& !app.sock.isClosed()) {

					try {
						InputStream in = app.sock.getInputStream();

						// See if any bytes are available from the Middleman

						int bytes_avail = in.available();
						if (bytes_avail > 0) {

							Log.i("MY_MESSAGE", "bytes available (GameScreen timer)");
							
							// If so, read them in and create a sring

							byte buf[] = new byte[bytes_avail];
							in.read(buf);

							final String s0 = new String(buf, 0, bytes_avail, "US-ASCII");


							// As explained in the tutorials, the GUI can not be
							// updated in an asyncrhonous task.  So, update the GUI
							// using the UI thread.
							//if(s2.equals("Y")){
							//received_life=true;
							//}

							runOnUiThread(new Runnable() {
								public void run() {									
									//EditText et = (EditText) findViewById(R.id.RecvdMessage2);
									//et.setText(s0);
									if(s0.equals("b")){
										//Toast t = Toast.makeText(getApplicationContext(),"Victory!!! Press back to play again", Toast.LENGTH_LONG);
										//t.show();
										
										//VIBRATION
										// Create a New Intent and start the service
									    //Intent intentVibrate =new Intent(getApplicationContext(),VibrateService.class);
										//startService(intentVibrate);
										
									}
									else if(s0.equals("a")){
										//Toast s = Toast.makeText(getApplicationContext(),"Defeat!!! Press back to play again", Toast.LENGTH_LONG);
										//s.show();
										
										//VIBRATION
										// Create a New Intent and start the service
									    //Intent intentVibrate =new Intent(getApplicationContext(),VibrateService.class);
										//startService(intentVibrate);
										
									}
									
										
										Log.i("MY_MESSAGE", "life updated (GameScreen UiThread)");
									
								}


							});

						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}

