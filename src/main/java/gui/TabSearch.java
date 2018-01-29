package gui;



import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.adapter.JavaBeanStringProperty;
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import controller.SearchManagement;
import eu.h2020.symbiote.core.ci.QueryResourceResult;
import eu.h2020.symbiote.core.internal.CoreQueryRequest;



class ModifiableObservableStringList extends javafx.collections.ModifiableObservableListBase<String> {

	private List<String> theList;

	public ModifiableObservableStringList(List<String> theList) {
		this.theList=theList;
	}
	
	@Override
	public String get(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void doAdd(int index, String element) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String doSet(int index, String element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String doRemove(int index) {
		// TODO Auto-generated method stub
		return null;
	}

}




public class TabSearch {

	Tab tab;
	TableView table;
	
	List<Object> strongHold=new ArrayList<Object>();


	
	CoreQueryRequest cr=new CoreQueryRequest();
	public static QueryResourceResult currentSelection=null;
	
	ObservableList<QueryResourceResult> data=FXCollections.observableArrayList();

	
	StringProperty obsProps_property=new SimpleStringProperty(); 
	
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

		
		cr.setPlatform_id("AIT-openUwedat");
		/*
        String owner=null;
        String name=null;
        String id=null;
        String description=null;
        String location_name=null;
        Double location_lat=null;
        Double location_long=null;
        Integer max_distance=null;
        String[] observed_property=null;
        String resource_type=null;
        Boolean should_rank=null;

		 */
		
		int row=0;
		
		
		
		addTextRow(row, thePane, "platform_Id", "platform_id");
		row++;

		addTextRow(row, thePane, "platformName", "platform_name");
		row++;


		addTextRow(row, thePane, "observedProperties", obsProps_property);
		row++;


		
        Button btn = new Button();
        btn.setText("Do search ...");
        btn.setOnAction(new EventHandler<ActionEvent>() {
 
            @Override
            public void handle(ActionEvent event) {
            	

            	String obsProp=TabSearch.this.obsProps_property.get();
            	if (obsProp!=null) {
	            	String[] obsProps=new String[] {obsProp};
	            	List<String> obsPropList=Arrays.asList(obsProps);
	            	cr.setObserved_property(obsPropList);
            	} else {
	            	cr.setObserved_property(null);            		
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

	
	
	private void addTextRow(int row, GridPane thePane, String label, String propertyName) {

		JavaBeanStringProperty property=null;
		try {
			property=JavaBeanStringPropertyBuilder.create().bean(cr).name(propertyName).build();
		} catch (NoSuchMethodException e) {
			// Developer too stupid error
			e.printStackTrace();
		}

		
		addTextRow(row, thePane, label, property);

	}

	
	private void addTextRow(int row, GridPane thePane, String label, StringProperty theProperty) {

		Label lbl=new Label();
		lbl.setText(label);

		GridPane.setRowIndex(lbl, row);
		GridPane.setColumnIndex(lbl, 0);

		TextField field=new TextField();
		GridPane.setRowIndex(field, row);
		GridPane.setColumnIndex(field, 1);
		GridPane.setHgrow(field, Priority.ALWAYS);

		Bindings.bindBidirectional(field.textProperty(), theProperty);
		
        thePane.getChildren().add(lbl);
        thePane.getChildren().add(field);

	}

	
	private void addListboxRow(int row, GridPane thePane, String label, String[] selections, String propertyName) {

		Label lbl=new Label();
		lbl.setText(label);

		GridPane.setRowIndex(lbl, row);
		GridPane.setColumnIndex(lbl, 0);

		ListView<String> field=new ListView<String>();
		GridPane.setRowIndex(field, row);
		GridPane.setColumnIndex(field, 1);
		GridPane.setHgrow(field, Priority.ALWAYS);

		
		for (String s : selections)
			field.getItems().add(s);
		
		List<String> l=cr.getObserved_property();
		if (l==null) {
			l=new ArrayList<String>();
			cr.setObserved_property(l);
		}
		ModifiableObservableStringList property=new ModifiableObservableStringList(l);
		strongHold.add(property);

		Bindings.bindContent(property, field.getSelectionModel().getSelectedItems());
		
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
