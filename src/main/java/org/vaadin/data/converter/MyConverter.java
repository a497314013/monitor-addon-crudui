package org.vaadin.data.converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

class StringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {

	@Override
	public Result<LocalDateTime> convertToModel(String value, ValueContext arg1)
	{
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		return Result.ok(LocalDateTime.parse(value, df));
	}

	@Override
	public String convertToPresentation(LocalDateTime value, ValueContext arg1)
	{
		if (value == null) {
            return null;
        }

        return value.toString();
	}
}