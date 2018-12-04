package org.vaadin.crudui.crud;

import java.io.Serializable;

@FunctionalInterface
public interface DeleteOperationListener<T> extends Serializable {

    void perform(T domainObject);

}
