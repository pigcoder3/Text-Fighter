package com.seanjohnson.textfighter.location;

import com.seanjohnson.textfighter.Achievement;
import com.seanjohnson.textfighter.AchievementTest;
import com.seanjohnson.textfighter.Player;
import com.seanjohnson.textfighter.method.Reward;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class ChoiceTest {

	public static Player player;

	@Test
	public void testInvokingMethods() {
		player = new Player();
		player.setCoins(0);
		player.setMagic(0);

		try {
			Method method;

			/*//Requirements
			ArrayList<Requirement> requirements = new ArrayList<>();
			Requirement requirement = new Requirement(method, new ArrayList<Object>(Arrays.asList(new Object[]{true})), new ArrayList<Class>(Arrays.asList(new Class[]{Boolean.class})), player, true);
			requirements.add(requirement);*/

			//(Method method, ArrayList<Object> arguments, ArrayList<Class> argumentTypes, Object field, ArrayList<Requirement> requirements)

			//Rewards
			ArrayList<ChoiceMethod> methods = new ArrayList<>();
			method = Player.class.getMethod("gainCoins", new Class[]{int.class});
			ChoiceMethod coinReward = new ChoiceMethod(method, new ArrayList<Object>(Arrays.asList(new Object[]{10})), new ArrayList<Class>(Arrays.asList(new Class[]{int.class})), ChoiceTest.class.getField("player"), new ArrayList<>());
			method = Player.class.getMethod("gainMagic", new Class[]{int.class});
			ChoiceMethod magicReward = new ChoiceMethod(method, new ArrayList<Object>(Arrays.asList(new Object[]{20})), new ArrayList<Class>(Arrays.asList(new Class[]{int.class})), ChoiceTest.class.getField("player"), new ArrayList<>());
			methods.add(coinReward);
			methods.add(magicReward);

			Choice c = new Choice("choice", "atest", "usage", methods, new ArrayList<>(), "fail");
			//(String name, String description, String usage, ArrayList<ChoiceMethod> methods, ArrayList<Requirement> requirements, String failMessage)

			c.invokeMethods(new ArrayList<>());
			assertEquals(10, player.getCoins());
			assertEquals(20, player.getMagic());
		} catch(NoSuchMethodException | NoSuchFieldException e) {
			e.printStackTrace();
		}

	}

}