package gui;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;

public class TabActorOperations {

	Tab theTab;
	
	public void init() throws IOException {
		theTab=new Tab();
		
		theTab.setText("Actor operations");
		
        Class<?> cl=getClass();
        URL resourceURL=cl.getResource("/fxml/ActorOperations.fxml");
        FXMLLoader loader=new FXMLLoader(resourceURL);
        loader.setController(this);
        Node root=loader.load();
        theTab.setContent(root);

        theTab.disableProperty().set(true);
		
	}

	public Tab getTab() {
		return theTab;
	}

}
