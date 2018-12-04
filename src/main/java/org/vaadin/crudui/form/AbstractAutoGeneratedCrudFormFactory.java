package org.vaadin.crudui.form;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.vaadin.component.OtherField;
import org.vaadin.crudui.crud.CrudOperation;
import org.vaadin.crudui.crud.CrudOperationException;
import org.vaadin.crudui.form.impl.field.provider.DefaultFieldProvider;
import org.vaadin.data.converter.StringToByteConverter;
import org.vaadin.data.converter.StringToCharacterConverter;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.LocalDateToDateConverter;
import com.vaadin.flow.data.converter.StringToBigDecimalConverter;
import com.vaadin.flow.data.converter.StringToBigIntegerConverter;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.data.converter.StringToFloatConverter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.converter.StringToLongConverter;
import com.vaadin.flow.internal.BeanUtil;
import com.vaadin.flow.shared.util.SharedUtil;

public abstract class AbstractAutoGeneratedCrudFormFactory<T> extends AbstractCrudFormFactory<T> {

    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -3514409493593217247L;
	protected Map<CrudOperation, String> buttonCaptions = new HashMap<>();
    protected Map<CrudOperation, Icon> buttonIcons = new HashMap<>();
    protected Map<CrudOperation, Set<String>> buttonStyleNames = new HashMap<>();
    protected Map<CrudOperation, String> buttonThemes = new HashMap<>();
    protected Map<CrudOperation,List<OtherField>> otherFieldswithValidor = new HashMap<>();
    protected String cancelButtonCaption = "Cancel";
    protected String validationErrorMessage = "Please fix the errors and try again";
    protected BeforeOperationButtonClick<T> beforeOperationButtonClick = t -> true;
    protected Class<T> domainType;

    protected Binder<T> binder;

    public AbstractAutoGeneratedCrudFormFactory(Class<T> domainType) {
        this.domainType = domainType;

        setButtonCaption(CrudOperation.READ, "Ok");
        setButtonCaption(CrudOperation.ADD, "Add");
        setButtonCaption(CrudOperation.UPDATE, "Update");
        setButtonCaption(CrudOperation.DELETE, "Yes, delete");

        setButtonIcon(CrudOperation.READ, null);
        setButtonIcon(CrudOperation.ADD, VaadinIcon.CHECK.create());
        setButtonIcon(CrudOperation.UPDATE, VaadinIcon.CHECK.create());
        setButtonIcon(CrudOperation.DELETE, VaadinIcon.TRASH.create());

        addButtonStyleName(CrudOperation.READ, null);
        addButtonTheme(CrudOperation.ADD, "primary");
        addButtonTheme(CrudOperation.UPDATE, "primary");
        addButtonTheme(CrudOperation.DELETE, "error");
        otherFieldswithValidor.put(CrudOperation.READ, new ArrayList<>());
        otherFieldswithValidor.put(CrudOperation.ADD, new ArrayList<>());
        otherFieldswithValidor.put(CrudOperation.UPDATE, new ArrayList<>());
        otherFieldswithValidor.put(CrudOperation.DELETE, new ArrayList<>());
        
        setVisibleProperties(discoverProperties().toArray(new String[0]));
    }

    public void setOtherFieldswithValidor(
		Map<CrudOperation, List<OtherField>> otherFieldswithValidor)
	{
		this.otherFieldswithValidor = otherFieldswithValidor;
	}
    
    public void addOtherFields(CrudOperation operation, OtherField otherField)
    {
    	this.otherFieldswithValidor.get(operation).add(otherField);
    }
    
    public void addOtherFields(CrudOperation operation, Component component)
	{
    	this.otherFieldswithValidor.get(operation).add(new OtherField(component));
	}
    
    public void addOtherFields(OtherField otherField)
	{
		this.otherFieldswithValidor.forEach((k,v)->v.add(otherField));
	}
    
    public void addOtherFields(Component component)
	{
    	this.otherFieldswithValidor.forEach((k,v)->v.add(new OtherField(component)));
	}
    
    public Binder<T> getBinder()
	{
		return binder;
	}
    
    public void setButtonCaption(CrudOperation operation, String caption) {
        buttonCaptions.put(operation, caption);
    }

    public void setButtonIcon(CrudOperation operation, Icon icon) {
        buttonIcons.put(operation, icon);
    }

    public void addButtonStyleName(CrudOperation operation, String styleName) {
        buttonStyleNames.putIfAbsent(operation, new HashSet<>());
        buttonStyleNames.get(operation).add(styleName);
    }

    public void addButtonTheme(CrudOperation operation, String theme) {
        buttonThemes.put(operation, theme);
    }

    public void setCancelButtonCaption(String cancelButtonCaption) {
        this.cancelButtonCaption = cancelButtonCaption;
    }

    public void setValidationErrorMessage(String validationErrorMessage) {
        this.validationErrorMessage = validationErrorMessage;
    }

    protected List<String> discoverProperties() {
        try {
            List<PropertyDescriptor> descriptors = BeanUtil.getBeanPropertyDescriptors(domainType);
            return descriptors.stream().filter(d -> !d.getName().equals("class")).map(d -> d.getName()).collect(Collectors.toList());
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }

    protected List<HasValueAndElement> buildFields(CrudOperation operation, T domainObject, boolean readOnly) {
        binder = buildBinder(operation, domainObject);
        ArrayList<HasValueAndElement> fields = new ArrayList<>();
        CrudFormConfiguration configuration = getConfiguration(operation);

        boolean focused = false;

        for (int i = 0; i < configuration.getVisibleProperties().size(); i++) {
            String property = configuration.getVisibleProperties().get(i);
            try {
                String fieldCaption = null;
                if (!configuration.getFieldCaptions().isEmpty()) {
                    fieldCaption = configuration.getFieldCaptions().get(i);
                }

                Class<?> propertyType = BeanUtil.getPropertyType(domainObject.getClass(), property);

                if (propertyType == null) {
                    throw new RuntimeException("Cannot find type for property " + domainObject.getClass().getName() + "." + property);
                }

                HasValueAndElement field = buildField(configuration, property, propertyType);

                if (field != null) {
                    configureField(field, property, fieldCaption, readOnly, configuration);
                    bindField(field, property, propertyType);
                    fields.add(field);

                    if (!focused) {
                        if (field.isEnabled() && !field.isReadOnly() && field instanceof Focusable) {
                            ((Focusable) field).focus();
                            focused = true;
                        }
                    }

                    FieldCreationListener creationListener = configuration.getFieldCreationListeners().get(property);
                    if (creationListener != null) {
                        creationListener.fieldCreated(field);
                    }
                }

            } catch (Exception e) {
                throw new RuntimeException("Error creating Field for property " + domainObject.getClass().getName() + "." + property, e);
            }
        }
        
        binder.readBean(domainObject);

        if (!fields.isEmpty() && !readOnly) {
            HasValue field = fields.get(0);
            if (field instanceof Focusable) {
                ((Focusable) field).focus();
            }
        }

        return fields;
    }

    protected HasValueAndElement buildField(CrudFormConfiguration configuration, String property, Class<?> propertyType) throws InstantiationException, IllegalAccessException {
        HasValueAndElement<?, ?> field;
        FieldProvider<?, ?> provider = configuration.getFieldProviders().get(property);

        if (provider != null) {
            field = provider.buildField();
        } else {
            Class<? extends HasValueAndElement<?, ?>> fieldType = configuration.getFieldTypes().get(property);
            if (fieldType != null) {
                field = fieldType.newInstance();
            } else {
                field = new DefaultFieldProvider(propertyType).buildField();
            }
        }

        return field;
    }

    private void configureField(HasValueAndElement field, String property, String fieldCaption, boolean readOnly, CrudFormConfiguration configuration) {
        if (fieldCaption == null) {
            fieldCaption = SharedUtil.propertyIdToHumanFriendly(property);
        }
        try {
            Method setLabelMethod = field.getClass().getMethod("setLabel", String.class);
            setLabelMethod.invoke(field, fieldCaption);

            if(configuration.getRequiredProperties().contains(property))
            {
            	Method setRequiredMethod = field.getClass().getMethod("setRequired", boolean.class);
            	setRequiredMethod.invoke(field, true);
            }
            
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignore) {
        }

        if (field != null && field instanceof HasSize) {
            ((HasSize) field).setWidth("100%");
        }
        field.setReadOnly(readOnly);
        if (!configuration.getDisabledProperties().isEmpty() && field instanceof HasEnabled) {
            ((HasEnabled) field).setEnabled(!configuration.getDisabledProperties().contains(property));
        }
        
        field.getElement().setAttribute("fieldname", property);
    }

    protected void bindField(HasValue field, String property, Class<?> propertyType) {
        Binder.BindingBuilder bindingBuilder = binder.forField(field);

        if (TextField.class.isAssignableFrom(field.getClass()) || PasswordField.class.isAssignableFrom(field.getClass())
                || TextArea.class.isAssignableFrom(field.getClass())) {
            bindingBuilder = bindingBuilder.withNullRepresentation("");
        }

        if (Double.class.isAssignableFrom(propertyType) || double.class.isAssignableFrom(propertyType)) {
            bindingBuilder = bindingBuilder.withConverter(new StringToDoubleConverter(null, "Must be a number"));

        } else if (Long.class.isAssignableFrom(propertyType) || long.class.isAssignableFrom(propertyType)) {
            bindingBuilder = bindingBuilder.withConverter(new StringToLongConverter(null, "Must be a number"));

        } else if (BigDecimal.class.isAssignableFrom(propertyType)) {
            bindingBuilder = bindingBuilder.withConverter(new StringToBigDecimalConverter(null, "Must be a number"));

        } else if (BigInteger.class.isAssignableFrom(propertyType)) {
            bindingBuilder = bindingBuilder.withConverter(new StringToBigIntegerConverter(null, "Must be a number"));

        } else if (Integer.class.isAssignableFrom(propertyType) || int.class.isAssignableFrom(propertyType)) {
            bindingBuilder = bindingBuilder.withConverter(new StringToIntegerConverter(null, "Must be a number"));

        } else if (Byte.class.isAssignableFrom(propertyType) || byte.class.isAssignableFrom(propertyType)) {
            bindingBuilder = bindingBuilder.withConverter(new StringToByteConverter(null, "Must be a number"));

        } else if (Character.class.isAssignableFrom(propertyType) || char.class.isAssignableFrom(propertyType)) {
            bindingBuilder = bindingBuilder.withConverter(new StringToCharacterConverter());

        } else if (Float.class.isAssignableFrom(propertyType) || float.class.isAssignableFrom(propertyType)) {
            bindingBuilder = bindingBuilder.withConverter(new StringToFloatConverter(null, "Must be a number"));

        } else if (Date.class.isAssignableFrom(propertyType) ) {
            bindingBuilder = bindingBuilder.withConverter(new LocalDateToDateConverter());
            
        }
       /* else if(LocalDate.class.isAssignableFrom(propertyType)) 
        {
            bindingBuilder = bindingBuilder.withConverter(new StringToDateConverter());

        }
        */
        /*else if (LocalDateTime.class.isAssignableFrom(propertyType)) {
            bindingBuilder = bindingBuilder.withConverter(new LocalDateTimeToDateConverter(ZoneId.systemDefault()));
        }*/

        bindingBuilder.bind(property);
    }

    protected Binder<T> buildBinder(CrudOperation operation, T domainObject) {
        Binder<T> binder;

        if (getConfiguration(operation).isUseBeanValidation()) {
            binder = new BeanValidationBinder(domainObject.getClass());
        } else {
            binder = new Binder(domainObject.getClass());
        }

        return binder;
    }
    
    protected boolean isOtherFieldsValid(CrudOperation operation)
    {
    	List<OtherField> _otherFields = otherFieldswithValidor.get(operation);
    	if(_otherFields == null || _otherFields.size() <= 0)
    	{
    		return true;
    	}
    	
    	for(OtherField _otherField : _otherFields)
    	{
    		if(_otherField.getValidor() != null && !_otherField.getValidor().isOtherFieldValid()) 
    		{
    			if(_otherField.getFailAction() != null)
    			{
    				_otherField.getFailAction().showError();
    			}
    			return false;
    		}
    	}
    	
    	return true;
    }
    
    protected Button buildOperationButton(CrudOperation operation, T domainObject, ComponentEventListener<ClickEvent<Button>> clickListener) {
        if (clickListener == null) {
            return null;
        }

        String caption = buttonCaptions.get(operation);
        Icon icon = buttonIcons.get(operation);
        Button button = icon != null ? new Button(caption, icon) : new Button(caption);
        if (buttonStyleNames.containsKey(operation)) {
            buttonStyleNames.get(operation).stream().filter(styleName -> styleName != null).forEach(styleName -> button.addClassName(styleName));
        }
        if (buttonThemes.containsKey(operation)) {
            button.getElement().setAttribute("theme", buttonThemes.get(operation));
        }

        button.addClickListener(event -> {
        	if (binder.writeBeanIfValid(domainObject)) {
                try {
                	if(isOtherFieldsValid(operation))
            		{
                		if(beforeOperationButtonClick.beforeOperationButtonClick(domainObject))
                		{
                			clickListener.onComponentEvent(event);
                		}
            		}
                } catch (Exception e) {
                    showError(operation, e);
                }
            } else {
                Notification.show(validationErrorMessage);
            }
        });
        return button;
    }

    @Override
    public void showError(CrudOperation operation, Exception e) {
        if (errorListener != null) {
            errorListener.accept(e);
        } else {
            if (CrudOperationException.class.isAssignableFrom(e.getClass())) {
            	Span errorMessage = new Span(e.getLocalizedMessage());
            	errorMessage.getStyle().set("color", "red");
            	 Notification notification = new Notification(errorMessage);
            	 notification.setDuration(2500);
            	 notification.open();
            } else {
                Notification.show(e.getMessage());
                throw new RuntimeException("Error executing " + operation.name() + " operation", e);
            }
        }
    }

    protected Button buildCancelButton(ComponentEventListener<ClickEvent<Button>> clickListener) {
        if (clickListener == null) {
            return null;
        }

        return new Button(cancelButtonCaption, clickListener);
    }

    protected Component buildFooter(CrudOperation operation, T domainObject, ComponentEventListener<ClickEvent<Button>> cancelButtonClickListener, ComponentEventListener<ClickEvent<Button>> operationButtonClickListener) {
        Button operationButton = buildOperationButton(operation, domainObject, operationButtonClickListener);
        Button cancelButton = buildCancelButton(cancelButtonClickListener);

        HorizontalLayout footerLayout = new HorizontalLayout();
        footerLayout.setSizeUndefined();
        footerLayout.setSpacing(true);
        footerLayout.setPadding(false);

        if (cancelButton != null) {
            footerLayout.add(cancelButton);
        }

        if (operationButton != null) {
            footerLayout.add(operationButton);
        }

        return footerLayout;
    }

}
