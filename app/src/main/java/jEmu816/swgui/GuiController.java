package jEmu816.swgui;

import java.util.ArrayList;
import java.util.List;

public interface GuiController {
	void loadFile(String filename);
	void runStopButtonClicked();
	void resetButtonClicked();
	void stepButtonClicked();
	void updateMachine(String machineClass);
	void updateSpeed(String speedKey);
	ArrayList<Object[]> getBreakpointsList();
	void setBreakpointsList(ArrayList<Object[]> list);
	boolean getBreakpointsEnabled();
	void setBreakpointsEnabled(boolean value);
	List<String> getMachineList();
	List<String> getClockSpeedList();
	public String getMachineKey();
	public String getSpeed();
}
