package hr.algebra.cugomatfx.helpers;

import hr.algebra.cugomatfx.enums.Language;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class MessageHelper {
    private static final String boundle_name = "Messages";
    private static ResourceBundle bundle;
    private static Locale currentLocale;

    static {
        currentLocale = new Locale(Language.HR.getCode());
        bundle = ResourceBundle.getBundle(boundle_name, currentLocale);
    }

    public static String getString(String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return "Missing key";
        }
    }

    public static void changeLanguage(String langCode) {
        currentLocale = new Locale(langCode);
        bundle = ResourceBundle.getBundle(boundle_name, currentLocale);
    }
}
