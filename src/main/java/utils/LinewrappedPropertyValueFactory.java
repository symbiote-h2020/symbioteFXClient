package utils;

import java.util.Collection;
import java.util.Iterator;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.PropertyValueFactory;


class LinewrappingObservableValue implements ObservableValue<Object> {
	final ObservableValue<?> obsV;
	
	
	public LinewrappingObservableValue(ObservableValue<?> obsV2) {
		obsV=obsV2;
	}


	@Override
	public void addListener(InvalidationListener listener) {
		obsV.addListener(listener);
	}


	@Override
	public void removeListener(InvalidationListener listener) {
		obsV.removeListener(listener);
	}


	@Override
	public void addListener(ChangeListener<Object> listener) {
		obsV.addListener(listener);
	}


	@Override
	public void removeListener(ChangeListener<Object> listener) {
		obsV.removeListener(listener);
	}


	public Object getValue() {
		Object t=obsV.getValue();
		if (t instanceof Collection) {
			Collection<Object> coll=(Collection<Object>)t;
			Iterator<Object> it=coll.iterator();
			
			StringBuffer buffer=new StringBuffer();
			
			while (it.hasNext()) {
				Object o=it.next();
				buffer.append("\n").append(o.toString());
			}
			
			if (buffer.length()>0)
				buffer.deleteCharAt(0);
			
			return buffer.toString();
		}
	
		return t.toString();
	}
	
}

public class LinewrappedPropertyValueFactory<S> extends PropertyValueFactory<S,Object> {

	public LinewrappedPropertyValueFactory(String property) {
		super(property);
	}

    @Override public ObservableValue<Object> call(CellDataFeatures<S,Object> param) {
    	ObservableValue<?> obsV=super.call(param);
    	LinewrappingObservableValue lwObsV=new LinewrappingObservableValue(obsV); 
    	return lwObsV;
    }

	
	
}
