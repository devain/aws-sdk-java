/*
 * Copyright 2010-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Portions copyright 2006-2009 James Murty. Please see LICENSE.txt
 * for applicable license terms and NOTICE.txt for applicable notices.
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
package com.amazonaws.services.s3.model.transform;

import static com.amazonaws.util.StringUtils.UTF8;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.internal.Constants;
import com.amazonaws.services.s3.internal.DeleteObjectsResponse;
import com.amazonaws.services.s3.internal.ObjectExpirationResult;
import com.amazonaws.services.s3.internal.ServerSideEncryptionResult;
import com.amazonaws.services.s3.internal.ServiceUtils;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.BucketCrossOriginConfiguration;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration.NoncurrentVersionTransition;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration.Rule;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration.Transition;
import com.amazonaws.services.s3.model.BucketLoggingConfiguration;
import com.amazonaws.services.s3.model.BucketReplicationConfiguration;
import com.amazonaws.services.s3.model.BucketTaggingConfiguration;
import com.amazonaws.services.s3.model.BucketVersioningConfiguration;
import com.amazonaws.services.s3.model.BucketWebsiteConfiguration;
import com.amazonaws.services.s3.model.CORSRule;
import com.amazonaws.services.s3.model.CORSRule.AllowedMethods;
import com.amazonaws.services.s3.model.CanonicalGrantee;
import com.amazonaws.services.s3.model.CompleteMultipartUploadResult;
import com.amazonaws.services.s3.model.CopyObjectResult;
import com.amazonaws.services.s3.model.DeleteObjectsResult.DeletedObject;
import com.amazonaws.services.s3.model.EmailAddressGrantee;
import com.amazonaws.services.s3.model.Grantee;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.MultiObjectDeleteException.DeleteError;
import com.amazonaws.services.s3.model.MultipartUpload;
import com.amazonaws.services.s3.model.MultipartUploadListing;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.Owner;
import com.amazonaws.services.s3.model.PartListing;
import com.amazonaws.services.s3.model.PartSummary;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.RedirectRule;
import com.amazonaws.services.s3.model.ReplicationDestinationConfig;
import com.amazonaws.services.s3.model.ReplicationRule;
import com.amazonaws.services.s3.model.RequestPaymentConfiguration;
import com.amazonaws.services.s3.model.RequestPaymentConfiguration.Payer;
import com.amazonaws.services.s3.model.RoutingRule;
import com.amazonaws.services.s3.model.RoutingRuleCondition;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.S3VersionSummary;
import com.amazonaws.services.s3.model.StorageClass;
import com.amazonaws.services.s3.model.TagSet;
import com.amazonaws.services.s3.model.VersionListing;
import com.amazonaws.util.DateUtils;

/**
 * XML Sax parser to read XML documents returned by S3 via the REST interface,
 * converting these documents into objects.
 */
public class XmlResponsesSaxParser {
    private static final Log log = LogFactory.getLog(XmlResponsesSaxParser.class);

    private XMLReader xr = null;

    private boolean sanitizeXmlDocument = true;

    /**
     * Constructs the XML SAX parser.
     *
     * @throws AmazonClientException
     */
    public XmlResponsesSaxParser() throws AmazonClientException {
        // Ensure we can load the XML Reader.
        try {
            xr = XMLReaderFactory.createXMLReader();
        } catch (SAXException e) {
            throw new AmazonClientException("Couldn't initialize a SAX driver to create an XMLReader", e);
        }
    }

    /**
     * Parses an XML document from an input stream using a document handler.
     *
     * @param handler
     *            the handler for the XML document
     * @param inputStream
     *            an input stream containing the XML document to parse
     *
     * @throws IOException
     *             on error reading from the input stream (ie connection reset)
     * @throws AmazonClientException
     *             on error with malformed XML, etc
     */
    protected void parseXmlInputStream(DefaultHandler handler, InputStream inputStream)
            throws IOException {
        try {

            if (log.isDebugEnabled()) {
                log.debug("Parsing XML response document with handler: " + handler.getClass());
            }

            BufferedReader breader = new BufferedReader(new InputStreamReader(inputStream,
                Constants.DEFAULT_ENCODING));
            xr.setContentHandler(handler);
            xr.setErrorHandler(handler);
            xr.parse(new InputSource(breader));

        } catch (IOException e) {
            throw e;

        } catch (Throwable t) {
            try {
                inputStream.close();
            } catch (IOException e) {
                if (log.isErrorEnabled()) {
                    log.error("Unable to close response InputStream up after XML parse failure", e);
                }
            }
            throw new AmazonClientException("Failed to parse XML document with handler "
                + handler.getClass(), t);
        }
    }

    protected InputStream sanitizeXmlDocument(DefaultHandler handler, InputStream inputStream)
            throws IOException {

        if (!sanitizeXmlDocument) {
            // No sanitizing will be performed, return the original input stream unchanged.
            return inputStream;
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Sanitizing XML document destined for handler " + handler.getClass());
            }

            InputStream sanitizedInputStream = null;

            try {

                /*
                 * Read object listing XML document from input stream provided into a
                 * string buffer, so we can replace troublesome characters before
                 * sending the document to the XML parser.
                 */
                StringBuilder listingDocBuffer = new StringBuilder();
                BufferedReader br = new BufferedReader(
                    new InputStreamReader(inputStream, Constants.DEFAULT_ENCODING));

                char[] buf = new char[8192];
                int read = -1;
                while ((read = br.read(buf)) != -1) {
                    listingDocBuffer.append(buf, 0, read);
                }
                br.close();

                /*
                 * Replace any carriage return (\r) characters with explicit XML
                 * character entities, to prevent the SAX parser from
                 * misinterpreting 0x0D characters as 0x0A and being unable to
                 * parse the XML.
                 */
                String listingDoc = listingDocBuffer.toString().replaceAll("\r", "&#013;");

                sanitizedInputStream = new ByteArrayInputStream(
                    listingDoc.getBytes(UTF8));

            } catch (IOException e) {
                throw e;

            } catch (Throwable t) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    if (log.isErrorEnabled()) {
                        log.error("Unable to close response InputStream after failure sanitizing XML document", e);
                    }
                }
                throw new AmazonClientException("Failed to sanitize XML document destined for handler "
                    + handler.getClass(), t);
            }
            return sanitizedInputStream;
        }
    }

    /**
     * Checks if the specified string is empty or null and if so, returns null.
     * Otherwise simply returns the string.
     *
     * @param s
     *            The string to check.
     * @return Null if the specified string was null, or empty, otherwise
     *         returns the string the caller passed in.
     */
    private static String checkForEmptyString(String s) {
        if (s == null) return null;
        if (s.length() == 0) return null;

        return s;
    }

    /**
     * Safely parses the specified string as an integer and returns the value.
     * If a NumberFormatException occurs while parsing the integer, an error is
     * logged and -1 is returned.
     *
     * @param s
     *            The string to parse and return as an integer.
     *
     * @return The integer value of the specified string, otherwise -1 if there
     *         were any problems parsing the string as an integer.
     */
    private static int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            log.error("Unable to parse integer value '" + s + "'", nfe);
        }

        return -1;
    }

    /**
     * Safely parses the specified string as a long and returns the value. If a
     * NumberFormatException occurs while parsing the long, an error is logged
     * and -1 is returned.
     *
     * @param s
     *            The string to parse and return as a long.
     *
     * @return The long value of the specified string, otherwise -1 if there
     *         were any problems parsing the string as a long.
     */
    private static long parseLong(String s) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException nfe) {
            log.error("Unable to parse long value '" + s + "'", nfe);
        }

        return -1;
    }

    /**
     * Parses a ListBucket response XML document from an input stream.
     *
     * @param inputStream
     *            XML data input stream.
     * @return the XML handler object populated with data parsed from the XML
     *         stream.
     * @throws AmazonClientException
     */
    public ListBucketHandler parseListBucketObjectsResponse(InputStream inputStream)
            throws IOException {
        ListBucketHandler handler = new ListBucketHandler();
        parseXmlInputStream(handler, sanitizeXmlDocument(handler, inputStream));
        return handler;
    }

    /**
     * Parses a ListVersions response XML document from an input stream.
     *
     * @param inputStream
     *            XML data input stream.
     * @return the XML handler object populated with data parsed from the XML
     *         stream.
     * @throws AmazonClientException
     */
    public ListVersionsHandler parseListVersionsResponse(InputStream inputStream)
            throws IOException {
        ListVersionsHandler handler = new ListVersionsHandler();
        parseXmlInputStream(handler, sanitizeXmlDocument(handler, inputStream));
        return handler;
    }

    /**
     * Parses a ListAllMyBuckets response XML document from an input stream.
     *
     * @param inputStream
     *            XML data input stream.
     * @return the XML handler object populated with data parsed from the XML
     *         stream.
     * @throws AmazonClientException
     */
    public ListAllMyBucketsHandler parseListMyBucketsResponse(InputStream inputStream)
            throws IOException {
        ListAllMyBucketsHandler handler = new ListAllMyBucketsHandler();
        parseXmlInputStream(handler, sanitizeXmlDocument(handler, inputStream));
        return handler;
    }

    /**
     * Parses an AccessControlListHandler response XML document from an input
     * stream.
     *
     * @param inputStream
     *            XML data input stream.
     * @return the XML handler object populated with data parsed from the XML
     *         stream.
     *
     * @throws AmazonClientException
     */
    public AccessControlListHandler parseAccessControlListResponse(InputStream inputStream)
            throws IOException {
        AccessControlListHandler handler = new AccessControlListHandler();
        parseXmlInputStream(handler, inputStream);
        return handler;
    }

    /**
     * Parses a LoggingStatus response XML document for a bucket from an input
     * stream.
     *
     * @param inputStream
     *            XML data input stream.
     * @return the XML handler object populated with data parsed from the XML
     *         stream.
     *
     * @throws AmazonClientException
     */
    public BucketLoggingConfigurationHandler parseLoggingStatusResponse(InputStream inputStream)
            throws IOException {
        BucketLoggingConfigurationHandler handler = new BucketLoggingConfigurationHandler();
        parseXmlInputStream(handler, inputStream);
        return handler;
    }

    public BucketLifecycleConfigurationHandler parseBucketLifecycleConfigurationResponse(InputStream inputStream)
            throws IOException {
        BucketLifecycleConfigurationHandler handler = new BucketLifecycleConfigurationHandler();
        parseXmlInputStream(handler, inputStream);
        return handler;
    }

    public BucketCrossOriginConfigurationHandler parseBucketCrossOriginConfigurationResponse(InputStream inputStream)
            throws IOException {
        BucketCrossOriginConfigurationHandler handler = new BucketCrossOriginConfigurationHandler();
        parseXmlInputStream(handler, inputStream);
        return handler;
    }

    public String parseBucketLocationResponse(InputStream inputStream)
            throws IOException {
        BucketLocationHandler handler = new BucketLocationHandler();
        parseXmlInputStream(handler, inputStream);
        return handler.getLocation();
    }

    public BucketVersioningConfigurationHandler parseVersioningConfigurationResponse(InputStream inputStream)
            throws IOException {
        BucketVersioningConfigurationHandler handler = new BucketVersioningConfigurationHandler();
        parseXmlInputStream(handler, inputStream);
        return handler;
    }

    public BucketWebsiteConfigurationHandler parseWebsiteConfigurationResponse(InputStream inputStream)
            throws IOException {
        BucketWebsiteConfigurationHandler handler = new BucketWebsiteConfigurationHandler();
        parseXmlInputStream(handler, inputStream);
        return handler;
    }


    public BucketReplicationConfigurationHandler parseReplicationConfigurationResponse(InputStream inputStream)
            throws IOException {
        BucketReplicationConfigurationHandler handler = new BucketReplicationConfigurationHandler();
        parseXmlInputStream(handler, inputStream);
        return handler;
    }

    public BucketTaggingConfigurationHandler parseTaggingConfigurationResponse(InputStream inputStream)
            throws IOException {
        BucketTaggingConfigurationHandler handler = new BucketTaggingConfigurationHandler();
        parseXmlInputStream(handler, inputStream);
        return handler;
    }

    public DeleteObjectsHandler parseDeletedObjectsResult(InputStream inputStream)
            throws IOException {
        DeleteObjectsHandler handler = new DeleteObjectsHandler();
        parseXmlInputStream(handler, inputStream);
        return handler;
    }

    public CopyObjectResultHandler parseCopyObjectResponse(InputStream inputStream)
            throws IOException {
        CopyObjectResultHandler handler = new CopyObjectResultHandler();
        parseXmlInputStream(handler, inputStream);
        return handler;
    }

    public CompleteMultipartUploadHandler parseCompleteMultipartUploadResponse(InputStream inputStream)
            throws IOException {
        CompleteMultipartUploadHandler handler = new CompleteMultipartUploadHandler();
        parseXmlInputStream(handler, inputStream);
        return handler;
    }

    public InitiateMultipartUploadHandler parseInitiateMultipartUploadResponse(InputStream inputStream)
            throws IOException {
        InitiateMultipartUploadHandler handler = new InitiateMultipartUploadHandler();
        parseXmlInputStream(handler, inputStream);
        return handler;
    }

    public ListMultipartUploadsHandler parseListMultipartUploadsResponse(InputStream inputStream)
            throws IOException {
        ListMultipartUploadsHandler handler = new ListMultipartUploadsHandler();
        parseXmlInputStream(handler, inputStream);
        return handler;
    }

    public ListPartsHandler parseListPartsResponse(InputStream inputStream)
            throws IOException {
        ListPartsHandler handler = new ListPartsHandler();
        parseXmlInputStream(handler, inputStream);
        return handler;
    }


    /**
     * @param inputStream
     *
     * @return true if the bucket's is configured as Requester Pays, false if it
     *         is configured as Owner pays.
     *
     * @throws AmazonClientException
     */
    public RequestPaymentConfigurationHandler parseRequestPaymentConfigurationResponse(InputStream inputStream)
            throws IOException {
        RequestPaymentConfigurationHandler handler = new RequestPaymentConfigurationHandler();
        parseXmlInputStream(handler, inputStream);
        return handler;
    }

    // ////////////
    // Handlers //
    // ////////////

    /**
     * Handler for ListBucket response XML documents.
     */
    public static class ListBucketHandler extends AbstractHandler {
        private final ObjectListing objectListing = new ObjectListing();

        private S3ObjectSummary currentObject = null;
        private Owner currentOwner = null;
        private String lastKey = null;

        public ObjectListing getObjectListing() {
            return objectListing;
        }

        @Override
        protected void doStartElement(
                String uri,
                String name,
                String qName,
                Attributes attrs) {

            if (in("ListBucketResult")) {
                if (name.equals("Contents")) {
                    currentObject = new S3ObjectSummary();
                    currentObject.setBucketName(objectListing.getBucketName());
                }
            }

            else if (in("ListBucketResult", "Contents")) {
                if (name.equals("Owner")) {
                    currentOwner = new Owner();
                }
            }
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (atTopLevel()) {
                if (name.equals("ListBucketResult")) {
                    /*
                     * S3 only includes the NextMarker XML element if the
                     * request specified a delimiter, but for consistency we'd
                     * like to always give easy access to the next marker if
                     * we're returning a list of results that's truncated.
                     */
                    if (objectListing.isTruncated()
                        && objectListing.getNextMarker() == null) {

                        String nextMarker = null;
                        if (!objectListing.getObjectSummaries().isEmpty()) {
                            nextMarker = objectListing.getObjectSummaries()
                                .get(objectListing.getObjectSummaries().size() - 1)
                                .getKey();

                        } else if (!objectListing.getCommonPrefixes().isEmpty()) {
                            nextMarker = objectListing.getCommonPrefixes()
                                .get(objectListing.getCommonPrefixes().size() - 1);
                        } else {
                            log.error("S3 response indicates truncated results, "
                                    + "but contains no object summaries or "
                                    + "common prefixes.");
                        }

                        objectListing.setNextMarker(nextMarker);
                    }
                }
            }

            else if (in("ListBucketResult")) {
                if (name.equals("Name")) {
                    objectListing.setBucketName(getText());
                    if (log.isDebugEnabled()) {
                        log.debug("Examining listing for bucket: "
                                + objectListing.getBucketName());
                    }

                } else if (name.equals("Prefix")) {
                    objectListing.setPrefix(checkForEmptyString(getText()));

                } else if (name.equals("Marker")) {
                    objectListing.setMarker(checkForEmptyString(getText()));

                } else if (name.equals("NextMarker")) {
                    objectListing.setNextMarker(getText());

                } else if (name.equals("MaxKeys")) {
                    objectListing.setMaxKeys(parseInt(getText()));

                } else if (name.equals("Delimiter")) {
                    objectListing.setDelimiter(checkForEmptyString(getText()));

                } else if (name.equals("EncodingType")) {
                    objectListing.setEncodingType(checkForEmptyString(getText()));

                } else if (name.equals("IsTruncated")) {
                    String isTruncatedStr =
                        getText().toLowerCase(Locale.getDefault());

                    if (isTruncatedStr.startsWith("false")) {
                        objectListing.setTruncated(false);
                    } else if (isTruncatedStr.startsWith("true")) {
                        objectListing.setTruncated(true);
                    } else {
                        throw new IllegalStateException(
                                "Invalid value for IsTruncated field: "
                                + isTruncatedStr);
                    }

                } else if (name.equals("Contents")) {
                    objectListing.getObjectSummaries().add(currentObject);
                    currentObject = null;
                }
            }

            else if (in("ListBucketResult", "Contents")) {
                if (name.equals("Key")) {
                    lastKey = getText();
                    currentObject.setKey(lastKey);

                } else if (name.equals("LastModified")) {
                    currentObject.setLastModified(
                            ServiceUtils.parseIso8601Date(getText()));

                } else if (name.equals("ETag")) {
                    currentObject.setETag(
                            ServiceUtils.removeQuotes(getText()));

                } else if (name.equals("Size")) {
                    currentObject.setSize(parseLong(getText()));

                } else if (name.equals("StorageClass")) {
                    currentObject.setStorageClass(getText());

                } else if (name.equals("Owner")) {
                    currentObject.setOwner(currentOwner);
                    currentOwner = null;
                }
            }

            else if (in("ListBucketResult", "Contents", "Owner")) {
                if (name.equals("ID")) {
                    currentOwner.setId(getText());

                } else if (name.equals("DisplayName")) {
                    currentOwner.setDisplayName(getText());
                }
            }

            else if (in("ListBucketResult", "CommonPrefixes")) {
                if (name.equals("Prefix")) {
                    objectListing.getCommonPrefixes().add(getText());
                }
            }
        }
    }

    /**
     * Handler for ListAllMyBuckets response XML documents. The document is
     * parsed into {@link Bucket}s available via the {@link #getBuckets()}
     * method.
     */
    public static class ListAllMyBucketsHandler extends AbstractHandler {

        private final List<Bucket> buckets = new ArrayList<Bucket>();
        private Owner bucketsOwner = null;

        private Bucket currentBucket = null;

        /**
         * @return the buckets listed in the document.
         */
        public List<Bucket> getBuckets() {
            return buckets;
        }

        /**
         * @return the owner of the buckets.
         */
        public Owner getOwner() {
            return bucketsOwner;
        }

        @Override
        protected void doStartElement(
                String uri,
                String name,
                String qName,
                Attributes attrs) {

            if (in("ListAllMyBucketsResult")) {
                if (name.equals("Owner")) {
                    bucketsOwner = new Owner();
                }
            } else if (in("ListAllMyBucketsResult", "Buckets")) {
                if (name.equals("Bucket")) {
                    currentBucket = new Bucket();
                    currentBucket.setOwner(bucketsOwner);
                }
            }
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (in("ListAllMyBucketsResult", "Owner")) {
                if (name.equals("ID")) {
                    bucketsOwner.setId(getText());

                } else if (name.equals("DisplayName")) {
                    bucketsOwner.setDisplayName(getText());
                }
            }

            else if (in("ListAllMyBucketsResult", "Buckets")) {
                if (name.equals("Bucket")) {
                    buckets.add(currentBucket);
                    currentBucket = null;
                }
            }

            else if (in("ListAllMyBucketsResult", "Buckets", "Bucket")) {
                if (name.equals("Name")) {
                    currentBucket.setName(getText());

                } else if (name.equals("CreationDate")) {
                    Date creationDate = DateUtils.parseISO8601Date(getText());
                    currentBucket.setCreationDate(creationDate);
                }
            }
        }
    }

    /**
     * Handler for AccessControlList response XML documents. The document is
     * parsed into an {@link AccessControlList} object available via the
     * {@link #getAccessControlList()} method.
     */
    public static class AccessControlListHandler extends AbstractHandler {

        private final AccessControlList accessControlList =
            new AccessControlList();

        private Grantee currentGrantee = null;
        private Permission currentPermission = null;

        /**
         * @return an object representing the ACL document.
         */
        public AccessControlList getAccessControlList() {
            return accessControlList;
        }

        @Override
        protected void doStartElement(
                String uri,
                String name,
                String qName,
                Attributes attrs) {

            if (in("AccessControlPolicy")) {
                if (name.equals("Owner")) {
                    accessControlList.setOwner(new Owner());

                }
            }

            else if (in("AccessControlPolicy", "AccessControlList", "Grant")) {
                if (name.equals("Grantee")) {
                    String type = XmlResponsesSaxParser
                        .findAttributeValue( "xsi:type", attrs );

                    if ("AmazonCustomerByEmail".equals(type)) {
                        currentGrantee = new EmailAddressGrantee(null);
                    } else if ("CanonicalUser".equals(type)) {
                        currentGrantee = new CanonicalGrantee(null);
                    } else if ("Group".equals(type)) {
                        /*
                         * Nothing to do for GroupGrantees here since we
                         * can't construct an empty enum value early.
                         */
                    }
                }
            }
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (in("AccessControlPolicy", "Owner")) {
                if (name.equals("ID")) {
                    accessControlList.getOwner().setId(getText());
                } else if (name.equals("DisplayName")) {
                    accessControlList.getOwner().setDisplayName(getText());
                }
            }

            else if (in("AccessControlPolicy", "AccessControlList")) {
                if (name.equals("Grant")) {
                    accessControlList.grantPermission(
                            currentGrantee, currentPermission);

                    currentGrantee = null;
                    currentPermission = null;
                }
            }

            else if (in("AccessControlPolicy", "AccessControlList", "Grant")) {
                if (name.equals("Permission")) {
                    currentPermission = Permission.parsePermission(getText());
                }
            }

            else if (in("AccessControlPolicy", "AccessControlList", "Grant", "Grantee")) {
                if (name.equals("ID")) {
                    currentGrantee.setIdentifier(getText());

                } else if (name.equals("EmailAddress")) {
                    currentGrantee.setIdentifier(getText());

                } else if (name.equals("URI")) {
                    /*
                     * Only GroupGrantees contain an URI element in them, and we
                     * can't construct currentGrantee during startElement for a
                     * GroupGrantee since it's an enum.
                     */
                    currentGrantee = GroupGrantee.parseGroupGrantee(getText());

                } else if (name.equals("DisplayName")) {
                    ((CanonicalGrantee) currentGrantee)
                        .setDisplayName(getText());
                }
            }
        }
    }

    /**
     * Handler for LoggingStatus response XML documents for a bucket. The
     * document is parsed into an {@link BucketLoggingConfiguration} object available
     * via the {@link #getBucketLoggingConfiguration()} method.
     */
    public static class BucketLoggingConfigurationHandler extends AbstractHandler {

        private final BucketLoggingConfiguration bucketLoggingConfiguration =
                new BucketLoggingConfiguration();

        /**
         * @return
         * an object representing the bucket's LoggingStatus document.
         */
        public BucketLoggingConfiguration getBucketLoggingConfiguration() {
            return bucketLoggingConfiguration;
        }

        @Override
        protected void doStartElement(
                String uri,
                String name,
                String qName,
                Attributes attrs) {

        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (in("BucketLoggingStatus", "LoggingEnabled")) {
                if (name.equals("TargetBucket")) {
                    bucketLoggingConfiguration
                        .setDestinationBucketName(getText());

                } else if (name.equals("TargetPrefix")) {
                    bucketLoggingConfiguration
                        .setLogFilePrefix(getText());
                }
            }
        }
    }

    /**
     * Handler for CreateBucketConfiguration response XML documents for a
     * bucket. The document is parsed into a String representing the bucket's
     * location, available via the {@link #getLocation()} method.
     */
    public static class BucketLocationHandler extends AbstractHandler {

        private String location = null;

        /**
         * @return
         * the bucket's location.
         */
        public String getLocation() {
            return location;
        }

        @Override
        protected void doStartElement(
                String uri,
                String name,
                String qName,
                Attributes attrs) {

        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (atTopLevel()) {
                if (name.equals("LocationConstraint")) {
                    String elementText = getText();
                    if (elementText.length() == 0) {
                        location = null;
                    } else {
                        location = elementText;
                    }
                }
            }
        }
    }

    public static class CopyObjectResultHandler extends AbstractSSEHandler implements ObjectExpirationResult {

        // Data items for successful copy
        private final CopyObjectResult result = new CopyObjectResult();

        // Data items for failed copy
        private String errorCode = null;
        private String errorMessage = null;
        private String errorRequestId = null;
        private String errorHostId = null;
        private boolean receivedErrorResponse = false;

        @Override
        protected ServerSideEncryptionResult sseResult() {
            return result;
        }

        public Date getLastModified() {
            return result.getLastModifiedDate();
        }

        public String getVersionId() {
            return result.getVersionId();
        }

        public void setVersionId(String versionId) {
            result.setVersionId(versionId);
        }

        @Override
        public Date getExpirationTime() {
            return result.getExpirationTime();
        }

        @Override
        public void setExpirationTime(Date expirationTime) {
            result.setExpirationTime(expirationTime);
        }

        @Override
        public String getExpirationTimeRuleId() {
            return result.getExpirationTimeRuleId();
        }

        @Override
        public void setExpirationTimeRuleId(String expirationTimeRuleId) {
            result.setExpirationTimeRuleId(expirationTimeRuleId);
        }

        public String getETag() {
            return result.getETag();
        }

        public String getErrorCode() {
            return errorCode;
        }

        public String getErrorHostId() {
            return errorHostId;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public String getErrorRequestId() {
            return errorRequestId;
        }

        public boolean isErrorResponse() {
            return receivedErrorResponse;
        }

        @Override
        protected void doStartElement(
                String uri,
                String name,
                String qName,
                Attributes attrs) {

            if (atTopLevel()) {
                if (name.equals("CopyObjectResult") || name.equals("CopyPartResult")) {
                    receivedErrorResponse = false;
                } else if (name.equals("Error")) {
                    receivedErrorResponse = true;
                }
            }
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (in("CopyObjectResult") || in ("CopyPartResult")) {
                if (name.equals("LastModified")) {
                    result.setLastModifiedDate(ServiceUtils.parseIso8601Date(getText()));
                } else if (name.equals("ETag")) {
                    result.setETag(ServiceUtils.removeQuotes(getText()));
                }
            }

            else if (in("Error")) {
                if (name.equals("Code")) {
                    errorCode = getText();
                } else if (name.equals("Message")) {
                    errorMessage = getText();
                } else if (name.equals("RequestId")) {
                    errorRequestId = getText();
                } else if (name.equals("HostId")) {
                    errorHostId = getText();
                }
            }
        }
    }

    /**
     * Handler for parsing RequestPaymentConfiguration XML response associated
     * with an Amazon S3 bucket. The XML response is parsed into a
     * <code>RequestPaymentConfiguration</code> object.
     */
    public static class RequestPaymentConfigurationHandler extends AbstractHandler {

        private String payer = null;

        public RequestPaymentConfiguration getConfiguration(){
            return new RequestPaymentConfiguration(Payer.valueOf(payer));
        }

        @Override
        protected void doStartElement(
                String uri,
                String name,
                String qName,
                Attributes attrs) {

        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (in("RequestPaymentConfiguration")) {
                if (name.equals("Payer")) {
                    payer = getText();
                }
            }
        }
    }

    /**
     * Handler for ListVersionsResult XML document.
     */
    public static class ListVersionsHandler extends AbstractHandler {

        private final VersionListing versionListing = new VersionListing();

        private S3VersionSummary currentVersionSummary;
        private Owner currentOwner;

        public VersionListing getListing() {
            return versionListing;
        }

        @Override
        protected void doStartElement(
                String uri,
                String name,
                String qName,
                Attributes attrs) {

            if (in("ListVersionsResult")) {
                if (name.equals("Version")) {
                    currentVersionSummary = new S3VersionSummary();
                    currentVersionSummary.setBucketName(
                            versionListing.getBucketName());

                } else if (name.equals("DeleteMarker")) {
                    currentVersionSummary = new S3VersionSummary();
                    currentVersionSummary.setBucketName(
                            versionListing.getBucketName());
                    currentVersionSummary.setIsDeleteMarker(true);
                }
            }

            else if (in("ListVersionsResult", "Version")
                    || in("ListVersionsResult", "DeleteMarker")) {
                if (name.equals("Owner")) {
                    currentOwner = new Owner();
                }
            }
        }

        @Override
        protected void doEndElement(
                String uri,
                String name,
                String qName) {

            if (in("ListVersionsResult")) {
                if (name.equals("Name")) {
                    versionListing.setBucketName(getText());

                } else if (name.equals("Prefix")) {
                    versionListing.setPrefix(checkForEmptyString(getText()));

                } else if (name.equals("KeyMarker")) {
                    versionListing.setKeyMarker(checkForEmptyString(getText()));

                } else if (name.equals("VersionIdMarker")) {
                    versionListing.setVersionIdMarker(checkForEmptyString(
                            getText()));

                } else if (name.equals("MaxKeys")) {
                    versionListing.setMaxKeys(Integer.parseInt(getText()));

                } else if (name.equals("Delimiter")) {
                    versionListing.setDelimiter(checkForEmptyString(getText()));

                } else if (name.equals("EncodingType")) {
                    versionListing.setEncodingType(checkForEmptyString(
                            getText()));

                } else if (name.equals("NextKeyMarker")) {
                    versionListing.setNextKeyMarker(getText());

                } else if (name.equals("NextVersionIdMarker")) {
                    versionListing.setNextVersionIdMarker(getText());

                } else if (name.equals("IsTruncated")) {
                    versionListing.setTruncated("true".equals(getText()));

                } else if (name.equals("Version")
                        || name.equals("DeleteMarker")) {

                    versionListing.getVersionSummaries()
                        .add(currentVersionSummary);

                    currentVersionSummary = null;
                }
            }

            else if (in("ListVersionsResult", "CommonPrefixes")) {
                if (name.equals("Prefix")) {
                    versionListing.getCommonPrefixes()
                        .add(checkForEmptyString(getText()));
                }
            }

            else if (in("ListVersionsResult", "Version")
                    || in("ListVersionsResult", "DeleteMarker")) {

                if (name.equals("Key")) {
                    currentVersionSummary.setKey(getText());

                } else if (name.equals("VersionId")) {
                    currentVersionSummary.setVersionId(getText());

                } else if (name.equals("IsLatest")) {
                    currentVersionSummary.setIsLatest("true".equals(getText()));

                } else if (name.equals("LastModified")) {
                    currentVersionSummary.setLastModified(
                            ServiceUtils.parseIso8601Date(getText()));

                } else if (name.equals("ETag")) {
                    currentVersionSummary.setETag(
                            ServiceUtils.removeQuotes(getText()));

                } else if (name.equals("Size")) {
                    currentVersionSummary.setSize(Long.parseLong(getText()));

                } else if (name.equals("Owner")) {
                    currentVersionSummary.setOwner(currentOwner);
                    currentOwner = null;

                } else if (name.equals("StorageClass")) {
                    currentVersionSummary.setStorageClass(getText());
                }
            }

            else if (in("ListVersionsResult", "Version", "Owner")
                    || in("ListVersionsResult", "DeleteMarker", "Owner")) {

                if (name.equals("ID")) {
                    currentOwner.setId(getText());
                } else if (name.equals("DisplayName")) {
                    currentOwner.setDisplayName(getText());
                }
            }
        }
    }

    public static class BucketWebsiteConfigurationHandler extends AbstractHandler {

        private final BucketWebsiteConfiguration configuration =
                new BucketWebsiteConfiguration(null);

        private RoutingRuleCondition currentCondition = null;
        private RedirectRule currentRedirectRule = null;
        private RoutingRule currentRoutingRule = null;

        public BucketWebsiteConfiguration getConfiguration() {
            return configuration;
        }

        @Override
        protected  void doStartElement(
                String uri,
                String name,
                String qName,
                Attributes attrs) {

            if (in("WebsiteConfiguration")) {
                if (name.equals("RedirectAllRequestsTo")) {
                    currentRedirectRule = new RedirectRule();
                }
            }

            else if (in("WebsiteConfiguration", "RoutingRules")) {
                if (name.equals("RoutingRule")) {
                    currentRoutingRule = new RoutingRule();
                }
            }

            else if (in("WebsiteConfiguration", "RoutingRules", "RoutingRule")) {
                if (name.equals("Condition")) {
                    currentCondition = new RoutingRuleCondition();
                } else if (name.equals("Redirect")) {
                    currentRedirectRule = new RedirectRule();
                }
            }
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (in("WebsiteConfiguration")) {
                if (name.equals("RedirectAllRequestsTo")) {
                    configuration.setRedirectAllRequestsTo(currentRedirectRule);
                    currentRedirectRule = null;
                }
            }

            else if (in("WebsiteConfiguration", "IndexDocument")) {
                if (name.equals("Suffix")) {
                    configuration.setIndexDocumentSuffix(getText());
                }
            }

            else if (in("WebsiteConfiguration", "ErrorDocument")) {
                if (name.equals("Key")) {
                    configuration.setErrorDocument(getText());
                }
            }

            else if (in("WebsiteConfiguration", "RoutingRules")) {
                if (name.equals("RoutingRule")) {
                    configuration.getRoutingRules().add(currentRoutingRule);
                    currentRoutingRule = null;
                }
            }

            else if (in("WebsiteConfiguration", "RoutingRules", "RoutingRule")) {
                if (name.equals("Condition")) {
                    currentRoutingRule.setCondition(currentCondition);
                    currentCondition = null;
                } else if (name.equals("Redirect")) {
                    currentRoutingRule.setRedirect(currentRedirectRule);
                    currentRedirectRule = null;
                }
            }

            else if (in("WebsiteConfiguration", "RoutingRules", "RoutingRule", "Condition")) {
                if (name.equals("KeyPrefixEquals")) {
                    currentCondition.setKeyPrefixEquals(getText());
                } else if (name.equals("HttpErrorCodeReturnedEquals")) {
                    currentCondition.setHttpErrorCodeReturnedEquals(getText());
                }
            }

            else if (in("WebsiteConfiguration", "RedirectAllRequestsTo")
                    || in("WebsiteConfiguration", "RoutingRules", "RoutingRule", "Redirect")) {

                if (name.equals("Protocol")) {
                    currentRedirectRule.setProtocol(getText());

                } else if (name.equals("HostName")) {
                    currentRedirectRule.setHostName(getText());

                } else if (name.equals("ReplaceKeyPrefixWith")) {
                    currentRedirectRule.setReplaceKeyPrefixWith(getText());

                } else if (name.equals("ReplaceKeyWith")) {
                    currentRedirectRule.setReplaceKeyWith(getText());

                } else if (name.equals("HttpRedirectCode")) {
                    currentRedirectRule.setHttpRedirectCode(getText());
                }
            }
        }
    }

    public static class BucketVersioningConfigurationHandler extends AbstractHandler {

        private final BucketVersioningConfiguration configuration =
                new BucketVersioningConfiguration();

        public BucketVersioningConfiguration getConfiguration() { return configuration; }

        @Override
        protected void doStartElement(
                String uri,
                String name,
                String qName,
                Attributes attrs) {

        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (in("VersioningConfiguration")) {
                if (name.equals("Status")) {
                    configuration.setStatus(getText());

                } else if (name.equals("MfaDelete")) {
                    String mfaDeleteStatus = getText();

                    if (mfaDeleteStatus.equals("Disabled")) {
                        configuration.setMfaDeleteEnabled(false);
                    } else if (mfaDeleteStatus.equals("Enabled")) {
                        configuration.setMfaDeleteEnabled(true);
                    } else {
                        configuration.setMfaDeleteEnabled(null);
                    }
                }
            }
        }
    }


    /*
     * <?xml version="1.0" encoding="UTF-8"?>
     * <CompleteMultipartUploadResult xmlns="http://s3.amazonaws.com/doc/2006-03-01/">
     *     <Location>http://Example-Bucket.s3.amazonaws.com/Example-Object</Location>
     *     <Bucket>Example-Bucket</Bucket>
     *     <Key>Example-Object</Key>
     *     <ETag>"3858f62230ac3c915f300c664312c11f-9"</ETag>
     * </CompleteMultipartUploadResult>
     *
     * Or if an error occurred while completing:
     *
     * <?xml version="1.0" encoding="UTF-8"?>
     * <Error>
     *     <Code>InternalError</Code>
     *     <Message>We encountered an internal error. Please try again.</Message>
     *     <RequestId>656c76696e6727732072657175657374</RequestId>
     *     <HostId>Uuag1LuByRx9e6j5Onimru9pO4ZVKnJ2Qz7/C1NPcfTWAtRPfTaOFg==</HostId>
     * </Error>
     */
    public static class CompleteMultipartUploadHandler extends AbstractSSEHandler
            implements ObjectExpirationResult {
        // Successful completion
        private CompleteMultipartUploadResult result;

        // Error during completion
        private AmazonS3Exception ase;
        private String hostId;
        private String requestId;
        private String errorCode;

        @Override
        protected ServerSideEncryptionResult sseResult() {
            return result;
        }
        /**
         * @see com.amazonaws.services.s3.model.CompleteMultipartUploadResult#getExpirationTime()
         */
        @Override
        public Date getExpirationTime() {
            return result == null ? null : result.getExpirationTime();
        }

        /**
         * @see com.amazonaws.services.s3.model.CompleteMultipartUploadResult#setExpirationTime(java.util.Date)
         */
        @Override
        public void setExpirationTime(Date expirationTime) {
            if (result != null) {
                result.setExpirationTime(expirationTime);
            }
        }

        /**
         * @see com.amazonaws.services.s3.model.CompleteMultipartUploadResult#getExpirationTimeRuleId()
         */
        @Override
        public String getExpirationTimeRuleId() {
            return result == null ? null : result.getExpirationTimeRuleId();
        }

        /**
         * @see com.amazonaws.services.s3.model.CompleteMultipartUploadResult#setExpirationTimeRuleId(java.lang.String)
         */
        @Override
        public void setExpirationTimeRuleId(String expirationTimeRuleId) {
            if (result != null) {
                result.setExpirationTimeRuleId(expirationTimeRuleId);
            }
        }

        public CompleteMultipartUploadResult getCompleteMultipartUploadResult() {
            return result;
        }

        public AmazonS3Exception getAmazonS3Exception() {
            return ase;
        }

        @Override
        protected void doStartElement(
                String uri,
                String name,
                String qName,
                Attributes attrs) {

            if (atTopLevel()) {
                if (name.equals("CompleteMultipartUploadResult")) {
                    result = new CompleteMultipartUploadResult();
                }
            }
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (atTopLevel()) {
                if (name.equals("Error")) {
                    if (ase != null) {
                        ase.setErrorCode(errorCode);
                        ase.setRequestId(requestId);
                        ase.setExtendedRequestId(hostId);
                    }
                }
            }

            else if (in("CompleteMultipartUploadResult")) {
                if (name.equals("Location")) {
                    result.setLocation(getText());
                } else if (name.equals("Bucket")) {
                    result.setBucketName(getText());
                } else if (name.equals("Key")) {
                    result.setKey(getText());
                } else if (name.equals("ETag")) {
                    result.setETag(ServiceUtils.removeQuotes(getText()));
                }
            }

            else if (in("Error")) {
                if (name.equals("Code")) {
                    errorCode = getText();
                } else if (name.equals("Message")) {
                    ase = new AmazonS3Exception(getText());
                } else if (name.equals("RequestId")) {
                    requestId = getText();
                } else if (name.equals("HostId")) {
                    hostId = getText();
                }
            }
        }
    }

    /*
     * <?xml version="1.0" encoding="UTF-8"?>
     * <InitiateMultipartUploadResult xmlns="http://s3.amazonaws.com/doc/2006-03-01/">
     *     <Bucket>example-bucket</Bucket>
     *     <Key>example-object</Key>
     *     <UploadId>VXBsb2FkIElEIGZvciA2aWWpbmcncyBteS1tb3ZpZS5tMnRzIHVwbG9hZA</UploadId>
     * </InitiateMultipartUploadResult>
     */
    public static class InitiateMultipartUploadHandler extends AbstractHandler {

        private final InitiateMultipartUploadResult result =
                new InitiateMultipartUploadResult();

        public InitiateMultipartUploadResult getInitiateMultipartUploadResult() {
            return result;
        }

        @Override
        protected void doStartElement(
                String uri,
                String name,
                String qName,
                Attributes attrs) {

        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (in("InitiateMultipartUploadResult")) {
                if (name.equals("Bucket")) {
                    result.setBucketName(getText());

                } else if (name.equals("Key")) {
                    result.setKey(getText());

                } else if (name.equals("UploadId")) {
                    result.setUploadId(getText());
                }
            }
        }
    }

    /*
     * HTTP/1.1 200 OK
     * x-amz-id-2: Uuag1LuByRx9e6j5Onimru9pO4ZVKnJ2Qz7/C1NPcfTWAtRPfTaOFg==
     * x-amz-request-id: 656c76696e6727732072657175657374
     * Date: Tue, 16 Feb 2010 20:34:56 GMT
     * Content-Length: 1330
     * Connection: keep-alive
     * Server: AmazonS3
     *
     * <?xml version="1.0" encoding="UTF-8"?>
     * <ListMultipartUploadsResult xmlns="http://s3.amazonaws.com/doc/2006-03-01/">
     *     <Bucket>bucket</Bucket>
     *     <KeyMarker></KeyMarker>
     *     <Delimiter>/</Delimiter>
     *     <Prefix/>
     *     <UploadIdMarker></UploadIdMarker>
     *     <NextKeyMarker>my-movie.m2ts</NextKeyMarker>
     *     <NextUploadIdMarker>YW55IGlkZWEgd2h5IGVsdmluZydzIHVwbG9hZCBmYWlsZWQ</NextUploadIdMarker>
     *     <MaxUploads>3</MaxUploads>
     *     <IsTruncated>true</IsTruncated>
     *     <Upload>
     *         <Key>my-divisor</Key>
     *         <UploadId>XMgbGlrZSBlbHZpbmcncyBub3QgaGF2aW5nIG11Y2ggbHVjaw</UploadId>
     *         <Owner>
     *             <ID>b1d16700c70b0b05597d7acd6a3f92be</ID>
     *             <DisplayName>delving</DisplayName>
     *         </Owner>
     *         <StorageClass>STANDARD</StorageClass>
     *         <Initiated>Tue, 26 Jan 2010 19:42:19 GMT</Initiated>
     *     </Upload>
     *     <Upload>
     *         <Key>my-movie.m2ts</Key>
     *         <UploadId>VXBsb2FkIElEIGZvciBlbHZpbmcncyBteS1tb3ZpZS5tMnRzIHVwbG9hZA</UploadId>
     *         <Owner>
     *             <ID>b1d16700c70b0b05597d7acd6a3f92be</ID>
     *             <DisplayName>delving</DisplayName>
     *         </Owner>
     *         <StorageClass>STANDARD</StorageClass>
     *         <Initiated>Tue, 16 Feb 2010 20:34:56 GMT</Initiated>
     *     </Upload>
     *     <Upload>
     *         <Key>my-movie.m2ts</Key>
     *         <UploadId>YW55IGlkZWEgd2h5IGVsdmluZydzIHVwbG9hZCBmYWlsZWQ</UploadId>
     *         <Owner>
     *             <ID>b1d16700c70b0b05597d7acd6a3f92be</ID>
     *             <DisplayName>delving</DisplayName>
     *         </Owner>
     *         <StorageClass>STANDARD</StorageClass>
     *         <Initiated>Wed, 27 Jan 2010 03:02:01 GMT</Initiated>
     *     </Upload>
     *    <CommonPrefixes>
     *        <Prefix>photos/</Prefix>
     *    </CommonPrefixes>
     *    <CommonPrefixes>
     *        <Prefix>videos/</Prefix>
     *    </CommonPrefixes>
     * </ListMultipartUploadsResult>
     */
    public static class ListMultipartUploadsHandler extends AbstractHandler {

        private final MultipartUploadListing result =
                new MultipartUploadListing();

        private MultipartUpload currentMultipartUpload;
        private Owner currentOwner;

        public MultipartUploadListing getListMultipartUploadsResult() {
            return result;
        }

        @Override
        protected void doStartElement(
                String uri,
                String name,
                String qName,
                Attributes attrs) {

            if (in("ListMultipartUploadsResult")) {
                if (name.equals("Upload")) {
                    currentMultipartUpload = new MultipartUpload();
                }
            } else if (in("ListMultipartUploadsResult", "Upload")) {
                if (name.equals("Owner") || name.equals("Initiator")) {
                    currentOwner = new Owner();
                }
            }
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (in("ListMultipartUploadsResult")) {
                if (name.equals("Bucket")) {
                    result.setBucketName(getText());
                } else if (name.equals("KeyMarker")) {
                    result.setKeyMarker(checkForEmptyString(getText()));
                } else if (name.equals("Delimiter")) {
                    result.setDelimiter(checkForEmptyString(getText()));
                } else if (name.equals("Prefix")) {
                    result.setPrefix(checkForEmptyString(getText()));
                } else if (name.equals("UploadIdMarker")) {
                    result.setUploadIdMarker(checkForEmptyString(getText()));
                } else if (name.equals("NextKeyMarker")) {
                    result.setNextKeyMarker(checkForEmptyString(getText()));
                } else if (name.equals("NextUploadIdMarker")) {
                    result.setNextUploadIdMarker(checkForEmptyString(getText()));
                } else if (name.equals("MaxUploads")) {
                    result.setMaxUploads(Integer.parseInt(getText()));
                } else if (name.equals("EncodingType")) {
                    result.setEncodingType(checkForEmptyString(getText()));
                } else if (name.equals("IsTruncated")) {
                    result.setTruncated(Boolean.parseBoolean(getText()));
                } else if (name.equals("Upload")) {
                    result.getMultipartUploads().add(currentMultipartUpload);
                    currentMultipartUpload = null;
                }
            }

            else if (in("ListMultipartUploadsResult", "CommonPrefixes")) {
                if (name.equals("Prefix")) {
                    result.getCommonPrefixes().add(getText());
                }
            }

            else if (in("ListMultipartUploadsResult", "Upload")) {
                if (name.equals("Key")) {
                    currentMultipartUpload.setKey(getText());
                } else if (name.equals("UploadId")) {
                    currentMultipartUpload.setUploadId(getText());
                } else if (name.equals("Owner")) {
                    currentMultipartUpload.setOwner(currentOwner);
                    currentOwner = null;
                } else if (name.equals("Initiator")) {
                    currentMultipartUpload.setInitiator(currentOwner);
                    currentOwner = null;
                } else if (name.equals("StorageClass")) {
                    currentMultipartUpload.setStorageClass(getText());
                } else if (name.equals("Initiated")) {
                    currentMultipartUpload.setInitiated(
                            ServiceUtils.parseIso8601Date(getText()));
                }
            }

            else if (in("ListMultipartUploadsResult", "Upload", "Owner")
                  || in("ListMultipartUploadsResult", "Upload", "Initiator")) {

                if (name.equals("ID")) {
                    currentOwner.setId(checkForEmptyString(getText()));
                } else if (name.equals("DisplayName")) {
                    currentOwner.setDisplayName(checkForEmptyString(getText()));
                }
            }
        }
    }

    /*
     * HTTP/1.1 200 OK
     * x-amz-id-2: Uuag1LuByRx9e6j5Onimru9pO4ZVKnJ2Qz7/C1NPcfTWAtRPfTaOFg==
     * x-amz-request-id: 656c76696e6727732072657175657374
     * Date: Tue, 16 Feb 2010 20:34:56 GMT
     * Content-Length: 985
     * Connection: keep-alive
     * Server: AmazonS3
     *
     * <?xml version="1.0" encoding="UTF-8"?>
     * <ListPartsResult xmlns="http://s3.amazonaws.com/doc/2006-03-01/">
     *     <Bucket>example-bucket</Bucket>
     *     <Key>example-object</Key>
     *     <UploadId>XXBsb2FkIElEIGZvciBlbHZpbmcncyVcdS1tb3ZpZS5tMnRzEEEwbG9hZA</UploadId>
     *     <Owner>
     *         <ID>x1x16700c70b0b05597d7ecd6a3f92be</ID>
     *         <DisplayName>username</DisplayName>
     *     </Owner>
     *     <Initiator>
     *         <ID>x1x16700c70b0b05597d7ecd6a3f92be</ID>
     *         <DisplayName>username</DisplayName>
     *     </Initiator>
     *     <StorageClass>STANDARD</StorageClass>
     *     <PartNumberMarker>1</PartNumberMarker>
     *     <NextPartNumberMarker>3</NextPartNumberMarker>
     *     <MaxParts>2</MaxParts>
     *     <IsTruncated>true</IsTruncated>
     *     <Part>
     *         <PartNumber>2</PartNumber>
     *         <LastModified>Wed, 27 Jan 2010 03:02:03 GMT</LastModified>
     *         <ETag>"7778aef83f66abc1fa1e8477f296d394"</ETag>
     *         <Size>10485760</Size>
     *     </Part>
     *     <Part>
     *        <PartNumber>3</PartNumber>
     *        <LastModified>Wed, 27 Jan 2010 03:02:02 GMT</LastModified>
     *        <ETag>"aaaa18db4cc2f85cedef654fccc4a4x8"</ETag>
     *        <Size>10485760</Size>
     *     </Part>
     * </ListPartsResult>
     */
    public static class ListPartsHandler extends AbstractHandler {

        private final PartListing result = new PartListing();

        private PartSummary currentPart;
        private Owner currentOwner;

        public PartListing getListPartsResult() {
            return result;
        }

        @Override
        protected void doStartElement(
                String uri,
                String name,
                String qName,
                Attributes attrs) {

            if (in("ListPartsResult")) {
                if (name.equals("Part")) {
                    currentPart = new PartSummary();
                } else if (name.equals("Owner") || name.equals("Initiator")) {
                    currentOwner = new Owner();
                }
            }
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (in("ListPartsResult")) {
                if (name.equals("Bucket")) {
                    result.setBucketName(getText());
                } else if (name.equals("Key")) {
                    result.setKey(getText());
                } else if (name.equals("UploadId")) {
                    result.setUploadId(getText());
                } else if (name.equals("Owner")) {
                    result.setOwner(currentOwner);
                    currentOwner = null;
                } else if (name.equals("Initiator")) {
                    result.setInitiator(currentOwner);
                    currentOwner = null;
                } else if (name.equals("StorageClass")) {
                    result.setStorageClass(getText());
                } else if (name.equals("PartNumberMarker")) {
                    result.setPartNumberMarker(parseInteger(getText()));
                } else if (name.equals("NextPartNumberMarker")) {
                    result.setNextPartNumberMarker(parseInteger(getText()));
                } else if (name.equals("MaxParts")) {
                    result.setMaxParts(parseInteger(getText()));
                } else if (name.equals("EncodingType")) {
                    result.setEncodingType(checkForEmptyString(getText()));
                } else if (name.equals("IsTruncated")) {
                    result.setTruncated(Boolean.parseBoolean(getText()));
                } else if (name.equals("Part")) {
                    result.getParts().add(currentPart);
                    currentPart = null;
                }
            }

            else if (in("ListPartsResult", "Part")) {
                if (name.equals("PartNumber")) {
                    currentPart.setPartNumber(Integer.parseInt(getText()));
                } else if (name.equals("LastModified")) {
                    currentPart.setLastModified(
                            ServiceUtils.parseIso8601Date(getText()));
                } else if (name.equals("ETag")) {
                    currentPart.setETag(ServiceUtils.removeQuotes(getText()));
                } else if (name.equals("Size")) {
                    currentPart.setSize(Long.parseLong(getText()));
                }
            }

            else if (in("ListPartsResult", "Owner")
                  || in("ListPartsResult", "Initiator")) {

                if (name.equals("ID")) {
                    currentOwner.setId(checkForEmptyString(getText()));
                } else if (name.equals("DisplayName")) {
                    currentOwner.setDisplayName(checkForEmptyString(getText()));
                }
            }
        }

        private Integer parseInteger(String text) {
            text = checkForEmptyString(getText());
            if (text == null) return null;
            return Integer.parseInt(text);
        }
    }

    /**
     * Handler for parsing the get replication configuration response from
     * Amazon S3. Sample HTTP response is given below.
     *
     * <pre>
     * <ReplicationConfiguration>
     * 	<Rule>
     *   	<ID>replication-rule-1-1421862858808</ID>
     *   	<Prefix>testPrefix1</Prefix>
     *   	<Status>Enabled</Status>
     *   	<Destination>
     *       	<Bucket>bucketARN</Bucket>
     *   	</Destination>
     *	</Rule>
     *	<Rule>
     *   	<ID>replication-rule-2-1421862858808</ID>
     *   	<Prefix>testPrefix2</Prefix>
     *   	<Status>Disabled</Status>
     *   	<Destination>
     *       	<Bucket>arn:aws:s3:::bucket-dest-replication-integ-test-1421862858808</Bucket>
     *   	</Destination>
     *	</Rule>
     * </ReplicationConfiguration>
     * </pre>
     */
    public static class BucketReplicationConfigurationHandler extends
            AbstractHandler {

        private final BucketReplicationConfiguration bucketReplicationConfiguration = new BucketReplicationConfiguration();
        private String currentRuleId;
        private ReplicationRule currentRule;
        private ReplicationDestinationConfig destinationConfig;
        private static final String REPLICATION_CONFIG = "ReplicationConfiguration";
        private static final String ROLE = "Role";
        private static final String RULE = "Rule";
        private static final String DESTINATION = "Destination";
        private static final String ID = "ID";
        private static final String PREFIX = "Prefix";
        private static final String STATUS = "Status";
        private static final String BUCKET = "Bucket";
        private static final String STORAGECLASS = "StorageClass";

        public BucketReplicationConfiguration getConfiguration() {
            return bucketReplicationConfiguration;
        }

        @Override
        protected void doStartElement(String uri, String name, String qName,
                Attributes attrs) {

            if (in(REPLICATION_CONFIG)) {
                if (name.equals(RULE)) {
                    currentRule = new ReplicationRule();
                }
            } else if (in(REPLICATION_CONFIG, RULE)) {
                if (name.equals(DESTINATION)) {
                    destinationConfig = new ReplicationDestinationConfig();
                }
            }
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (in(REPLICATION_CONFIG)) {
                if (name.equals(RULE)) {
                    bucketReplicationConfiguration.addRule(currentRuleId,
                            currentRule);
                    currentRule = null;
                    currentRuleId = null;
                    destinationConfig = null;
                } else if (name.equals(ROLE)) {
                    bucketReplicationConfiguration.setRoleARN(getText());
                }
            } else if (in(REPLICATION_CONFIG, RULE)) {
                if (name.equals(ID)) {
                    currentRuleId = getText();
                } else if (name.equals(PREFIX)) {
                    currentRule.setPrefix(getText());
                } else {
                    if (name.equals(STATUS)) {
                        currentRule.setStatus(getText());

                    } else if (name.equals(DESTINATION)) {
                        currentRule.setDestinationConfig(destinationConfig);
                    }
                }
            } else if (in(REPLICATION_CONFIG, RULE, DESTINATION)) {
                if (name.equals(BUCKET)) {
                    destinationConfig.setBucketARN(getText());
                } else if (name.equals(STORAGECLASS)) {
                    destinationConfig.setStorageClass(getText());
                }
            }
        }
    }

    public static class BucketTaggingConfigurationHandler extends AbstractHandler {

        private final BucketTaggingConfiguration configuration =
                new BucketTaggingConfiguration();

        private Map<String, String> currentTagSet;
        private String currentTagKey;
        private String currentTagValue;

        public BucketTaggingConfiguration getConfiguration() {
            return configuration;
        }

        @Override
        protected void doStartElement(
                String uri,
                String name,
                String qName,
                Attributes attrs) {

            if (in("Tagging")) {
                if (name.equals("TagSet")) {
                    currentTagSet = new HashMap<String, String>();
                }
            }
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (in("Tagging")) {
                if (name.equals("TagSet")) {
                    configuration.getAllTagSets()
                        .add(new TagSet(currentTagSet));
                    currentTagSet = null;
                }
            }

            else if (in("Tagging", "TagSet")) {
                if (name.equals("Tag")) {
                    if (currentTagKey != null && currentTagValue != null) {
                        currentTagSet.put(currentTagKey, currentTagValue);
                    }
                    currentTagKey = null;
                    currentTagValue = null;
                }
            }

            else if (in("Tagging", "TagSet", "Tag")) {
                if (name.equals("Key")) {
                    currentTagKey = getText();
                } else if (name.equals("Value")) {
                    currentTagValue = getText();
                }
            }
        }
    }

    /*
        HTTP/1.1 200 OK
        x-amz-id-2: Uuag1LuByRx9e6j5Onimru9pO4ZVKnJ2Qz7/C1NPcfTWAtRPfTaOFg==
        x-amz-request-id: 656c76696e6727732072657175657374
        Date: Tue, 20 Sep 2012 20:34:56 GMT
        Content-Type: application/xml
        Transfer-Encoding: chunked
        Connection: keep-alive
        Server: AmazonS3

        <?xml version="1.0" encoding="UTF-8"?>
        <DeleteResult>
            <Deleted>
               <Key>Key</Key>
               <VersionId>Version</VersionId>
            </Deleted>
            <Error>
               <Key>Key</Key>
               <VersionId>Version</VersionId>
               <Code>Code</Code>
               <Message>Message</Message>
            </Error>
            <Deleted>
               <Key>Key</Key>
               <DeleteMarker>true</DeleteMarker>
               <DeleteMarkerVersionId>Version</DeleteMarkerVersionId>
            </Deleted>
        </DeleteResult>
     */
    public static class DeleteObjectsHandler extends AbstractHandler {

        private final DeleteObjectsResponse response =
                new DeleteObjectsResponse();

        private DeletedObject currentDeletedObject = null;
        private DeleteError currentError = null;

        public DeleteObjectsResponse getDeleteObjectResult() {
            return response;
        }

        @Override
        protected void doStartElement(
                String uri,
                String name,
                String qName,
                Attributes attrs) {

            if (in("DeleteResult")) {
                if (name.equals("Deleted")) {
                    currentDeletedObject = new DeletedObject();
                } else if (name.equals("Error")) {
                    currentError = new DeleteError();
                }
            }
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (in("DeleteResult")) {
                if (name.equals("Deleted")) {
                    response.getDeletedObjects().add(currentDeletedObject);
                    currentDeletedObject = null;
                } else if (name.equals("Error")) {
                    response.getErrors().add(currentError);
                    currentError = null;
                }
            }

            else if (in("DeleteResult", "Deleted")) {
                if (name.equals("Key")) {
                    currentDeletedObject.setKey(getText());

                } else if (name.equals("VersionId")) {
                    currentDeletedObject.setVersionId(getText());

                } else if (name.equals("DeleteMarker")) {
                    currentDeletedObject.setDeleteMarker(
                            getText().equals("true"));

                } else if (name.equals("DeleteMarkerVersionId")) {
                    currentDeletedObject.setDeleteMarkerVersionId(getText());
                }
            }

            else if (in("DeleteResult", "Error")) {
                if (name.equals("Key")) {
                    currentError.setKey(getText());

                } else if (name.equals("VersionId")) {
                    currentError.setVersionId(getText());

                } else if (name.equals("Code")) {
                    currentError.setCode(getText());

                } else if (name.equals("Message")) {
                    currentError.setMessage(getText());
                }
            }
        }
    }

    /**
     * HTTP/1.1 200 OK
    x-amz-id-2: Uuag1LuByRx9e6j5Onimru9pO4ZVKnJ2Qz7/C1NPcfTWAtRPfTaOFg==
    x-amz-request-id: 656c76696e6727732072657175657374
    Date: Tue, 20 Sep 2012 20:34:56 GMT
    Content-Length: xxx
    Connection: keep-alive
    Server: AmazonS3

  <LifecycleConfiguration>
      <Rule>
          <ID>logs-rule</ID>
          <Prefix>logs/</Prefix>
          <Status>Enabled</Status>
          <Transition>
              <Days>30</Days>
              <StorageClass>STANDARD_IA</StorageClass>
          </Transition>
          <Transition>
              <Days>90</Days>
              <StorageClass>GLACIER</StorageClass>
          </Transition>
          <Expiration>
              <Days>365</Days>
          </Expiration>
          <NoncurrentVersionTransition>
              <NoncurrentDays>7</NoncurrentDays>
              <StorageClass>STANDARD_IA</StorageClass>
          </NoncurrentVersionTransition>
          <NoncurrentVersionTransition>
              <NoncurrentDays>14</NoncurrentDays>
              <StorageClass>GLACIER</StorageClass>
          </NoncurrentVersionTransition>
          <NoncurrentVersionExpiration>
              <NoncurrentDays>365</NoncurrentDays>
          </NoncurrentVersionExpiration>
     </Rule>
     <Rule>
         <ID>image-rule</ID>
         <Prefix>image/</Prefix>
         <Status>Enabled</Status>
         <Transition>
             <Date>2012-12-31T00:00:00.000Z</Date>
             <StorageClass>GLACIER</StorageClass>
         </Transition>
         <Expiration>
             <Date>2020-12-31T00:00:00.000Z</Date>
         </Expiration>
     </Rule>
  </LifecycleConfiguration>
     */
    public static class BucketLifecycleConfigurationHandler extends AbstractHandler {

        private final BucketLifecycleConfiguration configuration =
                new BucketLifecycleConfiguration(new ArrayList<Rule>());

        private Rule currentRule;
        private Transition currentTransition;
        private NoncurrentVersionTransition currentNcvTransition;

        public BucketLifecycleConfiguration getConfiguration() {
            return configuration;
        }

        @Override
        protected void doStartElement(
                String uri,
                String name,
                String qName,
                Attributes attrs) {

            if (in("LifecycleConfiguration")) {
                if (name.equals("Rule")) {
                    currentRule = new Rule();
                }
            } else if (in("LifecycleConfiguration", "Rule")) {
                if (name.equals("Transition")) {
                    currentTransition = new Transition();
                } else if (name.equals("NoncurrentVersionTransition")) {
                    currentNcvTransition = new NoncurrentVersionTransition();
                }
            }
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (in("LifecycleConfiguration")) {
                if (name.equals("Rule")) {
                    configuration.getRules().add(currentRule);
                    currentRule = null;
                }
            }

            else if (in("LifecycleConfiguration", "Rule")) {
                if ( name.equals("ID") ) {
                    currentRule.setId(getText());

                } else if ( name.equals("Prefix") ) {
                    currentRule.setPrefix(getText());

                } else if ( name.equals("Status") ) {
                    currentRule.setStatus(getText());

                } else if (name.equals("Transition")) {
                    currentRule.addTransition(currentTransition);
                    currentTransition = null;

                } else if (name.equals("NoncurrentVersionTransition")) {
                    currentRule.addNoncurrentVersionTransition(
                            currentNcvTransition);
                    currentNcvTransition = null;
                }
            }

            else if (in("LifecycleConfiguration", "Rule", "Expiration")) {
                if (name.equals("Date")) {
                    currentRule.setExpirationDate(
                            ServiceUtils.parseIso8601Date(getText()));
                } else if (name.equals("Days")) {
                    currentRule.setExpirationInDays(
                            Integer.parseInt(getText()));
                }
            }

            else if (in("LifecycleConfiguration", "Rule", "Transition")) {
                if (name.equals("StorageClass")) {
                    currentTransition.setStorageClass(getText());
                } else if (name.equals("Date")) {
                    currentTransition.setDate(
                            ServiceUtils.parseIso8601Date(getText()));

                } else if (name.equals("Days")) {
                    currentTransition.setDays(Integer.parseInt(getText()));
                }
            }

            else if (in("LifecycleConfiguration", "Rule", "NoncurrentVersionExpiration")) {
                if (name.equals("NoncurrentDays")) {
                    currentRule.setNoncurrentVersionExpirationInDays(
                            Integer.parseInt(getText()));
                }
            }

            else if (in("LifecycleConfiguration", "Rule", "NoncurrentVersionTransition")) {
                if (name.equals("StorageClass")) {
                    currentNcvTransition.setStorageClass(getText());
                } else if (name.equals("NoncurrentDays")) {
                    currentNcvTransition.setDays(Integer.parseInt(getText()));
                }
            }
        }
    }

    /*
    HTTP/1.1 200 OK
    x-amz-id-2: Uuag1LuByRx9e6j5Onimru9pO4ZVKnJ2Qz7/C1NPcfTWAtRPfTaOFg==
    x-amz-request-id: 656c76696e6727732072657175657374
    Date: Tue, 20 Sep 2011 20:34:56 GMT
    Content-Length: Some Length
    Connection: keep-alive
    Server: AmazonS3
    <CORSConfiguration>
       <CORSRule>
         <AllowedOrigin>http://www.foobar.com</AllowedOrigin>
         <AllowedMethod>GET</AllowedMethod>
         <MaxAgeSeconds>3000</MaxAgeSec>
         <ExposeHeader>x-amz-server-side-encryption</ExposeHeader>
       </CORSRule>
    </CORSConfiguration>
    */
    public static class BucketCrossOriginConfigurationHandler extends AbstractHandler {

        private final BucketCrossOriginConfiguration configuration =
                new BucketCrossOriginConfiguration(new ArrayList<CORSRule>());

        private CORSRule currentRule;
        private List<AllowedMethods> allowedMethods = null;
        private List<String> allowedOrigins = null;
        private List<String> exposedHeaders = null;
        private List<String> allowedHeaders = null;

        public BucketCrossOriginConfiguration getConfiguration() {
            return configuration;
        }

        @Override
        protected void doStartElement(
                String uri,
                String name,
                String qName,
                Attributes attrs) {

            if (in("CORSConfiguration")) {
                if (name.equals("CORSRule")) {
                    currentRule = new CORSRule();
                }
            } else if (in("CORSConfiguration", "CORSRule")) {
                if (name.equals("AllowedOrigin")) {
                    if (allowedOrigins == null) {
                        allowedOrigins = new ArrayList<String>();
                    }
                } else if (name.equals("AllowedMethod")) {
                    if (allowedMethods == null) {
                        allowedMethods = new ArrayList<AllowedMethods>();
                    }
                } else if (name.equals("ExposeHeader")) {
                    if (exposedHeaders == null) {
                        exposedHeaders = new ArrayList<String>();
                    }
                } else if (name.equals("AllowedHeader")) {
                    if (allowedHeaders == null) {
                        allowedHeaders = new LinkedList<String>();
                    }
                }
            }
        }

        @Override
        protected void doEndElement(String uri, String name, String qName) {
            if (in("CORSConfiguration")) {
                if (name.equals("CORSRule")) {
                    currentRule.setAllowedHeaders(allowedHeaders);
                    currentRule.setAllowedMethods(allowedMethods);
                    currentRule.setAllowedOrigins(allowedOrigins);
                    currentRule.setExposedHeaders(exposedHeaders);
                    allowedHeaders = null;
                    allowedMethods = null;
                    allowedOrigins = null;
                    exposedHeaders = null;

                    configuration.getRules().add(currentRule);
                    currentRule = null;
                }
            } else if (in("CORSConfiguration", "CORSRule")) {
                if (name.equals("ID")) {
                    currentRule.setId(getText());

                } else if (name.equals("AllowedOrigin")) {
                    allowedOrigins.add(getText());

                } else if (name.equals("AllowedMethod")) {
                    allowedMethods.add(AllowedMethods.fromValue(getText()));

                } else if (name.equals("MaxAgeSeconds")) {
                    currentRule.setMaxAgeSeconds(Integer.parseInt(getText()));

                } else if (name.equals("ExposeHeader")) {
                    exposedHeaders.add(getText());

                } else if (name.equals("AllowedHeader")) {
                    allowedHeaders.add(getText());
                }
            }
        }
    }

    private static String findAttributeValue(
            String qnameToFind,
            Attributes attrs) {

        for (int i = 0; i < attrs.getLength(); i++) {
            String qname = attrs.getQName(i);
            if (qname.trim().equalsIgnoreCase(qnameToFind.trim())) {
                return attrs.getValue(i);
            }
        }

        return null;
    }
}
