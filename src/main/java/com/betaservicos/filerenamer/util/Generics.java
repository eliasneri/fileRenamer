package com.betaservicos.filerenamer.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Normalizer;

public class Generics {
    private static final Logger logger = LoggerFactory.getLogger(Generics.class);

    public static String normalizeString (String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+","");
    }

}
