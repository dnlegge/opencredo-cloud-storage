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

package org.opencredo.aws;

import org.opencredo.aws.s3.BucketStatus;
import org.opencredo.storage.StorageCommunicationException;
import org.opencredo.storage.StorageOperations;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 */
public interface S3Operations extends StorageOperations {

    // **********************************
    // CONFIGURATION
    // **********************************

    /**
     * Create a new s3 bucket with the provided bucket name
     * 
     * @param bucketName
     * @throws StorageCommunicationException
     */
    public void createBucket(String bucketName) throws StorageCommunicationException;

    /**
     * Delete the s3 bucket with the provided bucket name
     * 
     * @param bucketName
     * @throws StorageCommunicationException
     */
    public void deleteBucket(String bucketName) throws StorageCommunicationException;

    /**
     * Delete the s3 bucket with the provided bucket name
     * 
     * @param bucketName
     * @param key
     * @throws StorageCommunicationException
     */
    public void deleteObject(String bucketName, String key) throws StorageCommunicationException;

    /**
     * Get the status of the bucket.
     * 
     * @param bucketName
     * @return
     */
    public BucketStatus getBucketStatus(String bucketName) throws StorageCommunicationException;

}