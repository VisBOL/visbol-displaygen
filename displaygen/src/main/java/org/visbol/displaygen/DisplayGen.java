
package org.visbol.displaygen;

import com.beust.jcommander.JCommander;
import org.sbolstandard.core2.SBOLDocument;

import com.beust.jcommander.Parameter;
import org.sbolstandard.core2.SBOLReader;
import org.visbol.displaylist.DisplayList;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.*;

class DisplayGen
{
    private static class Args
    {
        @Parameter(names = "-i", description = "Input filename")
        public String input;

        @Parameter(names = "-o", description = "Output filename")
        public String output;
    }

    public static void main(String[] args)
    {
/*        Args programArgs = new Args();

        JCommander jCommander = new JCommander(programArgs, args);

        jCommander.setProgramName("displayGen");

        if(programArgs.input == null || programArgs.output == null)
        {
            jCommander.usage();
            return;
        }*/

        SBOLDocument document = null;

        try
        {

            document = SBOLReader.read(new FileInputStream("/Users/james/sbol2.xml"));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        DisplayListGenerator generator = new DisplayListGenerator(document);

        DisplayList displayList = generator.getDisplayList();

        Gson gson = new Gson();

        String json = gson.toJson(displayList);


        System.out.print(json);
      /*  FileWriter writer;

        try
        {
            writer = new FileWriter(programArgs.output);
            writer.write(json);
            writer.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }*/
    }
}