package org.vaadin.crudui.layout.impl;

import org.vaadin.crudui.crud.CrudOperation;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.splitlayout.SplitLayout;

public class HorizontalSplitCrudLayout extends AbstractTwoComponentsCrudLayout {

    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 2463499962514210610L;

	public HorizontalSplitCrudLayout() {
        secondComponentHeaderLayout.setMargin(false);
    }

    @Override
    protected SplitLayout buildMainLayout() {
        SplitLayout mainLayout = new SplitLayout(firstComponent, secondComponent);
        mainLayout.setSizeFull();
        mainLayout.setSplitterPosition(65);
        return mainLayout;
    }

    @Override
    protected void addToolbarLayout(Component toolbarLayout) {
        secondComponentHeaderLayout.add(toolbarLayout);
    }

    @Override
    public void addToolbarComponent(Component component) {
        if (!secondComponentHeaderLayout.isVisible()) {
            secondComponentHeaderLayout.setVisible(true);
            secondComponent.getElement().insertChild(secondComponent.getComponentCount() - 1, secondComponentHeaderLayout.getElement());
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
