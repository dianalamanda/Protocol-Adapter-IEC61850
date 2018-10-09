/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.processors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.requests.SetEventNotificationsDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.DeviceRequestMessageType;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.SsldDeviceRequestMessageProcessor;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.RequestMessageData;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.Iec61850DeviceResponseHandler;
import org.opensmartgridplatform.dto.valueobjects.EventNotificationMessageDataContainerDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

/**
 * Class for processing common set event notifications request messages
 */
@Component("iec61850CommonSetEventNotificationsRequestMessageProcessor")
public class CommonSetEventNotificationsRequestMessageProcessor extends SsldDeviceRequestMessageProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(CommonSetEventNotificationsRequestMessageProcessor.class);

    public CommonSetEventNotificationsRequestMessageProcessor() {
        super(DeviceRequestMessageType.SET_EVENT_NOTIFICATIONS);
    }

    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {
        LOGGER.debug("Processing common set event notifications request message");

        MessageMetadata messageMetadata;
        EventNotificationMessageDataContainerDto eventNotificationsContainer;
        try {
            messageMetadata = MessageMetadata.fromMessage(message);
            eventNotificationsContainer = (EventNotificationMessageDataContainerDto) message.getObject();
        } catch (final JMSException e) {
            LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
            return;
        }

        final RequestMessageData requestMessageData = RequestMessageData.newBuilder().messageMetadata(messageMetadata)
                .build();

        this.printDomainInfo(requestMessageData);

        final Iec61850DeviceResponseHandler iec61850DeviceResponseHandler = this
                .createIec61850DeviceResponseHandler(requestMessageData, message);

        final DeviceRequest.Builder deviceRequestBuilder = DeviceRequest.newBuilder().messageMetaData(messageMetadata);

        this.deviceService.setEventNotifications(
                new SetEventNotificationsDeviceRequest(deviceRequestBuilder, eventNotificationsContainer),
                iec61850DeviceResponseHandler);
    }
}