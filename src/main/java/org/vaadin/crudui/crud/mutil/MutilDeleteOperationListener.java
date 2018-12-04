package org.vaadin.crudui.crud.mutil;

import java.io.Serializable;
import java.util.Collection;

@FunctionalInterface
public interface MutilDeleteOperationListener<T> extends Serializable {

    void perform(Collection<T> domainObject);

}
