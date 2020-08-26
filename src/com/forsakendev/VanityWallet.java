package com.forsakendev;

import com.forsakendev.handlers.CsvHandler;
import com.forsakendev.handlers.TaskHandler;

public class VanityWallet {
		
	private static String APP_NAME = "VanityWallet";
	private static String VERSION = "v0.1.0";
	
	public static CsvHandler csvHandle;
	
	public static void main(String[] args) {
        System.out.println("Starting " + APP_NAME + " " + VERSION + "!\nBitcoin Vanity Address Bruteforcer!"); 
        csvHandle = new CsvHandler("Name Importer");
        TaskHandler taskHandle = new TaskHandler();
        taskHandle.start();
	}
}