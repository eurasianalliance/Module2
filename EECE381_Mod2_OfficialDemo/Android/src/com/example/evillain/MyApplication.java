package com.example.evillain;

import java.net.Socket;

import android.app.Application;

public class MyApplication extends Application {

	Socket sock = null;
	
	Player player = new Player();
	int[] DE2_COMMANDS = new int[5];
}
