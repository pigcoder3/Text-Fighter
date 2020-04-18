package com.seanjohnson.textfighter.item;

import com.seanjohnson.textfighter.CustomVariable;
import com.seanjohnson.textfighter.Player;
import com.seanjohnson.textfighter.TextFighter;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ArmorTest {

	/**
	 * Ensures that {@link Player#getCustomVariableFromName(String)} returns the correct custom variable value
	 */
	@Test
	public void returnsCorrectCustomVariable() {
		//setUp
		Armor armor = new Armor("armor", "atest", 10, 10 ,10, false, new ArrayList<>());
		ArrayList<CustomVariable> cv = new ArrayList<>();
		cv.add(new CustomVariable("testCustomVariableString", "stringValue", String.class, false, true));
		cv.add(new CustomVariable("testCustomVariableInteger", 5, Integer.class, true, false));
		armor.setCustomVariables(cv);

		//Testing a custom variable with type String
		assertAll( "getCustomVariableFromName returns correct variable",
				() -> assertEquals("stringValue", armor.getCustomVariableFromName("testCustomVariableString")),
				() -> assertEquals(5, armor.getCustomVariableFromName("testCustomVariableInteger")));
	}

	/**
	 * Ensures that {@link Player#setCustomVariableByName(String, Object)} sets the custom variable correctly
	 */
	@Test
	public void setsCorrectCustomVariable() {
		//setUp
		Armor armor = new Armor("armor", "atest", 10, 10 ,10, false, new ArrayList<>());
		ArrayList<CustomVariable> cv = new ArrayList<>();
		cv.add(new CustomVariable("testCustomVariableString", "stringValue", String.class, false, true));
		cv.add(new CustomVariable("testCustomVariableInteger", 5, Integer.class, true, false));
		armor.setCustomVariables(cv);

		armor.setCustomVariableByName("testCustomVariableString", "alternateStringValue");
		armor.setCustomVariableByName("testCustomVariableInteger", 6);

		ArrayList<CustomVariable> newCV = armor.getCustomVariables();

		assertAll( "setCustomVariableByName sets variable to incorrect value",
				() -> assertEquals("alternateStringValue", newCV.get(0).getValue()),
				() -> assertEquals(6, newCV.get(1).getValue()));
	}

	@Test
	public void nameCannotBeNull() {
		Armor armor = new Armor("armor", "atest", 10, 10 ,10, false, new ArrayList<>());

		armor.setName(null);
		assertEquals("armor", armor.getName());

		//Make sure it can change
		armor.setName("testName");
		assertEquals("testName", armor.getName());
	}

	@Test
	public void durabilityCannotChangeIfUnbreakable() {
		Armor armor = new Armor("armor", "atest", 10, 10 ,10, true, new ArrayList<>());

		armor.setDurability(0);
		assertEquals(10, armor.getDurability());

		armor.setDurability(10);
		armor.increaseDurability(-11);
		assertEquals(10, armor.getDurability());

		armor.setDurability(10);
		armor.decreaseDurability(11);
		assertEquals(10, armor.getDurability());

		armor.setDurability(10);
		armor.increaseDurability(11);
		assertEquals(10, armor.getDurability());

		armor.setDurability(10);
		armor.decreaseDurability(-11);
		assertEquals(10, armor.getDurability());
	}

	@Test
	public void durabilityCannotGoBelowZero() {
		Armor armor = new Armor("armor", "atest", 10, 10 ,10, true, new ArrayList<>());

		armor.setDurability(-1);
		assertEquals(10, armor.getDurability());

		armor.setDurability(10);
		armor.increaseDurability(-11);
		assertEquals(10, armor.getDurability());

		armor.setDurability(10);
		armor.decreaseDurability(11);
		assertEquals(10, armor.getDurability());
	}

	@Test
	public void durabilityCannotGoAboveMax() {
		Armor armor = new Armor("armor", "atest", 10, 10 ,10, false, new ArrayList<>());

		armor.setDurability(11);
		assertEquals(10, armor.getDurability());

		armor.setDurability(10);
		armor.increaseDurability(11);
		assertEquals(10, armor.getDurability());

		armor.setDurability(10);
		armor.decreaseDurability(-11);
		assertEquals(10, armor.getDurability());
	}

	@Test
	public void protectionCannotGoBelowZero() {
		Armor armor = new Armor("armor", "atest", 10, 10 ,10, false, new ArrayList<>());

		armor.setProtectionAmount(-1);
		assertEquals(0, armor.getProtectionAmount());
	}

	@Test
	public void removedFromInventoryWhenBroken() {
		Player player = new Player();
		TextFighter.player = player;

		Armor armor = new Armor("armor", "atest", 10, 10 ,10, false, new ArrayList<>());
		TextFighter.armors.add(armor);
		player.addToInventory("armor", "armor");
		armor.broken();

		assertNull(player.getFromInventory("armor","armor"));

		TextFighter.armors.clear();
		TextFighter.player = null;
	}

}