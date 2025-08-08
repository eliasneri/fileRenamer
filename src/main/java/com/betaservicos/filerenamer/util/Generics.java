package com.betaservicos.filerenamer.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Normalizer;

public class Generics {
    private static final Logger logger = LoggerFactory.getLogger(Generics.class);

    public static String normalizeString (String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }

        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+","");
    }

    public static boolean isNullOrBlank(String s) {
        return s == null || s.isBlank() || s.equals("null");
    }

}
