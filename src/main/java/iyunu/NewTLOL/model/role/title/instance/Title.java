package iyunu.NewTLOL.model.role.title.instance;

import iyunu.NewTLOL.model.role.title.ETitleState;

public class Title {

	private int index;
	private String name;
	private String desc;
	private ETitleState state = ETitleState.none;
	private boolean isSelected = false;

	public Title copy() {
		Title title = new Title();
		title.setIndex(index);
		title.setName(name);
		title.setDesc(desc);
		title.setState(state);
		title.setSelected(isSelected);
		return title;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index
	 *            the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * @param desc
	 *            the desc to set
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}

	/**
	 * @return the state
	 */
	public ETitleState getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(ETitleState state) {
		this.state = state;
	}

	/**
	 * @return the isSelected
	 */
	public boolean isSelected() {
		return isSelected;
	}

	/**
	 * @param isSelected
	 *            the isSelected to set
	 */
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

}
