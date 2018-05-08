package main;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import controller.ConnectionManager;
import controller.UserManagement;
import gui.TabCreateUser;
import gui.TabGetObservations;
import gui.TabSearch;
import gui.TabSettings;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class ClientMain extends Application {
	
	

	public static final String propFileName="SymbioteClientFX.properties";
	public static Properties theProperties;
	

	@Override
	public void start(Stage primaryStage) throws Exception {
		

			theProperties=new Properties();
			
			File propertiesFile=new File("SymbioteClientFX.properties");
			FileInputStream fis=new FileInputStream(propertiesFile);
			
			theProperties.load(fis);
			
			fis.close();


			ConnectionManager connManager=new ConnectionManager();
			connManager.init();
		
			UserManagement.init();		
		
	       	primaryStage.setTitle("Symbiote FX Client!");
	        
	        TabPane tabPane = new TabPane();

	        
	        TabSettings tss=new TabSettings(propFileName, theProperties);
	        tss.init();
	        tss.addConfChangedHandler(connManager);
	        tabPane.getTabs().add(tss.getTab());

	        
	        TabCreateUser tcu=new TabCreateUser(theProperties);
	        tcu.init();
	        tabPane.getTabs().add(tcu.getTab());

	        
	        TabSearch tsea=new TabSearch();
	        tsea.init();
	        tabPane.getTabs().add(tsea.getTab());
	        
	        
	        TabGetObservations tgo=new TabGetObservations();
	        tgo.init();
	        tabPane.getTabs().add(tgo.getTab());
	        	        
	        primaryStage.setScene(new Scene(tabPane, 300, 250));
	        primaryStage.show();		
	}


	 public static void main(String[] args) {
		    launch(args);
	 }
	
}
