import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import eu.h2020.symbiote.core.ci.QueryResourceResult;


public class TabSearch {

	Tab tab;
	
	private TableView table;
	
	ObservableList<QueryResourceResult> data=FXCollections.observableArrayList();
	
	public void init() {
        tab = new Tab();
        tab.setText("Search ...");
        
        Button btn = new Button();
        btn.setText("Do search ...");
        btn.setOnAction(new EventHandler<ActionEvent>() {
 
            @Override
            public void handle(ActionEvent event) {
                SearchManagement.doParametricSearch();
                
                data.clear();
                
                for (QueryResourceResult qr : SearchManagement.resources) {
                	data.add(qr);
                }
                
                
            }
        });
        

        table = new TableView();
        
        TableColumn idColumn = new TableColumn("id");
        idColumn.setCellValueFactory(new PropertyValueFactory<QueryResourceResult, String>("id"));
        
        TableColumn nameColumn = new TableColumn("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<QueryResourceResult, String>("name"));
        
        TableColumn locnameColumn = new TableColumn("Locationname");
        locnameColumn.setCellValueFactory(new PropertyValueFactory<QueryResourceResult, String>("locationName"));
        
        TableColumn descColumn = new TableColumn("description");
        nameColumn.setCellValueFactory(new PropertyValueFactory<QueryResourceResult, String>("description"));
        
        table.getColumns().addAll(idColumn, nameColumn, locnameColumn, descColumn);

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