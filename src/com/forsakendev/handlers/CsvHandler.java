package com.forsakendev.handlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class CsvHandler {
	
	public List<String> nameArray;
	private String name;
	private String dir;
	
	public CsvHandler(String name) {
		nameArray = new ArrayList<String>();
		this.name = name;
		dir = System.getProperty("user.dir")+"/data/";
	}
	
	public void run() {
		if(TaskHandler.running) {
			String dirLoc = dir + "names.dat";
			File file= new File(dirLoc);
			
			if(!file.exists())
				try {
					file.mkdir();
					file.createNewFile();
				} catch(Exception e) {}
			
			Scanner inputStream;
			
			try {
			    inputStream = new Scanner(file);
			
			    while(inputStream.hasNext()){
			        String line= inputStream.next();
			        String[] values = line.split(",");
			        
			        nameArray.addAll(Arrays.asList(values));
			        System.out.println(name + " Names Imported: " + values.length);

			        TaskHandler.csvRead = true;
			    }
			    inputStream.close();
			} 
			catch (FileNotFoundException e) {
			    e.printStackTrace();
			}
		}
	}
}
