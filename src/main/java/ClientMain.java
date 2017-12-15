import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class ClientMain extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		

			ConnectionManagement.init();
		
			UserManagement.init();		
		
	       	primaryStage.setTitle("Symbiote FX Client!");
	        
	        TabPane tabPane = new TabPane();
	        
	        TabCreateUser tcu=new TabCreateUser();
	        tcu.init();
	        tabPane.getTabs().add(tcu.getTab());

	        
	        TabSearch ts=new TabSearch();
	        ts.init();
	        tabPane.getTabs().add(ts.getTab());
	        	        
	        primaryStage.setScene(new Scene(tabPane, 300, 250));
	        primaryStage.show();		
	}


	 public static void main(String[] args) {
		    launch(args);
	 }
	
}