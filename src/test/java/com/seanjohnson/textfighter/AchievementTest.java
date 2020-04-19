package com.seanjohnson.textfighter;

import com.seanjohnson.textfighter.method.Requirement;
import com.seanjohnson.textfighter.method.Reward;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class AchievementTest {

	public static Player player;

	@Test
	public void testInvokingRewards() {
		player = new Player();
		player.setCoins(0);
		player.setMagic(0);

		try {
			Method method;

			/*//Requirements
			ArrayList<Requirement> requirements = new ArrayList<>();
			Requirement requirement = new Requirement(method, new ArrayList<Object>(Arrays.asList(new Object[]{true})), new ArrayList<Class>(Arrays.asList(new Class[]{Boolean.class})), player, true);
			requirements.add(requirement);*/

			//Rewards
			ArrayList<Reward> rewards = new ArrayList<>();
			method = Player.class.getMethod("gainCoins", new Class[]{int.class});
			Reward coinReward = new Reward(method, new ArrayList<Object>(Arrays.asList(new Object[]{10})), new ArrayList<Class>(Arrays.asList(new Class[]{int.class})), AchievementTest.class.getField("player"), new ArrayList<>(), 100, "10 coins");
			method = Player.class.getMethod("gainMagic", new Class[]{int.class});
			Reward magicReward = new Reward(method, new ArrayList<Object>(Arrays.asList(new Object[]{20})), new ArrayList<Class>(Arrays.asList(new Class[]{int.class})), AchievementTest.class.getField("player"), new ArrayList<>(), 100, "20 magic");
			rewards.add(coinReward);
			rewards.add(magicReward);

			// Give the player 10 coins and 20 magic points
			Achievement ach = new Achievement("testAchievement", "gives the player 10 coins and 20 magic points", new ArrayList<>(), rewards);

			ach.invokeRewardMethods();
			assertEquals(10, player.getCoins());
			assertEquals(20, player.getMagic());
		} catch(NoSuchMethodException | NoSuchFieldException e) {
			e.printStackTrace();
		}

	}

}