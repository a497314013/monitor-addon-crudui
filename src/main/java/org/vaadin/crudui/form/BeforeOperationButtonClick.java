/*
 * @(#) BeforeOperationButtonClick.java 2018年11月28日 下午3:23:48
 *
 * Copyright 2018 Rockwell Automation, Inc. All rights reserved.
 * Rockwell Automation PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */package org.vaadin.crudui.form;

public interface BeforeOperationButtonClick<T>
{
	boolean beforeOperationButtonClick(T domin);
}
