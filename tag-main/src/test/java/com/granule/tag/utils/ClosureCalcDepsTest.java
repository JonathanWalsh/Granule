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

import com.granule.ExternalFragment;
import com.granule.FragmentDescriptor;
import com.granule.InternalFragment;
import com.granule.SimpleRequestProxy;
import com.granule.calcdeps.CalcDeps;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

/**
 * User: Dario Wunsch Date: 20.07.2010 Time: 2:51:42
 */
public class ClosureCalcDepsTest extends TestCase {
    public void testBasic() {
        String testCode = "goog.require('goog.dom');\n" + "goog.require('goog.date');\n//goog.require('goog.fx.DragListGroup');\n"
                + "goog.require('goog.i18n.DateTimeSymbols');\n" + "goog.require('goog.ui.DatePicker');\n<%--goog.require('goog.fx.DragListGroup');\n--%>"
                + "        var dp = new goog.ui.DatePicker();\n"
                + "        dp.render(document.getElementById('widget_dp'));\n" + "\n"
                + "        goog.events.listen(dp, goog.ui.DatePicker.Events.CHANGE,\n"
                + "                function(event)\n" + "                {\n"
                + "                    goog.dom.setTextContent(document.getElementById('label_dp'), event.date ?\n"
                + "                            event.date.toIsoString(true) : 'none');\n" + "                });\n"
                + "\n" + "        goog.dom.setTextContent(document.getElementById('label_dp'),\n"
                + "                dp.getDate().toIsoString(true));";
        List<FragmentDescriptor> fragments = new ArrayList<FragmentDescriptor>();
        FragmentDescriptor fd = new ExternalFragment("src/test/resources/js/closure/goog/base.js");
        fragments.add(fd);
        fd = new InternalFragment(testCode);
        fragments.add(fd);

        CalcDeps cd = new CalcDeps();
        try {
            String path = new java.io.File(".").getCanonicalPath();
            List<FragmentDescriptor> results = cd.calcDeps(fragments, new SimpleRequestProxy(path),
                    new ArrayList<String>());
            String exp = "src/test/resources/js/closure/goog/base.js\n" +
                    "src/test/resources/js/closure/goog/debug/error.js\n" +
                    "src/test/resources/js/closure/goog/string/string.js\n" +
                    "src/test/resources/js/closure/goog/asserts/asserts.js\n" +
                    "src/test/resources/js/closure/goog/array/array.js\n" +
                    "src/test/resources/js/closure/goog/useragent/useragent.js\n" +
                    "src/test/resources/js/closure/goog/dom/browserfeature.js\n" +
                    "src/test/resources/js/closure/goog/dom/tagname.js\n" +
                    "src/test/resources/js/closure/goog/dom/classes.js\n" +
                    "src/test/resources/js/closure/goog/math/coordinate.js\n" +
                    "src/test/resources/js/closure/goog/math/size.js\n" +
                    "src/test/resources/js/closure/goog/object/object.js\n" +
                    "src/test/resources/js/closure/goog/dom/dom.js\n" +
                    "src/test/resources/js/closure/goog/date/datelike.js\n" +
                    "src/test/resources/js/closure/goog/date/date.js\n" +
                    "src/test/resources/js/closure/goog/i18n/datetimesymbols.js\n" +
                    "src/test/resources/js/closure/goog/dom/a11y.js\n" +
                    "src/test/resources/js/closure/goog/debug/entrypointregistry.js\n" +
                    "src/test/resources/js/closure/goog/debug/errorhandlerweakdep.js\n" +
                    "src/test/resources/js/closure/goog/events/browserfeature.js\n" +
                    "src/test/resources/js/closure/goog/disposable/disposable.js\n" +
                    "src/test/resources/js/closure/goog/events/event.js\n" +
                    "src/test/resources/js/closure/goog/events/eventtype.js\n" +
                    "src/test/resources/js/closure/goog/reflect/reflect.js\n" +
                    "src/test/resources/js/closure/goog/events/browserevent.js\n" +
                    "src/test/resources/js/closure/goog/events/eventwrapper.js\n" +
                    "src/test/resources/js/closure/goog/events/listener.js\n" +
                    "src/test/resources/js/closure/goog/structs/simplepool.js\n" +
                    "src/test/resources/js/closure/goog/useragent/jscript.js\n" +
                    "src/test/resources/js/closure/goog/events/pools.js\n" +
                    "src/test/resources/js/closure/goog/events/events.js\n" +
                    "src/test/resources/js/closure/goog/events/eventtarget.js\n" +
                    "src/test/resources/js/closure/goog/events/keycodes.js\n" +
                    "src/test/resources/js/closure/goog/events/keyhandler.js\n" +
                    "src/test/resources/js/closure/goog/i18n/timezone.js\n" +
                    "src/test/resources/js/closure/goog/i18n/datetimeformat.js\n" +
                    "src/test/resources/js/closure/goog/math/box.js\n" +
                    "src/test/resources/js/closure/goog/math/rect.js\n" +
                    "src/test/resources/js/closure/goog/style/style.js\n" +
                    "src/test/resources/js/closure/goog/events/eventhandler.js\n" +
                    "src/test/resources/js/closure/goog/ui/idgenerator.js\n" +
                    "src/test/resources/js/closure/goog/ui/component.js\n" +
                    "src/test/resources/js/closure/goog/ui/datepicker.js";
            String[] expected = exp.split("\n");
            assertEquals(expected.length + 1, results.size());
            for (int i = 0; i < expected.length; i++) {
                assert (results.get(i) instanceof ExternalFragment);
                assertEquals(((ExternalFragment) results.get(i)).getFilePath(), expected[i]);
            }
            assert (results.get(expected.length) instanceof InternalFragment);
            assertEquals(((InternalFragment) results.get(expected.length)).getText(), testCode);

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}
