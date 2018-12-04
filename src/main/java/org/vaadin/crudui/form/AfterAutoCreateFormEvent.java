/*
 * @(#) AfterAutoCreateFormEvent.java 2018年11月27日 下午7:59:27
 *
 * Copyright 2018 Rockwell Automation, Inc. All rights reserved.
 * Rockwell Automation PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */package org.vaadin.crudui.form;

import com.vaadin.flow.component.formlayout.FormLayout;

@FunctionalInterface
public interface AfterAutoCreateFormEvent<T>
{
	 void perfom(FormLayout formLayout,T domainObject);
}
