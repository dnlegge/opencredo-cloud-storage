/* Copyright 2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencredo.aws.si.s3;

import java.io.File;

import org.opencredo.aws.AwsCredentials;
import org.opencredo.aws.s3.BucketStatus;
import org.opencredo.aws.s3.S3Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageHandler;
import org.springframework.integration.message.MessageHandlingException;
import org.springframework.util.Assert;

/**
 * MessageHandler for writing S3Objects to a Bucket. Depending on the Message's
 * payload, the relevant handler turns the payload into an S3Object. If the
 * payload is string, the content is written to a default destination. If the
 * payload is S3Object or File, it should have the filename property in it's
 * header.
 * 
 * @author Eren Aykin (eren.aykin@opencredo.com)
 */
public class S3WritingMessageHandler implements MessageHandler, InitializingBean {
    private final static Logger LOG = LoggerFactory.getLogger(S3WritingMessageHandler.class);

    private volatile S3KeyNameGenerator s3KeyNameGenerator = new S3DefaultKeyNameGenerator();

    // private AWSCredentials awsCredentials;

    private S3Template s3Template;
    private final String bucketName;

    public S3WritingMessageHandler(AwsCredentials awsCredentials, final String bucketName) {
        this.s3Template = new S3Template(awsCredentials);
        this.bucketName = bucketName;
    }

    /**
     * write the content of Message to S3 Bucket with handlers that convert the
     * message payload to s3object.
     * 
     * @param message
     */
    public void handleMessage(Message<?> message) {
        Assert.notNull(message, "message must not be null");
        Object payload = message.getPayload();
        Assert.notNull(payload, "message payload must not be null");
        if (LOG.isDebugEnabled()) {
            LOG.debug("message: " + message);
        }
        String key;
        if (message.getHeaders().containsKey("key")) {
            key = message.getHeaders().get("key", String.class);
        } else {
            key = s3KeyNameGenerator.generateKeyName(message);
        }

        try {
            if ((payload instanceof File)) {
                s3Template.send(bucketName, key, (File) payload);
            } else if (payload instanceof String) {
                s3Template.send(bucketName, key, (String) payload);
            } else {
                throw new IllegalArgumentException("unsupported Message payload type [" + payload.getClass().getName()
                        + "]");
            }
        } catch (IllegalArgumentException e) {
            throw new S3IntegrationException("Illegal Argument", e);
        } catch (Exception e) {
            throw new MessageHandlingException(message, "failed to write Message payload to file", e);
        }
    }

    /**
     * @param fileNameGenerator
     */
    public void setS3KeyNameGenerator(S3KeyNameGenerator fileNameGenerator) {
        Assert.notNull(fileNameGenerator, "FileNameGenerator must not be null");
        this.s3KeyNameGenerator = fileNameGenerator;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.s3Template, "No template");
        Assert.isTrue(s3Template.getBucketStatus(bucketName) == BucketStatus.MINE, "Bucket '" + bucketName
                + "' is not accessible.");
    }

}
