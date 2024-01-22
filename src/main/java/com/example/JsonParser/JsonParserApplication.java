package com.example.JsonParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;

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
		String command = "";
		while(!command.equals("exit")) {
			// read command
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			command = reader.readLine();
			String cmd = command.split(" ")[0];

			// switch commands
			if(cmd.equals("read")) {

				// get file argument
				String arg = "";
				if(command.split("").length > 0) {
					arg = command.split(" ")[1];
				}

				// read file
				try {
					String fileName = "./data/" + arg + ".json";
					readFile(fileName);
				} catch (IOException e) {
					logger.warn("Failed to read file");
				}
			}
			else if (!cmd.equals("exit")){
				logger.warn("Unknown command");
			}
		}
	}

	private void readFile(String fileName) throws IOException {
		InputStream inputStream = new FileInputStream(fileName);
		String data = readFromInputStream(inputStream);
		logger.info("Read file {}",fileName);
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
