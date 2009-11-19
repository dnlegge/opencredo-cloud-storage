package com.opencredo.integration.s3;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.service.S3ObjectsChunk;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import org.springframework.integration.core.Message;
import static  org.jets3t.service.S3Service.*;

@RunWith(MockitoJUnitRunner.class)
public class S3FileReadingMessageSourceTest {
	
	private final Log logger = LogFactory.getLog(this.getClass());
	
	private static final String bucketName = "sibucket";
	
	private S3InboundAdapter systemUnderTest;
	
	private  S3Bucket s3Bucket = new S3Bucket("sibucket", "LOCATION_EUROPE");
	private S3Object[] s3ObjectArray = new S3Object[]{new S3Object(s3Bucket, "test.txt")};
	
	@Test
	public void testChunkCanBeCreated(){
		S3ObjectsChunk chunk = new S3ObjectsChunk(null, null, s3ObjectArray, null, null);
		assertEquals("not expected bucket", chunk.getObjects()[0].getBucketName(), "sibucket");
		assertNotNull("chunk cannot be created.", chunk);
	}
	
    @Test
    public void testReceiveMessage() throws S3ServiceException {
    	S3Bucket s3BucketMock = mock(S3Bucket.class);
    	S3Service s3ServiceMock = mock(RestS3Service.class);
    	S3Resource s3resourceMock = mock(S3Resource.class);
    	when(s3resourceMock.getS3Service()).thenReturn(s3ServiceMock, s3ServiceMock, s3ServiceMock);
    	when(s3resourceMock.getS3Bucket()).thenReturn(s3BucketMock, s3BucketMock, s3BucketMock);
    	
    	when(s3ServiceMock.checkBucketStatus(anyString())).thenReturn(BUCKET_STATUS__MY_BUCKET);    	
    	when(s3ServiceMock.listObjectsChunked(anyString(),
	             anyString(), anyString(), anyLong(), anyString(), anyBoolean())).thenReturn(new S3ObjectsChunk(null, null, s3ObjectArray, null, null));
    	when(s3BucketMock.getName()).thenReturn("sibucket","sibucket");
    	
    	systemUnderTest = new S3InboundAdapter();
    	systemUnderTest.setS3Resource(s3resourceMock);
    	
    	Message<Map> message = systemUnderTest.receive();
    	
    	assertNotNull("Queue should not be empty at this point.", systemUnderTest.getQueueToBeReceived());
    	
    	assertEquals("unexpected message content", "sibucket", message.getPayload().get("bucketName"));
    	assertEquals("unexpected key", "test.txt", message.getPayload().get("key"));
    }
    
}