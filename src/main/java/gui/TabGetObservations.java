package gui;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.Duration;

import com.sun.javafx.collections.ChangeHelper;

import controller.SearchManagement;
import eu.h2020.symbiote.core.ci.QueryResourceResult;
import eu.h2020.symbiote.model.cim.Observation;


public class TabGetObservations {

	Tab tab;
	
	private TableView<Observation> table;
	int maxTableLength=50;
	
	String url=null;
	
	
	ObservableList<Observation> data=FXCollections.observableArrayList();
	
	public void init() {
        tab = new Tab();
        tab.setText("Get Observations ...");
        
        Button btn = new Button();
        btn.setText("Get them ...");
        btn.setOnAction(new EventHandler<ActionEvent>() {
 
            @Override
            public void handle(ActionEvent event) {

            	if (TabSearch.currentSelection==null) {
            		System.out.println("Ignoring pressed button as no resource is selected");
            		return;
            	}
            	
            	String resourceID=TabSearch.currentSelection.getId();
            	
            	url=SearchManagement.getResourceURL(resourceID)+"/Observations?$top=1";
            	
            	Timeline fetcher = new Timeline(new KeyFrame(Duration.seconds(5), new EventHandler<ActionEvent>() {

            	    @Override
            	    public void handle(ActionEvent event) {
            	        System.out.println("this is called every 5 seconds on UI thread");
//            	        String localURL="https://enviro5.ait.ac.at/symbiote/rap/Sensors('5a311a7fec44ba19e65d8e31')/Observations?$top=1";
            	        Observation obs=SearchManagement.getObservation(url);
            	        if (data.size()>maxTableLength)
            	        	data.remove(0);
            	        data.add(obs);
            	    }
            	}));
            	fetcher.setCycleCount(Timeline.INDEFINITE);
            	fetcher.play();
                
                
            }
        });
        

        table = new TableView<Observation>();

        TableColumn timeColumn = new TableColumn("ResultTime");
        timeColumn.setCellValueFactory(new PropertyValueFactory<QueryResourceResult, String>("resultTime"));
        
        TableColumn valColumn = new TableColumn("obsValues");
        valColumn.setCellValueFactory(new PropertyValueFactory<QueryResourceResult, String>("obsValues"));
        
//        TableColumn locnameColumn = new TableColumn("Locationname");
//        locnameColumn.setCellValueFactory(new PropertyValueFactory<QueryResourceResult, String>("locationName"));
//        
//        TableColumn descColumn = new TableColumn("description");
//        nameColumn.setCellValueFactory(new PropertyValueFactory<QueryResourceResult, String>("description"));
        
        table.getColumns().addAll(timeColumn, valColumn);

        table.setItems(data);
        
        
        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(btn, table);
        
        StackPane root = new StackPane();
        root.getChildren().add(vbox);
        

        tab.setContent(root);

	}

	public Tab getTab() {
		return tab;
	}

}
