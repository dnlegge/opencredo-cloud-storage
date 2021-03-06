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

package org.opencredo.cloud.storage.si.adapter.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.opencredo.cloud.storage.BlobDetails;
import org.opencredo.cloud.storage.si.adapter.ReadingMessageSource;
import org.opencredo.cloud.storage.si.comparator.BlobDetailsComparator;
import org.opencredo.cloud.storage.si.filter.internal.AcceptOnceBlobNameFilter;
import org.opencredo.cloud.storage.si.filter.internal.PatternMatchingBlobNameFilter;
import org.opencredo.cloud.storage.test.TestPropertiesAccessor;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 */
public class InboundChannelAdapterParserTest {

    @Test
    public void testInboundAdapterLoadWithMinimumSettings() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "InboundChannelAdapterParserTest-context.xml", this.getClass());

        Object bean = context.getBean("inbound-adapter");
        assertNotNull("Adapter not found", bean);
        System.out.println(bean.getClass());
        DirectFieldAccessor d = new DirectFieldAccessor(bean);
        Object value = d.getPropertyValue("source");
        assertNotNull("Source not found", value);
        System.out.println(value.getClass());

        ReadingMessageSource rms = (ReadingMessageSource) value;
        DirectFieldAccessor adapterDirect = new DirectFieldAccessor(rms);
        assertNotNull("'template' not found", adapterDirect.getPropertyValue("template"));
        assertNotNull("'filter' queue not found", adapterDirect.getPropertyValue("filter"));
        assertTrue(adapterDirect.getPropertyValue("filter") instanceof AcceptOnceBlobNameFilter);
        assertEquals(TestPropertiesAccessor.getDefaultContainerName(), adapterDirect.getPropertyValue("containerName"));
    }

    @Test
    public void testInboundAdapterLoadWithFilter() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "InboundChannelAdapterParserTest-withFilter-context.xml", this.getClass());

        Object bean = context.getBean("inbound-adapter");
        assertNotNull("Adapter not found", bean);
        System.out.println(bean.getClass());
        DirectFieldAccessor d = new DirectFieldAccessor(bean);
        Object value = d.getPropertyValue("source");
        assertNotNull("Source not found", value);
        System.out.println(value.getClass());

        ReadingMessageSource rms = (ReadingMessageSource) value;
        DirectFieldAccessor adapterDirect = new DirectFieldAccessor(rms);
        assertNotNull("'template' not found", adapterDirect.getPropertyValue("template"));
        assertNotNull("'filter' queue not found", adapterDirect.getPropertyValue("filter"));
        assertTrue(adapterDirect.getPropertyValue("filter") instanceof PatternMatchingBlobNameFilter);
        assertEquals(TestPropertiesAccessor.getDefaultContainerName(), adapterDirect.getPropertyValue("containerName"));
    }

    @Test(expected = BeanDefinitionStoreException.class)
    public void testInboundAdapterLoadWithoutContainer() {
        try {
            new ClassPathXmlApplicationContext("InboundChannelAdapterParserTest-noContainer-context.xml", this.getClass());
            fail("Context load should fail");
        } catch (BeanDefinitionStoreException e) {
            System.err.println(e.getMessage());
            throw e;
        }
    }

    @Test(expected = BeanDefinitionStoreException.class)
    public void testInboundAdapterLoadWithoutTemplate() {
        try {
            new ClassPathXmlApplicationContext("InboundChannelAdapterParserTest-noTemplate-context.xml", this
                    .getClass());
            fail("Context load should fail");
        } catch (BeanDefinitionStoreException e) {
            System.err.println(e.getMessage());
            throw e;
        }
    }

    @Test
    public void testInboundAdapterLoadWithComparator() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "InboundChannelAdapterParserTest-withComparator-context.xml", this.getClass());

        Object bean = context.getBean("inbound-adapter");
        assertNotNull("Adapter not found", bean);
        System.out.println(bean.getClass());
        DirectFieldAccessor d = new DirectFieldAccessor(bean);
        Object value = d.getPropertyValue("source");
        assertNotNull("Source not found", value);
        System.out.println(value.getClass());

        ReadingMessageSource rms = (ReadingMessageSource) value;
        DirectFieldAccessor adapterDirect = new DirectFieldAccessor(rms);
        assertNotNull("'template' not found", adapterDirect.getPropertyValue("template"));
        assertNotNull("'filter' queue not found", adapterDirect.getPropertyValue("filter"));
        assertTrue(adapterDirect.getPropertyValue("filter") instanceof AcceptOnceBlobNameFilter);
        assertEquals(TestPropertiesAccessor.getDefaultContainerName(), adapterDirect.getPropertyValue("containerName"));
    }

    @Test
    public void testInboundAdapterLoadFull() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "InboundChannelAdapterParserTest-full-context.xml", this.getClass());

        Object bean = context.getBean("inbound-adapter");
        assertNotNull("Adapter not found", bean);
        System.out.println(bean.getClass());
        DirectFieldAccessor d = new DirectFieldAccessor(bean);
        Object value = d.getPropertyValue("source");
        assertNotNull("Source not found", value);
        System.out.println(value.getClass());

        ReadingMessageSource rms = (ReadingMessageSource) value;
        DirectFieldAccessor adapterDirect = new DirectFieldAccessor(rms);
        assertNotNull("'template' not found", adapterDirect.getPropertyValue("template"));
        assertNotNull("'filter' queue not found", adapterDirect.getPropertyValue("filter"));
        assertTrue(adapterDirect.getPropertyValue("filter") instanceof PatternMatchingBlobNameFilter);
        assertEquals(TestPropertiesAccessor.getDefaultContainerName(), adapterDirect.getPropertyValue("containerName"));
    }

    /**
     * Used in spring context for parser tests.
     * 
     * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
     * 
     */
    static class MockBlobObjectComparator implements BlobDetailsComparator {

        public int compare(BlobDetails o1, BlobDetails o2) {
            return 0;
        }

    }
}
