package com.opencredo.integration.s3.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class S3ResourceParser implements BeanDefinitionParser {

	public BeanDefinition parse(Element element, ParserContext parserContext) {
		 BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition("com.opencredo.integration.s3.S3Resource");
		  
		 String bucketName = element.getAttribute(S3AdapterParserUtils.BUCKET_NAME_ATTRIBUTE);
		  
		 builder.addPropertyReference(S3AdapterParserUtils.BUCKET_NAME_PROPERTY, bucketName);
		  
		 return builder.getBeanDefinition();
	}

}
