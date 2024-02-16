package com.example.JsonParser.app;

import com.example.JsonParser.functions.*;
import com.example.JsonParser.objects.Category;
import com.example.JsonParser.objects.DataSet;
import com.example.JsonParser.objects.Person;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.util.*;

@SpringBootApplication
public class JsonParserApplication implements CommandLineRunner {

	private static Logger logger = LoggerFactory.getLogger(JsonParserApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(JsonParserApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		logger.info("START JsonParserApplication");
		readCommandsLoop();
		logger.info("EXIT JsonParserApplication");
	}

	private void readCommandsLoop() throws IOException {
		String userInput = "";
		while(!userInput.equals(Constants.exitCommand)) {
			// read command
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			userInput = reader.readLine();
			String userCmd = userInput.split(" ")[0];

			// switch commands
			if(userCmd.equals(Constants.parseCommand)) {
				HashMap<String, Category> categoryHashMap = Parser.processParseCommand(userInput);
				Analyzer.getInstance().initialize(categoryHashMap);
			}
			else if (userCmd.equals(Constants.getCommand)) {
				Reporter.processGetCommand(userInput);
			}
			else if (userCmd.equals(Constants.showCommand)) {
				Reporter.processShowCommand(userInput);
			}
			else if (userCmd.equals(Constants.countCommand)) {
				Reporter.processCountCommand(userInput);
			}
			else if (userCmd.equals(Constants.exportCommand)) {
				Exporter.processExportCommand(userInput);
			}
			else if (!userCmd.equals(Constants.exitCommand)){
				logger.warn("Unknown command");
			}
		}
	}



}
