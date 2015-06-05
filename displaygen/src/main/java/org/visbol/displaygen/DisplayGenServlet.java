package org.visbol.displaygen;

import com.google.gson.Gson;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLReader;
import org.visbol.displaylist.DisplayList;

import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.nio.charset.StandardCharsets;

public class DisplayGenServlet extends HttpServlet
{
    Gson gson = new Gson();

    public DisplayGenServlet()
    {
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        response.setContentType("application/json");

        PrintWriter writer = response.getWriter();

        String field = request.getParameter("document");

        String callback = request.getParameter("callback");

        if(callback != null)
            writer.print(callback + "(");

        SBOLDocument document = null;

        try
        {
            document = SBOLReader.read(new ByteArrayInputStream(field.getBytes(StandardCharsets.UTF_8)));
        }
        catch(Exception e)
        {
            gson.toJson(e, writer);

            if(callback != null)
                writer.print(")");

            return;
        }

        DisplayListGenerator generator = new DisplayListGenerator(document);

        DisplayList displayList = generator.getDisplayList();

        gson.toJson(displayList, writer);

        if(callback != null)
            writer.print(")");
    }

}

