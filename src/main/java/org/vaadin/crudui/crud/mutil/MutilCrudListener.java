package org.vaadin.crudui.crud.mutil;

import java.io.Serializable;
import java.util.Collection;

public interface MutilCrudListener<T> extends Serializable {

    Collection<T> findAll();

    T add(T domainObjectToAdd);

    T update(T domainObjectToUpdate);

    void delete(Collection<T> domainObjectToDelete);

}
