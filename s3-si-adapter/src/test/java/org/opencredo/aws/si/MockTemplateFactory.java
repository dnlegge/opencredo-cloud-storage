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
package org.opencredo.aws.si;

import static org.mockito.Mockito.*;
import org.opencredo.aws.AwsOperations;
import org.opencredo.aws.s3.BucketStatus;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 * 
 */
public class MockTemplateFactory implements FactoryBean {

    private AwsOperations template;

    /**
     * 
     */
    public MockTemplateFactory() {
        template = mock(AwsOperations.class);
        when(template.getBucketStatus(anyString())).thenReturn(BucketStatus.MINE);
    }

    /**
     * @return
     * @throws Exception
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
    public Object getObject() throws Exception {
        return template;
    }

    /**
     * @return
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    @SuppressWarnings("unchecked")
    public Class getObjectType() {
        return AwsOperations.class;
    }

    /**
     * @return
     * @see org.springframework.beans.factory.FactoryBean#isSingleton()
     */
    public boolean isSingleton() {
        return true;
    }

    /**
     * @return the template
     */
    public AwsOperations getTemplate() {
        return template;
    }
}
