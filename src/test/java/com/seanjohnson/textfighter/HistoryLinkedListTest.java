package com.seanjohnson.textfighter;

import org.junit.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryLinkedListTest {

	@Test
	public void correctlyAddsCommand() {
		HistoryLinkedList hl = new HistoryLinkedList();

		hl.addCommand("sampleCommand1");
		hl.addCommand("sampleCommand2");
		hl.addCommand("sampleCommand3");
		hl.addCommand("sampleCommand4");

		assertEquals("sampleCommand4", hl.get(0));
		assertEquals("sampleCommand3", hl.get(1));
		assertEquals("sampleCommand2", hl.get(2));
		assertEquals("sampleCommand1", hl.get(3));
	}

	@Test
	public void doesNotOverflow() {
		HistoryLinkedList hl = new HistoryLinkedList();
		hl.maxHistory = 4;

		hl.addCommand("sampleCommand1");
		hl.addCommand("sampleCommand2");
		hl.addCommand("sampleCommand3");
		hl.addCommand("sampleCommand4");
		hl.addCommand("sampleCommand5");

		String commandHistory = "";
		Iterator i = hl.iterator();
		while(i.hasNext()) {
			commandHistory+=i.next();
		}

		assertEquals("sampleCommand5sampleCommand4sampleCommand3sampleCommand2", commandHistory);
	}

}