package com.example.JsonParser.app;

import com.example.JsonParser.app.objects.Category;
import com.example.JsonParser.app.objects.Person;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.swing.text.html.HTMLDocument;
import java.io.*;
import java.util.Arrays;
import java.util.Iterator;

@SpringBootApplication
public class JsonParserApplication implements CommandLineRunner {

	private static Logger logger = LoggerFactory.getLogger(JsonParserApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(JsonParserApplication.class, args);
	}

	private static String exitCommand = "exit";

	@Override
	public void run(String... args) throws Exception {
		logger.info("START JsonParserApplication");
		readCommandsLoop();
		logger.info("EXIT JsonParserApplication");
	}

	private void readCommandsLoop() throws IOException {
		String userInput = "";
		while(!userInput.equals(exitCommand)) {
			// read command
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			userInput = reader.readLine();
			String userCmd = userInput.split(" ")[0];

			// switch commands
			if(userCmd.equals("read")) {
				processReadCommand(userInput);
			}
			else if (!userCmd.equals(exitCommand)){
				logger.warn("Unknown command");
			}
		}
	}

	private void processReadCommand(String command) {
		// get file argument
		String arg = "";
		if(command.split("").length > 0) {
			arg = command.split(" ")[1];
		}

		// read file
		try {
			Category category = parseFileToCategory(arg);
			logger.info("Finished parsing {} with {} elements", category, category.getPeople().length);
		} catch (IOException e) {
			logger.warn("Failed to read file");
		}
	}

	private Category parseFileToCategory(String catID) throws IOException {

		String fileName = "./data/" + catID + ".json";
		logger.info("Read file {}",fileName);
		InputStream inputStream = new FileInputStream(fileName);
		String jsonString = readFromInputStream(inputStream);

		logger.info("Start parsing {}", fileName);
		JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
		String categoryName = jsonObject.get("Category").getAsString();
		JsonArray peopleArray = jsonObject.get("Results").getAsJsonArray();

		Category category = new Category(catID, categoryName);
		for(int i=0; i<peopleArray.size(); i++) {
			// parse personJSON
			JsonObject personJSON = peopleArray.get(i).getAsJsonObject();
			Person personObject = new Person();
			personObject.setId(personJSON.get("Number").getAsString());
			personObject.setName(personJSON.get("Name").getAsString());
			personObject.setBirthYear(personJSON.get("BirthYear").getAsString());
			personObject.setDeathYear(personJSON.get("DeathYear").getAsString());
			personObject.setHomeCountry(personJSON.get("HomeCountry").getAsString());
			personObject.setAchievements(personJSON.get("Achievements").getAsString());
			// parse keywords
			JsonArray keywords = personJSON.get("Keywords").getAsJsonArray();
			personObject.setKeyWords(new String[keywords.size()]);
			for(int j=0; j< personObject.getKeyWords().length; j++) {
				personObject.getKeyWords()[j] = keywords.get(j).getAsString();
			}
			// add personJSON to category
			category.getPeople()[i] = personObject;
			logger.info("Parse {}", personObject);
		}
		return category;
	}

	private String readFromInputStream(InputStream inputStream) throws IOException {
		StringBuilder resultStringBuilder = new StringBuilder();
		try (BufferedReader br
					 = new BufferedReader(new InputStreamReader(inputStream))) {
			String line;
			while ((line = br.readLine()) != null) {
				resultStringBuilder.append(line).append("\n");
			}
		}
		return resultStringBuilder.toString();
	}



}
