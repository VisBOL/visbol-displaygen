
package org.visbol.displaygen;

import org.sbolstandard.core2.*;
import org.sbolstandard.core2.ComponentDefinition;

import java.net.URI;
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

        org.visbol.displaylist.Segment segment = new org.visbol.displaylist.Segment();

        for(ComponentDefinition componentDefinition : document.getComponentDefinitions())
        {
            for(SequenceAnnotation annotation : componentDefinition.getSequenceAnnotations())
            {
                Component component = annotation.getComponent();

                String glyphCode;

                String glyphName = annotation.getName();

                if(glyphName == null)
                    glyphName = component.getName();

                String glyphID = annotation.getDisplayId();

                if(glyphID == null)
                    glyphID = component.getDisplayId();

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

                    String[] pathSO = path.split("SO_");

                    if(pathSO.length == 2)
                    {
                        String soNum = pathSO[1];

                        glyphCode = org.visbol.displaylist.GlyphMap.getGlyphCode("SO:" + soNum);

                        if(glyphCode == null)
                        {
                            System.out.println("Unknown SO: " + soNum);
                            continue;
                        }

                        int idNumber = glyphTypeIDs.getOrDefault(glyphCode, 0) + 1;

                        glyphTypeIDs.put(glyphCode, idNumber);

                        org.visbol.displaylist.Glyph glyph = new org.visbol.displaylist.Glyph(glyphCode,
                                glyphName,
                                glyphCode + "-" + idNumber,
                                glyphStrand,
                                start,
                                end);

                        segment.sequence.add(glyph);
                    } else
                    {
                        System.out.println("Couldn't find SO number in URI: " + path);
                    }
                }
            }
        }

        displayList.components.get(0).segments.add(segment);
    }

    public org.visbol.displaylist.DisplayList getDisplayList()
    {
        return displayList;
    }



}

