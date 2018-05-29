package gui;



import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import utils.LinewrappedPropertyValueFactory;
import utils.ObservedPropertyContainer;
import utils.javafx.NullAwareChangeListener;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


import controller.SearchManagement;
import eu.h2020.symbiote.core.ci.QueryResourceResult;
import eu.h2020.symbiote.core.internal.CoreQueryRequest;


class ObservableVariable<T> implements ObservableValue<T> {
	T theValue;
	
	List<WeakReference<ChangeListener<? super T>>> listeners=new ArrayList<WeakReference<ChangeListener<? super T>>>();
	
	
	
	public void setValue(T v) {
		T theOldValue=theValue;
		theValue=v;
		
		fireEvent(theOldValue);
	}
	
	public T getValue() {
		return theValue;
	}
	
	
	private void fireEvent(T theOldValue) {

		Iterator<WeakReference<ChangeListener<? super T>>> it=listeners.iterator();
		
		while (it.hasNext()) {
			WeakReference<ChangeListener<? super T>> wr=it.next();
			if (wr==null) {
				it.remove();
				continue;
			}
			
			ChangeListener<? super T> listener=wr.get();
			
			if (listener==null) {
				it.remove();
				continue;
			}

			listener.changed(this, theOldValue, this.theValue);
		}
		
	}

	@Override
	public void addListener(InvalidationListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addListener(ChangeListener<? super T> listener) {
		removeListener(listener);	// Make sure, we are not adding the same thingy twice.
		
		WeakReference<ChangeListener<? super T>> wr=new WeakReference<ChangeListener<? super T>>(listener);
		listeners.add(wr);
	}

	@Override
	public void removeListener(ChangeListener<? super T> cl) {
		Iterator<WeakReference<ChangeListener<? super T>>> it=listeners.iterator();
		
		while (it.hasNext()) {
			WeakReference<ChangeListener<? super T>> wr=it.next();
			if (wr==null) {
				it.remove();
				continue;
			}
			
			ChangeListener<? super T> listener=wr.get();
			
			if (listener==null) {
				it.remove();
				continue;
			}
			
			if(listener==cl) {	// Yes, really ==, not equals. We want it to be the same object, not an equal object.
				it.remove();
				continue;
			}
		}
		
	}
}


public class TabSearch {

	Tab tab;
	TableView table;
	
	CoreQueryRequest cr=new CoreQueryRequest();
	
	static ObservableVariable<QueryResourceResult> currentSensor=new ObservableVariable<QueryResourceResult>();	
	static ObservableVariable<QueryResourceResult> currentActor=new ObservableVariable<QueryResourceResult>();	
	static ObservableVariable<QueryResourceResult> currentService=new ObservableVariable<QueryResourceResult>();	
		
	ObservableList<QueryResourceResult> data=FXCollections.observableArrayList();

	ObservedPropertyContainer obsProps_property=new ObservedPropertyContainer();
	
	ObservedPropertyContainer obsProps_property_uri=new ObservedPropertyContainer();

	
	@FXML
    public TableView<QueryResourceResult> tableIdServices;

	@FXML
    public TableView<QueryResourceResult> tableIdSensors;

	
	
	public void init() throws IOException {
		
		Tab tabSearchParameter=setupParameterTab();
		Tab tabSearchResults=setupNewResultTab();
		
		Tab tabManage=setupManageTab();
		
		
        tab = new Tab();
        tab.setText("Search ...");
                

        TabPane tabPane = new TabPane();
        
        
        tabPane.getTabs().add(tabSearchParameter);
        tabPane.getTabs().add(tabSearchResults);
        tabPane.getTabs().add(tabManage);
        
        tab.setContent(tabPane);

	}


	
	
	private Tab setupNewResultTab() throws IOException {

		Tab newSearchResults=new Tab();
		newSearchResults.setText("new Search results...");

		
        Class<?> cl=getClass();
        URL resourceURL=cl.getResource("/fxml/SearchtabResults.fxml");
        FXMLLoader loader=new FXMLLoader(resourceURL);
        loader.setController(this);
        Node root=loader.load();
        newSearchResults.setContent(root);

        
        ObservableList<TableColumn<QueryResourceResult, ?>> columns=tableIdServices.getColumns();
        
        TableColumn col = columns.get(0);
        col.setCellValueFactory(new PropertyValueFactory<QueryResourceResult, String>("id"));

        col = columns.get(1);
        col.setCellValueFactory(new PropertyValueFactory<QueryResourceResult, String>("name"));

        col = columns.get(2);
        col.setCellValueFactory(new PropertyValueFactory<QueryResourceResult, String>("description"));

        col = columns.get(3);
        col.setCellValueFactory(new PropertyValueFactory<QueryResourceResult, String>("inputParameters"));

        col = columns.get(4);
        col.setCellValueFactory(new PropertyValueFactory<QueryResourceResult, String>("platformId"));

        col = columns.get(5);
        col.setCellValueFactory(new PropertyValueFactory<QueryResourceResult, String>("platformName"));

        
        tableIdServices.setItems(SearchManagement.services);

        ChangeListener selectionListenerService=new ChangeListener() {
			@Override
			public void changed(ObservableValue observable, Object oldValue, Object newValue) {
//				currentActor.setValue(null);
				tableIdSensors.getSelectionModel().clearSelection();
				currentService.setValue((QueryResourceResult) newValue);
			}
        };
        tableIdServices.getSelectionModel().selectedItemProperty().addListener(selectionListenerService);

        
        
        columns=tableIdSensors.getColumns();
        
        col = columns.get(0);
        col.setCellValueFactory(new PropertyValueFactory<QueryResourceResult, String>("id"));

        col = columns.get(1);
        col.setCellValueFactory(new PropertyValueFactory<QueryResourceResult, String>("name"));
        
        col = columns.get(2);
        col.setCellValueFactory(new PropertyValueFactory<QueryResourceResult, String>("locationName"));
        
        col = columns.get(3);
        col.setCellValueFactory(new PropertyValueFactory<QueryResourceResult, String>("locationLongitude"));
        
        col = columns.get(4);
        col.setCellValueFactory(new PropertyValueFactory<QueryResourceResult, String>("locationLatitude"));

        col = columns.get(5);
        col.setCellValueFactory(new LinewrappedPropertyValueFactory<QueryResourceResult>("observedProperties"));

        col = columns.get(6);
        col.setCellValueFactory(new PropertyValueFactory<QueryResourceResult, String>("description"));
        
        tableIdSensors.setItems(SearchManagement.sensors);
        
        
        ChangeListener selectionListenerSensor=new ChangeListener() {
			@Override
			public void changed(ObservableValue observable, Object oldValue, Object newValue) {
//				currentActor.setValue(null);
				tableIdServices.getSelectionModel().clearSelection();
				currentSensor.setValue((QueryResourceResult) newValue);
			}
        };
        
        tableIdSensors.getSelectionModel().selectedItemProperty().addListener(selectionListenerSensor);
        
        

        
        
		return newSearchResults;
	}




	private Tab setupParameterTab() {
		
		
		GridPane thePane=new GridPane();

		
		//cr.setPlatform_id("AIT-openUwedat");
		/*
        Integer max_distance=null;
        String resource_type=null;
        Boolean should_rank=null;

		 */
		
		int row=0;
		
		
		
		addTextRow(row, thePane, cr, "platform_Id", "platform_id");
		row++;

		addTextRow(row, thePane, cr, "platformName", "platform_name");
		row++;

		addTextRow(row, thePane, obsProps_property, "observedProperties", "obsProps");
		row++;

		addTextRow(row, thePane, obsProps_property_uri, "observedPropertiesURI", "obsProps");
		row++;
		
		addTextRow(row, thePane, cr, "owner", "owner");
		row++;

		addTextRow(row, thePane, cr, "name", "name");
		row++;

		addTextRow(row, thePane, cr, "id", "id");
		row++;

		addTextRow(row, thePane, cr, "description", "description");
		row++;

		addTextRow(row, thePane, cr, "location_name", "location_name");
		row++;

		addTextRow(row, thePane, cr, "location_lat", "location_lat");
		row++;

		addTextRow(row, thePane, cr, "location_lon", "location_long");
		row++;

		addTextRow(row, thePane, cr, "max_distance", "max_distance");
		row++;

		
		
        Button btn = new Button();
        btn.setText("Do search ...");
        btn.setOnAction(new EventHandler<ActionEvent>() {
 
            @Override
            public void handle(ActionEvent event) {
            	

            	String obsProp=TabSearch.this.obsProps_property.getObsProps();
            	if (obsProp!=null) {
//            		obsProp=obsProp.replaceAll(" ", "+");
	            	String[] obsProps=new String[] {obsProp};
	            	List<String> obsPropList=Arrays.asList(obsProps);
	            	cr.setObserved_property(obsPropList);
            	} else {
	            	cr.setObserved_property(null);            		
            	}
            	
            	String obsPropURI=TabSearch.this.obsProps_property_uri.getObsProps();
            	if (obsPropURI!=null) {
	            	String[] obsPropsURI=new String[] {obsPropURI};
	            	List<String> obsPropURIList=Arrays.asList(obsPropsURI);
	            	cr.setObserved_property_iri(obsPropURIList);
            	} else {
	            	cr.setObserved_property_iri(null);            		
            	}
            	
                SearchManagement.doParametricSearch(cr);
                
                data.clear();
                
                for (QueryResourceResult qr : SearchManagement.resources) {
                	data.add(qr);
                }
                
                
            }
        });
        

        thePane.getChildren().add(btn);
        
        
        thePane.setHgap(10);
        thePane.setVgap(10);
        thePane.setPadding(new Insets(25, 25, 25, 25));
        
		GridPane.setRowIndex(btn, row);
		GridPane.setColumnIndex(btn, 0);
        
        
        Tab tabSearchParameter=new Tab();
        tabSearchParameter.setText("Search parameter");
        
        tabSearchParameter.setContent(thePane);
        
        

        
		return tabSearchParameter;
	}

	
	
	private void addTextRow(int row, GridPane thePane, Object obj, String label, String propertyName) {

		StringConverter converter=null;
		PropertyDescriptor pd=null;
		try {
			pd=new PropertyDescriptor(propertyName, obj.getClass());
			
			Class<?> dataType=pd.getPropertyType();
			
			if (dataType.equals(String.class)) {
				converter=new DefaultStringConverter();
			} else if (dataType.equals(Double.class)) {
				converter=new DoubleStringConverter();
			} else if (dataType.equals(Integer.class)) {
				converter=new IntegerStringConverter();
			} else {
				throw new IllegalArgumentException("Type "+dataType.getCanonicalName()+" is unexpected");
			}
						
		} catch (IllegalArgumentException e) {
			// Developer too stupid error
			e.printStackTrace();
			throw(e);
		} catch (IntrospectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		addTextRow(row, thePane, obj, label, pd, converter);
		
		
	}

	
	
	
	private void addTextRow(int row, GridPane thePane, Object obj, String label, PropertyDescriptor pd, StringConverter converter)
	{

		Label lbl=new Label();
		lbl.setText(label);

		GridPane.setRowIndex(lbl, row);
		GridPane.setColumnIndex(lbl, 0);

		TextField field=new TextField();
		GridPane.setRowIndex(field, row);
		GridPane.setColumnIndex(field, 1);
		GridPane.setHgrow(field, Priority.ALWAYS);

		Method m=pd.getReadMethod();
		
		Object currentValue;
		try {
			currentValue = m.invoke(obj);
			String currenttext=converter.toString(currentValue);
			field.textProperty().set(currenttext);			
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
		
		field.textProperty().addListener(new NullAwareChangeListener(field, pd, obj, converter));
		
        thePane.getChildren().add(lbl);
        thePane.getChildren().add(field);

	}

	
	
	private Tab setupManageTab() {
		
        Tab tabManage=new Tab();
        tabManage.setText("Manage sensors");

		GridPane thePane=new GridPane();


        Button btn = new Button();
        btn.setText("Add user");
        btn.setOnAction(new EventHandler<ActionEvent>() {
 
            @Override
            public void handle(ActionEvent event) {
//                UserManagement.addUser();
            }
        });
        
		GridPane.setRowIndex(btn, 0);
		GridPane.setColumnIndex(btn, 0);
        
		
        tabManage.setContent(thePane);
        
		return tabManage;
	}


	
	
	public Tab getTab() {
		return tab;
	}

}
