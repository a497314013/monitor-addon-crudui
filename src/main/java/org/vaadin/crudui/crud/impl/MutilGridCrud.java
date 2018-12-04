package org.vaadin.crudui.crud.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ButtonType;
import org.claspina.confirmdialog.ConfirmDialog;
import org.vaadin.crudui.crud.CrudOperation;
import org.vaadin.crudui.crud.LazyFindAllCrudOperationListener;
import org.vaadin.crudui.crud.mutil.AbstractMutilCrud;
import org.vaadin.crudui.crud.mutil.MutilCrudListener;
import org.vaadin.crudui.form.CrudFormFactory;
import org.vaadin.crudui.form.impl.form.factory.DefaultCrudFormFactory;
import org.vaadin.crudui.layout.CrudLayout;
import org.vaadin.crudui.layout.impl.WindowBasedCrudLayout;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.selection.SelectionEvent;

public class MutilGridCrud<T> extends AbstractMutilCrud<T> {

    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -3212577556758814742L;
	protected String rowCountCaption = "%d items(s) found";
    protected String savedMessage = "Item saved";
    protected String deletedSuccessMessage = "%s item(s) deleted";
    protected String deletedMessage = "Are you sure to delete those items?";

    protected Button findAllButton;
    protected Button addButton;
    protected Button updateButton;
    protected Button deleteButton;
    protected Grid<T> grid;
    private T firstSelectedObj;
    private int seq;
    protected boolean isRefreshAtGridLoad = false;

    private boolean clickRowToUpdate;

    public MutilGridCrud(Class<T> domainType) {
        this(domainType, new WindowBasedCrudLayout(), new DefaultCrudFormFactory<>(domainType), null);
    }

    public MutilGridCrud(Class<T> domainType, CrudLayout crudLayout) {
        this(domainType, crudLayout, new DefaultCrudFormFactory<>(domainType), null);
    }

    public MutilGridCrud(Class<T> domainType, CrudFormFactory<T> crudFormFactory) {
        this(domainType, new WindowBasedCrudLayout(), crudFormFactory, null);
    }

    public MutilGridCrud(Class<T> domainType, MutilCrudListener<T> crudListener) {
        this(domainType, new WindowBasedCrudLayout(), new DefaultCrudFormFactory<>(domainType), crudListener);
    }

    public MutilGridCrud(Class<T> domainType, CrudLayout crudLayout, CrudFormFactory<T> crudFormFactory) {
        this(domainType, crudLayout, crudFormFactory, null);
    }

    public MutilGridCrud(Class<T> domainType, CrudLayout crudLayout, CrudFormFactory<T> crudFormFactory, MutilCrudListener<T> crudListener) {
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

        grid = new Grid<>(domainType);
        grid.setSizeFull();
        
        grid.setSelectionMode(SelectionMode.MULTI);
        grid.addSelectionListener(e -> gridSelectionChanged(e));
        crudLayout.setMainComponent(grid);
        updateButtons();
    }
    
    public void setGridIndexVisible(boolean isVisible)
    {
    	if(grid.getColumnByKey("#seq") != null)
    	{
    		grid.getColumnByKey("#seq").setVisible(isVisible);
    	}
    }
    
    public void addGridRowIndex(String header)
    {
    	seq = 1;
    	grid.addColumn(item->this.getSeq()).setHeader(header).setKey("#seq");
    	/*grid.addSortListener(e->{
    		this.seq = 1;
    	});*/
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

    public T getFirstSelectedObj()
	{
		return firstSelectedObj;
	}
    
    private int getSeq()
	{
    	if(seq > grid.getDataProvider().size(new Query<>()))
    	{
    		seq = 1;
    	}
		return seq++;
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
        grid.deselectAll();
    }

    public void setClickRowToUpdate(boolean clickRowToUpdate) {
        this.clickRowToUpdate = clickRowToUpdate;
    }

    protected void updateButtons() {
        boolean rowSelected = !grid.getSelectedItems().isEmpty();
        updateButton.setEnabled(rowSelected);
        deleteButton.setEnabled(rowSelected);
    }

    protected void gridSelectionChanged(SelectionEvent<Grid<T>, T> e) {
        updateButtons();
        T domainObject = e.getFirstSelectedItem().orElse(null);
        firstSelectedObj = domainObject;
        if (domainObject != null) {
            if (clickRowToUpdate) {
                updateButtonClicked();
            }
        } else {
            crudLayout.hideForm();
        }
    }

    protected void findAllButtonClicked() {
        grid.deselectAll();
        refreshGrid();
        Notification.show(String.format(rowCountCaption, grid.getDataProvider().size(new Query<>())));
    }

    protected void addButtonClicked() {
        try {
            T domainObject = domainType.newInstance();
            showForm(CrudOperation.ADD, domainObject, false, savedMessage, event -> {
                addOperation.perform(domainObject);
                refreshGrid();
            });
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    protected void updateButtonClicked() {
    	if(firstSelectedObj == null)
    	{
    		return;
    	}
        T domainObject = firstSelectedObj;
        showForm(CrudOperation.UPDATE, domainObject, false, savedMessage, event -> {
    		updateOperation.perform(domainObject);
			refreshGrid();
        });
    }

    protected void deleteButtonClicked() {
        Set<T> domainObjects = grid.getSelectedItems();
        Dialog dialog = ConfirmDialog.createQuestion().withCaption("System alert")
		.withMessage(deletedMessage)
		.withButton(ButtonType.OK,null,"primary error",()->{
			deleteOperation.perform(domainObjects);
			refreshGrid();
			Notification.show(String.format(deletedSuccessMessage,domainObjects.size()+""));
		},ButtonOption.focus(),ButtonOption.caption("Yes"))
		.withButton(ButtonType.CANCEL,null,"secondary",crudLayout::hideForm, ButtonOption.caption("Cancel"));
        
        crudLayout.showFormForMutilDelete(CrudOperation.DELETE, dialog);
    }
    
    protected void showForm(CrudOperation operation, T domainObject, boolean readOnly, String successMessage, ComponentEventListener<ClickEvent<Button>> buttonClickListener) {
        Component form = crudFormFactory.buildNewForm(operation, domainObject, readOnly, cancelClickEvent -> {
            if (clickRowToUpdate) {
                grid.deselectAll();
            } else {
                crudLayout.hideForm();
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