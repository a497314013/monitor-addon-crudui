package org.vaadin.crudui.crud.mutil;

import java.util.Collections;

import org.vaadin.crudui.crud.AddOperationListener;
import org.vaadin.crudui.crud.FindAllCrudOperationListener;
import org.vaadin.crudui.crud.LazyCrudListener;
import org.vaadin.crudui.crud.LazyFindAllCrudOperationListener;
import org.vaadin.crudui.crud.UpdateOperationListener;
import org.vaadin.crudui.form.CrudFormFactory;
import org.vaadin.crudui.layout.CrudLayout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;

public abstract class AbstractMutilCrud<T> extends Composite<VerticalLayout> implements MutilCrud<T>, HasSize {

    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -7407535199635391967L;

	protected Class<T> domainType;

    protected FindAllCrudOperationListener<T> findAllOperation = () -> Collections.emptyList();
    protected AddOperationListener<T> addOperation = t -> null;
    protected UpdateOperationListener<T> updateOperation = t -> null;
    protected MutilDeleteOperationListener<T> deleteOperation = t -> { };

    protected CrudLayout crudLayout;
    protected CrudFormFactory<T> crudFormFactory;

    public AbstractMutilCrud(Class<T> domainType, CrudLayout crudLayout, CrudFormFactory<T> crudFormFactory, MutilCrudListener<T> crudListener) {
        this.domainType = domainType;
        this.crudLayout = crudLayout;
        this.crudFormFactory = crudFormFactory;

        if (crudListener != null) {
            setCrudListener(crudListener);
        }
        getContent().add((Component) crudLayout);
        getContent().setPadding(false);
        getContent().setMargin(false);
        setSizeFull();
    }

    public CrudLayout getCrudLayout() {
        return crudLayout;
    }

    @Override
    public void setCrudFormFactory(CrudFormFactory<T> crudFormFactory) {
        this.crudFormFactory = crudFormFactory;
    }

    @Override
    public void setFindAllOperation(FindAllCrudOperationListener<T> findAllOperation) {
        this.findAllOperation = findAllOperation;
    }

    @Override
    public void setFindAllOperation(DataProvider<T, Void> dataProvider) {
        this.findAllOperation = (LazyFindAllCrudOperationListener<T>) () -> dataProvider;
    }

    @Override
    public void setAddOperation(AddOperationListener<T> addOperation) {
        this.addOperation = addOperation;
    }

    @Override
    public void setUpdateOperation(UpdateOperationListener<T> updateOperation) {
        this.updateOperation = updateOperation;
    }

    @Override
    public void setDeleteOperation(MutilDeleteOperationListener<T> deleteOperation) {
        this.deleteOperation = deleteOperation;
    }

    @Override
    public void setOperations(FindAllCrudOperationListener<T> findAllOperation, AddOperationListener<T> addOperation, UpdateOperationListener<T> updateOperation, MutilDeleteOperationListener<T> deleteOperation) {
        setFindAllOperation(findAllOperation);
        setAddOperation(addOperation);
        setUpdateOperation(updateOperation);
        setDeleteOperation(deleteOperation);
    }

    @Override
    public CrudFormFactory<T> getCrudFormFactory() {
        return crudFormFactory;
    }

	@SuppressWarnings("unchecked")
	@Override
    public void setCrudListener(MutilCrudListener<T> crudListener) {
        setAddOperation(crudListener::add);
        setUpdateOperation(crudListener::update);
        setDeleteOperation(crudListener::delete);

        if (LazyCrudListener.class.isAssignableFrom(crudListener.getClass())) {
            setFindAllOperation((LazyFindAllCrudOperationListener<T>) () -> ((LazyCrudListener<T>)crudListener).getDataProvider());
        } else {
            setFindAllOperation(crudListener::findAll);
        }
    }

}
