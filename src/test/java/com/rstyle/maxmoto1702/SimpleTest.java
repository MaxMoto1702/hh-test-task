package com.rstyle.maxmoto1702;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SimpleTest {

    @Test
    public void testMethod() {
        assertThat(new Main().method(), is(0));
    }
}