/*
 * @(#) FieldValidor.java 2018年11月27日 上午11:27:06
 *
 * Copyright 2018 Rockwell Automation, Inc. All rights reserved.
 * Rockwell Automation PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */package org.vaadin.data.validor;

import com.vaadin.flow.function.SerializablePredicate;

public class FieldValidor<T>
{
	private SerializablePredicate<T> validor;
	private String message;
	
	public SerializablePredicate<T> getValidor()
	{
		return validor;
	}
	public void setValidor(SerializablePredicate<T> validor)
	{
		this.validor = validor;
	}
	public String getMessage()
	{
		return message;
	}
	public void setMessage(String message)
	{
		this.message = message;
	}
}
