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

import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AllTests {
  public static Test suite() throws Exception {
    // suspend logging
    Logger.getLogger("com.granule").setLevel(Level.OFF);

    TestSuite suite = new TestSuite();

    suite.addTestSuite(PathUtilsTest.class);

    return suite;
  }
}
