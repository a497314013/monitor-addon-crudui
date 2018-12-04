package org.vaadin.crudui.layout.impl;

import java.util.HashMap;
import java.util.Map;

import org.vaadin.crudui.crud.CrudOperation;
import org.vaadin.crudui.layout.CrudLayout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class WindowBasedCrudLayout extends Composite<VerticalLayout> implements CrudLayout, HasSize {

    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -4714119202282312833L;
	protected VerticalLayout mainLayout = new VerticalLayout();
	public final static String FILTER_FIELD_LABEL = "labelName";
    protected VerticalLayout headerLayout = new VerticalLayout();
    protected HorizontalLayout toolbarLayout = new HorizontalLayout();
    protected HorizontalLayout filterButtonLayout = new HorizontalLayout();
    protected HorizontalLayout formLayoutParent = new HorizontalLayout();
    protected FormLayout filterFormLayout = new FormLayout();
    protected VerticalLayout mainComponentLayout = new VerticalLayout();
    protected Dialog dialog;
    protected String formWindowWidth;

    protected Map<CrudOperation, String> windowCaptions = new HashMap<>();

    public WindowBasedCrudLayout() {
        getContent().setPadding(false);
        getContent().setMargin(false);
        getContent().add(mainLayout);

        mainLayout.setSizeFull();
        setSizeFull();

        headerLayout.setSizeFull();
        headerLayout.setVisible(false);
        headerLayout.setSpacing(false);
        headerLayout.setMargin(false);

        toolbarLayout.setVisible(false);
        toolbarLayout.setPadding(false);
        toolbarLayout.setMargin(false);
        toolbarLayout.setWidth("100%");

        filterFormLayout.setWidth("70%");
        filterFormLayout.getStyle().set("textAlign", "right");
        headerLayout.setPadding(false);
        headerLayout.add(filterButtonLayout);
        Hr hr = new Hr();
        hr.setWidth("99%");
        hr.getStyle().set("opacity", "0.4");
        headerLayout.add(hr);
        headerLayout.add(formLayoutParent);
        formLayoutParent.add(filterFormLayout);
        Hr hr2 = new Hr();
        hr2.setWidth("99%");
        hr2.getStyle().set("opacity", "0.4");
        headerLayout.add(hr2);
        headerLayout.setHeight(null);

        filterButtonLayout.setSizeFull();
        filterButtonLayout.setWidth("95%");
        formLayoutParent.setWidth("95%");
        toolbarLayout.setWidth("95%");
        filterButtonLayout.getStyle().set("marginLeft", "5px");
        filterButtonLayout.getStyle().set("marginTop", "5px");
        formLayoutParent.getStyle().set("marginLeft", "5px");
        toolbarLayout.getStyle().set("marginLeft", "5px");
        toolbarLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
        filterButtonLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
        mainComponentLayout.setMargin(false);
        mainComponentLayout.setPadding(false);
        formLayoutParent.setMargin(false);
        formLayoutParent.setPadding(false);
        mainComponentLayout.add(toolbarLayout);
        mainLayout.setMargin(false);
        mainLayout.setPadding(false);
        mainLayout.getElement().setAttribute("theme", "");
        mainComponentLayout.getElement().setAttribute("theme", "");
        mainLayout.add(mainComponentLayout);
        mainLayout.expand(mainComponentLayout);

        setWindowCaption(CrudOperation.ADD, "Add");
        setWindowCaption(CrudOperation.UPDATE, "Update");
        setWindowCaption(CrudOperation.DELETE, "Are you sure you want to delete this item(s)?");
    }

    @Override
    public void setMainComponent(Component component) {
        mainComponentLayout.removeAll();
        mainComponentLayout.add(toolbarLayout);
        mainComponentLayout.add(component);
    }
    
    @Override
    public void addFilterButton(Component component)
    {
    	component.getElement().getStyle().set("opacity", "0.8");
    	filterButtonLayout.setVerticalComponentAlignment(FlexComponent.Alignment.END, component);
    	filterButtonLayout.add(component);
    }

    @Override
    public void addFilterComponent(Component component) {
    	if (!headerLayout.isVisible()) {
    		headerLayout.setVisible(true);
    		mainLayout.getElement().insertChild(mainLayout.getComponentCount() - 1, headerLayout.getElement());
    	}
    	formLayoutParent.setVisible(true);
    	filterFormLayout.setVisible(true);
    	Label label = new Label();
        label.setText(component.getElement().getProperty("labelName"));
        component.getElement().getStyle().set("opacity", "0.8");
        filterFormLayout.getElement().getOuterHTML();
        filterFormLayout.addFormItem(component, component.getElement().getProperty("labelName"));
    }

    @Override
    public void addToolbarComponent(Component component) {
        if (!headerLayout.isVisible()) {
            headerLayout.setVisible(true);
            mainLayout.getElement().insertChild(mainLayout.getComponentCount() - 1, headerLayout.getElement());
        }

        toolbarLayout.setVisible(true);
        toolbarLayout.add(component);
    }

    private void showDialog(String caption, Component form) {
        VerticalLayout dialogLayout = new VerticalLayout(form);
        dialogLayout.setMargin(false);
        dialogLayout.setPadding(false);
        dialog = new Dialog(new H3(caption), dialogLayout);
        dialog.setWidth("600px");
        dialog.open();
    }
    
    private void showDialog(Dialog dialog) {
    	this.dialog = dialog;
    	dialog.open();
    }

    
    
    @Override
    public void showForm(CrudOperation operation, Component form) {
        if (!operation.equals(CrudOperation.READ)) {
            showDialog(windowCaptions.get(operation), form);
        }
    }
    
    public void showFormForMutilDelete(CrudOperation operation, Component form) {
        if (operation.equals(CrudOperation.DELETE)) {
            showDialog((Dialog)form);
        }
    }

    @Override
    public void hideForm() {
        if (dialog != null) {
            dialog.close();
        }
    }

    public WindowBasedCrudLayout setWindowCaption(CrudOperation operation, String caption) {
        windowCaptions.put(operation, caption);
        return this;
    }

    public void setFormWindowWidth(String formWindowWidth) {
        this.formWindowWidth = formWindowWidth;
    }

}
