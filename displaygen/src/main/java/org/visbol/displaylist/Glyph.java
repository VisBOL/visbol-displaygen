package org.visbol.displaylist;

public class Glyph extends DisplayListEntity
{
    public String type;
    public String name;
    public String id;
    public String strand;
    public Integer start;
    public Integer end;

    public Glyph(String type,
                 String name,
                 String id,
                 String strand,
                 Integer startCodon,
                 Integer endCodon)
    {
        this.type = type;
        this.name = name;
        this.id = id;
        this.strand = strand;
        this.start = startCodon;
        this.end = endCodon;
    }
}
