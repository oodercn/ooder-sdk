package net.ooder.agent.client.iot.enums;

import org.springframework.core.convert.converter.Converter;

public class DeviceDataTypeKeyConverter implements Converter<String, DeviceDataTypeKey> {

    public DeviceDataTypeKey convert(String source) {
	String value = source.trim();
	if ("".equals(value)) {
	    return null;
	}
	return DeviceDataTypeKey.fromType(source);

    }
}
