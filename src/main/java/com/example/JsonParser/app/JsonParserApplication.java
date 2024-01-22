package com.example.JsonParser.app;

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
			String fileName = "./data/" + arg + ".json";
			readAndParseFile(fileName);
		} catch (IOException e) {
			logger.warn("Failed to read file");
		}
	}

	private void readAndParseFile(String fileName) throws IOException {
		logger.info("Read file {}",fileName);
		InputStream inputStream = new FileInputStream(fileName);
		String jsonString = readFromInputStream(inputStream);

		logger.info("Start parsing {}", fileName);
		JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
		String category = jsonObject.get("Category").getAsString();
		JsonArray people = jsonObject.get("Results").getAsJsonArray();

		for(int i=0; i<people.size(); i++) {
			JsonObject person = people.get(i).getAsJsonObject();
			Person p = new Person();
			p.setId(person.get("Number").getAsString());
			p.setName(person.get("Name").getAsString());
			p.setBirthYear(person.get("BirthYear").getAsString());
			p.setDeathYear(person.get("DeathYear").getAsString());
			p.setHomeCountry(person.get("HomeCountry").getAsString());
			p.setAchievements(person.get("Achievements").getAsString());
			p.setKeyWords(person.get("Keywords").getAsJsonArray().toString().split(""));
			logger.info("Parse {}", p);
		}

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
