package com.apt.textrank.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @project textrank
 * @package com.apt.util
 * @class Util.java (UTF-8)
 * @date 08/09/2013
 * @author Arnold Paye
 */
public class Util {

    /**
     * Get a dictionary from a file "diccionario.dic"
     * @param pathResources
     * @return 
     */
    public static List<String> getDictionary(String pathResources) throws FileNotFoundException, IOException {
        List<String> dictionary = new ArrayList<String>();
        File file = new File(pathResources, "diccionario.dic");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
            dictionary.add(line);
        }
        return dictionary;
    }
}
