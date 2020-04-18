package com.seanjohnson.textfighter;

import java.util.ArrayList;
import java.util.LinkedList;

public class HistoryLinkedList<E> extends LinkedList {

	//NOTE: The first element is the current command (The user has not yet hit enter)

	/**The maximum number of recent input commands saved*/
	public int maxHistory = 50; //Input history. Works like a shell history.

	/**The current command history browsing history*/
	private int currentIndex = 0;

	/**
	 * Returns the currentIndex
	 * @return      {@link #currentIndex}
	 */
	public int getCurrentIndex() { return currentIndex; }
	/**
	 * Sets the current browsing index.
	 * @param i     The desired index.
	 * @throws IndexOutOfBoundsException    Thrown when an index not within size limits is given or the index is less than 0.
	 */
	public void setCurrentIndex(int i) throws IndexOutOfBoundsException {
		if(i > size()-1 || i < 0) { //The index cannot go out of bounds
			throw new IndexOutOfBoundsException("Specified index not within size bounds: " + i);
		} else {
			currentIndex = i;
		}
	}

	/**
	 * Returns the value at the current index.
	 * @return      The current index value.
	 */
	public E getCurrentIndexValue() { return (E)get(currentIndex); }

	/**
	 * Adds a command to the history. If the size is maxed out, remove the oldest command.
	 * @param command   The command to add
	 */
	public void addCommand(E command) {
		this.offerFirst(command);

		if(this.size() > maxHistory) {
			this.removeLast();
		}
	}

	/**
	 * Prints out the entire command history
	 */
	public void printHistory() {
		for(int i=0; i<size(); i++) {
			System.out.println(this.get(i));
		}
	}


}
