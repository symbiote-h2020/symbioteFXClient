package utils.javafx;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

public class NullAwareChangeListener implements ChangeListener<String> {

	TextField field;
	PropertyDescriptor pd;
	Object obj;
	StringConverter converter;
	
	public NullAwareChangeListener(TextField field, PropertyDescriptor pd, Object obj, StringConverter converter) {
		this.field=field;
		this.pd=pd;
		this.obj=obj;
		this.converter=converter;
	}
	
	@Override
	public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		
		Object convertedValue=null;
		
		if (newValue==null || newValue.isEmpty())
			convertedValue=null;
		else
			convertedValue=converter.fromString(newValue);
		
		Method m=pd.getWriteMethod();
		try {
			m.invoke(obj, convertedValue);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
