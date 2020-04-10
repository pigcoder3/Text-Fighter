package com.seanjohnson.textfighter;

import com.seanjohnson.textfighter.enemy.Enemy;
import com.seanjohnson.textfighter.item.Armor;
import com.seanjohnson.textfighter.item.SpecialItem;
import com.seanjohnson.textfighter.item.Tool;
import com.seanjohnson.textfighter.item.Weapon;
import com.seanjohnson.textfighter.location.Location;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class PackMethodsTest {

	Player player;

	/**
	 * {@link PackMethods#getAllItemSimpleOutputsFromInventory(String)}
	 */
	@Test
	public void simpleItemOutputsFromInventoryGivenCorrectly() {
		player = new Player();

		Weapon testWeapon = new Weapon("testWeapon", "a test weapon", 10, 0, 0, new ArrayList<>(), 100, 100, false);
		TextFighter.weapons.add(testWeapon);
		Armor testArmor = new Armor("testArmor", "a test armor", 10, 100, 100, false, new ArrayList<>());
		TextFighter.armors.add(testArmor);
		Tool testTool = new Tool("testTool", "a test tool", new ArrayList<>(), 100, 100, false);
		TextFighter.tools.add(testTool);
		SpecialItem testSpecialItem = new SpecialItem("testSpecialItem", "a test special item", new ArrayList<>());
		TextFighter.specialItems.add(testSpecialItem);
		player.addToInventory("testWeapon", "weapon");
		player.addToInventory("testArmor", "armor");
		player.addToInventory("testTool", "tool");
		player.addToInventory("testSpecialItem", "specialitem");

		TextFighter.player = player;

		String outputs = PackMethods.getAllItemSimpleOutputsFromInventory("all").toString().replaceAll(", ","").replace("\n","");

		assertEquals(
				testWeapon.getSimpleOutput().replace("\n","") +
						testArmor.getSimpleOutput().replace("\n","") +
						testTool.getSimpleOutput().replace("\n","") +
						testSpecialItem.getSimpleOutput().replace("\n",""),
				outputs.substring(1,outputs.length()-1));

		TextFighter.player = null;

	}

	@Test
	public void itemOutputsFromInventoryGivenCorrectly() {
		player = new Player();

		Weapon testWeapon = new Weapon("testWeapon", "a test weapon", 10, 0, 0, new ArrayList<>(), 100, 100, false);
		TextFighter.weapons.add(testWeapon);
		Armor testArmor = new Armor("testArmor", "a test armor", 10, 100, 100, false, new ArrayList<>());
		TextFighter.armors.add(testArmor);
		Tool testTool = new Tool("testTool", "a test tool", new ArrayList<>(), 100, 100, false);
		TextFighter.tools.add(testTool);
		SpecialItem testSpecialItem = new SpecialItem("testSpecialItem", "a test special item", new ArrayList<>());
		TextFighter.specialItems.add(testSpecialItem);
		player.addToInventory("testWeapon", "weapon");
		player.addToInventory("testArmor", "armor");
		player.addToInventory("testTool", "tool");
		player.addToInventory("testSpecialItem", "specialitem");

		TextFighter.player = player;

		String outputs = PackMethods.getAllItemOutputsFromInventory("all").toString().replaceAll(", ","").replace("\n","");

		assertEquals(
				testWeapon.getOutput().replace("\n","") +
						testArmor.getOutput().replace("\n","") +
						testTool.getOutput().replace("\n","") +
						testSpecialItem.getOutput().replace("\n",""),
				outputs.substring(1,outputs.length()-1));

		TextFighter.player = null;

	}

	@Test
	public void correctOutputByNameAndType() {

		Weapon testWeapon = new Weapon("testWeapon", "a test weapon", 10, 0, 0, new ArrayList<>(), 100, 100, false);
		TextFighter.weapons.add(testWeapon);
		Armor testArmor = new Armor("testArmor", "a test armor", 10, 100, 100, false, new ArrayList<>());
		TextFighter.armors.add(testArmor);
		Tool testTool = new Tool("testTool", "a test tool", new ArrayList<>(), 100, 100, false);
		TextFighter.tools.add(testTool);
		SpecialItem testSpecialItem = new SpecialItem("testSpecialItem", "a test special item", new ArrayList<>());
		TextFighter.specialItems.add(testSpecialItem);

		assertEquals(
				testWeapon.getOutput(),
				PackMethods.getOutputByNameAndType("testWeapon", "weapon")
		);
		assertEquals(
				testArmor.getOutput(),
				PackMethods.getOutputByNameAndType("testArmor", "armor")
		);
		assertEquals(
				testTool.getOutput(),
				PackMethods.getOutputByNameAndType("testTool", "tool")
		);
		assertEquals(
				"",
				PackMethods.getOutputByNameAndType("testSpecialItem", "weapon")
		);

	}

	@Test
	public void getsCorrectPossibleEnemyOutputs() {
		player = new Player();

		Enemy enemy1 = new Enemy("testEnemy1", "a test enemy", 1, 1, 1, false, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
		Enemy enemy2 = new Enemy("testEnemy2", "a test enemy", 1, 1, 1, false, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
		Enemy enemy3 = new Enemy("testEnemy3", "a test enemy", 1, 1, 1, false, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

		TextFighter.enemies.add(enemy1);
		TextFighter.enemies.add(enemy2);
		TextFighter.enemies.add(enemy3);

		TextFighter.player = player;
		String outputs = PackMethods.getPossibleEnemyOutputs().toString().replace(", ", "");

		assertEquals(enemy1.getOutput() + enemy2.getOutput() + enemy3.getOutput(), outputs.substring(1,outputs.length()-1));

		TextFighter.player = null;
		TextFighter.enemies.clear();
		TextFighter.possibleEnemies.clear();
	}

	@Test
	public void correctlyMovesPlayer() {
		player = new Player();
		TextFighter.player = player;
		//(String name, ArrayList<UserInterface> interfaces, ArrayList<Choice> choices, ArrayList<TFMethod> premethods, ArrayList<TFMethod> postmethods) {
		Location loc = new Location("testLocation1", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
		TextFighter.locations.add(loc);
		Location loc2 = new Location("testLocation2", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
		TextFighter.locations.add(loc2);
		player.setLocation(loc.getName());

		PackMethods.movePlayer(loc2.getName());

		assertEquals(loc2.getName(), player.getLocation().getName());

		TextFighter.locations.clear();
		TextFighter.player = null;

	}

	@Test
	public void correctlyCalculatesFromTwoIntegers() {
		assertEquals(5, PackMethods.calculateFromTwoIntegers(2, "+", 3));
		assertEquals(-1, PackMethods.calculateFromTwoIntegers(2, "-", 3));
		assertEquals(6, PackMethods.calculateFromTwoIntegers(2, "*", 3));
		assertEquals(2/3, PackMethods.calculateFromTwoIntegers(2, "/", 3));
	}

	@Test
	public void correctlyCalculatesFromTwoDoubles() {
		assertEquals(5, PackMethods.calculateFromTwoDoubles(2.0, "+", 3.0));
		assertEquals(-1, PackMethods.calculateFromTwoDoubles(2.0, "-", 3.0));
		assertEquals(6, PackMethods.calculateFromTwoDoubles(2.0, "*", 3.0));
		assertEquals(2/3.0, PackMethods.calculateFromTwoDoubles(2.0, "/", 3.0));
	}

	@Test
	public void correctlyComparesStringVariables() {
		assertTrue(PackMethods.variableComparison("hello", "hello"));
		assertFalse(PackMethods.variableComparison("hello", "world"));
	}

	@Test
	public void correctlyComparesDoubleVariables() {
		assertTrue(PackMethods.variableComparison(0.5, "<",1.5));
		assertTrue(PackMethods.variableComparison(1.5, ">",0.5));
		assertTrue(PackMethods.variableComparison(0.5, "<=",1.5));
		assertTrue(PackMethods.variableComparison(1.5, ">=",0.5));
		assertFalse(PackMethods.variableComparison(1.5, "<",0.5));
		assertFalse(PackMethods.variableComparison(0.5, ">",1.5));
		assertFalse(PackMethods.variableComparison(1.5, "<=",0.5));
		assertFalse(PackMethods.variableComparison(0.5, ">=",1.5));
	}

	@Test
	public void correctlyComparesIntegerVariables() {
		assertTrue(PackMethods.variableComparison(1, "<",2));
		assertTrue(PackMethods.variableComparison(2, ">",1));
		assertTrue(PackMethods.variableComparison(1, "<=",2));
		assertTrue(PackMethods.variableComparison(2, ">=",1));
		assertFalse(PackMethods.variableComparison(2, "<",1));
		assertFalse(PackMethods.variableComparison(1, ">",2));
		assertFalse(PackMethods.variableComparison(2, "<=",1));
		assertFalse(PackMethods.variableComparison(1, ">=",2));
	}

	@Test
	public void correctlyComparesBooleans() {
		assertTrue(PackMethods.boolAndBool(true, true));
		assertFalse(PackMethods.boolAndBool(true, false));
		assertFalse(PackMethods.boolAndBool(false, true));
		assertFalse(PackMethods.boolAndBool(false, false));
	}

}