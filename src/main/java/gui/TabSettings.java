package gui;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import events.ConfigurationChangedHandler;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;



class PropertyInputListener implements ChangeListener<Boolean> {

	
	private Properties props;
	private String key;
	private TextField field;
	private ConfigurationChangedHandler cch;
	private int changeType;

	public PropertyInputListener(TextField field, Properties props, String key, ConfigurationChangedHandler cch, int changeType) {
		this.props=props;
		this.key=key;
		this.field=field;
		this.cch=cch;
		this.changeType=changeType;
		
		field.focusedProperty().addListener(this);
		String propValue=props.getProperty(key);
		field.setText(propValue);
	}

	@Override
	public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		if (oldValue==true) {
			String propValue=field.getText();
			if (propValue==null)
				props.remove(key);
			else
				props.setProperty(key, propValue);
			
			this.cch.confChanged(changeType);
		}
	}
	
}


public class TabSettings implements ConfigurationChangedHandler {

	Tab tab;

	private final String propfilename;
	private final Properties props;

	private List<ConfigurationChangedHandler> cchs=new ArrayList<ConfigurationChangedHandler>();

	
	public TabSettings(String propfilename, Properties props) {
		this.propfilename=propfilename;
		this.props=props;
	}
	
	public void init() {

		



		int row=0;
		GridPane thePane=new GridPane();

		addTextRow(row, thePane, "core.url", "core.url", ConfigurationChangedHandler.CoreURLChanged);

		row++;
		
		addTextRow(row, thePane, "core.aamURL", "core.aamURL", -1);
		
		row++;

		addTextRow(row, thePane, "homeplatform", "homeplatform", -1);
		
		row++;

		addTextRow(row, thePane, "appuser", "appuser", -1);
		
		row++;

		addTextRow(row, thePane, "apppass", "apppass", -1);
		
		row++;
		
		addTextRow(row, thePane, "platform.owner.name", "platform.owner.name", -1);
		
		row++;
		
		addTextRow(row, thePane, "platform.owner.pass", "platform.owner.pass", -1);
		
		row++;
		
		addTextRow(row, thePane, "keystore.Path", "keystore.Path", -1);
		
		row++;
		
		addTextRow(row, thePane, "keystore.Password", "keystore.Password", -1);
		
		row++;
		
		
		
		Button saveBtn=new Button();
		saveBtn.setText("Save properties");
		saveBtn.setOnAction(new EventHandler<ActionEvent>() {
	            @Override
	            public void handle(ActionEvent event) {
	            	FileWriter out;
					try {
						out = new FileWriter(propfilename);
		            	props.store(out, null);
		            	out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
	            }
	        });		
		GridPane.setRowIndex(saveBtn, row);
		GridPane.setColumnIndex(saveBtn, 0);

        thePane.getChildren().add(saveBtn);
        
        
        thePane.setHgap(10);
        thePane.setVgap(10);
        thePane.setPadding(new Insets(25, 25, 25, 25));
        
        ColumnConstraints col2Constr=new ColumnConstraints();
        col2Constr.setMaxWidth(Double.MAX_VALUE);
        
        thePane.getColumnConstraints().add(col2Constr);
        
        tab = new Tab();
        tab.setText("Settings");
        
        tab.setContent(thePane);

	}
	
	
	private void addTextRow(int row, GridPane thePane, String label, String propKey, int changeType) {

		Label lbl=new Label();
		lbl.setText(label);

		GridPane.setRowIndex(lbl, row);
		GridPane.setColumnIndex(lbl, 0);

		TextField field=new TextField();
		GridPane.setRowIndex(field, row);
		GridPane.setColumnIndex(field, 1);
		GridPane.setHgrow(field, Priority.ALWAYS);
		new PropertyInputListener(field, props, propKey, this, changeType);


        thePane.getChildren().add(lbl);
        thePane.getChildren().add(field);

	}
	
	
	public Tab getTab() {
		return tab;
	}

	
	public void addConfChangedHandler(ConfigurationChangedHandler cch) {
		if (this.cchs.contains(cch))
			return;
		this.cchs.add(cch);
	}

	@Override
	public void confChanged(int changedSetting) {
		if (changedSetting==-1)
			return;
		
		for (ConfigurationChangedHandler cch : this.cchs) {
			cch.confChanged(changedSetting);
		}
	}
}
