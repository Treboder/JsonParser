package com.example.JsonParser.app;

import com.example.JsonParser.objects.Category;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SpringBootApplication
public class JsonParserApplication implements CommandLineRunner {

	private static Logger logger = LoggerFactory.getLogger(JsonParserApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(JsonParserApplication.class, args);
	}

	private static String parseCommand = "parse";
	private static String getCommand = "get";
	private static String showCommand = "show";
	private static String exitCommand = "exit";
	private static String directory = "./data/";

	private HashMap<String, Category> categoryHashMap = new HashMap<>();
	private HashMap<String, List<String>> namesHashmapWithListOfCategories = new HashMap<>();

	/* Commands:

			parse a1
			parse all

			get a1
			get all

			show a1
			show all
			show multi-category
			show duplicates
	 */

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
			if(userCmd.equals(parseCommand)) {
				this.categoryHashMap = processParseCommand(userInput);
				this.namesHashmapWithListOfCategories = createMapWithNamesAndListOfCategories();
			}
			else if (userCmd.equals(getCommand)) {
				processGetCommand(userInput);
			}
			else if (userCmd.equals(showCommand)) {
				processShowCommand(userInput);
			}
			else if (!userCmd.equals(exitCommand)){
				logger.warn("Unknown command");
			}
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////// Parse Section /////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////

	private HashMap<String, Category> processParseCommand(String command) {
		// get file argument
		String arg = "";
		if(command.split("").length > 0) {
			arg = command.split(" ")[1];
		}

		HashMap<String, Category> map = new HashMap<>();

		// read all data files
		if(arg.equals("all")) {

			// get list of files
			List<Path> filePaths = null;
			try {
				filePaths = Files.walk(Paths.get(directory))
						.filter(Files::isRegularFile)
						.toList();

				logger.info("Found {} data files to parse", filePaths.size());
			} catch (IOException e) {
				logger.error("Failed to to fetch data files to parse");
			}

			// go through all files
			if(filePaths != null) {
				for (int i=0; i<filePaths.size(); i++) {
					String fileName = filePaths.get(i).getFileName().toString();
					String fileID = fileName.replace(".json", "");
					// read certain file specified with name
					try {
						Category category = parseFileToCategory(fileID);
						if(category != null) {
							logger.info("Parsed {} with {} elements", category, category.getPeople().length);
							map.put(category.getCategoryID(), category);
						}
						else
							logger.error("Failed to parse {}", fileID);

					} catch (IOException e) {
						logger.error("Failed to read file");
					}
				}
			}
		}

		// read certain file specified with argument
		else {
			try {
				Category category = parseFileToCategory(arg);
				if(category != null)
					logger.info("Parsed {} with {} elements", category, category.getPeople().length);
				else
					logger.error("Failed to parse {}", arg);
			} catch (IOException e) {
				logger.warn("Failed to read file");
			}
		}

		return map;
	}

	private Category parseFileToCategory(String catID) throws IOException {

		String fileName = directory + catID + ".json";
		logger.debug("Read file {}",fileName);
		InputStream inputStream = new FileInputStream(fileName);
		String jsonString = readFromInputStream(inputStream);

		logger.debug("Start parsing {}", fileName);

		JsonObject jsonObject = null;
		try {
			jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
		} catch (Exception e) {
			logger.error("Failed to parse {}", fileName);
			logger.error(e.getMessage());
			return null;
		}

		String categoryName = jsonObject.get("Category").getAsString();
		JsonArray peopleArray = jsonObject.get("Results").getAsJsonArray();
		Category category = new Category(catID, categoryName);

		for(int i=0; i<peopleArray.size(); i++) {

			// prepare source and target object
			JsonObject personJSON = peopleArray.get(i).getAsJsonObject();
			Person personObject = new Person();

			// parse personJSON
			personObject.setId(parseJsonField(personJSON, "Number"));
			personObject.setName(parseJsonField(personJSON, "Name"));
			personObject.setBirthYear(parseJsonField(personJSON, "BirthYear"));
			personObject.setDeathYear(parseJsonField(personJSON, "DeathYear"));
			personObject.setHomeCountry(parseJsonField(personJSON, "HomeCountry"));
			personObject.setAchievements(parseJsonField(personJSON, "Achievements"));

			// parse keywords
			try {
				JsonArray keywords = personJSON.get("Keywords").getAsJsonArray();
				personObject.setKeyWords(new String[keywords.size()]);
				for(int j=0; j< personObject.getKeyWords().length; j++) {
					personObject.getKeyWords()[j] = keywords.get(j).getAsString();
				}
			} catch (Exception e) {
				logger.error("Failed to parse keywords from {}", personJSON);
			}

			// add personJSON to category
			category.getPeople()[i] = personObject;
			logger.debug("Parse {}", personObject);
		}
		return category;
	}

	private String parseJsonField(JsonObject personJSON, String fieldName) {
		String value = "n/a";
		try {
			value = personJSON.get(fieldName).getAsString();
			return value;
		} catch (Exception e) {
			if(fieldName.equals("DeathYear")) {
				logger.debug("Failed to parse field {} from {}", fieldName, personJSON);
				return value;
			}
			else {
				logger.error("Failed to parse field {} from {}", fieldName, personJSON);
				return value;
			}
		}
	}

	private String readFromInputStream(InputStream inputStream) throws IOException {
		StringBuilder resultStringBuilder = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
			String line;
			while ((line = br.readLine()) != null) {
				resultStringBuilder.append(line).append("\n");
			}
		}
		return resultStringBuilder.toString();
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////// Analyze Section///////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////

	private HashMap<String, List<String>> createMapWithNamesAndListOfCategories() {
		HashMap<String, List<String>> hashmap = new HashMap<>();
		for (String categoryID : categoryHashMap.keySet().stream().toList()) {
			Category c = categoryHashMap.get(categoryID);
			for (Person p : c.getPeople()) {

				// add entry
				if(!hashmap.containsKey(p.getName()))
					hashmap.put(p.getName(), new ArrayList<>());

				// add category to the name
				hashmap.get(p.getName()).add(c.getCategoryName());

//				List<String> categories = hashmap.get(p.getName());
//				if(p.getName().equals("Charlemagne"))
//					categories.size();
			}
		}
		return hashmap;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////// Report Section///////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////

	private void processGetCommand(String command) {
		// get file argument
		String arg = "";
		if(command.split("").length > 0) {
			arg = command.split(" ")[1];
		}

		// get all categories parsed
		if(arg.equals("all")) {
			for(String categoryID : this.categoryHashMap.keySet().stream().toList()) {
				Category c = this.categoryHashMap.get(categoryID);
				logger.info("Get {} ({}) with {} elements", categoryID, c.getCategoryName(), c.getPeople().length);
			}
		}
		else {
			// get certain category parsed
			if(this.categoryHashMap.containsKey(arg)) {
				Category c = this.categoryHashMap.get(arg);
				logger.info("Get {} ({})  ", arg, c.getCategoryName());
			}
			else
				logger.warn("Cant find {}", arg);
		}
	}

	private void processShowCommand(String command) {
		// get file argument
		String arg = "";
		if(command.split("").length > 0) {
			arg = command.split(" ")[1];
		}

		// get all categories parsed
		if(arg.equals("all")) {
			for(String categoryID : categoryHashMap.keySet().stream().sorted().toList()) {
				Category c = categoryHashMap.get(categoryID);
				logger.info("Get {} ({}) with {} people", categoryID, c.getCategoryName(), c.getPeople().length);
				for(Person p : c.getPeople())
					logger.info("Show {}", p);
			}
		}
		else if(arg.equals("names")) {

			for(String name : this.namesHashmapWithListOfCategories.keySet().stream().sorted().toList())
				logger.info("{} ({})", name, this.namesHashmapWithListOfCategories.get(name));
		}
		else if(arg.equals("multi-category")) {

			// find people in more than one category
			List<String> multiCatList = new ArrayList<>();
			for(String name : this.namesHashmapWithListOfCategories.keySet().stream().toList()) {
				if(namesHashmapWithListOfCategories.get(name).size() > 1)
					multiCatList.add(name);
			}

			// show duplicates
			for(String duplicate : multiCatList)
				for(String category : this.namesHashmapWithListOfCategories.get(duplicate))
					logger.info("{} ({})", duplicate, category);
		}
		else if(arg.equals("duplicates")) {

			// find people in more than one category
			List<String> multiCatList = new ArrayList<>();
			for(String name : this.namesHashmapWithListOfCategories.keySet().stream().toList()) {
				if(namesHashmapWithListOfCategories.get(name).size() > 1) {
					multiCatList.add(name);
//					List<String> categories = namesHashmapWithListOfCategories.get(name);
//					if(name.equals("Charlemagne"))
//						categories.size();
				}

			}

			// find duplicates
			List<String> duplicates = new ArrayList<>();
			for(String name : multiCatList) {
				for(int i=0; i<namesHashmapWithListOfCategories.get(name).size(); i++ )
					for(int j=i+1; j<namesHashmapWithListOfCategories.get(name).size(); j++ ) {
						if(namesHashmapWithListOfCategories.get(name).get(i).equals(namesHashmapWithListOfCategories.get(name).get(j)))
							if(!duplicates.contains(name))
								duplicates.add(name);
					}
			}

			// show duplicates
			logger.info("Found {} duplicates", duplicates.size());
			for(String duplicate : duplicates)
				for(String category : this.namesHashmapWithListOfCategories.get(duplicate))
					logger.info("{} ({})", duplicate, category);
		}
		else {
			// get certain category parsed
			if(categoryHashMap.containsKey(arg)) {
				Category c = categoryHashMap.get(arg);
				logger.info("Get {} ({}) with {} people", arg, c.getCategoryName(), this.categoryHashMap.get(arg).getPeople().length);
				for(Person p : c.getPeople())
					logger.info("Show {}", p);
			}
			else
				logger.warn("Cant find {}", arg);
		}
	}

	// count all distinct names
	// ensure distinct mosaics -> multi-category-people with different templates in every category
	// own all people in one category -> get no1 with all images from the category (unique tiles)
	// own all mosaics of one person -> get another image of the person with

}
