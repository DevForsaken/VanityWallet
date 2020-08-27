package com.forsakendev.handlers;


import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.security.Security;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Pattern;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.forsakendev.VanityWallet;
import com.forsakendev.threads.KeyThread;
import com.forsakendev.threads.QuitThread;

public class TaskHandler {

	public static boolean running;
	public static boolean csvRead;
	public static ArrayList<Integer> hashes;
	
	public TaskHandler() {
		TaskHandler.running = true;
		TaskHandler.csvRead = false;
		TaskHandler.hashes = new ArrayList<Integer>();
		
		Security.addProvider(new BouncyCastleProvider());
	}
	
	public void start() {
		/** Makes Sure Directories are Created for Files **/
		File file = new File(System.getProperty("user.dir") + "/data/");
		boolean exit = false;
        if(!file.exists()) {
        	file.mkdir();
        	file = new File(System.getProperty("user.dir") + "/data/names.dat");
        	try {
        		file.createNewFile();
        	} catch(Exception e) {}
        	exit = true;
        }
        file = new File(System.getProperty("user.dir") + "/found/");
        if(!file.exists()) {
        	file.mkdir();
        	exit = true;
        }
        file = null;
        if(exit) {
        	System.out.println("Restart Required!\nMake sure to add names to the names.dat in data folder!");
        	TaskHandler.running = false;
        }
        
		/** Prepares Necessary Data for Application**/
		// Gets and Array of all wallets with a balance
		VanityWallet.csvHandle.run();
		
		// Sort address array to optimize array searching with binary
		Collections.sort(VanityWallet.csvHandle.nameArray);		
		
		for(String name : VanityWallet.csvHandle.nameArray)
			System.out.println("  - " + name);
		
		/** This Section Creates the Threads to Generate Keys **/
		ArrayList<KeyThread> keyThreads = new ArrayList<KeyThread>();
		
		/** KeyGen Threads **/
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String line = "";
		System.out.println("Work your way up till you find that your hash doesnt really get better.\nPlease enter how many threads to run.");
		boolean valid = false;
		int threads = 1;
		while (valid == false) {
			try {
		    	line = in.readLine();
			    if(Pattern.matches("[0-9]+", line)) {
			    	valid = true;
			    	System.out.println("line: " + line);
			    	threads = Integer.parseInt(line);
			    } else
			    	System.out.println("Please enter a valid number!");
		    }
		    catch(Exception e) {}
		}
		
		// Creating threads to be used for KeyGen
		for(int i = 0; i < threads; i+=1) {
			KeyThread thread = new KeyThread("Thread-"+(i+1), VanityWallet.csvHandle.nameArray);
			keyThreads.add(thread);
		}
		
		// Free Some Memory
		VanityWallet.csvHandle = null;
		System.gc();
		
		// Threads are Started Here
		System.out.println("Starting Key Generation on " + keyThreads.size() + " thread(s).");
		for(KeyThread thread : keyThreads)
			thread.start();

		/** Hashrate is Printed Here **/
		long refreshTime = System.currentTimeMillis();
		while(System.currentTimeMillis() <= refreshTime+1250) {}
		refreshTime = System.currentTimeMillis();
		
		// Starts thread for quit event
		QuitThread quit = new QuitThread();
		quit.start();
		System.out.println("Type 'quit' to Stop Application!");
		
		while(TaskHandler.running) {
		    if(System.currentTimeMillis() >= refreshTime+15000) {
				System.out.println("Hashrate: " + getHashrate() + " KH/s");
				refreshTime = System.currentTimeMillis();
			}
		}
		
		System.out.println("Hashrate: " + getHashrate() + " KH/s");
	}
	
	private double getHashrate() {
		DecimalFormat hdf = new DecimalFormat("###.##");
		int hashCount = 0;
		
		for(int i : hashes)
			hashCount += i;
		
		double hashrate = Double.valueOf(hdf.format((hashCount / 15.0)/1000));
		TaskHandler.hashes = new ArrayList<Integer>();
		
		return hashrate;
	}
	
}
