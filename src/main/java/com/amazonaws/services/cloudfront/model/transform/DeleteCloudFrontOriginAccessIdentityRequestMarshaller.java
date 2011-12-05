/*
 * Copyright 2010-2011 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 * 
 *  http://aws.amazon.com/apache2.0
 * 
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.services.cloudfront.model.transform;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.amazonaws.AmazonClientException;
import com.amazonaws.Request;
import com.amazonaws.DefaultRequest;
import com.amazonaws.http.HttpMethodName;
import com.amazonaws.services.cloudfront.model.*;
import com.amazonaws.transform.Marshaller;
import com.amazonaws.util.StringInputStream;
import com.amazonaws.util.StringUtils;
import com.amazonaws.util.XMLWriter;

/**
 * Delete Cloud Front Origin Access Identity Request Marshaller
 */
public class DeleteCloudFrontOriginAccessIdentityRequestMarshaller implements Marshaller<Request<DeleteCloudFrontOriginAccessIdentityRequest>, DeleteCloudFrontOriginAccessIdentityRequest> {

    public Request<DeleteCloudFrontOriginAccessIdentityRequest> marshall(DeleteCloudFrontOriginAccessIdentityRequest deleteCloudFrontOriginAccessIdentityRequest) {
        if (deleteCloudFrontOriginAccessIdentityRequest == null) {
		    throw new AmazonClientException("Invalid argument passed to marshall(...)");
		}

        Request<DeleteCloudFrontOriginAccessIdentityRequest> request = new DefaultRequest<DeleteCloudFrontOriginAccessIdentityRequest>(deleteCloudFrontOriginAccessIdentityRequest, "AmazonCloudFront");
        request.setHttpMethod(HttpMethodName.DELETE);
        request.addHeader("If-Match", deleteCloudFrontOriginAccessIdentityRequest.getIfMatch());
	            

        String uriResourcePath = "2010-11-01/origin-access-identity/cloudfront/{Id}"; 
        uriResourcePath = uriResourcePath.replace("{Id}", getString(deleteCloudFrontOriginAccessIdentityRequest.getId())); 
        request.setResourcePath(uriResourcePath);
	            
        StringWriter stringWriter = new StringWriter();
        XMLWriter xmlWriter = new XMLWriter(stringWriter, "http://cloudfront.amazonaws.com/doc/2010-11-01/");
        
        



        try {
            request.setContent(new StringInputStream(stringWriter.getBuffer().toString()));
        } catch (UnsupportedEncodingException e) {
            throw new AmazonClientException("Unable to marshall request to XML", e);
        }

        return request;
    }
    
    private String getString(String s) {
        if (s == null) return "";
        return s;
    }
}