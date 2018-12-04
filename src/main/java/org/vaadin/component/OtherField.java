/*
 * @(#) OtherField.java 2018年11月27日 上午11:46:31
 *
 * Copyright 2018 Rockwell Automation, Inc. All rights reserved.
 * Rockwell Automation PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */package org.vaadin.component;

import org.vaadin.data.validor.OtherFieldValidor;
import org.vaadin.data.validor.ValidorFailAction;

import com.vaadin.flow.component.Component;

public class OtherField
{
	private OtherFieldValidor validor = () -> true;
	private ValidorFailAction failAction = () -> {};
	private Component field;
	
	public OtherField()
	{
		
	}
	
	public OtherField(Component field)
	{
		this.field = field;
	}
	
	public OtherFieldValidor getValidor()
	{
		return validor;
	}

	public void setValidor(OtherFieldValidor validor)
	{
		this.validor = validor;
	}

	public Component getField()
	{
		return field;
	}

	public void setField(Component field)
	{
		this.field = field;
	}
	
	public ValidorFailAction getFailAction()
	{
		return failAction;
	}

	public void setFailAction(ValidorFailAction failAction)
	{
		this.failAction = failAction;
	}
	
}
