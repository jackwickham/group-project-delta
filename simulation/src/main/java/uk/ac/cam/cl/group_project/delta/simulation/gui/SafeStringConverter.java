package uk.ac.cam.cl.group_project.delta.simulation.gui;

import javafx.util.StringConverter;

public class SafeStringConverter<T> extends StringConverter<T> {

	private StringConverter<T> converter;

	public SafeStringConverter(StringConverter<T> converter) {
		this.converter = converter;
	}

	@Override
	public String toString(T t) {
		try {
			return converter.toString(t);
		}
		catch (IllegalArgumentException e) {
			return null;
		}
	}

	@Override
	public T fromString(String s) {
		try {
			return converter.fromString(s);
		}
		catch (IllegalArgumentException e) {
			return null;
		}
	}

}
