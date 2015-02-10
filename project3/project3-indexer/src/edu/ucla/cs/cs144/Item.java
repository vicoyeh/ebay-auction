package edu.ucla.cs.cs144;

public class Item {

	String name;
	String category;
	String description;
	String iid;
	String cid;

	public Item() {}

	public Item(String iid, String cid, String name, String category, String description) {
		this.iid = iid;
		this.cid = cid;
		this.name = name;
		this.category = category;
		this.description = description;
	}

	public String toString() {
		return "Item: " + name + "(" + category + ")";
	}

}