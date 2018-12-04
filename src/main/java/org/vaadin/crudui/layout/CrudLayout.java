package org.vaadin.crudui.layout;

import com.vaadin.flow.component.Component;
import org.vaadin.crudui.crud.CrudOperation;

import java.util.stream.Stream;

/**
 * @author Alejandro Duarte
 */
public interface CrudLayout {

    void setMainComponent(Component component);

    void addFilterComponent(Component component);
    
    void addFilterButton(Component component);

    default void addFilterComponents(Component... components) {
        Stream.of(components).forEach(this::addFilterComponent);
    }

    void addToolbarComponent(Component component);

    void showForm(CrudOperation operation, Component form);

    void showFormForMutilDelete(CrudOperation operation, Component form);
    
    void hideForm();

}
