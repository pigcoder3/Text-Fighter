package com.seanjohnson.textfighter.method;

import com.seanjohnson.textfighter.Achievement;
import com.seanjohnson.textfighter.AchievementTest;
import com.seanjohnson.textfighter.Player;
import com.seanjohnson.textfighter.display.UiTag;
import com.seanjohnson.textfighter.enemy.EnemyAction;
import com.seanjohnson.textfighter.enemy.EnemyActionMethod;
import com.seanjohnson.textfighter.location.Choice;
import com.seanjohnson.textfighter.location.ChoiceMethod;
import org.junit.Test;

import javax.swing.text.AsyncBoxView;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class MethodTest {

	public static Player player;

	@Test
	public void testRequirementInvokingMethods() {
		player = new Player();

		try {
			Method method;

			//Requirements
			method = Player.class.getMethod("getAlive", new Class[]{});
			Requirement req1 = new Requirement(method, new ArrayList<Object>(), new ArrayList<Class>(), MethodTest.class.getField("player"), true);
			method = Player.class.getMethod("getAlive", new Class[]{});
			Requirement req2 = new Requirement(method, new ArrayList<Object>(), new ArrayList<Class>(), MethodTest.class.getField("player"), false);

			player.setAlive(true);
			assertTrue(req1.invokeMethod());
			assertFalse(req2.invokeMethod()); //Note that `neededBoolean` is false for req2

		} catch(NoSuchMethodException | NoSuchFieldException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testTFMethodInvokingMethods() {
		player = new Player();

		try {
			Method method;

			//Methods
			method = Player.class.getMethod("setMetalScraps", int.class);
			TFMethod tf1 = new TFMethod(method, new ArrayList<Object>(Arrays.asList(new Object[]{15})), new ArrayList<Class>(Arrays.asList(new Class[]{int.class})), MethodTest.class.getField("player"), new ArrayList<>());
			method = Player.class.getMethod("spendMetalScraps", int.class);
			TFMethod tf2 = new TFMethod(method, new ArrayList<Object>(Arrays.asList(new Object[]{5})), new ArrayList<Class>(Arrays.asList(new Class[]{int.class})), MethodTest.class.getField("player"), new ArrayList<>());

			tf1.invokeMethod();
			assertEquals(15, player.getMetalScraps());
			player.setMetalScraps(10);
			tf2.invokeMethod();
			assertEquals(5, player.getMetalScraps());

		} catch(NoSuchMethodException | NoSuchFieldException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testRewardInvokingMethods() {
		player = new Player();

		try {
			Method method;

			//Methods
			method = Player.class.getMethod("gainCoins", new Class[]{int.class});
			Reward coinReward = new Reward(method, new ArrayList<Object>(Arrays.asList(new Object[]{10})), new ArrayList<Class>(Arrays.asList(new Class[]{int.class})), MethodTest.class.getField("player"), new ArrayList<>(), 100, "10 coins");
			method = Player.class.getMethod("gainMagic", new Class[]{int.class});
			Reward magicReward = new Reward(method, new ArrayList<Object>(Arrays.asList(new Object[]{20})), new ArrayList<Class>(Arrays.asList(new Class[]{int.class})), MethodTest.class.getField("player"), new ArrayList<>(), 100, "20 magic");

			player.setCoins(0);
			assertEquals("10 coins", coinReward.invokeMethod());
			assertEquals(10, player.getCoins());
			player.setMagic(10);
			magicReward.invokeMethod();
			assertEquals(30, player.getMagic());

		} catch(NoSuchMethodException | NoSuchFieldException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testFieldMethodInvokingMethods() {
		player = new Player();

		try {
			Method method;

			//Methods
			method = Player.class.getMethod("getCoins", new Class[]{});
			FieldMethod coin = new FieldMethod(method, new ArrayList<Object>(), new ArrayList<Class>(), MethodTest.class.getField("player"));
			method = Player.class.getMethod("getMetalScraps", new Class[]{});
			FieldMethod metal = new FieldMethod(method, new ArrayList<Object>(), new ArrayList<Class>(), MethodTest.class.getField("player"));

			player.setCoins(0);
			assertEquals(0, coin.invokeMethod());
			player.setMetalScraps(10);
			assertEquals(10, metal.invokeMethod());

		} catch(NoSuchMethodException | NoSuchFieldException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void choiceMethodInvokingMethods() {
		player = new Player();
		player.setCoins(0);
		player.setMetalScraps(0);

		try {
			Method method;

			//Methods
			method = Player.class.getMethod("gainCoins", new Class[]{int.class});
			ChoiceMethod coin = new ChoiceMethod(method, new ArrayList<Object>(Arrays.asList(new Object[]{10})), new ArrayList<Class>(Arrays.asList(new Class[]{int.class})), MethodTest.class.getField("player"), new ArrayList<>());
			method = Player.class.getMethod("gainMetalScraps", new Class[]{int.class});
			ChoiceMethod metal = new ChoiceMethod(method, new ArrayList<Object>(Arrays.asList(new Object[]{10})), new ArrayList<Class>(Arrays.asList(new Class[]{int.class})), MethodTest.class.getField("player"), new ArrayList<>());

			coin.invokeMethod();
			metal.invokeMethod();

			assertEquals(10, player.getCoins());
			assertEquals(10, player.getMetalScraps());

		} catch(NoSuchMethodException | NoSuchFieldException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void enemyActionMethodInvokingMethods() {
		player = new Player();
		player.setCoins(0);
		player.setMetalScraps(0);

		try {
			Method method;

			//Methods
			method = Player.class.getMethod("gainCoins", new Class[]{int.class});
			EnemyActionMethod coin = new EnemyActionMethod(method, new ArrayList<Object>(Arrays.asList(new Object[]{10})), new ArrayList<Class>(Arrays.asList(new Class[]{int.class})), MethodTest.class.getField("player"));
			method = Player.class.getMethod("gainMetalScraps", new Class[]{int.class});
			EnemyActionMethod metal = new EnemyActionMethod(method, new ArrayList<Object>(Arrays.asList(new Object[]{10})), new ArrayList<Class>(Arrays.asList(new Class[]{int.class})), MethodTest.class.getField("player"));

			//(Method method, ArrayList<Object> arguments, ArrayList<Class> argumentTypes, Object field)

			coin.invokeMethod();
			metal.invokeMethod();

			assertEquals(10, player.getCoins());
			assertEquals(10, player.getMetalScraps());

		} catch(NoSuchMethodException | NoSuchFieldException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void UITagMethodInvokingMethods() {
		player = new Player();

		try {
			Method method;
			//(String tag, Method method, ArrayList<Object> arguments, ArrayList<Class> argumentTypes, Object field, ArrayList<Requirement> requirements)
			//Methods
			method = Player.class.getMethod("getMetalScraps", new Class[]{});
			UiTag coin = new UiTag("<test>", method, new ArrayList<Object>(), new ArrayList<Class>(), MethodTest.class.getField("player"), new ArrayList<>());

			player.setCoins(0);
			assertEquals(0, coin.invokeMethod());

		} catch(NoSuchMethodException | NoSuchFieldException e) {
			e.printStackTrace();
		}

	}

}