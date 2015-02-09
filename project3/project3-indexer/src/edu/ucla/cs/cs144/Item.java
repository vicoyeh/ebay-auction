package edu.ucla.cs.cs144;

public class Item {

	String name;
	String category;
	String description;

	public Item() {
	}

	public Item(String name, String category, String description) {
		this.name = name;
		this.category = category;
		this.description = description;
	}

	public String toString() {
		return "Item: " + name + "(" + category + ")";
	}

}