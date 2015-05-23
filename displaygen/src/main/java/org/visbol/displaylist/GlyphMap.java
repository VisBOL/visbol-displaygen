
package org.visbol.displaylist;

import java.util.HashMap;
import java.util.Map;

public class GlyphMap
{
    public static String getGlyphCode(String soNum)
    {
        return glyphMap.get(soNum);
    }

    private static Map<String, String> glyphMap = new HashMap<String, String>()
    {{
        put("SO:0000167", "promoter");
        put("SO:0000057", "operator");
        put("SO:0000141", "terminator");
        put("SO:0000627", "insulator");
        put("SO:0000296", "origin");
        put("SO:0005850", "pbs");
        put("SO:0001687", "rts");
        put("SO:0001691", "blunt-rts");
        put("SO:0001953", "assembly-scar");
        put("SO:0001957", "rse");
        put("SO:0000316", "cds");
        put("SO:0000139", "res");
        put("SO:0001956", "pts");
        put("SO:0001955", "pse");
    }};

}
