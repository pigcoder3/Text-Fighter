package com.seanjohnson.textfighter;

import com.seanjohnson.textfighter.display.*;
import com.seanjohnson.textfighter.location.*;
import com.seanjohnson.textfighter.method.*;
import com.seanjohnson.textfighter.item.*;
import org.junit.Test;
import org.w3c.dom.Text;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

	private static Player player;
	/**
	 * Ensures that {@link Player#getCustomVariableFromName(String)} returns the correct custom variable value
	 */
	@Test
	public void returnsCorrectCustomVariable() {
		//setUp
		player = new Player();
		ArrayList<CustomVariable> cv = new ArrayList<>();
		cv.add(new CustomVariable("testCustomVariableString", "stringValue", String.class, false, true));
		cv.add(new CustomVariable("testCustomVariableInteger", 5, Integer.class, true, false));
		player.setCustomVariables(cv);

		//Testing a custom variable with type String
		assertAll( "getCustomVariableFromName returns correct variable",
				() -> assertEquals("stringValue", player.getCustomVariableFromName("testCustomVariableString")),
				() -> assertEquals(5, player.getCustomVariableFromName("testCustomVariableInteger")));
	}

	/**
	 * Ensures that {@link Player#setCustomVariableByName(String, Object)} sets the custom variable correctly
	 */
	@Test
	public void setsCorrectCustomVariable() {
		//setUp
		player = new Player();
		ArrayList<CustomVariable> cv = new ArrayList<>();
		cv.add(new CustomVariable("testCustomVariableString", "stringValue", String.class, false, true));
		cv.add(new CustomVariable("testCustomVariableInteger", 5, Integer.class, true, false));
		player.setCustomVariables(cv);

		player.setCustomVariableByName("testCustomVariableString", "alternateStringValue");
		player.setCustomVariableByName("testCustomVariableInteger", 6);

		ArrayList<CustomVariable> newCV = player.getCustomVariables();

		assertAll( "setCustomVariableByName sets variable to incorrect value",
				() -> assertEquals("alternateStringValue", newCV.get(0).getValue()),
				() -> assertEquals(6, newCV.get(1).getValue()));
	}

	/**
	 * Make sure the weapon given to {@link Player#setCurrentWeapon(String)} is properly set.
	 * If the weapon name passed to {@link Player#setCurrentWeapon(String)} is null, then fists should be used.
	 * Effectively unequipping your weapon
	 */
	@Test
	public void setWeaponToCorrectWeapon() {
		player = new Player();

		Weapon testWeapon = new Weapon("testWeapon", "a test weapon", 10, 0, 0, new ArrayList<>(), 100, 100, false);
		TextFighter.weapons.add(testWeapon);
		TextFighter.weapons.add(new Weapon("fists", "Your fists, you don't need a description about what that is.", 5, 10, 5, new ArrayList<>(), 100, 100, true));
		player.addToInventory("testWeapon", "weapon"); //The weapon should be a different one

		//Make sure that it can set weapon when given correct name
		player.setCurrentWeapon("testWeapon");
		assertEquals("testWeapon", player.getCurrentWeaponName());

		//Make sure it is set to weapon if null value given (effectively unequipping weapon)
		player.setCurrentWeapon(null);
		assertEquals("fists", player.getCurrentWeaponName());

		//Make sure the weapon does not change if the name of a weapon that the player does not have is given
		player.setCurrentWeapon("invalidweapon");
		assertEquals("fists", player.getCurrentWeaponName());

		TextFighter.weapons.clear();
	}

	@Test
	public void ensureHealthStaysAboveZero() {
		player = new Player();
		player.setHp(10);

		player.damaged(20, "Testing the damage");
		assertEquals(0, player.getHp());

		player.setHp(10);
		player.heal(-20);
		assertEquals(0, player.getHp());

		player.setHp(10);
		player.heal(10);
		assertEquals(20, player.getHp()); //Just to make sure that it is not always set to 0
	}

	@Test
	public void ensureMaxHealthStaysAboveZero() {
		player = new Player();
		player.setMaxHp(10);

		player.setMaxHp(-2);
		assertEquals(0, player.getMaxHp());
	}

	@Test
	public void ensureHealthStaysBelowMaxHealth() {
		//at or below maxhp
		player = new Player();
		player.setHp(20);
		player.setMaxHp(10);

		assertEquals(10, player.getHp()); //Make sure hp always stays at or below maxhp
	}

	@Test
	public void ensureKillsStaysAboveZero() {
		player = new Player();
		player.setKills(2);

		player.increaseKills(-10);
		assertEquals(0, player.getKills());

		player.setKills(2);
		player.decreaseKills(10);
		assertEquals(0, player.getKills());

		player.setKills(-2);
		assertEquals(0,player.getKills());
	}

	@Test
	public void ensureHealthPotionsStayAboveZero() {
		player = new Player();
		player.setHealthPotions(2);

		player.increaseHealthPotions(-10);
		assertEquals(0, player.getHealthPotions());

		player.setHealthPotions(2);
		player.decreaseHealthPotions(10);
		assertEquals(0, player.getHealthPotions());

		player.setHealthPotions(-2);
		assertEquals(0, player.getHealthPotions());
	}

	@Test
	public void ensureStrengthPotionsStayAboveZero() {
		player = new Player();
		player.setStrengthPotions(2);

		player.increaseStrengthPotions(-10);
		assertEquals(0, player.getStrengthPotions());

		player.setStrengthPotions(2);
		player.decreaseStrengthPotions(10);
		assertEquals(0, player.getStrengthPotions());

		player.setStrengthPotions(-2);
		assertEquals(0, player.getStrengthPotions());
	}

	@Test
	public void ensureInvincibilityPotionsStayAboveZero() {
		player = new Player();
		player.setInvincibilityPotions(2);

		player.increaseInvincibilityPotions(-10);
		assertEquals(0, player.getInvincibilityPotions());

		player.setInvincibilityPotions(2);
		player.decreaseInvincibilityPotions(10);
		assertEquals(0, player.getInvincibilityPotions());

		player.setInvincibilityPotions(-2);
		assertEquals(0, player.getInvincibilityPotions());
	}

	@Test
	public void ensureLevelStaysAboveZero() {
		//The minimum value for the player's level should be 1
		player = new Player();
		player.setLevel(10);

		player.decreaseLevel(11);
		assertEquals(1, player.getLevel());

		player.setLevel(10);
		player.increaseLevel(-11);
		assertEquals(1, player.getLevel());

		player.setLevel(10);
		player.increaseLevel(11);
		assertEquals(21, player.getLevel()); //Just to make sure that it is not always set to 1
	}

	@Test
	public void ensureExpStaysAboveZero() {
		player = new Player();
		player.setExperience(10);

		player.decreaseExperience(20);
		assertEquals(0, player.getExperience());

		player.setExperience(10);
		player.increaseExperience(-20);
		assertEquals(0, player.getExperience());

		player.setExperience(10);
		player.increaseExperience(10);
		assertEquals(20, player.getExperience()); //Just to make sure that it is not always set to 0
	}

	@Test
	public void ensureCoinsStayAboveZero() {
		player = new Player();
		player.setCoins(10);

		player.spendCoins(20);
		assertEquals(10, player.getCoins()); //Cannot spend coins that you dont have

		player.setCoins(10);
		player.gainCoins(-20);
		assertEquals(0, player.getCoins());

		player.setCoins(10);
		player.gainCoins(10);
		assertEquals(20, player.getCoins()); //Just to make sure that it is not always set to 0
	}

	@Test
	public void ensureMagicStayAboveZero() {
		player = new Player();
		player.setMagic(10);

		player.spendMagic(20);
		assertEquals(10, player.getMagic()); //Cannot spend magic that you dont have

		player.setMagic(10);
		player.gainMagic(-20);
		assertEquals(0, player.getMagic());

		player.setMagic(10);
		player.gainMagic(10);
		assertEquals(20, player.getMagic()); //Just to make sure that it is not always set to 0
	}

	@Test
	public void ensureMetalScrapsStayAboveZero() {
		player = new Player();
		player.setMetalScraps(10);

		player.spendMetalScraps(20);
		assertEquals(10, player.getMetalScraps()); //Cannot spend metal scraps that you dont have

		player.setMetalScraps(10);
		player.gainMetalScraps(-20);
		assertEquals(0, player.getMetalScraps());

		player.setMetalScraps(10);
		player.gainMetalScraps(10);
		assertEquals(20, player.getMetalScraps()); //Just to make sure that it is not always set to 0
	}

	/**
	 * Ensure that {@link Player#addToInventory(String, String) } adds the correct item and type to the player's inventory
	 */
	@Test
	public void addsToInventoryCorrectly() {
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

		assertEquals(TextFighter.weapons.get(0).getName(), player.getInventory().get(0).getName());
		assertEquals(TextFighter.armors.get(0).getName(), player.getInventory().get(1).getName());
		assertEquals(TextFighter.tools.get(0).getName(), player.getInventory().get(2).getName());
		assertEquals(TextFighter.specialItems.get(0).getName(), player.getInventory().get(3).getName());

		TextFighter.weapons.clear();
		TextFighter.armors.clear();
		TextFighter.tools.clear();
		TextFighter.specialItems.clear();
	}

	/**
	 * Ensure that {@link Player#removeFromInventory(String, String)} removes the correct item and type to the player's inventory.
	 */
	@Test
	public void removesFromInventoryCorrectly() {
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

		player.removeFromInventory("testArmor", "armor");
		assertEquals(TextFighter.tools.get(0).getName(), player.getInventory().get(1).getName());

		player.removeFromInventory("testWeapon", "weapon");
		assertEquals(TextFighter.specialItems.get(0).getName(), player.getInventory().get(1).getName());

		TextFighter.weapons.clear();
		TextFighter.armors.clear();
		TextFighter.tools.clear();
		TextFighter.specialItems.clear();
	}

	/**
	 * Ensure that {@link Player#removeFromInventory(String, String)} removes the correct item and type to the player's inventory.
	 */
	@Test
	public void determinesIfCarryingCorrectly() {
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

		assertTrue(player.isCarrying("testWeapon", "weapon"));
		assertTrue(player.isCarrying("testTool", "tool"));
		assertFalse(player.isCarrying("notArmor", "weapon"));
		assertFalse(player.isCarrying("notArmor", "armor"));
		assertFalse(player.isCarrying("notSpecialItem", "specialitem"));

		TextFighter.weapons.clear();
		TextFighter.armors.clear();
		TextFighter.tools.clear();
		TextFighter.specialItems.clear();
	}

	/**
	 * Ensure that {@link Player#removeFromInventory(String, String)} removes the correct item and type to the player's inventory.
	 */
	@Test
	public void getFromInventoryCorrectly() {
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

		assertEquals(TextFighter.weapons.get(0).getName(), player.getFromInventory("testWeapon", "weapon").getName());
		assertEquals(TextFighter.armors.get(0).getName(), player.getFromInventory("testArmor", "armor").getName());
		assertNull(player.getFromInventory("testTool", "weapon"));
		assertEquals(TextFighter.specialItems.get(0).getName(), player.getFromInventory("testSpecialItem", "specialitem").getName());

		TextFighter.weapons.clear();
		TextFighter.armors.clear();
		TextFighter.tools.clear();
		TextFighter.specialItems.clear();
	}

	/**
	 * Ensure that {@link Player#achievementEarned(Achievement)} Adds the correct achievement.
	 */
	@Test
	public void addsAchievementCorrectly() {
		player = new Player();
		Achievement ach = new Achievement("testAchievement", "a test achievement", new ArrayList<>(), new ArrayList<>());
		player.achievementEarned(ach);

		assertEquals(ach, player.getAchievements().get(0));

		TextFighter.achievements.clear();
	}

	/**
	 * Ensure that {@link Player#isAchievementEarned(String)} correctly returns whether or not the player has the achievement.
	 */
	@Test
	public void determineIfHasAchievementCorrectly() {
		player = new Player();
		Achievement ach = new Achievement("testAchievement", "a test achievement", new ArrayList<>(), new ArrayList<>());
		TextFighter.achievements.add(ach);
		player.setAchievements(TextFighter.achievements);

		assertTrue(player.isAchievementEarned("testAchievement"));

		TextFighter.achievements.clear();
	}

	/**
	 * Ensure that {@link Player#calculateTotalProtection()} calculates correctly (accounting for default protection, armor)
	 */
	@Test
	public void calculatesProtectionCorrectly() {
		player = new Player();
		double totalProtection = Player.defaultTotalProtection + 20;
		TextFighter.armors.add(new Armor("chestplate", "a test armor piece", 10.0,100, 100, false, new ArrayList<>()));
		TextFighter.armors.add(new Armor("leggings", "a test armor piece", 10.0,100, 100, false, new ArrayList<>()));
		player.addToInventory("chestplate", "armor");
		player.addToInventory("leggings", "armor");

		assertEquals(totalProtection, player.calculateTotalProtection());

		TextFighter.armors.clear();
	}

	/**
	 * Ensure that {@link Player#calculateStrength()} calculate correctly (accounting for weapon, no weapon, and strength potion multiplier)
	 */
	@Test
	public void calculatesStrengthCorrectly() {
		player = new Player();

		Weapon testWeapon = new Weapon("testWeapon", "a test weapon", 10, 0, 0, new ArrayList<>(), 100, 100, false);
		TextFighter.weapons.add(testWeapon);
		TextFighter.weapons.add(new Weapon("fists", "Your fists, you don't need a description about what that is.", 5, 10, 5, new ArrayList<>(), 100, 100, true));
		player.addToInventory("testWeapon", "weapon");
		player.setCurrentWeapon("testWeapon");
		assertEquals(player.calculateStrength(), 10);

		player.setTurnsWithStrengthLeft(1);
		assertEquals(10*Player.defaultStrengthPotionMultiplier, player.calculateStrength());

		player.setCurrentWeapon("fists");
		assertEquals(TextFighter.weapons.get(0).getDamage(), player.calculateStrength());

		player.setTurnsWithStrengthLeft(1);
		assertEquals(Player.defaultStrength*Player.defaultStrengthPotionMultiplier, player.calculateStrength());

		TextFighter.weapons.clear();
	}

}