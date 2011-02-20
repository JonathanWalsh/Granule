/*
 * Copyright 2010 Granule Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.granule.tag.utils;

import junit.framework.TestCase;
import com.granule.utils.OptionsHandler;

/**
 * User: Dario Wunsch
 * Date: 13.08.2010
 * Time: 19:33:29
 */
public class OptionsHandlerTest extends TestCase {
    public void testOptions() {
        OptionsHandler oh = new OptionsHandler();
        String method = "closure-compiler";

        String s = "aaaaa";
        String expected= "closure-compiler.aaaaa\n";
        assertEquals(oh.handle(s, method), expected);

        s="--aa";
        expected = "closure-compiler.aa\n";
        assertEquals(oh.handle(s, method), expected);

        s = "--aa true";
        expected = "closure-compiler.aa=true\n";
        assertEquals(oh.handle(s, method), expected);

        s = "--aa true --bb 15 --ccc                          --compilation_level=SIMPLE_OPTIMIZATIONS";
        expected = "closure-compiler.aa=true \n"+
                "closure-compiler.bb=15 \n"+
                "closure-compiler.ccc=                         \n"+
                "closure-compiler.compilation_level=SIMPLE_OPTIMIZATIONS\n";
        assertEquals(oh.handle(s, method), expected);

        s = "--aa \"aa\"";
        expected = "closure-compiler.aa=\"aa\"\n";
        assertEquals(oh.handle(s, method), expected);

        s = "--aa \"aa aa bb --cc 1589 4477";
        expected = "closure-compiler.aa=\"aa aa bb --cc 1589 4477\n";
        assertEquals(oh.handle(s, method), expected);

        s = "--aa 'aa'";
        expected = "closure-compiler.aa='aa'\n";
        assertEquals(oh.handle(s, method), expected);

        s = "--aa 'aa--/asas/ssss' --c true";
        expected = "closure-compiler.aa='aa--/asas/ssss' \nclosure-compiler.c=true\n";
        assertEquals(oh.handle(s, method), expected);

        s = "--aa \"aa--/asas/ssss\" --c true";
        expected = "closure-compiler.aa=\"aa--/asas/ssss\" \nclosure-compiler.c=true\n";
        assertEquals(oh.handle(s, method), expected);
    }
}
