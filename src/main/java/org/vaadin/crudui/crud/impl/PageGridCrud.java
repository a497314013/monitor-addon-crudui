package org.vaadin.crudui.crud.impl;

import java.util.Collection;
import java.util.Collections;

import org.vaadin.crudui.crud.AbstractCrud;
import org.vaadin.crudui.crud.CrudListener;
import org.vaadin.crudui.crud.CrudOperation;
import org.vaadin.crudui.crud.LazyFindAllCrudOperationListener;
import org.vaadin.crudui.form.CrudFormFactory;
import org.vaadin.crudui.form.impl.form.factory.DefaultCrudFormFactory;
import org.vaadin.crudui.layout.CrudLayout;
import org.vaadin.crudui.layout.impl.WindowBasedCrudLayout;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.Query;

public class PageGridCrud<T> extends AbstractCrud<T> {

    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -3212577556758814742L;
	protected String rowCountCaption = "%d items(s) found";
    protected String savedMessage = "Item saved";
    protected String deletedMessage = "Item deleted";

    protected Button findAllButton;
    protected Button addButton;
    protected Button updateButton;
    protected Button deleteButton;
    protected Grid<T> grid;
    protected boolean isRefreshAtGridLoad = false;

    private boolean clickRowToUpdate;

    public PageGridCrud(Class<T> domainType) {
        this(domainType, new WindowBasedCrudLayout(), new DefaultCrudFormFactory<>(domainType), null);
    }

    public PageGridCrud(Class<T> domainType, CrudLayout crudLayout) {
        this(domainType, crudLayout, new DefaultCrudFormFactory<>(domainType), null);
    }

    public PageGridCrud(Class<T> domainType, CrudFormFactory<T> crudFormFactory) {
        this(domainType, new WindowBasedCrudLayout(), crudFormFactory, null);
    }

    public PageGridCrud(Class<T> domainType, CrudListener<T> crudListener) {
        this(domainType, new WindowBasedCrudLayout(), new DefaultCrudFormFactory<>(domainType), crudListener);
    }

    public PageGridCrud(Class<T> domainType, CrudLayout crudLayout, CrudFormFactory<T> crudFormFactory) {
        this(domainType, crudLayout, crudFormFactory, null);
    }

    public PageGridCrud(Class<T> domainType, CrudLayout crudLayout, CrudFormFactory<T> crudFormFactory, CrudListener<T> crudListener) {
        super(domainType, crudLayout, crudFormFactory, crudListener);
        initLayout();
    }

    protected void initLayout() {
        findAllButton = new Button(VaadinIcon.REFRESH.create(), e -> findAllButtonClicked());
        findAllButton.getElement().setAttribute("title", "Refresh list");

        crudLayout.addToolbarComponent(findAllButton);

        addButton = new Button(VaadinIcon.PLUS.create(), e -> addButtonClicked());
        addButton.getElement().setAttribute("title", "Add");
        crudLayout.addToolbarComponent(addButton);

        updateButton = new Button(VaadinIcon.PENCIL.create(), e -> updateButtonClicked());
        updateButton.getElement().setAttribute("title", "Update");
        crudLayout.addToolbarComponent(updateButton);

        deleteButton = new Button(VaadinIcon.TRASH.create(), e -> deleteButtonClicked());
        deleteButton.getElement().setAttribute("title", "Delete");
        crudLayout.addToolbarComponent(deleteButton);

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setPadding(false);
        verticalLayout.setMargin(false);
        verticalLayout.setSizeFull();
        verticalLayout.getStyle().set("border", "1px solid red");
        grid = new Grid<>(domainType);
        grid.setSizeFull();
        grid.addSelectionListener(e -> gridSelectionChanged());
        verticalLayout.add(grid);
        crudLayout.setMainComponent(verticalLayout);

        updateButtons();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if(isRefreshAtGridLoad)
        {
        	refreshGrid();
        }
    }

    @Override
    public void setAddOperationVisible(boolean visible) {
        addButton.setVisible(visible);
    }

    @Override
    public void setUpdateOperationVisible(boolean visible) {
        updateButton.setVisible(visible);
    }

    @Override
    public void setDeleteOperationVisible(boolean visible) {
        deleteButton.setVisible(visible);
    }

    @Override
    public void setFindAllOperationVisible(boolean visible) {
        findAllButton.setVisible(false);
    }

    public void refreshGrid() {
        if (LazyFindAllCrudOperationListener.class.isAssignableFrom(findAllOperation.getClass())) {
            LazyFindAllCrudOperationListener<T> findAll = (LazyFindAllCrudOperationListener<T>) findAllOperation;

            grid.setDataProvider(findAll.getDataProvider());

        } else {
            Collection<T> items = findAllOperation.findAll();
            if(items != null && items.size() > 0)
            {
            	grid.setItems(items);
            }
            else
            {
            	grid.setItems(Collections.emptyList());
            }
        }
    }

    public void setClickRowToUpdate(boolean clickRowToUpdate) {
        this.clickRowToUpdate = clickRowToUpdate;
    }

    protected void updateButtons() {
        boolean rowSelected = !grid.asSingleSelect().isEmpty();
        updateButton.setEnabled(rowSelected);
        deleteButton.setEnabled(rowSelected);
    }

    protected void gridSelectionChanged() {
        updateButtons();
        T domainObject = grid.asSingleSelect().getValue();

        if (domainObject != null) {
            if (clickRowToUpdate) {
                updateButtonClicked();
            } else {
                Component form = crudFormFactory.buildNewForm(CrudOperation.READ, domainObject, true, null, event -> {
                    grid.asSingleSelect().clear();
                });

                crudLayout.showForm(CrudOperation.READ, form);
            }
        } else {
            crudLayout.hideForm();
        }
    }

    protected void findAllButtonClicked() {
        grid.asSingleSelect().clear();
        refreshGrid();
        Notification.show(String.format(rowCountCaption, grid.getDataProvider().size(new Query<>())));
    }

    protected void addButtonClicked() {
        try {
            T domainObject = domainType.newInstance();
            showForm(CrudOperation.ADD, domainObject, false, savedMessage, event -> {
                T addedObject = addOperation.perform(domainObject);
                refreshGrid();
                grid.asSingleSelect().setValue(addedObject);
            });
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    protected void updateButtonClicked() {
        T domainObject = grid.asSingleSelect().getValue();
        showForm(CrudOperation.UPDATE, domainObject, false, savedMessage, event -> {
            T updatedObject = updateOperation.perform(domainObject);
            grid.asSingleSelect().clear();
            refreshGrid();
            grid.asSingleSelect().setValue(updatedObject);
        });
    }

    protected void deleteButtonClicked() {
        T domainObject = grid.asSingleSelect().getValue();
        showForm(CrudOperation.DELETE, domainObject, true, deletedMessage, event -> {
            deleteOperation.perform(domainObject);
            refreshGrid();
            grid.asSingleSelect().clear();
        });
    }

    protected void showForm(CrudOperation operation, T domainObject, boolean readOnly, String successMessage, ComponentEventListener<ClickEvent<Button>> buttonClickListener) {
        Component form = crudFormFactory.buildNewForm(operation, domainObject, readOnly, cancelClickEvent -> {
            if (clickRowToUpdate) {
                grid.asSingleSelect().clear();
            } else {
                T selected = grid.asSingleSelect().getValue();
                crudLayout.hideForm();
                grid.asSingleSelect().clear();
                grid.asSingleSelect().setValue(selected);
            }
        }, operationPerformedClickEvent -> {
            buttonClickListener.onComponentEvent(operationPerformedClickEvent);
            crudLayout.hideForm();
            Notification.show(successMessage);
        });

        crudLayout.showForm(operation, form);
    }
    
    protected void showDialog(CrudOperation operation, T domainObject, boolean readOnly, String successMessage, ComponentEventListener<ClickEvent<Button>> buttonClickListener) {
        Component form = crudFormFactory.buildNewForm(operation, domainObject, readOnly, cancelClickEvent -> {
            if (clickRowToUpdate) {
                grid.asSingleSelect().clear();
            } else {
                T selected = grid.asSingleSelect().getValue();
                crudLayout.hideForm();
                grid.asSingleSelect().clear();
                grid.asSingleSelect().setValue(selected);
            }
        }, operationPerformedClickEvent -> {
            buttonClickListener.onComponentEvent(operationPerformedClickEvent);
            crudLayout.hideForm();
            Notification.show(successMessage);
        });

        crudLayout.showForm(operation, form);
    }

    public Grid<T> getGrid() {
        return grid;
    }

    public Button getFindAllButton() {
        return findAllButton;
    }

    public Button getAddButton() {
        return addButton;
    }

    public Button getUpdateButton() {
        return updateButton;
    }

    public Button getDeleteButton() {
        return deleteButton;
    }

    public void setRowCountCaption(String rowCountCaption) {
        this.rowCountCaption = rowCountCaption;
    }

    public void setSavedMessage(String savedMessage) {
        this.savedMessage = savedMessage;
    }

    public void setDeletedMessage(String deletedMessage) {
        this.deletedMessage = deletedMessage;
    }

}