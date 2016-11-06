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

import com.granule.miniparser.Scanner;
import junit.framework.TestCase;

/**
 * User: Dario Wunsch
 * Date: 22.12.10
 * Time: 4:09
 */
public class ScannerTest extends TestCase {
    public void testScanner() {
        assertEquals(Scanner.isTagStart("<aaaa>", 1), true);
        assertEquals(Scanner.isTagStart("<a/>", 1), true);
        assertEquals(Scanner.isTagStart("<ScRiPt>", 1), true);
        assertEquals(Scanner.isTagStart("</aaa>", 1), false);
        assertEquals(Scanner.isTagStart("<1aaa>", 1), false);
        assertEquals(Scanner.isTagStart("<a\n >", 1), true);
        assertEquals(Scanner.isTagStart("<Å\n >", 1), false);
        assertEquals(Scanner.isTagStart("<  >", 1), false);
    }
}
