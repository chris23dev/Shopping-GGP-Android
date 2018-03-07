package com.ggp.theclub.util;

import android.util.Patterns;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.model.Address;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;

import java8.util.stream.StreamSupport;

public final class StringUtils {
    private static final char SPACE = ' ';
    private static final char NO_BREAK_SPACE = '\u00A0';
    private static final String VERSION_DELIMITER = "\\.";
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final String THE_REGEX = "^THE +";

    public static String minutesToPrettyTime(int totalMinutes, String hourText, String minuteText) {
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        return String.format("%d %s %02d %s", hours, hourText, minutes, minuteText);
    }

    /**
     * Returns true if the string is null or 0-length.
     * @param str the string to be examined
     * @return true if str is null or zero length
     *
     * Note: Copied from TextUtils in order to help with unit testing
     */
    public static boolean isEmpty(CharSequence str) {
        if (str == null || str.length() == 0)
            return true;
        else
            return false;
    }

    public static String formatAddress(Address address) {
        if (address == null) {
            return "";
        }

        String line1 = address.getLine1() == null ? "" : address.getLine1() + ",";
        String city = address.getCity() == null ? "" : address.getCity();
        String state = address.getState() == null ? "" : address.getState();
        String zip = address.getZip() == null ? "" : address.getZip();

        return String.format("%s %s %s %s", line1, city, state, zip).trim().replace("  ", " ");
    }

    public static String prettyPrintRawPhoneNumber(String rawPhoneNumber) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            PhoneNumber phoneNumber = phoneUtil.parse(rawPhoneNumber, Locale.getDefault().getCountry());
            return phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
        } catch (NumberParseException ex) {
            return rawPhoneNumber == null ? "" : rawPhoneNumber;
        }
    }

    public static String characterSeparatedString(List<String> strings, String separator) {
        if (strings == null) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        final String finalSeparator = separator == null ? "" : separator;

        StreamSupport.stream(strings).forEachOrdered(s -> {
            if(!isEmpty(s.trim()) && !isEmpty(builder.toString().trim())) {
                s = finalSeparator + s;
            }
            builder.append(s);
        });

        return builder.toString().trim();
    }

    public static boolean isValidPassword(String password) {
        return !isEmpty(password) && password.length() >= MIN_PASSWORD_LENGTH;
    }

    public static boolean isValidEmail(String email) {
        return !isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPhoneNumber(String rawPhoneNumber) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            String locale = MallApplication.getApp().getResources().getConfiguration().locale.getCountry();
            PhoneNumber phoneNumber = phoneUtil.parse(rawPhoneNumber, locale);
            return phoneUtil.isValidNumber(phoneNumber);
        } catch (NumberParseException ex) {
            return false;
        }
    }

    public static String getNameForSorting(String name) {
        return StringUtils.isEmpty(name) ? name : name.toUpperCase().replaceFirst(THE_REGEX, "").replace(" ", "");
    }

    public static String getNonWrappingString(String s){
        return StringUtils.isEmpty(s) ? s : s.replace(SPACE, NO_BREAK_SPACE);
    }

    public static String capitalizeFirstLetter(String s) {
        if (isEmpty(s)) {
            return s;
        }
        if (s.length() == 1) {
            return s.toUpperCase();
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    public static String getMd5Hash(String stringToHash) {
        if (stringToHash == null) {
            return null;
        }

        try {
            StringBuilder builder = new StringBuilder();
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(stringToHash.getBytes("UTF8"));

            byte stringToHashBytes[] = m.digest();

            for (int i = 0; i < stringToHashBytes.length; i++) {
                builder.append(Integer.toHexString((0x000000ff & stringToHashBytes[i]) | 0xffffff00).substring(6));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException ex) {
            return null;
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
}
