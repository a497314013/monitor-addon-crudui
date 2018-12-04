package org.vaadin.crudui.layout.impl;

import org.vaadin.crudui.crud.CrudOperation;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout.Orientation;

public class VerticalSplitCrudLayout extends AbstractTwoComponentsCrudLayout {

    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -3359731547992852568L;

	public VerticalSplitCrudLayout() {
        secondComponentHeaderLayout.setMargin(true);
    }

    @Override
    protected SplitLayout buildMainLayout() {
        SplitLayout mainLayout = new SplitLayout(firstComponent, secondComponent);
        mainLayout.setOrientation(Orientation.VERTICAL);
        mainLayout.setSizeFull();
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
