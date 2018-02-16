package gui;



import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
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
import utils.ObservedPropertyContainer;
import utils.javafx.NullAwareChangeListener;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import controller.SearchManagement;
import eu.h2020.symbiote.core.ci.QueryResourceResult;
import eu.h2020.symbiote.core.internal.CoreQueryRequest;






public class TabSearch {

	Tab tab;
	TableView table;
	
	CoreQueryRequest cr=new CoreQueryRequest();
	public static QueryResourceResult currentSelection=null;
	
	ObservableList<QueryResourceResult> data=FXCollections.observableArrayList();

	ObservedPropertyContainer obsProps_property=new ObservedPropertyContainer();
	
	ObservedPropertyContainer obsProps_property_uri=new ObservedPropertyContainer();
	public void init() {
		
		Tab tabSearchParameter=setupParameterTab();
		Tab tabSearchResults=setupResultTab();
		
        tab = new Tab();
        tab.setText("Search ...");
        

        TabPane tabPane = new TabPane();
        
        
        tabPane.getTabs().add(tabSearchParameter);
        tabPane.getTabs().add(tabSearchResults);
        
        tab.setContent(tabPane);

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

	
	
	private Tab setupResultTab() {
		
        Tab tabSearchResults=new Tab();
        tabSearchResults.setText("Search results");

        
        
        table = new TableView<Object>();
        
        TableColumn idColumn = new TableColumn("id");
        idColumn.setCellValueFactory(new PropertyValueFactory<QueryResourceResult, String>("id"));
        
        TableColumn nameColumn = new TableColumn("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<QueryResourceResult, String>("name"));
        
        TableColumn locnameColumn = new TableColumn("Locationname");
        locnameColumn.setCellValueFactory(new PropertyValueFactory<QueryResourceResult, String>("locationName"));
        
        TableColumn longitudeColumn = new TableColumn("longitude");
        longitudeColumn.setCellValueFactory(new PropertyValueFactory<QueryResourceResult, String>("locationLongitude"));
        
        TableColumn latitudeColumn = new TableColumn("latitude");
        latitudeColumn.setCellValueFactory(new PropertyValueFactory<QueryResourceResult, String>("locationLatitude"));

        TableColumn obsPropColumn = new TableColumn("observedProperties");
        obsPropColumn.setCellValueFactory(new PropertyValueFactory<QueryResourceResult, List<String>>("observedProperties"));

        TableColumn descColumn = new TableColumn("description");
        descColumn.setCellValueFactory(new PropertyValueFactory<QueryResourceResult, String>("description"));
        
        table.getColumns().addAll(idColumn, nameColumn, locnameColumn, longitudeColumn, latitudeColumn, obsPropColumn, descColumn);

        table.setItems(data);
        
        
        ChangeListener selectionListener=new ChangeListener() {

			@Override
			public void changed(ObservableValue observable, Object oldValue, Object newValue) {
				currentSelection=(QueryResourceResult) newValue;
			}
        	
        };
        
        table.getSelectionModel().selectedItemProperty().addListener(selectionListener);
        
        

        tabSearchResults.setContent(table);
        
		return tabSearchResults;
	}


	
	
	public Tab getTab() {
		return tab;
	}

}
