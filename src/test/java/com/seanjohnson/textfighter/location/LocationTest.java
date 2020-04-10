package com.seanjohnson.textfighter.location;

import com.seanjohnson.textfighter.Player;
import com.seanjohnson.textfighter.method.MethodTest;
import com.seanjohnson.textfighter.method.Requirement;
import com.seanjohnson.textfighter.method.TFMethod;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class LocationTest {

	public static Player player;

	@Test
	public void premethodsFilteredCorrectly() {
		try {
			player = new Player();
			player.setAlive(true);
			Method method;
			//Methods
			ArrayList<TFMethod> premethods = new ArrayList<>();
			//(Method method, ArrayList<Object> arguments, ArrayList<Class> argumentTypes, Object field, boolean neededBoolean)
			Requirement req = new Requirement(Player.class.getMethod("getAlive", new Class[]{}), new ArrayList<Object>(), new ArrayList<Class>(), LocationTest.class.getField("player"), false);
			method = Player.class.getMethod("setMetalScraps", int.class);
			TFMethod tf1 = new TFMethod(method, new ArrayList<Object>(Arrays.asList(new Object[]{15})), new ArrayList<Class>(Arrays.asList(new Class[]{int.class})), LocationTest.class.getField("player"), new ArrayList<>(Arrays.asList(new Requirement[]{req})));
			method = Player.class.getMethod("spendMetalScraps", int.class);
			TFMethod tf2 = new TFMethod(method, new ArrayList<Object>(Arrays.asList(new Object[]{5})), new ArrayList<Class>(Arrays.asList(new Class[]{int.class})), LocationTest.class.getField("player"), new ArrayList<>());
			premethods.add(tf1);
			premethods.add(tf2);

			Location loc = new Location("testLocation", new ArrayList<>(), new ArrayList<>(), premethods, new ArrayList<>());
			loc.filterPremethods();

			assertEquals(1, loc.getPremethods().size());

		} catch (NoSuchMethodException | NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void premethodsInvokedCorrectly() {
		try {
			player = new Player();
			Method method;
			//Methods
			ArrayList<TFMethod> premethods = new ArrayList<>();
			method = Player.class.getMethod("setMetalScraps", int.class);
			TFMethod tf1 = new TFMethod(method, new ArrayList<Object>(Arrays.asList(new Object[]{15})), new ArrayList<Class>(Arrays.asList(new Class[]{int.class})), LocationTest.class.getField("player"), new ArrayList<>());
			method = Player.class.getMethod("spendMetalScraps", int.class);
			TFMethod tf2 = new TFMethod(method, new ArrayList<Object>(Arrays.asList(new Object[]{5})), new ArrayList<Class>(Arrays.asList(new Class[]{int.class})), LocationTest.class.getField("player"), new ArrayList<>());
			premethods.add(tf1);
			premethods.add(tf2);

			Location loc = new Location("testLocation", new ArrayList<>(), new ArrayList<>(), premethods, new ArrayList<>());
			loc.invokePremethods();

			assertEquals(10, player.getMetalScraps());

		} catch (NoSuchMethodException | NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void postmethodsFilteredCorrectly() {
		try {
			player = new Player();
			player.setAlive(true);
			Method method;
			//Methods
			ArrayList<TFMethod> postmethods = new ArrayList<>();
			//(Method method, ArrayList<Object> arguments, ArrayList<Class> argumentTypes, Object field, boolean neededBoolean)
			Requirement req = new Requirement(Player.class.getMethod("getAlive", new Class[]{}), new ArrayList<Object>(), new ArrayList<Class>(), LocationTest.class.getField("player"), false);
			method = Player.class.getMethod("setMetalScraps", int.class);
			TFMethod tf1 = new TFMethod(method, new ArrayList<Object>(Arrays.asList(new Object[]{15})), new ArrayList<Class>(Arrays.asList(new Class[]{int.class})), LocationTest.class.getField("player"), new ArrayList<>(Arrays.asList(new Requirement[]{req})));
			method = Player.class.getMethod("spendMetalScraps", int.class);
			TFMethod tf2 = new TFMethod(method, new ArrayList<Object>(Arrays.asList(new Object[]{5})), new ArrayList<Class>(Arrays.asList(new Class[]{int.class})), LocationTest.class.getField("player"), new ArrayList<>());
			postmethods.add(tf1);
			postmethods.add(tf2);

			Location loc = new Location("testLocation", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), postmethods);
			loc.filterPostmethods();

			assertEquals(1, loc.getPostmethods().size());

		} catch (NoSuchMethodException | NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void postmethodsInvokedCorrectly() {
		try {
			player = new Player();
			Method method;
			//Methods
			ArrayList<TFMethod> postmethods = new ArrayList<>();
			method = Player.class.getMethod("setMetalScraps", int.class);
			TFMethod tf1 = new TFMethod(method, new ArrayList<Object>(Arrays.asList(new Object[]{15})), new ArrayList<Class>(Arrays.asList(new Class[]{int.class})), LocationTest.class.getField("player"), new ArrayList<>());
			method = Player.class.getMethod("spendMetalScraps", int.class);
			TFMethod tf2 = new TFMethod(method, new ArrayList<Object>(Arrays.asList(new Object[]{5})), new ArrayList<Class>(Arrays.asList(new Class[]{int.class})), LocationTest.class.getField("player"), new ArrayList<>());
			postmethods.add(tf1);
			postmethods.add(tf2);

			Location loc = new Location("testLocation", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), postmethods);
			loc.invokePostmethods();

			assertEquals(10, player.getMetalScraps());

		} catch (NoSuchMethodException | NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void choicesFilteredCorrectly() {
		try {
			player = new Player();
			player.setAlive(true);
			Method method;
			//Methods
			ArrayList<Choice> choices = new ArrayList<>();
			Requirement req = new Requirement(Player.class.getMethod("getAlive", new Class[]{}), new ArrayList<Object>(), new ArrayList<Class>(), LocationTest.class.getField("player"), false);
			Choice tf1 = new Choice("choice1", "atest", "usage", new ArrayList<>(), new ArrayList<>(Arrays.asList(new Requirement[]{req})),"failed");
			Choice tf2 = new Choice("choice2", "atest", "usage", new ArrayList<>(), new ArrayList<>(),"failed");
			choices.add(tf1);
			choices.add(tf2);

			Location loc = new Location("testLocation", new ArrayList<>(), choices, new ArrayList<>(), new ArrayList<>());
			loc.filterPossibleChoices();

			assertEquals(1, loc.getPossibleChoices().size());

		} catch (NoSuchMethodException | NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

}