/*
 * Copyright 2002-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencredo.cloud.storage.si.filter.internal;

import java.util.regex.Pattern;

import org.opencredo.cloud.storage.BlobDetails;
import org.opencredo.cloud.storage.si.filter.AbstractBlobDetailsFilter;
import org.springframework.util.Assert;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 */
public class PatternMatchingBlobNameFilter extends AbstractBlobDetailsFilter {

    private final Pattern pattern;

    /**
     * @param pattern
     */
    public PatternMatchingBlobNameFilter(String regex) {
        Assert.hasText(regex, "'regex' must not be null");
        this.pattern = Pattern.compile(regex);
    }

    /**
     * @param obj
     */
    protected boolean accept(BlobDetails obj) {
        return (obj != null) && (obj.getName() != null) && this.pattern.matcher(obj.getName()).matches();
    }

}
