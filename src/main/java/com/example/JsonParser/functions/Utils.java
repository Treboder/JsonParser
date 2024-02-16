package com.example.JsonParser.functions;

import com.example.JsonParser.app.JsonParserApplication;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Utils {

    private static Logger logger = LoggerFactory.getLogger(JsonParserApplication.class);


    public static String parseJsonField(JsonObject personJSON, String fieldName) {
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

    public static String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }


}
