package com.ggp.theclub.util;

import com.ggp.theclub.model.Address;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class StringUtilsTest {
    @Test
    public void testIsEmpty() throws Exception {
        assertTrue(StringUtils.isEmpty(null));
        assertTrue(StringUtils.isEmpty(""));
        assertFalse(StringUtils.isEmpty(" "));
        assertFalse(StringUtils.isEmpty("test"));
    }

    @Test
    public void testAddressFormat() throws Exception {
        Address addr = new Address();
        addr.setCity("city");
        addr.setLine1("street address");
        addr.setState("state");
        addr.setZip("zip");
        assertEquals("street address, city state zip", StringUtils.formatAddress(addr));

        addr.setCity("city");
        addr.setLine1(null);
        addr.setState("state");
        addr.setZip("zip");
        assertEquals("city state zip", StringUtils.formatAddress(addr));

        addr.setCity("city");
        addr.setLine1("street address");
        addr.setState(null);
        addr.setZip("zip");
        assertEquals("street address, city zip", StringUtils.formatAddress(addr));

        addr.setCity("city");
        addr.setLine1("street address");
        addr.setState("state");
        addr.setZip(null);
        assertEquals("street address, city state", StringUtils.formatAddress(addr));

        addr.setCity(null);
        addr.setLine1("street address");
        addr.setState("state");
        addr.setZip("zip");
        assertEquals("street address, state zip", StringUtils.formatAddress(addr));

        assertTrue(StringUtils.formatAddress(null).isEmpty());
    }

    @Test
    public void testPrettyPrintRawPhoneNumber() {
        String phoneNumber = StringUtils.prettyPrintRawPhoneNumber("8675309");
        assertEquals("+1 8675309", phoneNumber);

        phoneNumber = StringUtils.prettyPrintRawPhoneNumber("8008675309");
        assertEquals("+1 800-867-5309", phoneNumber);

        phoneNumber = StringUtils.prettyPrintRawPhoneNumber("18008675309");
        assertEquals("+1 800-867-5309", phoneNumber);

        phoneNumber = StringUtils.prettyPrintRawPhoneNumber("not a phone number");
        assertEquals("not a phone number", phoneNumber);

        phoneNumber = StringUtils.prettyPrintRawPhoneNumber(null);
        assertEquals("", phoneNumber);
    }

    @Test
    public void testCharacterSeparatedString() {
        List<String> strings = new ArrayList<>();
        assertEquals("", StringUtils.characterSeparatedString(strings, ", "));

        strings.add("a");
        assertEquals("a", StringUtils.characterSeparatedString(strings, ", "));

        strings.add("b");
        strings.add("c ");
        assertEquals("a, b, c", StringUtils.characterSeparatedString(strings, ", "));
        assertEquals("a|b|c", StringUtils.characterSeparatedString(strings, "|"));
        assertEquals("a b c", StringUtils.characterSeparatedString(strings, " "));
        assertEquals("abc", StringUtils.characterSeparatedString(strings, null));
        assertEquals("", StringUtils.characterSeparatedString(null, "-"));
    }

    @Test
    public void testGetSortingName() {
        assertEquals("FORCEAWAKENS", StringUtils.getNameForSorting("The Force Awakens"));
        assertEquals("FORCEAWAKENS", StringUtils.getNameForSorting("The         Force Awakens"));
        assertEquals("ABEAUTIFULMIND", StringUtils.getNameForSorting("A Beautiful Mind"));
        assertEquals("THESEUS", StringUtils.getNameForSorting("Theseus"));
        assertEquals("ENTERTHEDRAGON", StringUtils.getNameForSorting("Enter the Dragon"));
        assertNull(StringUtils.getNameForSorting(null));
    }

    @Test
    public void testGetMd5Hash() {
        assertEquals("d67c5cbf5b01c9f91932e3b8def5e5f8", StringUtils.getMd5Hash("teststring"));
        assertNull(StringUtils.getMd5Hash(null));
    }
}