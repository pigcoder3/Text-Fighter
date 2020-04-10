package com.seanjohnson.textfighter.item;

import com.seanjohnson.textfighter.CustomVariable;
import com.seanjohnson.textfighter.Player;
import com.seanjohnson.textfighter.TextFighter;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ToolTest {

	/**
	 * Ensures that {@link Player#getCustomVariableFromName(String)} returns the correct custom variable value
	 */
	@Test
	public void returnsCorrectCustomVariable() {
		//setUp
		Tool tool = new Tool("tool", "atest", new ArrayList<>(), 10 ,10, false);
		ArrayList<CustomVariable> cv = new ArrayList<>();
		cv.add(new CustomVariable("testCustomVariableString", "stringValue", String.class, false, true));
		cv.add(new CustomVariable("testCustomVariableInteger", 5, Integer.class, true, false));
		tool.setCustomVariables(cv);

		//Testing a custom variable with type String
		assertAll( "getCustomVariableFromName returns correct variable",
				() -> assertEquals("stringValue", tool.getCustomVariableFromName("testCustomVariableString")),
				() -> assertEquals(5, tool.getCustomVariableFromName("testCustomVariableInteger")));
	}

	/**
	 * Ensures that {@link Player#setCustomVariableByName(String, Object)} sets the custom variable correctly
	 */
	@Test
	public void setsCorrectCustomVariable() {
		//setUp
		Tool tool = new Tool("tool", "atest", new ArrayList<>(), 10 ,10, false);
		ArrayList<CustomVariable> cv = new ArrayList<>();
		cv.add(new CustomVariable("testCustomVariableString", "stringValue", String.class, false, true));
		cv.add(new CustomVariable("testCustomVariableInteger", 5, Integer.class, true, false));
		tool.setCustomVariables(cv);

		tool.setCustomVariableByName("testCustomVariableString", "alternateStringValue");
		tool.setCustomVariableByName("testCustomVariableInteger", 6);

		ArrayList<CustomVariable> newCV = tool.getCustomVariables();

		assertAll( "setCustomVariableByName sets variable to incorrect value",
				() -> assertEquals("alternateStringValue", newCV.get(0).getValue()),
				() -> assertEquals(6, newCV.get(1).getValue()));
	}

	@Test
	public void nameCannotBeNull() {
		Tool tool = new Tool("tool", "atest", new ArrayList<>(), 10 ,10, false);

		tool.setName(null);
		assertEquals("tool", tool.getName());

		//Make sure it can change
		tool.setName("testName");
		assertEquals("testName", tool.getName());
	}

	@Test
	public void durabilityCannotChangeIfUnbreakable() {
		Tool tool = new Tool("tool", "atest", new ArrayList<>(), 10 ,10, true);

		tool.setDurability(0);
		assertEquals(10, tool.getDurability());

		tool.setDurability(10);
		tool.increaseDurability(-11);
		assertEquals(10, tool.getDurability());

		tool.setDurability(10);
		tool.decreaseDurability(11);
		assertEquals(10, tool.getDurability());

		tool.setDurability(10);
		tool.increaseDurability(11);
		assertEquals(10, tool.getDurability());

		tool.setDurability(10);
		tool.decreaseDurability(-11);
		assertEquals(10, tool.getDurability());
	}

	@Test
	public void durabilityCannotGoBelowZero() {
		Tool tool = new Tool("tool", "atest", new ArrayList<>(), 10 ,10, false);

		tool.setDurability(-1);
		assertEquals(0, tool.getDurability());

		tool.setDurability(10);
		tool.increaseDurability(-11);
		assertEquals(0, tool.getDurability());

		tool.setDurability(10);
		tool.decreaseDurability(11);
		assertEquals(0, tool.getDurability());
	}

	@Test
	public void durabilityCannotGoAboveMax() {
		Tool tool = new Tool("tool", "atest", new ArrayList<>(), 10 ,10, false);

		tool.setDurability(11);
		assertEquals(10, tool.getDurability());

		tool.setDurability(10);
		tool.increaseDurability(11);
		assertEquals(10, tool.getDurability());

		tool.setDurability(10);
		tool.decreaseDurability(-11);
		assertEquals(10, tool.getDurability());
	}

	@Test
	public void removedFromInventoryWhenBroken() {
		Player player = new Player();
		TextFighter.player = player;

		Tool tool = new Tool("tool", "atest", new ArrayList<>(), 10 ,10, false);
		TextFighter.tools.add(tool);
		player.addToInventory("tool", "tool");
		tool.broken();

		assertNull(player.getFromInventory("tool","tool"));

		TextFighter.armors.clear();
		TextFighter.player = null;
	}

}