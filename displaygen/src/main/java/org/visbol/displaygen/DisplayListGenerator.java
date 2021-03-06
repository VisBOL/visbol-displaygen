
package org.visbol.displaygen;

import com.google.gson.Gson;
import org.sbolstandard.core2.*;
import org.sbolstandard.core2.ComponentDefinition;
import org.visbol.displaylist.Glyph;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.Buffer;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DisplayListGenerator
{
    private org.visbol.displaylist.DisplayList displayList = new org.visbol.displaylist.DisplayList();

    private Map<String, Integer> glyphTypeIDs = new HashMap<String, Integer>();

    public DisplayListGenerator(SBOLDocument document)
    {
        displayList.components.add(new org.visbol.displaylist.Component());

        for(ComponentDefinition componentDefinition : document.getComponentDefinitions())
        {
        	addComponentDefinition(componentDefinition); 
        }
        
        for(Collection collection : document.getCollections())
        {
        	addCollection(collection);
        }
    }
    
    private void addCollection(Collection collection)
    {
    	for(TopLevel topLevel : collection.getMembers())
    	{
    		if(topLevel instanceof ComponentDefinition)
    		{
    			addComponentDefinition((ComponentDefinition) topLevel);
    		}
    		else if(topLevel instanceof Collection)
    		{
    			addCollection((Collection) topLevel);
    		}
    	}
    }
    
    private void addComponentDefinition(ComponentDefinition componentDefinition)
    {
        org.visbol.displaylist.Segment segment = new org.visbol.displaylist.Segment();

        segment.name = componentDefinition.getName();

        if(segment.name == null)
            segment.name = componentDefinition.getDisplayId();

        for(SequenceAnnotation annotation : componentDefinition.getSequenceAnnotations())
        {
            Component component = annotation.getComponent();

            String glyphCode = null;

            String glyphName = annotation.getName();

            if(glyphName == null)
                glyphName = component.getName();

            String glyphID = annotation.getDisplayId();

            if(glyphID == null || glyphID.length() == 0)
                glyphID = component.getDisplayId();

            if(glyphName == null)
                glyphName = component.getDefinition().getName();

            if(glyphName == null)
                glyphName = glyphID;

            String glyphStrand = "positive";

            Set<Location> locations = annotation.getLocations();

            int start = Integer.MAX_VALUE;
            int end = Integer.MIN_VALUE;

            for(Location location : locations)
            {
                if(location.getOrientation() == OrientationType.REVERSECOMPLEMENT)
                    glyphStrand = "negative";

                if(location instanceof Range)
                {
                    start = Math.min(start, ((Range) location).getStart());
                    end = Math.max(end, ((Range) location).getEnd());
                }
            }

            ComponentDefinition annotationComponentDefinition = component.getDefinition();

            if(annotationComponentDefinition == null)
                continue;

            for(URI uri : annotationComponentDefinition.getRoles())
            {
                String path = uri.getPath();

                /* Look for SO of the form SO_*
                 */
                String[] pathSO = path.split("SO_");

                /* If not found, look for SO of the form SO:*
                 */
                if(pathSO.length != 2)
                    pathSO = path.split("SO:");

                /* If found, map to a glyph
                 */
                if(pathSO.length == 2)
                {
                    String soNum = pathSO[1];

                    try
                    {
                        HttpURLConnection connection = (HttpURLConnection)
                                new URL("http://dasbol.org/so/SO:" + soNum).openConnection();

                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(connection.getInputStream()));

                        StringBuilder builder = new StringBuilder();

                        String line;

                        while ((line = reader.readLine()) != null)
                            builder.append(line + "\n");

                        SOMapping mapping = new Gson().fromJson(builder.toString(), SOMapping.class);

                        glyphCode = mapping.glyph;
                    }
                    catch(Exception e)
                    {
                        /* glyphCode will be null */
                    }

                }
            }

            if(glyphCode == null)
                glyphCode = "user-defined";

            int idNumber = glyphTypeIDs.getOrDefault(glyphCode, 0) + 1;

            glyphTypeIDs.put(glyphCode, idNumber);

            org.visbol.displaylist.Glyph glyph = new org.visbol.displaylist.Glyph(glyphCode,
                    glyphName,
                    glyphCode + "-" + idNumber,
                    glyphStrand,
                    start,
                    end);

            segment.sequence.add(glyph);
        }

        if(segment.sequence.size() > 0)
        {
            segment.sequence.sort(new Comparator<Glyph>()
            {
                public int compare(Glyph glyphA, Glyph glyphB)
                {
                    return glyphA.start - glyphB.start;
                }
            });

            displayList.components.get(0).segments.add(segment);
        }
    }

    public org.visbol.displaylist.DisplayList getDisplayList()
    {
        return displayList;
    }



}

