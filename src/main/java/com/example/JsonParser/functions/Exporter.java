package com.example.JsonParser.functions;

import com.example.JsonParser.app.JsonParserApplication;
import com.example.JsonParser.objects.DataSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class Exporter {

    private static Logger logger = LoggerFactory.getLogger(JsonParserApplication.class);

    public static void processExportCommand() {

        int names = Analyzer.getInstance().getNamesHashmapWithListOfCategories().keySet().stream().toList().size();
        int categories = Analyzer.getInstance().getCategoryHashMap().keySet().stream().toList().size();

        logger.info("Export {} people with {} categories", names, categories);
        DataSet dataset = new DataSet(Analyzer.getInstance().getCategoryHashMap());
        logger.debug(dataset.toJSON());

        OutputStream out = null;
        try {
            out = new FileOutputStream(new File( Constants.DATA_OUTPUT_DIR + Constants.DATA_OUTPUT_FILE));
            out.write(dataset.toJSON().getBytes());
            out.close();
            logger.info("Save {} ", Constants.DATA_OUTPUT_DIR + Constants.DATA_OUTPUT_FILE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
