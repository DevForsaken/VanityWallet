package com.forsakendev.threads;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.forsakendev.handlers.TaskHandler;

public class QuitThread extends Thread{
	private Thread t;
	
	public void run() {
		while(TaskHandler.running) {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			String line = "";
			try {
		    	line = in.readLine();
			    if(line.equalsIgnoreCase("quit"))
			    	TaskHandler.running = false;
			    else
			    	System.out.println("Type 'quit' to Stop Application!");
			    in.close();
		    }
		    catch(Exception e) {}
		}
		
	}

	public void start () {
		if (t == null) {
			t = new Thread (this);
			t.start ();
		}
	}
}
