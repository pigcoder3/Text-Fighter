package com.seanjohnson.textfighter.item;

import com.seanjohnson.textfighter.CustomVariable;
import com.seanjohnson.textfighter.Player;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class SpecialItemTest {

	/**
	 * Ensures that {@link Player#getCustomVariableFromName(String)} returns the correct custom variable value
	 */
	@Test
	public void returnsCorrectCustomVariable() {
		//setUp
		SpecialItem sp = new SpecialItem("specialitem", "atest", new ArrayList<>());
		ArrayList<CustomVariable> cv = new ArrayList<>();
		cv.add(new CustomVariable("testCustomVariableString", "stringValue", String.class, false, true));
		cv.add(new CustomVariable("testCustomVariableInteger", 5, Integer.class, true, false));
		sp.setCustomVariables(cv);

		//Testing a custom variable with type String
		assertAll( "getCustomVariableFromName returns correct variable",
				() -> assertEquals("stringValue", sp.getCustomVariableFromName("testCustomVariableString")),
				() -> assertEquals(5, sp.getCustomVariableFromName("testCustomVariableInteger")));
	}

	/**
	 * Ensures that {@link Player#setCustomVariableByName(String, Object)} sets the custom variable correctly
	 */
	@Test
	public void setsCorrectCustomVariable() {
		//setUp
		SpecialItem sp = new SpecialItem("specialitem", "atest", new ArrayList<>());
		ArrayList<CustomVariable> cv = new ArrayList<>();
		cv.add(new CustomVariable("testCustomVariableString", "stringValue", String.class, false, true));
		cv.add(new CustomVariable("testCustomVariableInteger", 5, Integer.class, true, false));
		sp.setCustomVariables(cv);

		sp.setCustomVariableByName("testCustomVariableString", "alternateStringValue");
		sp.setCustomVariableByName("testCustomVariableInteger", 6);

		ArrayList<CustomVariable> newCV = sp.getCustomVariables();

		assertAll( "setCustomVariableByName sets variable to incorrect value",
				() -> assertEquals("alternateStringValue", newCV.get(0).getValue()),
				() -> assertEquals(6, newCV.get(1).getValue()));
	}

	@Test
	public void nameCannotBeNull() {
		SpecialItem sp = new SpecialItem("specialitem", "atest", new ArrayList<>());

		sp.setName(null);
		assertEquals("specialitem", sp.getName());

		//Make sure it can change
		sp.setName("testName");
		assertEquals("testName", sp.getName());
	}

}