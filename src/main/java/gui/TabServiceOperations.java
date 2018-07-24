package gui;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import controller.SearchManagement;
import eu.h2020.symbiote.core.ci.QueryResourceResult;
import eu.h2020.symbiote.model.cim.Parameter;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;

public class TabServiceOperations {

	Tab theTab;
	
	QueryResourceResult theSelectedResource=null;
	
	
	@FXML 
	public TableView parameterTable;
	
	@FXML
    public Label inputParametersJSON;

	@FXML
    public Button sendRequestButton;

	
	static public class LocalInputParameter {
		StringProperty name=new SimpleStringProperty("Dead name");
		StringProperty value=new SimpleStringProperty("Dead value");
		
		public StringProperty nameProperty() {
			return name;
		}
		
		public StringProperty valueProperty() {
			return value;
		}
		
	}
	
	ObservableList<LocalInputParameter> localInputParameter=FXCollections.observableArrayList();
	
	public void init() throws IOException {
		theTab=new Tab();
		
		theTab.setText("Service operations");
		
        Class<?> cl=getClass();
        URL resourceURL=cl.getResource("/fxml/ServiceOperations.fxml");
        FXMLLoader loader=new FXMLLoader(resourceURL);
        loader.setController(this);
        Node root=loader.load();
        theTab.setContent(root);

        
        ChangeListener<QueryResourceResult> changeListenerSensors=new ChangeListener<QueryResourceResult>() {
			@Override
			public void changed(ObservableValue observable, QueryResourceResult oldValue, QueryResourceResult newValue) {
				theSelectedResource=newValue;
				
				if (newValue==null) {
			        theTab.disableProperty().set(true);
				// TODO: Stop the timer
				// TODO: Disable this tab if no selection at all.
				} else {
			        theTab.disableProperty().set(false);
			        prepareForNewService();
			        
				}
				
				
			}
        };
        
        TabSearch.currentService.addListener(changeListenerSensors);
        
        
        setupParameterTable();
        setupSendRequestButton();
        

        theTab.disableProperty().set(true);
		
	}

	public Tab getTab() {
		return theTab;
	}

	
	void setupParameterTable() {

        ObservableList<TableColumn<LocalInputParameter, String>> columns=parameterTable.getColumns();
        
        TableColumn<LocalInputParameter, String> col;

        col = columns.get(0);
        col.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        col = columns.get(1);
        col.setCellFactory(TextFieldTableCell.forTableColumn());
        col.setCellValueFactory(cellData -> cellData.getValue().valueProperty());
        col.setOnEditCommit(
        	    new EventHandler<CellEditEvent<LocalInputParameter, String>>() {
        	        @Override
        	        public void handle(CellEditEvent<LocalInputParameter, String> t) {
        	        	LocalInputParameter lip=t.getRowValue();
        	        	String newValue=t.getNewValue();
        	        	
        	        	lip.value.set(newValue);
        	        	
        	        	createParameterJSON();
        	        }
        	    }
        	);

        parameterTable.setItems(localInputParameter);		
	}

	
    void setupSendRequestButton() {
    	
    	EventHandler<ActionEvent> actionListener=new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				String resourceID=theSelectedResource.getId();
				String parameter=inputParametersJSON.getText();
				SearchManagement.callService(resourceID, parameter);
			}
    		
    	};
    	
    	sendRequestButton.setOnAction(actionListener);
    }

	
	void prepareForNewService() {
		List<Parameter> parameters=theSelectedResource.getInputParameters();
		
		localInputParameter.clear();
		
		for (Parameter p : parameters) {
			LocalInputParameter lip=new LocalInputParameter();
			lip.name.set(p.getName());
			localInputParameter.add(lip);
		}
		
	}
	
	
	void createParameterJSON( ) {
		LinkedHashMap<String, String> parametersForJackson=new LinkedHashMap<String, String>();

		for (LocalInputParameter lip : localInputParameter) {
			String name=lip.name.get();
			String value=lip.value.get();
			parametersForJackson.put(name, value);
		}
		
		ObjectMapper mapper = new ObjectMapper();
        String enCodedParameter="";
		try {
			Set<Entry<String, String>> entries=parametersForJackson.entrySet();
			Map.Entry<String, String>[] entryArray = (Entry<String, String>[]) entries.toArray(new Map.Entry[entries.size()]);
			
			enCodedParameter = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(entryArray);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		inputParametersJSON.setText(enCodedParameter);

	}
}
