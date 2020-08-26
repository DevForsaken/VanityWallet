package com.forsakendev.handlers;


import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.security.Security;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.forsakendev.VanityWallet;
import com.forsakendev.threads.KeyThread;

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
        if(!file.exists())
        	file.mkdir();
        file = new File(System.getProperty("user.dir") + "/found/");
        if(!file.exists())
        	file.mkdir();
        file = null;
        
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
		// Creating threads to be used for KeyGen
		for(int i = 0; i < 12; i+=1) {
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
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String line = "";
		
		System.out.println("Type 'quit' to Stop Application!");
		while(TaskHandler.running) {
		    if(System.currentTimeMillis() >= refreshTime+15000) {
				System.out.println("Hashrate: " + getHashrate() + " KH/s");
				refreshTime = System.currentTimeMillis();
			}
		    try {
		    	line = in.readLine();
			    if(line.equalsIgnoreCase("quit"))
			    	TaskHandler.running = false;
		    }
		    catch(Exception e) {}
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
