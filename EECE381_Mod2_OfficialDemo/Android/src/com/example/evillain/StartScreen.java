package com.example.evillain;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class StartScreen extends Activity {
	ImageView imgFavorite;
	
	public final static String PLAYER_NAME = "com.example.villain.PLAYER_NAME";
	public final static int START_IMAGE = 0;
	boolean received_action = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_screen);
		imgFavorite = (ImageView)findViewById(R.id.imageView2);
		
		Button b1 = (Button) findViewById(R.id.button1);
		Button b2 = (Button) findViewById(R.id.button2);
		Typeface typeFace =  Typeface.createFromAsset(getAssets(),"fonts/gamefont.ttf");
        b1.setTypeface(typeFace);
        b2.setTypeface(typeFace);
        
		// Initialize timer to check for initialized actions by DE2
		TCPReadTimerTask2 tcp_task2 = new TCPReadTimerTask2();
		Timer tcp_timer = new Timer();
		tcp_timer.schedule(tcp_task2, 3000, 550);
		
		Log.i("MY_MESSAGE", "in onCreate (StartScreen)");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i("MY_MESSAGE", "in onResume (StartScreen)");
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i("MY_MESSAGE", "in onPause (StartScreen)");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.i("MY_MESSAGE", "in onStop (StartScreen)");
	}
	public void takepicture(View view) {
    	Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    	startActivityForResult(intent,0);
    }
    
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		   // TODO Auto-generated method stub
		   super.onActivityResult(requestCode, resultCode, data);
		   Bitmap bp = (Bitmap) data.getExtras().get("data");
		   imgFavorite.setImageBitmap(bp);
    }

	public void assignPlayerName(View view) {

		int i = R.id.imageView2;
		//The following routine is used for changing button color upon pressing
				Button b = (Button)view;
				int buttonPressed = getResources().getIdentifier("gamebuttpressed", "drawable", getApplication().getPackageName());
				int buttonDefault = getResources().getIdentifier("gamebutt", "drawable", getApplication().getPackageName());
				b.setBackgroundResource(buttonPressed);
		// ------
		Vibrator v = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(200);
		
		// ------ Layout ----- 
		MyApplication app = (MyApplication) getApplication();
		
		
		//Do something in response to button
		Intent intent = new Intent(this, GameScreen.class);
		EditText editText = (EditText) findViewById(R.id.edit_hero_name);
		String playername = editText.getText().toString();
		intent.putExtra(PLAYER_NAME, playername);
		Log.i(playername, "in assignPLayerName (StartScreen)");
		Log.i("MY_MESSAGE", "in assignPLayerName (StartScreen)");
		Log.i("MY_MESSAGE", "Requesting actions (StartScreen)");
		
		Toast t1 = Toast.makeText(getApplicationContext(),"Initializing...", Toast.LENGTH_SHORT);
		t1.show();
		
		update_action();
		
		Log.i("MY_MESSAGE", "Done receiving actions (StartScreen)");
		Log.i("MY_MESSAGE", "starting new intent (StartScreen)");
		
		startActivity(intent);
	}

	public void update_action(){
		
		Log.i("MY_MESSAGE", "in update_action (StartScreen)");
		
		MyApplication app = (MyApplication) getApplication();
		while(received_action == false){		
			//sending for actions
			String buttonText = "Z";
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
	
	/*
	 * This timer is responsible for game initialization
	 * It continuously reads from the socket, until the last character is Y
	 */
		public class TCPReadTimerTask2 extends TimerTask{
			
			int convertedInt;
			
			public void run() {
				
				//Log.i("MY_MESSAGE", "timer running (StartScreen)");
				//System.out.println("recieved is "+received);
				
				MyApplication app = (MyApplication) getApplication();
				//receiving side
				if(received_action == false){

					if (app.sock != null && app.sock.isConnected()
							&& !app.sock.isClosed()) {

						try {
							InputStream in = app.sock.getInputStream();

							// See if any bytes are available from the Middleman

							int bytes_avail = in.available();
							if (bytes_avail > 0) {

								// If so, read them in and create a string
								
								Log.i("MY_MESSAGE", "bytes available (StartScreen timer)");

								byte buf2[] = new byte[bytes_avail];
								in.read(buf2);

								final String s0 = new String(buf2, 0, bytes_avail, "US-ASCII");
								//first action
								final String s1=s0.substring(0,1);
								//second action
								final String s2=s0.substring(1,2);
								//third action
								final String s3=s0.substring(2,3);
								//fourth action
								final String s4=s0.substring(3,4);
								//fifth action
								final String s5=s0.substring(4,5);
								//stop bit
								final String s6=s0.substring(5,6);

								if(s6.equals("Y")){
									received_action=true;
									
									Log.i("MY_MESSAGE", "received Y (StartScreen timer)");
									
									// Convert ASCII string to index from 0 to 19
									// and store in DE2_COMMANDS
									app.DE2_COMMANDS[0] = asciiToActionIndex(s1);
									app.DE2_COMMANDS[1] = asciiToActionIndex(s2);
									app.DE2_COMMANDS[2] = asciiToActionIndex(s3);
									app.DE2_COMMANDS[3] = asciiToActionIndex(s4);
									app.DE2_COMMANDS[4] = asciiToActionIndex(s5);
									
									// Set player's actions
									app.player.setAction1(s1);
									app.player.setAction2(s2);
									app.player.setAction3(s3);
									app.player.setAction4(s4);
									app.player.setAction5(s5);
																		
									for (int j = 0; j < 5; j++) {
										//System.out.println("(StartScreen) Action " + app.DE2_COMMANDS[j]);	
									}
									 									
								}

								/*
								runOnUiThread(new Runnable() {
									public void run() {										
										MyApplication app = (MyApplication) getApplication();
										if(s6.equals("Y")){										
											Log.i("MY_MESSAGE", "running onUiThread (StartScreen)");					
										}
									} // run
								} );// Ui
								*/
							} // bytes available
						} // try
						
						catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
			
			/*
			 * Convert the first char of a given String into corresponding index
			 * Param: a String encoded in Ascii
			 * Return: a corresponding index
			 */
			private int asciiToActionIndex(String str) {
				
				int numericValue = (int) str.charAt(0);
				return numericValue - 65;
			}
			
			
		}

}
