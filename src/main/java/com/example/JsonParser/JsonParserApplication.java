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
		logger.info("STARTING JsonParserApplication via command line runner");
		readFile();
	}

	private void readFile() throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		String fileName = "./data/a1.json";
		InputStream inputStream = new FileInputStream(fileName);
		String data = readFromInputStream(inputStream);
		logger.info("Read file ",fileName);
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
