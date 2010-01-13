package org.opencredo.aws.s3.transformer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.model.S3Object;
import org.opencredo.aws.s3.AWSCredentials;
import org.opencredo.aws.s3.S3IntegrationException;
import org.opencredo.aws.s3.S3Template;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;


public class S3ToFileTransformer {
	//private static final int BUFFER_SIZE = 10000;
	//private final Log logger = LogFactory.getLog(this.getClass());
	
	private AWSCredentials awsCredentials;
	
	@SuppressWarnings("unchecked")
	public Message<File> transform(Message<?> s3MetaDataMapMessage) throws IOException {
		//if (logger.isDebugEnabled()) logger.debug(s3MetaDataMapMessage.getPayload());
		Map<String, Object> metaDataMap = (Map<String, Object>) s3MetaDataMapMessage.getPayload();
		try {
			S3Template s3Template = new S3Template(awsCredentials);
			
			/* 
			//This returns Null even if the file is set with setInputFile 
			File receivedFile = s3Service.getObject(new S3Bucket("oc-test"), "testFile.test").getDataInputFile();
			*/
			
			MessageBuilder<File> builder;
			String fileName = metaDataMap.get("key").toString();
			String bucketName = metaDataMap.get("bucketName").toString();
			S3Object s3object = s3Template.getS3Service().getObject(s3Template.getS3Service().getBucket(bucketName), fileName);
			
			File fileToReturn = new File(fileName);
			InputStream is = s3object.getDataInputStream();
			FileOutputStream out = new FileOutputStream(fileToReturn);
		    byte buf[]=new byte[(int) s3object.getContentLength()];
		    int len;
		    while((len=is.read(buf))>0) out.write(buf,0,len);
		    out.close();
		    is.close();
			
			builder = (MessageBuilder<File>) MessageBuilder.withPayload(fileToReturn);
			return builder.build();
		} 
		catch (S3ServiceException e) {
			throw new S3IntegrationException("Message Transform Error", e);
		}				
	}
}
