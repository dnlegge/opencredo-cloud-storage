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

package org.opencredo.aws.s3;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.opencredo.aws.AwsCredentials;
import org.opencredo.aws.S3Operations;
import org.opencredo.storage.BlobObject;
import org.opencredo.storage.StorageCommunicationException;
import org.opencredo.storage.StorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

//TODO: Verify data transmission
//TODO: Add support for Access control lists
/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 */
public class S3Template implements S3Operations {
    private final static Logger LOG = LoggerFactory.getLogger(S3Template.class);

    private final S3Service s3Service;

    private final String defaultBucketName;

    /**
     * 
     * @param awsCredentials
     * @throws StorageCommunicationException
     */
    public S3Template(final AwsCredentials awsCredentials) throws StorageCommunicationException {
        this(awsCredentials, "bucket");
    }

    /**
     * @param awsCredentials
     * @param defaultBucketName
     * @throws StorageCommunicationException
     */
    public S3Template(final AwsCredentials awsCredentials, final String defaultBucketName)
            throws StorageCommunicationException {
        Assert.notNull(awsCredentials.getAccessKey(), "Access key is not provided");
        Assert.notNull(awsCredentials.getSecretAccessKey(), "Secret access key is not provided");

        this.defaultBucketName = defaultBucketName;
        try {
            s3Service = new RestS3Service(new org.jets3t.service.security.AWSCredentials(awsCredentials.getAccessKey(),
                    awsCredentials.getSecretAccessKey()));
        } catch (S3ServiceException e) {
            throw new StorageCommunicationException(e);
        }
    }

    // **********************************
    // CONFIGURATION
    // **********************************

    /**
     * @param bucketName
     * @throws StorageCommunicationException
     * @see org.opencredo.aws.S3Operations#createBucket(java.lang.String)
     */
    public void createBucket(String bucketName) throws StorageCommunicationException {
        Assert.notNull(bucketName, "Bucket name cannot be null");
        try {
            s3Service.createBucket(new S3Bucket(bucketName));
        } catch (S3ServiceException e) {
            throw new StorageCommunicationException("Bucket creation problem", e);
        }
    }

    /**
     * @param bucketName
     * @throws StorageCommunicationException
     * @see org.opencredo.aws.S3Operations#deleteBucket(java.lang.String)
     */
    public void deleteBucket(String bucketName) throws StorageCommunicationException {
        Assert.notNull(bucketName, "Bucket name cannot be null");
        LOG.debug("Delete bucket '{}'", bucketName);
        try {
            s3Service.deleteBucket(new S3Bucket(bucketName));
        } catch (S3ServiceException e) {
            throw new StorageCommunicationException("Bucket deletion problem", e);
        }
    }

    /**
     * @param bucketName
     * @param key
     * @throws StorageCommunicationException
     * @see org.opencredo.aws.S3Operations#deleteObject(java.lang.String,
     *      java.lang.String)
     */
    public void deleteObject(String bucketName, String key) throws StorageCommunicationException {
        Assert.notNull(bucketName, "Bucket name cannot be null");
        LOG.debug("Delete object '{}' in bucket '{}'", key, bucketName);
        try {
            s3Service.deleteObject(new S3Bucket(bucketName), key);
        } catch (S3ServiceException e) {
            throw new StorageCommunicationException("Bucket deletion problem", e);
        }
    }

    /**
     * @return
     * @throws StorageCommunicationException
     * @see org.opencredo.aws.S3Operations#listBuckets()
     */
    public String[] listBuckets() throws StorageCommunicationException {
        LOG.debug("List buckets");
        try {
            S3Bucket[] s3buckets = s3Service.listAllBuckets();
            String bucketNames[] = new String[s3buckets.length];
            for (int i = 0; i < s3buckets.length; i++)
                bucketNames[i] = s3buckets[i].getName();
            return bucketNames;
        } catch (S3ServiceException e) {
            throw new StorageCommunicationException("Bucket list problem", e);
        }
    }

    /**
     * @param bucketName
     * @return
     * @throws StorageCommunicationException
     * @see org.opencredo.aws.S3Operations#getBucketStatus(java.lang.String)
     */
    public BucketStatus getBucketStatus(String bucketName) throws StorageCommunicationException {
        LOG.debug("Get bucket '{}' status", bucketName);
        try {
            int bucketStatus = this.s3Service.checkBucketStatus(bucketName);
            switch (bucketStatus) {
            case S3Service.BUCKET_STATUS__MY_BUCKET:
                return BucketStatus.MINE;
            case S3Service.BUCKET_STATUS__DOES_NOT_EXIST:
                return BucketStatus.DOES_NOT_EXIST;
            case S3Service.BUCKET_STATUS__ALREADY_CLAIMED:
                return BucketStatus.ALREADY_CLAIMED;
            default:
                throw new StorageException("Unrecognised bucket status: " + bucketStatus);
            }
        } catch (S3ServiceException s3E) {
            throw new StorageCommunicationException("Failed to get status of bucket name " + bucketName, s3E);
        }
    }

    /**
     * @param bucketName
     * @return
     * @throws StorageCommunicationException
     * @see org.opencredo.aws.S3Operations#listBucketObjects(java.lang.String)
     */
    public List<BlobObject> listBucketObjects(String bucketName) throws StorageCommunicationException {
        LOG.debug("Get objects list for bucket '{}'", bucketName);
        try {
            S3Object[] s3Objects = this.s3Service.listObjects(new S3Bucket(bucketName));
            List<BlobObject> keys = new ArrayList<BlobObject>(s3Objects.length);
            for (S3Object s : s3Objects) {
                keys.add(new BlobObject(s.getBucketName(), s.getKey(), s.getETag(), s.getLastModifiedDate()));
            }
            return keys;
        } catch (S3ServiceException e) {
            throw new StorageCommunicationException("Failed to get bucket " + bucketName + " object details.", e);
        }
    }

    // **********************************
    // SEND/RECEIVE
    // **********************************

    // ********************** String send

    /**
     * @param key
     * @param stringToSend
     * @throws StorageCommunicationException
     * @see org.opencredo.aws.S3Operations#send(java.lang.String,
     *      java.lang.String)
     */
    public void send(String key, String stringToSend) throws StorageCommunicationException {
        Assert.notNull(this.defaultBucketName, "Default bucket name is not provided");
        send(defaultBucketName, key, stringToSend);
    }

    /**
     * @param bucketName
     * @param key
     * @param stringToSend
     * @throws StorageCommunicationException
     * @see org.opencredo.aws.S3Operations#send(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public void send(String bucketName, String key, String stringToSend) throws StorageCommunicationException {
        Assert.notNull(bucketName, "Bucket name cannot be null");
        LOG.debug("Send string to bucket '{}' with key '{}'", bucketName, key);
        try {
            s3Service.putObject(new S3Bucket(bucketName), new S3Object(key, stringToSend));
        } catch (S3ServiceException e) {
            throw new StorageCommunicationException("Sending string problem", e);
        } catch (NoSuchAlgorithmException e) {
            throw new StorageCommunicationException("No such algorithm", e);
        } catch (IOException e) {
            throw new StorageCommunicationException("Sending string IO problem", e);
        }
    }

    // ********************** File send

    /**
     * @param fileToSend
     * @throws StorageCommunicationException
     * @see org.opencredo.aws.S3Operations#send(java.io.File)
     */
    public void send(File fileToSend) throws StorageCommunicationException {
        Assert.notNull(this.defaultBucketName, "Default bucket name is not provided");
        send(defaultBucketName, fileToSend);
    }

    /**
     * @param bucketName
     * @param fileToSend
     * @throws StorageCommunicationException
     * @see org.opencredo.aws.S3Operations#send(java.lang.String, java.io.File)
     */
    public void send(String bucketName, File fileToSend) throws StorageCommunicationException {
        Assert.notNull(bucketName, "Bucket name cannot be null");
        sendFile(bucketName, null, fileToSend, false);
    }

    /**
     * @param bucketName
     * @param key
     * @param fileToSend
     * @throws StorageCommunicationException
     * @see org.opencredo.aws.S3Operations#send(java.lang.String,
     *      java.lang.String, java.io.File)
     */
    public void send(String bucketName, String key, File fileToSend) throws StorageCommunicationException {
        Assert.notNull(bucketName, "Bucket name cannot be null");
        sendFile(bucketName, key, fileToSend, true);
    }

    /**
     * 
     * @param bucketName
     * @param key
     * @param fileToSend
     * @param useKey
     *            <code>true</code> if provided key should be used,
     *            <code>false</code> - provided key will be ignored.
     * @throws StorageCommunicationException
     */
    private void sendFile(String bucketName, String key, File fileToSend, boolean useKey)
            throws StorageCommunicationException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Send file '{}' to bucket '{}' {} '{}'", new Object[] { fileToSend.getName(), bucketName,
                    (useKey ? "and use key" : "and ignore key"), key });
        }

        try {
            S3Object object = new S3Object(fileToSend);
            if (useKey) {
                object.setKey(key);
            }
            s3Service.putObject(new S3Bucket(bucketName), object);
        } catch (S3ServiceException e) {
            throw new StorageCommunicationException("Sending file problem", e);
        } catch (NoSuchAlgorithmException e) {
            throw new StorageCommunicationException("No such algorithm", e);
        } catch (IOException e) {
            throw new StorageCommunicationException("Sending string IO problem", e);
        }
    }

    // ********************** Input stream send

    /**
     * @param key
     * @param is
     * @throws StorageCommunicationException
     * @see org.opencredo.aws.S3Operations#send(java.lang.String,
     *      java.io.InputStream)
     */
    public void send(String key, InputStream is) throws StorageCommunicationException {
        Assert.notNull(this.defaultBucketName, "Default bucket name is not provided");
        send(defaultBucketName, key, is);
    }

    /**
     * @param bucketName
     * @param key
     * @param is
     * @throws StorageCommunicationException
     * @see org.opencredo.aws.S3Operations#send(java.lang.String,
     *      java.lang.String, java.io.InputStream)
     */
    public void send(String bucketName, String key, InputStream is) throws StorageCommunicationException {
        Assert.notNull(bucketName, "Bucket name cannot be null");
        LOG.debug("Send input-stream to bucket '{}' with key '{}'", bucketName, key);
        try {
            S3Object s3ObjectToSend = new S3Object(key);
            s3ObjectToSend.setDataInputStream(is);
            s3ObjectToSend.setContentLength(is.available());
            s3Service.putObject(new S3Bucket(bucketName), s3ObjectToSend);
        } catch (IOException e) {
            throw new StorageCommunicationException("Sending input stream IO problem", e);
        } catch (S3ServiceException e) {
            throw new StorageCommunicationException("Sending input stream problem", e);
        }
    }

    // ********************** String receive

    /**
     * @param keyName
     * @return
     * @throws StorageCommunicationException
     * @see org.opencredo.aws.S3Operations#receiveAsString(java.lang.String)
     */
    public String receiveAsString(String keyName) throws StorageCommunicationException {
        Assert.notNull(this.defaultBucketName, "Default bucket name is not provided");
        return receiveAsString(defaultBucketName, keyName);
    }

    /**
     * @param bucketName
     * @param key
     * @return
     * @throws StorageCommunicationException
     * @see org.opencredo.aws.S3Operations#receiveAsString(java.lang.String,
     *      java.lang.String)
     */
    public String receiveAsString(String bucketName, String key) throws StorageCommunicationException {
        Assert.notNull(bucketName, "Bucket name cannot be null");
        LOG.debug("Receive string from bucket '{}' with key '{}'", bucketName, key);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(s3Service.getObject(new S3Bucket(bucketName),
                    key).getDataInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = br.readLine()) != null) {
                if (sb.length() != 0) {
                    sb.append("\n");
                }
                sb.append(line);
            }
            br.close();
            return sb.toString();
        } catch (S3ServiceException e) {
            throw new StorageCommunicationException("Receiving as string problem", e);
        } catch (IOException e) {
            throw new StorageCommunicationException("Receiving as string IO problem", e);
        }
    }

    /**
     * @param key
     * @return
     * @throws StorageCommunicationException
     * @see org.opencredo.aws.S3Operations#receiveAsFile(java.lang.String)
     */
    public File receiveAsFile(String key) throws StorageCommunicationException {
        Assert.notNull(this.defaultBucketName, "Default bucket name is not provided");
        return receiveAsFile(defaultBucketName, key);
    }

    /**
     * @param bucketName
     * @param key
     * @return
     * @throws StorageCommunicationException
     * @see org.opencredo.aws.S3Operations#receiveAsFile(java.lang.String,
     *      java.lang.String)
     */
    public File receiveAsFile(String bucketName, String key) throws StorageCommunicationException {
        Assert.notNull(bucketName, "Bucket name cannot be null");
        LOG.debug("Receive file from bucket '{}' with key '{}'", bucketName, key);
        try {
            return s3Service.getObject(new S3Bucket(bucketName), key).getDataInputFile();
        } catch (S3ServiceException e) {
            throw new StorageCommunicationException("Receiving file problem", e);
        }
    }

    /**
     * @param key
     * @return
     * @throws StorageCommunicationException
     * @see org.opencredo.aws.S3Operations#receiveAsInputStream(java.lang.String)
     */
    public InputStream receiveAsInputStream(String key) throws StorageCommunicationException {
        Assert.notNull(this.defaultBucketName, "Default bucket name is not provided");
        return receiveAsInputStream(defaultBucketName, key);
    }

    /**
     * @param bucketName
     * @param key
     * @return
     * @throws StorageCommunicationException
     * @see org.opencredo.aws.S3Operations#receiveAsInputStream(java.lang.String,
     *      java.lang.String)
     */
    public InputStream receiveAsInputStream(String bucketName, String key) throws StorageCommunicationException {
        Assert.notNull(bucketName, "Bucket name cannot be null");
        LOG.debug("Receive input-stream from bucket '{}' with key '{}'", bucketName, key);
        try {
            return s3Service.getObject(new S3Bucket(bucketName), key).getDataInputStream();
        } catch (S3ServiceException e) {
            throw new StorageCommunicationException("Receiving input stream problem", e);
        }
    }

    /**
     * @return the defaultBucketName
     */
    public String getDefaultBucketName() {
        return defaultBucketName;
    }

}