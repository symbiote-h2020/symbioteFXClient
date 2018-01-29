package events;

public interface ConfigurationChangedHandler {
	
	static final int CoreURLChanged=1;

	
	
	public void confChanged(int changedSetting);
}
