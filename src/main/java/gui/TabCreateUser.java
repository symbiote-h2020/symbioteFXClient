package gui;
import java.util.Properties;

import controller.UserManagement;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.layout.StackPane;


public class TabCreateUser {

	Tab tab;
	
	final Properties theProperties;
	
	public TabCreateUser(Properties theProperties) {
		this.theProperties=theProperties;
	}


	public void init() {
		
        Button btn = new Button();
        btn.setText("Add user");
        btn.setOnAction(new EventHandler<ActionEvent>() {
 
			@Override
            public void handle(ActionEvent event) {
                UserManagement.addUser(TabCreateUser.this.theProperties);
            }
        });
        

        StackPane root = new StackPane();
        root.getChildren().add(btn);
        
        tab = new Tab();
        tab.setText("User management");
        
        tab.setContent(root);

	}
	
	
	public Tab getTab() {
		return tab;
	}
	
}
