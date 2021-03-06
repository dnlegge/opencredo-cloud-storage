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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opencredo.cloud.storage.BlobDetails;
import org.opencredo.cloud.storage.si.filter.BlobDetailsFilter;
import org.opencredo.cloud.storage.si.filter.internal.CompositeBlobDetailsFilter;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class CompositeBlobDetailsFilterTest {

    @Mock
    private BlobDetailsFilter mockBlobDetailsFilter1;

    @Mock
    private BlobDetailsFilter mockBlobDetailsFilter2;

    @Mock
    private BlobDetails mockBlobDetails;

    @Test
    public void testForwardedToFilters() throws Exception {
        CompositeBlobDetailsFilter compositeBlobDetailsFilter = new CompositeBlobDetailsFilter(mockBlobDetailsFilter1,
                mockBlobDetailsFilter2);
        List<BlobDetails> returnedObjects = Arrays.asList(new BlobDetails[] { mockBlobDetails });

        when(mockBlobDetailsFilter1.filter(anyListOf(BlobDetails.class))).thenReturn(returnedObjects);
        when(mockBlobDetailsFilter2.filter(anyListOf(BlobDetails.class))).thenReturn(returnedObjects);

        assertEquals(returnedObjects, compositeBlobDetailsFilter.filter(Arrays
                .asList(new BlobDetails[] { mockBlobDetails })));
    }

    @Test
    public void testForwardedToAddedFilters() throws Exception {
        CompositeBlobDetailsFilter compositeBlobDetailsFilter = new CompositeBlobDetailsFilter().addFilter(
                mockBlobDetailsFilter1, mockBlobDetailsFilter2);
        List<BlobDetails> returnedObjects = Arrays.asList(new BlobDetails[] { mockBlobDetails });

        when(mockBlobDetailsFilter1.filter(anyListOf(BlobDetails.class))).thenReturn(returnedObjects);
        when(mockBlobDetailsFilter2.filter(anyListOf(BlobDetails.class))).thenReturn(returnedObjects);

        assertEquals(returnedObjects, compositeBlobDetailsFilter.filter(Arrays
                .asList(new BlobDetails[] { mockBlobDetails })));
    }

    @Test
    public void testNegative() throws Exception {
        CompositeBlobDetailsFilter compositeBlobDetailsFilter = new CompositeBlobDetailsFilter(mockBlobDetailsFilter1,
                mockBlobDetailsFilter2);

        when(mockBlobDetailsFilter1.filter(anyListOf(BlobDetails.class))).thenReturn(new ArrayList<BlobDetails>(0));
        when(mockBlobDetailsFilter2.filter(anyListOf(BlobDetails.class))).thenReturn(new ArrayList<BlobDetails>(0));

        assertTrue(compositeBlobDetailsFilter.filter(Arrays.asList(new BlobDetails[] { mockBlobDetails })).isEmpty());
    }

}
