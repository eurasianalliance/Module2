package com.example.evillain;

public class Player {
	//int identity=0;
	String name="Player 1";
	String life;
	String action1;
	String action2;
	String action3;
	String action4;
	String action5;

	public void setPlayerName(String name){
		this.name=name;
	}

	public String getPlayerName(){
		return this.name;
	}

	public void setAction1(String action){
		this.action1=action;
	}

	public void setAction2(String action){
		this.action2=action;
	}

	public void setAction3(String action){
		this.action3=action;
	}

	public void setAction4(String action){
		this.action4=action;
	}

	public void setAction5(String action){
		this.action5=action;
	}
	public void setLife(String action){
		this.life=action;
	}
	public String getAction1(){
		return action1;
	}

	public String getAction2(){
		return action2;
	}
	public String getAction3(){
		return action3;
	}
	public String getAction4(){
		return action4;
	}
	public String getAction5(){
		return action5;
	}
	public int getLife(){
		int x = Integer.valueOf(life);
		return x;
	}
	
}
