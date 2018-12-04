package org.vaadin.crudui.layout.impl;

import org.vaadin.crudui.crud.CrudOperation;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class VerticalCrudLayout extends AbstractTwoComponentsCrudLayout {

    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 5013721509807646787L;

	@Override
    protected Component buildMainLayout() {
        VerticalLayout mainLayout = new VerticalLayout(firstComponent, secondComponent);
        mainLayout.setSizeFull();
        mainLayout.setMargin(false);
        mainLayout.setPadding(false);
        firstComponent.setSizeFull();
        secondComponent.setSizeFull();
        return mainLayout;
    }

    @Override
    protected void addToolbarLayout(Component toolbarLayout) {
        firstComponentHeaderLayout.add(toolbarLayout);
    }

    @Override
    public void addToolbarComponent(Component component) {
        if (!firstComponentHeaderLayout.isVisible()) {
            firstComponentHeaderLayout.setVisible(true);
            firstComponent.getElement().insertChild(firstComponent.getComponentCount() - 1, firstComponentHeaderLayout.getElement());
        }

        toolbarLayout.setVisible(true);
        toolbarLayout.add(component);
    }

	@Override
	public void addFilterButton(Component component)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showFormForMutilDelete(CrudOperation operation, Component form)
	{
		// TODO Auto-generated method stub
		
	}

}
