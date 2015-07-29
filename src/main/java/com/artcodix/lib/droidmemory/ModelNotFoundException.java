package com.artcodix.lib.droidmemory;

/**
 * This Exception is thrown when a Model with the provied ID is
 * not found. It either wasn't created or hard deleted.
 * @author Marco Schweizer
 * @version 0.1
 * @since 29.04.2014
 *
 */

public class ModelNotFoundException extends Exception {
	
	public ModelNotFoundException() {
		super("A model with the specified ID cannot be found!");
	}
	
	public ModelNotFoundException(String message) {
		super(message);
	}

}
