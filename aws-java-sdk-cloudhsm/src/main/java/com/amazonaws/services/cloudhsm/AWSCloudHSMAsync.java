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

import com.amazonaws.services.cloudhsm.model.*;

/**
 * Interface for accessing CloudHSM asynchronously. Each asynchronous method
 * will return a Java Future object representing the asynchronous operation;
 * overloads which accept an {@code AsyncHandler} can be used to receive
 * notification when an asynchronous operation completes.
 * <p>
 * <fullname>AWS CloudHSM Service</fullname>
 */
public interface AWSCloudHSMAsync extends AWSCloudHSM {

    /**
     * <p>
     * Creates a high-availability partition group. A high-availability
     * partition group is a group of partitions that spans multiple physical
     * HSMs.
     * </p>
     * 
     * @param createHapgRequest
     *        Contains the inputs for the <a>CreateHapgRequest</a> action.
     * @return A Java Future containing the result of the CreateHapg operation
     *         returned by the service.
     */
    java.util.concurrent.Future<CreateHapgResult> createHapgAsync(
            CreateHapgRequest createHapgRequest);

    /**
     * <p>
     * Creates a high-availability partition group. A high-availability
     * partition group is a group of partitions that spans multiple physical
     * HSMs.
     * </p>
     * 
     * @param createHapgRequest
     *        Contains the inputs for the <a>CreateHapgRequest</a> action.
     * @param asyncHandler
     *        Asynchronous callback handler for events in the lifecycle of the
     *        request. Users can provide an implementation of the callback
     *        methods in this interface to receive notification of successful or
     *        unsuccessful completion of the operation.
     * @return A Java Future containing the result of the CreateHapg operation
     *         returned by the service.
     */
    java.util.concurrent.Future<CreateHapgResult> createHapgAsync(
            CreateHapgRequest createHapgRequest,
            com.amazonaws.handlers.AsyncHandler<CreateHapgRequest, CreateHapgResult> asyncHandler);

    /**
     * <p>
     * Creates an uninitialized HSM instance. Running this command provisions an
     * HSM appliance and will result in charges to your AWS account for the HSM.
     * </p>
     * 
     * @param createHsmRequest
     *        Contains the inputs for the <a>CreateHsm</a> action.
     * @return A Java Future containing the result of the CreateHsm operation
     *         returned by the service.
     */
    java.util.concurrent.Future<CreateHsmResult> createHsmAsync(
            CreateHsmRequest createHsmRequest);

    /**
     * <p>
     * Creates an uninitialized HSM instance. Running this command provisions an
     * HSM appliance and will result in charges to your AWS account for the HSM.
     * </p>
     * 
     * @param createHsmRequest
     *        Contains the inputs for the <a>CreateHsm</a> action.
     * @param asyncHandler
     *        Asynchronous callback handler for events in the lifecycle of the
     *        request. Users can provide an implementation of the callback
     *        methods in this interface to receive notification of successful or
     *        unsuccessful completion of the operation.
     * @return A Java Future containing the result of the CreateHsm operation
     *         returned by the service.
     */
    java.util.concurrent.Future<CreateHsmResult> createHsmAsync(
            CreateHsmRequest createHsmRequest,
            com.amazonaws.handlers.AsyncHandler<CreateHsmRequest, CreateHsmResult> asyncHandler);

    /**
     * <p>
     * Creates an HSM client.
     * </p>
     * 
     * @param createLunaClientRequest
     *        Contains the inputs for the <a>CreateLunaClient</a> action.
     * @return A Java Future containing the result of the CreateLunaClient
     *         operation returned by the service.
     */
    java.util.concurrent.Future<CreateLunaClientResult> createLunaClientAsync(
            CreateLunaClientRequest createLunaClientRequest);

    /**
     * <p>
     * Creates an HSM client.
     * </p>
     * 
     * @param createLunaClientRequest
     *        Contains the inputs for the <a>CreateLunaClient</a> action.
     * @param asyncHandler
     *        Asynchronous callback handler for events in the lifecycle of the
     *        request. Users can provide an implementation of the callback
     *        methods in this interface to receive notification of successful or
     *        unsuccessful completion of the operation.
     * @return A Java Future containing the result of the CreateLunaClient
     *         operation returned by the service.
     */
    java.util.concurrent.Future<CreateLunaClientResult> createLunaClientAsync(
            CreateLunaClientRequest createLunaClientRequest,
            com.amazonaws.handlers.AsyncHandler<CreateLunaClientRequest, CreateLunaClientResult> asyncHandler);

    /**
     * <p>
     * Deletes a high-availability partition group.
     * </p>
     * 
     * @param deleteHapgRequest
     *        Contains the inputs for the <a>DeleteHapg</a> action.
     * @return A Java Future containing the result of the DeleteHapg operation
     *         returned by the service.
     */
    java.util.concurrent.Future<DeleteHapgResult> deleteHapgAsync(
            DeleteHapgRequest deleteHapgRequest);

    /**
     * <p>
     * Deletes a high-availability partition group.
     * </p>
     * 
     * @param deleteHapgRequest
     *        Contains the inputs for the <a>DeleteHapg</a> action.
     * @param asyncHandler
     *        Asynchronous callback handler for events in the lifecycle of the
     *        request. Users can provide an implementation of the callback
     *        methods in this interface to receive notification of successful or
     *        unsuccessful completion of the operation.
     * @return A Java Future containing the result of the DeleteHapg operation
     *         returned by the service.
     */
    java.util.concurrent.Future<DeleteHapgResult> deleteHapgAsync(
            DeleteHapgRequest deleteHapgRequest,
            com.amazonaws.handlers.AsyncHandler<DeleteHapgRequest, DeleteHapgResult> asyncHandler);

    /**
     * <p>
     * Deletes an HSM. Once complete, this operation cannot be undone and your
     * key material cannot be recovered.
     * </p>
     * 
     * @param deleteHsmRequest
     *        Contains the inputs for the <a>DeleteHsm</a> action.
     * @return A Java Future containing the result of the DeleteHsm operation
     *         returned by the service.
     */
    java.util.concurrent.Future<DeleteHsmResult> deleteHsmAsync(
            DeleteHsmRequest deleteHsmRequest);

    /**
     * <p>
     * Deletes an HSM. Once complete, this operation cannot be undone and your
     * key material cannot be recovered.
     * </p>
     * 
     * @param deleteHsmRequest
     *        Contains the inputs for the <a>DeleteHsm</a> action.
     * @param asyncHandler
     *        Asynchronous callback handler for events in the lifecycle of the
     *        request. Users can provide an implementation of the callback
     *        methods in this interface to receive notification of successful or
     *        unsuccessful completion of the operation.
     * @return A Java Future containing the result of the DeleteHsm operation
     *         returned by the service.
     */
    java.util.concurrent.Future<DeleteHsmResult> deleteHsmAsync(
            DeleteHsmRequest deleteHsmRequest,
            com.amazonaws.handlers.AsyncHandler<DeleteHsmRequest, DeleteHsmResult> asyncHandler);

    /**
     * <p>
     * Deletes a client.
     * </p>
     * 
     * @param deleteLunaClientRequest
     *        null
     * @return A Java Future containing the result of the DeleteLunaClient
     *         operation returned by the service.
     */
    java.util.concurrent.Future<DeleteLunaClientResult> deleteLunaClientAsync(
            DeleteLunaClientRequest deleteLunaClientRequest);

    /**
     * <p>
     * Deletes a client.
     * </p>
     * 
     * @param deleteLunaClientRequest
     *        null
     * @param asyncHandler
     *        Asynchronous callback handler for events in the lifecycle of the
     *        request. Users can provide an implementation of the callback
     *        methods in this interface to receive notification of successful or
     *        unsuccessful completion of the operation.
     * @return A Java Future containing the result of the DeleteLunaClient
     *         operation returned by the service.
     */
    java.util.concurrent.Future<DeleteLunaClientResult> deleteLunaClientAsync(
            DeleteLunaClientRequest deleteLunaClientRequest,
            com.amazonaws.handlers.AsyncHandler<DeleteLunaClientRequest, DeleteLunaClientResult> asyncHandler);

    /**
     * <p>
     * Retrieves information about a high-availability partition group.
     * </p>
     * 
     * @param describeHapgRequest
     *        Contains the inputs for the <a>DescribeHapg</a> action.
     * @return A Java Future containing the result of the DescribeHapg operation
     *         returned by the service.
     */
    java.util.concurrent.Future<DescribeHapgResult> describeHapgAsync(
            DescribeHapgRequest describeHapgRequest);

    /**
     * <p>
     * Retrieves information about a high-availability partition group.
     * </p>
     * 
     * @param describeHapgRequest
     *        Contains the inputs for the <a>DescribeHapg</a> action.
     * @param asyncHandler
     *        Asynchronous callback handler for events in the lifecycle of the
     *        request. Users can provide an implementation of the callback
     *        methods in this interface to receive notification of successful or
     *        unsuccessful completion of the operation.
     * @return A Java Future containing the result of the DescribeHapg operation
     *         returned by the service.
     */
    java.util.concurrent.Future<DescribeHapgResult> describeHapgAsync(
            DescribeHapgRequest describeHapgRequest,
            com.amazonaws.handlers.AsyncHandler<DescribeHapgRequest, DescribeHapgResult> asyncHandler);

    /**
     * <p>
     * Retrieves information about an HSM. You can identify the HSM by its ARN
     * or its serial number.
     * </p>
     * 
     * @param describeHsmRequest
     *        Contains the inputs for the <a>DescribeHsm</a> action.
     * @return A Java Future containing the result of the DescribeHsm operation
     *         returned by the service.
     */
    java.util.concurrent.Future<DescribeHsmResult> describeHsmAsync(
            DescribeHsmRequest describeHsmRequest);

    /**
     * <p>
     * Retrieves information about an HSM. You can identify the HSM by its ARN
     * or its serial number.
     * </p>
     * 
     * @param describeHsmRequest
     *        Contains the inputs for the <a>DescribeHsm</a> action.
     * @param asyncHandler
     *        Asynchronous callback handler for events in the lifecycle of the
     *        request. Users can provide an implementation of the callback
     *        methods in this interface to receive notification of successful or
     *        unsuccessful completion of the operation.
     * @return A Java Future containing the result of the DescribeHsm operation
     *         returned by the service.
     */
    java.util.concurrent.Future<DescribeHsmResult> describeHsmAsync(
            DescribeHsmRequest describeHsmRequest,
            com.amazonaws.handlers.AsyncHandler<DescribeHsmRequest, DescribeHsmResult> asyncHandler);

    /**
     * Simplified method form for invoking the DescribeHsm operation.
     *
     * @see #describeHsmAsync(DescribeHsmRequest)
     */
    java.util.concurrent.Future<DescribeHsmResult> describeHsmAsync();

    /**
     * Simplified method form for invoking the DescribeHsm operation with an
     * AsyncHandler.
     *
     * @see #describeHsmAsync(DescribeHsmRequest,
     *      com.amazonaws.handlers.AsyncHandler)
     */
    java.util.concurrent.Future<DescribeHsmResult> describeHsmAsync(
            com.amazonaws.handlers.AsyncHandler<DescribeHsmRequest, DescribeHsmResult> asyncHandler);

    /**
     * <p>
     * Retrieves information about an HSM client.
     * </p>
     * 
     * @param describeLunaClientRequest
     *        null
     * @return A Java Future containing the result of the DescribeLunaClient
     *         operation returned by the service.
     */
    java.util.concurrent.Future<DescribeLunaClientResult> describeLunaClientAsync(
            DescribeLunaClientRequest describeLunaClientRequest);

    /**
     * <p>
     * Retrieves information about an HSM client.
     * </p>
     * 
     * @param describeLunaClientRequest
     *        null
     * @param asyncHandler
     *        Asynchronous callback handler for events in the lifecycle of the
     *        request. Users can provide an implementation of the callback
     *        methods in this interface to receive notification of successful or
     *        unsuccessful completion of the operation.
     * @return A Java Future containing the result of the DescribeLunaClient
     *         operation returned by the service.
     */
    java.util.concurrent.Future<DescribeLunaClientResult> describeLunaClientAsync(
            DescribeLunaClientRequest describeLunaClientRequest,
            com.amazonaws.handlers.AsyncHandler<DescribeLunaClientRequest, DescribeLunaClientResult> asyncHandler);

    /**
     * Simplified method form for invoking the DescribeLunaClient operation.
     *
     * @see #describeLunaClientAsync(DescribeLunaClientRequest)
     */
    java.util.concurrent.Future<DescribeLunaClientResult> describeLunaClientAsync();

    /**
     * Simplified method form for invoking the DescribeLunaClient operation with
     * an AsyncHandler.
     *
     * @see #describeLunaClientAsync(DescribeLunaClientRequest,
     *      com.amazonaws.handlers.AsyncHandler)
     */
    java.util.concurrent.Future<DescribeLunaClientResult> describeLunaClientAsync(
            com.amazonaws.handlers.AsyncHandler<DescribeLunaClientRequest, DescribeLunaClientResult> asyncHandler);

    /**
     * <p>
     * Gets the configuration files necessary to connect to all high
     * availability partition groups the client is associated with.
     * </p>
     * 
     * @param getConfigRequest
     *        null
     * @return A Java Future containing the result of the GetConfig operation
     *         returned by the service.
     */
    java.util.concurrent.Future<GetConfigResult> getConfigAsync(
            GetConfigRequest getConfigRequest);

    /**
     * <p>
     * Gets the configuration files necessary to connect to all high
     * availability partition groups the client is associated with.
     * </p>
     * 
     * @param getConfigRequest
     *        null
     * @param asyncHandler
     *        Asynchronous callback handler for events in the lifecycle of the
     *        request. Users can provide an implementation of the callback
     *        methods in this interface to receive notification of successful or
     *        unsuccessful completion of the operation.
     * @return A Java Future containing the result of the GetConfig operation
     *         returned by the service.
     */
    java.util.concurrent.Future<GetConfigResult> getConfigAsync(
            GetConfigRequest getConfigRequest,
            com.amazonaws.handlers.AsyncHandler<GetConfigRequest, GetConfigResult> asyncHandler);

    /**
     * <p>
     * Lists the Availability Zones that have available AWS CloudHSM capacity.
     * </p>
     * 
     * @param listAvailableZonesRequest
     *        Contains the inputs for the <a>ListAvailableZones</a> action.
     * @return A Java Future containing the result of the ListAvailableZones
     *         operation returned by the service.
     */
    java.util.concurrent.Future<ListAvailableZonesResult> listAvailableZonesAsync(
            ListAvailableZonesRequest listAvailableZonesRequest);

    /**
     * <p>
     * Lists the Availability Zones that have available AWS CloudHSM capacity.
     * </p>
     * 
     * @param listAvailableZonesRequest
     *        Contains the inputs for the <a>ListAvailableZones</a> action.
     * @param asyncHandler
     *        Asynchronous callback handler for events in the lifecycle of the
     *        request. Users can provide an implementation of the callback
     *        methods in this interface to receive notification of successful or
     *        unsuccessful completion of the operation.
     * @return A Java Future containing the result of the ListAvailableZones
     *         operation returned by the service.
     */
    java.util.concurrent.Future<ListAvailableZonesResult> listAvailableZonesAsync(
            ListAvailableZonesRequest listAvailableZonesRequest,
            com.amazonaws.handlers.AsyncHandler<ListAvailableZonesRequest, ListAvailableZonesResult> asyncHandler);

    /**
     * Simplified method form for invoking the ListAvailableZones operation.
     *
     * @see #listAvailableZonesAsync(ListAvailableZonesRequest)
     */
    java.util.concurrent.Future<ListAvailableZonesResult> listAvailableZonesAsync();

    /**
     * Simplified method form for invoking the ListAvailableZones operation with
     * an AsyncHandler.
     *
     * @see #listAvailableZonesAsync(ListAvailableZonesRequest,
     *      com.amazonaws.handlers.AsyncHandler)
     */
    java.util.concurrent.Future<ListAvailableZonesResult> listAvailableZonesAsync(
            com.amazonaws.handlers.AsyncHandler<ListAvailableZonesRequest, ListAvailableZonesResult> asyncHandler);

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
     * @return A Java Future containing the result of the ListHapgs operation
     *         returned by the service.
     */
    java.util.concurrent.Future<ListHapgsResult> listHapgsAsync(
            ListHapgsRequest listHapgsRequest);

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
     * @param asyncHandler
     *        Asynchronous callback handler for events in the lifecycle of the
     *        request. Users can provide an implementation of the callback
     *        methods in this interface to receive notification of successful or
     *        unsuccessful completion of the operation.
     * @return A Java Future containing the result of the ListHapgs operation
     *         returned by the service.
     */
    java.util.concurrent.Future<ListHapgsResult> listHapgsAsync(
            ListHapgsRequest listHapgsRequest,
            com.amazonaws.handlers.AsyncHandler<ListHapgsRequest, ListHapgsResult> asyncHandler);

    /**
     * Simplified method form for invoking the ListHapgs operation.
     *
     * @see #listHapgsAsync(ListHapgsRequest)
     */
    java.util.concurrent.Future<ListHapgsResult> listHapgsAsync();

    /**
     * Simplified method form for invoking the ListHapgs operation with an
     * AsyncHandler.
     *
     * @see #listHapgsAsync(ListHapgsRequest,
     *      com.amazonaws.handlers.AsyncHandler)
     */
    java.util.concurrent.Future<ListHapgsResult> listHapgsAsync(
            com.amazonaws.handlers.AsyncHandler<ListHapgsRequest, ListHapgsResult> asyncHandler);

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
     * @return A Java Future containing the result of the ListHsms operation
     *         returned by the service.
     */
    java.util.concurrent.Future<ListHsmsResult> listHsmsAsync(
            ListHsmsRequest listHsmsRequest);

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
     * @param asyncHandler
     *        Asynchronous callback handler for events in the lifecycle of the
     *        request. Users can provide an implementation of the callback
     *        methods in this interface to receive notification of successful or
     *        unsuccessful completion of the operation.
     * @return A Java Future containing the result of the ListHsms operation
     *         returned by the service.
     */
    java.util.concurrent.Future<ListHsmsResult> listHsmsAsync(
            ListHsmsRequest listHsmsRequest,
            com.amazonaws.handlers.AsyncHandler<ListHsmsRequest, ListHsmsResult> asyncHandler);

    /**
     * Simplified method form for invoking the ListHsms operation.
     *
     * @see #listHsmsAsync(ListHsmsRequest)
     */
    java.util.concurrent.Future<ListHsmsResult> listHsmsAsync();

    /**
     * Simplified method form for invoking the ListHsms operation with an
     * AsyncHandler.
     *
     * @see #listHsmsAsync(ListHsmsRequest, com.amazonaws.handlers.AsyncHandler)
     */
    java.util.concurrent.Future<ListHsmsResult> listHsmsAsync(
            com.amazonaws.handlers.AsyncHandler<ListHsmsRequest, ListHsmsResult> asyncHandler);

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
     * @return A Java Future containing the result of the ListLunaClients
     *         operation returned by the service.
     */
    java.util.concurrent.Future<ListLunaClientsResult> listLunaClientsAsync(
            ListLunaClientsRequest listLunaClientsRequest);

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
     * @param asyncHandler
     *        Asynchronous callback handler for events in the lifecycle of the
     *        request. Users can provide an implementation of the callback
     *        methods in this interface to receive notification of successful or
     *        unsuccessful completion of the operation.
     * @return A Java Future containing the result of the ListLunaClients
     *         operation returned by the service.
     */
    java.util.concurrent.Future<ListLunaClientsResult> listLunaClientsAsync(
            ListLunaClientsRequest listLunaClientsRequest,
            com.amazonaws.handlers.AsyncHandler<ListLunaClientsRequest, ListLunaClientsResult> asyncHandler);

    /**
     * Simplified method form for invoking the ListLunaClients operation.
     *
     * @see #listLunaClientsAsync(ListLunaClientsRequest)
     */
    java.util.concurrent.Future<ListLunaClientsResult> listLunaClientsAsync();

    /**
     * Simplified method form for invoking the ListLunaClients operation with an
     * AsyncHandler.
     *
     * @see #listLunaClientsAsync(ListLunaClientsRequest,
     *      com.amazonaws.handlers.AsyncHandler)
     */
    java.util.concurrent.Future<ListLunaClientsResult> listLunaClientsAsync(
            com.amazonaws.handlers.AsyncHandler<ListLunaClientsRequest, ListLunaClientsResult> asyncHandler);

    /**
     * <p>
     * Modifies an existing high-availability partition group.
     * </p>
     * 
     * @param modifyHapgRequest
     *        null
     * @return A Java Future containing the result of the ModifyHapg operation
     *         returned by the service.
     */
    java.util.concurrent.Future<ModifyHapgResult> modifyHapgAsync(
            ModifyHapgRequest modifyHapgRequest);

    /**
     * <p>
     * Modifies an existing high-availability partition group.
     * </p>
     * 
     * @param modifyHapgRequest
     *        null
     * @param asyncHandler
     *        Asynchronous callback handler for events in the lifecycle of the
     *        request. Users can provide an implementation of the callback
     *        methods in this interface to receive notification of successful or
     *        unsuccessful completion of the operation.
     * @return A Java Future containing the result of the ModifyHapg operation
     *         returned by the service.
     */
    java.util.concurrent.Future<ModifyHapgResult> modifyHapgAsync(
            ModifyHapgRequest modifyHapgRequest,
            com.amazonaws.handlers.AsyncHandler<ModifyHapgRequest, ModifyHapgResult> asyncHandler);

    /**
     * <p>
     * Modifies an HSM.
     * </p>
     * 
     * @param modifyHsmRequest
     *        Contains the inputs for the <a>ModifyHsm</a> action.
     * @return A Java Future containing the result of the ModifyHsm operation
     *         returned by the service.
     */
    java.util.concurrent.Future<ModifyHsmResult> modifyHsmAsync(
            ModifyHsmRequest modifyHsmRequest);

    /**
     * <p>
     * Modifies an HSM.
     * </p>
     * 
     * @param modifyHsmRequest
     *        Contains the inputs for the <a>ModifyHsm</a> action.
     * @param asyncHandler
     *        Asynchronous callback handler for events in the lifecycle of the
     *        request. Users can provide an implementation of the callback
     *        methods in this interface to receive notification of successful or
     *        unsuccessful completion of the operation.
     * @return A Java Future containing the result of the ModifyHsm operation
     *         returned by the service.
     */
    java.util.concurrent.Future<ModifyHsmResult> modifyHsmAsync(
            ModifyHsmRequest modifyHsmRequest,
            com.amazonaws.handlers.AsyncHandler<ModifyHsmRequest, ModifyHsmResult> asyncHandler);

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
     * @return A Java Future containing the result of the ModifyLunaClient
     *         operation returned by the service.
     */
    java.util.concurrent.Future<ModifyLunaClientResult> modifyLunaClientAsync(
            ModifyLunaClientRequest modifyLunaClientRequest);

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
     * @param asyncHandler
     *        Asynchronous callback handler for events in the lifecycle of the
     *        request. Users can provide an implementation of the callback
     *        methods in this interface to receive notification of successful or
     *        unsuccessful completion of the operation.
     * @return A Java Future containing the result of the ModifyLunaClient
     *         operation returned by the service.
     */
    java.util.concurrent.Future<ModifyLunaClientResult> modifyLunaClientAsync(
            ModifyLunaClientRequest modifyLunaClientRequest,
            com.amazonaws.handlers.AsyncHandler<ModifyLunaClientRequest, ModifyLunaClientResult> asyncHandler);

}
