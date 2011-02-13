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
package com.granule.ant;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

/**
 * User: Dario Wünsch
 * Date: 24.09.2010
 * Time: 2:10:28
 */
public class BuildCacheTask extends Task {
    private String rootpath;
    private String outputpath=null;
    private Path pages;

    public void setRootpath(String rootpath) {
        this.rootpath = rootpath;
    }

    public void setOutputpath(String outputpath) {
        this.outputpath = outputpath;
    }

    public void setPages(Path value) {
        if (pages == null) {
            pages = value;
        } else {
            pages.append(value);
        }
    }

    public Path getPages() {
        return pages;
    }

    public Path createPages() {
        if (pages == null) {
            pages = new Path(getProject());
        }
        return pages.createPath();
    }

    public void setPagesRef(Reference r) {
        createPages().setRefid(r);
    }

    public void execute() throws BuildException {
        File f = new File(rootpath);
        String rootWebPath = f.getAbsolutePath();
        if (!f.exists())
            throw new BuildException("Root web-app path not found " + rootWebPath);
        if (outputpath!=null) {
            outputpath = (new File(outputpath)).getAbsolutePath();
        }
        JspProcessor jspProcessor = new JspProcessor();
        int errorCount = 0;
        try {
            errorCount = jspProcessor.generateCache(Arrays.asList(pages.list()), rootWebPath, outputpath);
        } catch (IOException e) {
            throw new BuildException(e);
        }
        if (errorCount>0 && errorCount<JspProcessor.MAX_ERROR_COUNT)
            System.out.println("Build cache errors!!! "+errorCount);
        if (errorCount>=JspProcessor.MAX_ERROR_COUNT)
            throw new BuildException("Build cache errors!!! " + errorCount);
    }
}
