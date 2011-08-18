package org.opencredo.cloud.storage.s3;

import org.opencredo.cloud.storage.ContainerStatus;
import org.opencredo.cloud.storage.StorageCommunicationException;
import org.opencredo.cloud.storage.StorageOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

public abstract class S3Template implements StorageOperations, InitializingBean {
    protected final static Logger LOG = LoggerFactory.getLogger(S3Template.class);
    protected static final String BUCKET_NAME_CANNOT_BE_NULL = "Bucket name cannot be null";
    private static final String BUCKET_CREATION_PROBLEM = "Bucket creation problem";
    private static final String BUCKET_DELETION_PROBLEM = "Bucket deletion problem";
    private static final String SERVICE_PROBLEM = "Service problem";
    private static final String RECEIVING_FILE_PROBLEM = "Receiving file problem";
    protected String defaultContainerName;
    protected AwsCredentials awsCredentials;

    public S3Template(final String defaultContainerName, final AwsCredentials awsCredentials) {
        this.defaultContainerName = defaultContainerName;
        this.awsCredentials = awsCredentials;
    }

    /**
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() {
        ContainerStatus containerStatus = checkContainerStatus(defaultContainerName);
        Assert.isTrue(containerStatus != ContainerStatus.ALREADY_CLAIMED, "Default bucket '" + defaultContainerName + "' already claimed.");
    }

    public abstract ContainerStatus checkContainerStatus(String containerName) throws StorageCommunicationException;

    /**
     * @param defaultContainerName the defaultContainerName to set
     */
    public void setDefaultContainerName(String defaultContainerName) {
        this.defaultContainerName = defaultContainerName;
    }

    /**
     * @return the defaultContainerName
     */
    public String getDefaultContainerName() {
        return defaultContainerName;
    }
}
