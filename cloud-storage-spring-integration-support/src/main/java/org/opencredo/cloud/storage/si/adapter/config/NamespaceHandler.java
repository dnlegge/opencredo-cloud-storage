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

import org.springframework.integration.config.xml.AbstractIntegrationNamespaceHandler;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 */
public class NamespaceHandler extends AbstractIntegrationNamespaceHandler {

    public void init() {
        this.registerBeanDefinitionParser("inbound-channel-adapter", new InboundChannelAdapterParser());
        this.registerBeanDefinitionParser("outbound-channel-adapter", new OutboundChannelAdapterParser());

    }
}
