/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.openmuc.openiec61850.Fc;

import com.alliander.osgp.adapter.protocol.iec61850.device.rtu.RtuCommand;
import com.alliander.osgp.adapter.protocol.iec61850.exceptions.NodeReadException;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.LogicalNode;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.NodeContainer;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.QualityConverter;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.SubDataAttribute;
import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementDto;

public class Iec61850AlarmCommand implements RtuCommand {

    private static final Map<Integer, DataAttribute> map;

    static {
        map = new HashMap<Integer, DataAttribute>();
        map.put(1, DataAttribute.ALARM_ONE);
        map.put(2, DataAttribute.ALARM_TWO);
        map.put(3, DataAttribute.ALARM_THREE);
        map.put(4, DataAttribute.ALARM_FOUR);
    }

    private int alarmIndex;

    public Iec61850AlarmCommand(final int alarmIndex) {
        this.alarmIndex = alarmIndex;
    }

    @Override
    public MeasurementDto execute(final Iec61850Client client, final DeviceConnection connection,
            final LogicalDevice logicalDevice) throws NodeReadException {
        final NodeContainer containingNode = connection.getFcModelNode(logicalDevice, LogicalNode.GENERIC_PROCESS_I_O,
                map.get(this.alarmIndex), Fc.ST);
        client.readNodeDataValues(connection.getConnection().getClientAssociation(), containingNode.getFcmodelNode());
        return this.translate(containingNode);
    }

    @Override
    public MeasurementDto translate(final NodeContainer containingNode) {
        return new MeasurementDto(1, map.get(this.alarmIndex).getDescription(),
                QualityConverter.toShort(containingNode.getQuality(SubDataAttribute.QUALITY).getValue()),
                new DateTime(containingNode.getDate(SubDataAttribute.TIME), DateTimeZone.UTC),
                containingNode.getBoolean(SubDataAttribute.STATE).getValue() ? 1 : 0);
    }

}