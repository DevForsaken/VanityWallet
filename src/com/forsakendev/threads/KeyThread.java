package com.forsakendev.threads;

import java.io.File;
import java.io.FileWriter;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECPoint;
import java.util.ArrayList;
import java.util.List;

import org.bitcoinj.core.Base58;

import org.bouncycastle.util.encoders.Hex;

import com.forsakendev.handlers.TaskHandler;

public class KeyThread extends Thread{
	private Thread t;
	private String threadName;
	
	private int itr;
	private int errors;
	
	private KeyPairGenerator keyGen;
	private ECGenParameterSpec ecSpec;
	private MessageDigest sha;
	private MessageDigest rmd;

	private ArrayList<String> found;
	private List<String> nameArray;
	
	private long refreshTime;

	public KeyThread(String name, List<String> database){
		threadName = name;
		nameArray = database;
		found = new ArrayList<String>();
		refreshTime = System.currentTimeMillis();
		itr = 0;
		
		try {
			keyGen = KeyPairGenerator.getInstance("EC");
			ecSpec = new ECGenParameterSpec("secp256k1");
			keyGen.initialize(ecSpec);
			
			sha = MessageDigest.getInstance("SHA-256");
			rmd = MessageDigest.getInstance("RipeMD160", "BC");
		}
		catch(Exception e) {
			System.out.print(e);
		}
	}

	public void run() {
		while(TaskHandler.running) {
			/** Adds the Amount of Hashes Done to TaskHandler Array **/
			if(System.currentTimeMillis() >= refreshTime+15000) {
				TaskHandler.hashes.add(itr);
				itr = 0;
				refreshTime = System.currentTimeMillis();
			}
			
			/** This Section Creates the Private Key, Public Key, WIF PK, and Wallet **/
			try {
				/** Generates KeyPair (Private and Public Keys) **/
				KeyPair kp = keyGen.generateKeyPair();
				PublicKey publicKey = kp.getPublic();
				PrivateKey privateKey = kp.getPrivate();
				
				/** Patching up the Private Key **/
				ECPrivateKey epvt = (ECPrivateKey)privateKey;
				String privateKeyStr = "80" + adjustTo64(epvt.getS().toString(16)).toUpperCase();

				/** Patching up the Public Key **/
				ECPublicKey epub = (ECPublicKey)publicKey;
				ECPoint pt = epub.getW();
				String publicKeyStr = "04" 
						+ adjustTo64(pt.getAffineX().toString(16)).toUpperCase() 
						+ adjustTo64(pt.getAffineY().toString(16)).toUpperCase();

				/** Creating Wallet Address from Public Key
				 	Hashing Public Key with Sha256 then Hashing Sha256 Hash with RipeMD160 **/
				byte[] r1 = rmd.digest(sha.digest(Hex.decode(publicKeyStr.getBytes("UTF-8"))));
				

				/** Prepends Leading 0 and Makes the Padded RipeMD160 Hash **/
				byte[] r2 = new byte[r1.length + 1];
				r2[0] = 0;
				for (int i = 0 ; i < r1.length ; i++) r2[i+1] = r1[i];
				
				/** Double Sha256 Hash **/
				byte[] s2 = sha.digest(sha.digest(r2));
							
				/** Takes the RipeMDPadded Public Key and the Checksum of ShaFinal and Combines Them **/
				byte[] walletBytes = new byte[25];
				System.arraycopy(r2, 0, walletBytes, 0, 21);
				System.arraycopy(s2, 0, walletBytes, 21, 4);
				
				/** Final Bitcoin Wallet Address **/
				String walletStr = Base58.encode(walletBytes);
				
				/** If a Valid Key Pair was Found and Contains Name in Vanity List Save Them **/
				if(searchNames(walletStr.toLowerCase())) {
					/** Decodes PK into Bytes and then Double Sha256 Hashes Them **/
					byte[] prvt = Hex.decode(privateKeyStr);
			        byte[] digest = sha.digest(sha.digest(prvt));
			        
			        /** Gets the Padded Private Key and the Checksum of the Sha256 and Combines Them **/
			        byte[] prvtBytes = new byte[37];
					System.arraycopy(prvt, 0, prvtBytes, 0, 33);
					System.arraycopy(digest, 0, prvtBytes, 33, 4);
					
					/** Final WIF Private Key **/
					privateKeyStr = Base58.encode(prvtBytes);
			        
					/** Saves the Keys and Wallet Address w/ Display in Log **/
					saveKey(privateKeyStr, publicKeyStr, walletStr);
					System.out.println("------- Key found ------");
					System.out.println("Private Key: " + privateKeyStr);			
					System.out.println("Public Key: " + publicKeyStr);	
					System.out.println("Bitcoin Address: " + Base58.encode(walletBytes));
					System.out.println("------------------------");
				}
				itr += 1;
			}
			catch (Exception e) {
				errors += 1;
			}
		}
		
		TaskHandler.hashes.add(itr);
		System.out.println(threadName + " Itr: " + itr + "  |  Errors: " + errors);
		
		if(!found.isEmpty())
			for(String priv: found) {
				System.out.println("  Private Key: " + priv);
			}
	}

	public void start () {
		if (t == null) {
			t = new Thread (this, threadName);
			t.start ();
		}
	}
	
	private String adjustTo64(String s) {
	    switch(s.length()) {
	    case 62: return "00" + s;
	    case 63: return "0" + s;
	    case 64: return s;
	    default:
	        throw new IllegalArgumentException("not a valid key: " + s);
	    }
	}
	
	private boolean searchNames(String str) {
		for(String name: nameArray) {
			if(str.contains(name.toLowerCase()))
				return true;
		}
		return false;
	}
	
	private void saveKey(String privateKey, String publicKey, String wallet) {
		File file= new File(System.getProperty("user.dir") + "/found/address-"+ wallet +".txt");
		try {
			file.createNewFile();
			FileWriter myWriter = new FileWriter(file);
		    myWriter.write("Private Key: " + privateKey + "\n");
		    myWriter.write("Public Key: " + publicKey + "\n");
		    myWriter.write("Wallet Address: " + wallet);
		    myWriter.close();
		}
		catch(Exception e2) {
			System.out.println(e2);
			found.add(privateKey);
		}
    }
}
