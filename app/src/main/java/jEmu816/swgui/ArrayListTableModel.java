package jEmu816.swgui;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

public class ArrayListTableModel extends DefaultTableModel {
	private final ArrayList<Object[]> data;

	public ArrayList<Object[]> getList() {
		return data;
	}

	public ArrayListTableModel(String[] columnNames, ArrayList<Object[]> list) {
		super(columnNames, 0);
		data = list;
	}

	public ArrayListTableModel(String[] columnNames) {
		super(columnNames, 0); // 0 rows initially
		data = new ArrayList<>();
	}

	public void addRow(Object[] row) {
		data.add(row);
		super.addRow(row);
	}

	public void removeRow(int rowIndex) {
		data.remove(rowIndex);
		super.removeRow(rowIndex);
	}

	public void setValueAt(Object value, int row, int col) {
		data.get(row)[col] = value;
		super.setValueAt(value, row, col);
	}
}
