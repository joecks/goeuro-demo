package de.halfreal.model;

public class Position {

	private GeoPosition geoPosition;
	private String name;
	private String type;

	public GeoPosition getGeoPosition() {
		return geoPosition;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public void setGeoPosition(GeoPosition geoPosition) {
		this.geoPosition = geoPosition;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		this.type = type;
	}

}
