package org.web3j.utils;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.web3j.utils.Strings.*;


public class StringsTest {

    @Test
    public void testToCsv() {
        assertThat(toCsv(Collections.<String>emptyList()), is(""));
        assertThat(toCsv(Collections.singletonList("a")), is("a"));
        assertThat(toCsv(Arrays.asList("a", "b", "c")), is("a, b, c"));
    }

    @Test
    public void testJoin() {
        assertThat(join(Arrays.asList("a", "b"), "|"), is("a|b"));
    }

    @Test
    public void testCapitaliseFirstLetter() {
        assertThat(capitaliseFirstLetter(""), is(""));
        assertThat(capitaliseFirstLetter("a"), is("A"));
        assertThat(capitaliseFirstLetter("aa"), is("Aa"));
        assertThat(capitaliseFirstLetter("A"), is("A"));
        assertThat(capitaliseFirstLetter("Ab"), is("Ab"));
    }

    @Test
    public void testRepeat() {
        assertThat(repeat('0', 0), is(""));
        assertThat(repeat('1', 3), is("111"));
    }

    @Test
    public void testZeros() {
        assertThat(zeros(0), is(""));
        assertThat(zeros(3), is("000"));
    }
}
