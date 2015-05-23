
package org.visbol.displaygen;

import org.sbolstandard.core.*;
import org.sbolstandard.core.util.SBOLBaseVisitor;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class DisplayListGenerator extends SBOLBaseVisitor<RuntimeException>
{
    private org.visbol.displaylist.DisplayList displayList = new org.visbol.displaylist.DisplayList();

    private Map<String, Integer> glyphTypeIDs = new HashMap<String, Integer>();

    public DisplayListGenerator(SBOLDocument document)
    {
        displayList.components.add(new org.visbol.displaylist.Component());

        visit(document);
    }

    @Override
    public void visit(SBOLDocument document)
    {
        for(SBOLRootObject object : document.getContents())
        {
            object.accept(this);
        }
    }

    @Override
    public void visit(Collection collection)
    {
        for(DnaComponent component : collection.getComponents())
        {
            visit(component);
        }
    }

    @Override
    public void visit(DnaComponent component)
    {
        org.visbol.displaylist.Segment segment = new org.visbol.displaylist.Segment();

        for(SequenceAnnotation annotation : component.getAnnotations())
        {
            if(annotation.getSubComponent() == null)
                continue;

            DnaComponent subComponent = annotation.getSubComponent();

            String glyphCode;
            String glyphName = subComponent.getName();
            String glyphID = subComponent.getDisplayId();

            if(glyphName == null)
                glyphName = glyphID;

            String glyphStrand = annotation.getStrand() == StrandType.NEGATIVE
                                     ? "negative" : "positive";

            for(URI uri : subComponent.getTypes())
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
                                            annotation.getBioStart(),
                                            annotation.getBioEnd());

                    segment.sequence.add(glyph);
                }
                else
                {
                    System.out.println("Couldn't find SO number in URI: " + path);
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

