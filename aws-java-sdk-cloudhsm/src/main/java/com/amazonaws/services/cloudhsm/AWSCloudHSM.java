/*
 * Copyright 2010-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.amazonaws.services.cloudhsm;

import com.amazonaws.*;
import com.amazonaws.regions.*;

import com.amazonaws.services.cloudhsm.model.*;

/**
 * Interface for accessing CloudHSM.
 * <p>
 * <fullname>AWS CloudHSM Service</fullname>
 */
public interface AWSCloudHSM {

    /**
     * Overrides the default endpoint for this client
     * ("https://cloudhsm.us-east-1.amazonaws.com/"). Callers can use this
     * method to control which AWS region they want to work with.
     * <p>
     * Callers can pass in just the endpoint (ex:
     * "cloudhsm.us-east-1.amazonaws.com/") or a full URL, including the
     * protocol (ex: "https://cloudhsm.us-east-1.amazonaws.com/"). If the
     * protocol is not specified here, the default protocol from this client's
     * {@link ClientConfiguration} will be used, which by default is HTTPS.
     * <p>
     * For more information on using AWS regions with the AWS SDK for Java, and
     * a complete list of all available endpoints for all AWS services, see: <a
     * href=
     * "http://developer.amazonwebservices.com/connect/entry.jspa?externalID=3912"
     * > http://developer.amazonwebservices.com/connect/entry.jspa?externalID=
     * 3912</a>
     * <p>
     * <b>This method is not threadsafe. An endpoint should be configured when
     * the client is created and before any service requests are made. Changing
     * it afterwards creates inevitable race conditions for any service requests
     * in transit or retrying.</b>
     *
     * @param endpoint
     *        The endpoint (ex: "cloudhsm.us-east-1.amazonaws.com/") or a full
     *        URL, including the protocol (ex:
     *        "https://cloudhsm.us-east-1.amazonaws.com/") of the region
     *        specific AWS endpoint this client will communicate with.
     */
    void setEndpoint(String endpoint);

    /**
     * An alternative to {@link AWSCloudHSM#setEndpoint(String)}, sets the
     * regional endpoint for this client's service calls. Callers can use this
     * method to control which AWS region they want to work with.
     * <p>
     * By default, all service endpoints in all regions use the https protocol.
     * To use http instead, specify it in the {@link ClientConfiguration}
     * supplied at construction.
     * <p>
     * <b>This method is not threadsafe. A region should be configured when the
     * client is created and before any service requests are made. Changing it
     * afterwards creates inevitable race conditions for any service requests in
     * transit or retrying.</b>
     *
     * @param region
     *        The region this client will communicate with. See
     *        {@link Region#getRegion(com.amazonaws.regions.Regions)} for
     *        accessing a given region. Must not be null and must be a region
     *        where the service is available.
     *
     * @see Region#getRegion(com.amazonaws.regions.Regions)
     * @see Region#createClient(Class,
     *      com.amazonaws.auth.AWSCredentialsProvider, ClientConfiguration)
     * @see Region#isServiceSupported(String)
     */
    void setRegion(Region region);

    /**
     * <p>
     * Creates a high-availability partition group. A high-availability
     * partition group is a group of partitions that spans multiple physical
     * HSMs.
     * </p>
     * 
     * @param createHapgRequest
     *        Contains the inputs for the <a>CreateHapgRequest</a> action.
     * @return Result of the CreateHapg operation returned by the service.
     * @throws CloudHsmServiceException
     *         Indicates that an exception occurred in the AWS CloudHSM service.
     * @throws CloudHsmInternalException
     *         Indicates that an internal error occurred.
     * @throws InvalidRequestException
     *         Indicates that one or more of the request parameters are not
     *         valid.
     */
    CreateHapgResult createHapg(CreateHapgRequest createHapgRequest);

    /**
     * <p>
     * Creates an uninitialized HSM instance. Running this command provisions an
     * HSM appliance and will result in charges to your AWS account for the HSM.
     * </p>
     * 
     * @param createHsmRequest
     *        Contains the inputs for the <a>CreateHsm</a> action.
     * @return Result of the CreateHsm operation returned by the service.
     * @throws CloudHsmServiceException
     *         Indicates that an exception occurred in the AWS CloudHSM service.
     * @throws CloudHsmInternalException
     *         Indicates that an internal error occurred.
     * @throws InvalidRequestException
     *         Indicates that one or more of the request parameters are not
     *         valid.
     */
    CreateHsmResult createHsm(CreateHsmRequest createHsmRequest);

    /**
     * <p>
     * Creates an HSM client.
     * </p>
     * 
     * @param createLunaClientRequest
     *        Contains the inputs for the <a>CreateLunaClient</a> action.
     * @return Result of the CreateLunaClient operation returned by the service.
     * @throws CloudHsmServiceException
     *         Indicates that an exception occurred in the AWS CloudHSM service.
     * @throws CloudHsmInternalException
     *         Indicates that an internal error occurred.
     * @throws InvalidRequestException
     *         Indicates that one or more of the request parameters are not
     *         valid.
     */
    CreateLunaClientResult createLunaClient(
            CreateLunaClientRequest createLunaClientRequest);

    /**
     * <p>
     * Deletes a high-availability partition group.
     * </p>
     * 
     * @param deleteHapgRequest
     *        Contains the inputs for the <a>DeleteHapg</a> action.
     * @return Result of the DeleteHapg operation returned by the service.
     * @throws CloudHsmServiceException
     *         Indicates that an exception occurred in the AWS CloudHSM service.
     * @throws CloudHsmInternalException
     *         Indicates that an internal error occurred.
     * @throws InvalidRequestException
     *         Indicates that one or more of the request parameters are not
     *         valid.
     */
    DeleteHapgResult deleteHapg(DeleteHapgRequest deleteHapgRequest);

    /**
     * <p>
     * Deletes an HSM. Once complete, this operation cannot be undone and your
     * key material cannot be recovered.
     * </p>
     * 
     * @param deleteHsmRequest
     *        Contains the inputs for the <a>DeleteHsm</a> action.
     * @return Result of the DeleteHsm operation returned by the service.
     * @throws CloudHsmServiceException
     *         Indicates that an exception occurred in the AWS CloudHSM service.
     * @throws CloudHsmInternalException
     *         Indicates that an internal error occurred.
     * @throws InvalidRequestException
     *         Indicates that one or more of the request parameters are not
     *         valid.
     */
    DeleteHsmResult deleteHsm(DeleteHsmRequest deleteHsmRequest);

    /**
     * <p>
     * Deletes a client.
     * </p>
     * 
     * @param deleteLunaClientRequest
     *        null
     * @return Result of the DeleteLunaClient operation returned by the service.
     * @throws CloudHsmServiceException
     *         Indicates that an exception occurred in the AWS CloudHSM service.
     * @throws CloudHsmInternalException
     *         Indicates that an internal error occurred.
     * @throws InvalidRequestException
     *         Indicates that one or more of the request parameters are not
     *         valid.
     */
    DeleteLunaClientResult deleteLunaClient(
            DeleteLunaClientRequest deleteLunaClientRequest);

    /**
     * <p>
     * Retrieves information about a high-availability partition group.
     * </p>
     * 
     * @param describeHapgRequest
     *        Contains the inputs for the <a>DescribeHapg</a> action.
     * @return Result of the DescribeHapg operation returned by the service.
     * @throws CloudHsmServiceException
     *         Indicates that an exception occurred in the AWS CloudHSM service.
     * @throws CloudHsmInternalException
     *         Indicates that an internal error occurred.
     * @throws InvalidRequestException
     *         Indicates that one or more of the request parameters are not
     *         valid.
     */
    DescribeHapgResult describeHapg(DescribeHapgRequest describeHapgRequest);

    /**
     * <p>
     * Retrieves information about an HSM. You can identify the HSM by its ARN
     * or its serial number.
     * </p>
     * 
     * @param describeHsmRequest
     *        Contains the inputs for the <a>DescribeHsm</a> action.
     * @return Result of the DescribeHsm operation returned by the service.
     * @throws CloudHsmServiceException
     *         Indicates that an exception occurred in the AWS CloudHSM service.
     * @throws CloudHsmInternalException
     *         Indicates that an internal error occurred.
     * @throws InvalidRequestException
     *         Indicates that one or more of the request parameters are not
     *         valid.
     */
    DescribeHsmResult describeHsm(DescribeHsmRequest describeHsmRequest);

    /**
     * Simplified method form for invoking the DescribeHsm operation.
     *
     * @see #describeHsm(DescribeHsmRequest)
     */
    DescribeHsmResult describeHsm();

    /**
     * <p>
     * Retrieves information about an HSM client.
     * </p>
     * 
     * @param describeLunaClientRequest
     *        null
     * @return Result of the DescribeLunaClient operation returned by the
     *         service.
     * @throws CloudHsmServiceException
     *         Indicates that an exception occurred in the AWS CloudHSM service.
     * @throws CloudHsmInternalException
     *         Indicates that an internal error occurred.
     * @throws InvalidRequestException
     *         Indicates that one or more of the request parameters are not
     *         valid.
     */
    DescribeLunaClientResult describeLunaClient(
            DescribeLunaClientRequest describeLunaClientRequest);

    /**
     * Simplified method form for invoking the DescribeLunaClient operation.
     *
     * @see #describeLunaClient(DescribeLunaClientRequest)
     */
    DescribeLunaClientResult describeLunaClient();

    /**
     * <p>
     * Gets the configuration files necessary to connect to all high
     * availability partition groups the client is associated with.
     * </p>
     * 
     * @param getConfigRequest
     *        null
     * @return Result of the GetConfig operation returned by the service.
     * @throws CloudHsmServiceException
     *         Indicates that an exception occurred in the AWS CloudHSM service.
     * @throws CloudHsmInternalException
     *         Indicates that an internal error occurred.
     * @throws InvalidRequestException
     *         Indicates that one or more of the request parameters are not
     *         valid.
     */
    GetConfigResult getConfig(GetConfigRequest getConfigRequest);

    /**
     * <p>
     * Lists the Availability Zones that have available AWS CloudHSM capacity.
     * </p>
     * 
     * @param listAvailableZonesRequest
     *        Contains the inputs for the <a>ListAvailableZones</a> action.
     * @return Result of the ListAvailableZones operation returned by the
     *         service.
     * @throws CloudHsmServiceException
     *         Indicates that an exception occurred in the AWS CloudHSM service.
     * @throws CloudHsmInternalException
     *         Indicates that an internal error occurred.
     * @throws InvalidRequestException
     *         Indicates that one or more of the request parameters are not
     *         valid.
     */
    ListAvailableZonesResult listAvailableZones(
            ListAvailableZonesRequest listAvailableZonesRequest);

    /**
     * Simplified method form for invoking the ListAvailableZones operation.
     *
     * @see #listAvailableZones(ListAvailableZonesRequest)
     */
    ListAvailableZonesResult listAvailableZones();

    /**
     * <p>
     * Lists the high-availability partition groups for the account.
     * </p>
     * <p>
     * This operation supports pagination with the use of the <i>NextToken</i>
     * member. If more results are available, the <i>NextToken</i> member of the
     * response contains a token that you pass in the next call to
     * <a>ListHapgs</a> to retrieve the next set of items.
     * </p>
     * 
     * @param listHapgsRequest
     *        null
     * @return Result of the ListHapgs operation returned by the service.
     * @throws CloudHsmServiceException
     *         Indicates that an exception occurred in the AWS CloudHSM service.
     * @throws CloudHsmInternalException
     *         Indicates that an internal error occurred.
     * @throws InvalidRequestException
     *         Indicates that one or more of the request parameters are not
     *         valid.
     */
    ListHapgsResult listHapgs(ListHapgsRequest listHapgsRequest);

    /**
     * Simplified method form for invoking the ListHapgs operation.
     *
     * @see #listHapgs(ListHapgsRequest)
     */
    ListHapgsResult listHapgs();

    /**
     * <p>
     * Retrieves the identifiers of all of the HSMs provisioned for the current
     * customer.
     * </p>
     * <p>
     * This operation supports pagination with the use of the <i>NextToken</i>
     * member. If more results are available, the <i>NextToken</i> member of the
     * response contains a token that you pass in the next call to
     * <a>ListHsms</a> to retrieve the next set of items.
     * </p>
     * 
     * @param listHsmsRequest
     *        null
     * @return Result of the ListHsms operation returned by the service.
     * @throws CloudHsmServiceException
     *         Indicates that an exception occurred in the AWS CloudHSM service.
     * @throws CloudHsmInternalException
     *         Indicates that an internal error occurred.
     * @throws InvalidRequestException
     *         Indicates that one or more of the request parameters are not
     *         valid.
     */
    ListHsmsResult listHsms(ListHsmsRequest listHsmsRequest);

    /**
     * Simplified method form for invoking the ListHsms operation.
     *
     * @see #listHsms(ListHsmsRequest)
     */
    ListHsmsResult listHsms();

    /**
     * <p>
     * Lists all of the clients.
     * </p>
     * <p>
     * This operation supports pagination with the use of the <i>NextToken</i>
     * member. If more results are available, the <i>NextToken</i> member of the
     * response contains a token that you pass in the next call to
     * <a>ListLunaClients</a> to retrieve the next set of items.
     * </p>
     * 
     * @param listLunaClientsRequest
     *        null
     * @return Result of the ListLunaClients operation returned by the service.
     * @throws CloudHsmServiceException
     *         Indicates that an exception occurred in the AWS CloudHSM service.
     * @throws CloudHsmInternalException
     *         Indicates that an internal error occurred.
     * @throws InvalidRequestException
     *         Indicates that one or more of the request parameters are not
     *         valid.
     */
    ListLunaClientsResult listLunaClients(
            ListLunaClientsRequest listLunaClientsRequest);

    /**
     * Simplified method form for invoking the ListLunaClients operation.
     *
     * @see #listLunaClients(ListLunaClientsRequest)
     */
    ListLunaClientsResult listLunaClients();

    /**
     * <p>
     * Modifies an existing high-availability partition group.
     * </p>
     * 
     * @param modifyHapgRequest
     *        null
     * @return Result of the ModifyHapg operation returned by the service.
     * @throws CloudHsmServiceException
     *         Indicates that an exception occurred in the AWS CloudHSM service.
     * @throws CloudHsmInternalException
     *         Indicates that an internal error occurred.
     * @throws InvalidRequestException
     *         Indicates that one or more of the request parameters are not
     *         valid.
     */
    ModifyHapgResult modifyHapg(ModifyHapgRequest modifyHapgRequest);

    /**
     * <p>
     * Modifies an HSM.
     * </p>
     * 
     * @param modifyHsmRequest
     *        Contains the inputs for the <a>ModifyHsm</a> action.
     * @return Result of the ModifyHsm operation returned by the service.
     * @throws CloudHsmServiceException
     *         Indicates that an exception occurred in the AWS CloudHSM service.
     * @throws CloudHsmInternalException
     *         Indicates that an internal error occurred.
     * @throws InvalidRequestException
     *         Indicates that one or more of the request parameters are not
     *         valid.
     */
    ModifyHsmResult modifyHsm(ModifyHsmRequest modifyHsmRequest);

    /**
     * <p>
     * Modifies the certificate used by the client.
     * </p>
     * <p>
     * This action can potentially start a workflow to install the new
     * certificate on the client's HSMs.
     * </p>
     * 
     * @param modifyLunaClientRequest
     *        null
     * @return Result of the ModifyLunaClient operation returned by the service.
     * @throws CloudHsmServiceException
     *         Indicates that an exception occurred in the AWS CloudHSM service.
     */
    ModifyLunaClientResult modifyLunaClient(
            ModifyLunaClientRequest modifyLunaClientRequest);

    /**
     * Shuts down this client object, releasing any resources that might be held
     * open. This is an optional method, and callers are not expected to call
     * it, but can if they want to explicitly release any open resources. Once a
     * client has been shutdown, it should not be used to make any more
     * requests.
     */
    void shutdown();

    /**
     * Returns additional metadata for a previously executed successful request,
     * typically used for debugging issues where a service isn't acting as
     * expected. This data isn't considered part of the result data returned by
     * an operation, so it's available through this separate, diagnostic
     * interface.
     * <p>
     * Response metadata is only cached for a limited period of time, so if you
     * need to access this extra diagnostic information for an executed request,
     * you should use this method to retrieve it as soon as possible after
     * executing a request.
     *
     * @param request
     *        The originally executed request.
     *
     * @return The response metadata for the specified request, or null if none
     *         is available.
     */
    ResponseMetadata getCachedResponseMetadata(AmazonWebServiceRequest request);
}
