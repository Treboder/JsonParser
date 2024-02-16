package com.example.JsonParser.functions;

import com.example.JsonParser.app.JsonParserApplication;
import com.example.JsonParser.objects.DataSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class Exporter {

    private static Logger logger = LoggerFactory.getLogger(JsonParserApplication.class);

    public static void processExportCommand(String command) {
        // get file argument
        String arg = "";
        if(command.split("").length > 0) {
            arg = command.split(" ")[1];
        }

        int names = Analyzer.getInstance().getNamesHashmapWithListOfCategories().keySet().stream().toList().size();
        int categories = Analyzer.getInstance().getCategoryHashMap().keySet().stream().toList().size();
        int multiCat = Analyzer.getInstance().getMultiCategoryPeopleList().size();

        if(arg.equals("all")) {
            logger.info("Export {} people with {} categories", names, categories);
            DataSet dataset = new DataSet(Analyzer.getInstance().getCategoryHashMap());
            logger.info(dataset.toJSON());
        }
        if(arg.equals("disk")) {
            logger.info("Save {} people with {} categories", names, categories);
            DataSet dataset = new DataSet(Analyzer.getInstance().getCategoryHashMap());

            OutputStream out = null;
            try {
                out = new FileOutputStream(new File( Constants.DATA_OUTPUT_DIR + "_data.json"));
                out.write(dataset.toJSON().getBytes());
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        else
            logger.warn("Failed to interpret command");

    }

}
