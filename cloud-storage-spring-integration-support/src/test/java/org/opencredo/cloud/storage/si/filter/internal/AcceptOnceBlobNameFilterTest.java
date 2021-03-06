/* Copyright 2009-2010 the original author or authors.
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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.opencredo.cloud.storage.BlobDetails;
import org.opencredo.cloud.storage.si.filter.internal.AcceptOnceBlobNameFilter;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 */
public class AcceptOnceBlobNameFilterTest {

    private Date currentDate = new Date(System.currentTimeMillis());
    AcceptOnceBlobNameFilter sut;

    @Test
    public void testUnseenBlobDetailsAdded() {
        sut = new AcceptOnceBlobNameFilter();
        sut.filter(Arrays.asList(new BlobDetails[] { new BlobDetails("", "a", "", currentDate),
                new BlobDetails("", "b", "", currentDate) }));
        List<BlobDetails> acceptedObjects = sut.filter(Arrays.asList(new BlobDetails[] { new BlobDetails("", "c", "",
                currentDate) }));
        Assert.assertEquals(1, acceptedObjects.size());
    }

    @Test
    public void testSeenBlobDetailsNotAdded() {
        sut = new AcceptOnceBlobNameFilter();
        sut.filter(Arrays.asList(new BlobDetails[] { new BlobDetails("", "a", "", currentDate),
                new BlobDetails("", "b", "", currentDate) }));
        List<BlobDetails> acceptedObjects = sut.filter(Arrays.asList(new BlobDetails[] { new BlobDetails("", "a", "",
                currentDate) }));
        Assert.assertEquals(0, acceptedObjects.size());
    }

}
