package com.artcodix.lib.droidmemory;

/**
 * This Exception is thrown when you try to retrieve a Model that was
 * deleted before. This makes soft deletion possible.
 * @author Marco Schweizer
 * @version 0.1
 * @since 29.04.2014
 *
 */

public class ModelDeletedException extends Exception {

	public ModelDeletedException() {
		super("The specified model is deleted!");
	}
	
	public ModelDeletedException(String message) {
		super(message);
	}
	
}
