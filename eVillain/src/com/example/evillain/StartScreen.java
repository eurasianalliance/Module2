package com.example.evillain;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class StartScreen extends Activity {
	public final static String PLAYER_NAME = "com.example.villain.PLAYER_NAME";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_screen);
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
    
    public void assignPlayerName(View view) {
    	//Do something in response to button
    	Intent intent = new Intent(this, GameScreen.class);
    	EditText editText = (EditText) findViewById(R.id.edit_hero_name);
    	String playername = editText.getText().toString();
    	intent.putExtra(PLAYER_NAME, playername);
    	
    	Log.i(playername, "in assignPLayerName (StartScreen)");
    	startActivity(intent);
    }
}
