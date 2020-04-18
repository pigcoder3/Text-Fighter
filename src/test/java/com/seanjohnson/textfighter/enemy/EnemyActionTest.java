package com.seanjohnson.textfighter.enemy;

import com.seanjohnson.textfighter.Player;
import com.seanjohnson.textfighter.location.Choice;
import com.seanjohnson.textfighter.location.ChoiceMethod;
import com.seanjohnson.textfighter.location.ChoiceTest;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class EnemyActionTest {

	public static Player player;

	@Test
	public void testInvokingMethods() {
		player = new Player();
		player.setCoins(0);
		player.setMagic(0);

		try {
			Method method;

			//Rewards
			ArrayList<EnemyActionMethod> methods = new ArrayList<>();
			method = Player.class.getMethod("gainCoins", new Class[]{int.class});
			EnemyActionMethod coinMethod = new EnemyActionMethod(method, new ArrayList<Object>(Arrays.asList(new Object[]{10})), new ArrayList<Class>(Arrays.asList(new Class[]{int.class})), EnemyActionTest.class.getField("player"));
			method = Player.class.getMethod("gainMagic", new Class[]{int.class});
			EnemyActionMethod magicMethod = new EnemyActionMethod(method, new ArrayList<Object>(Arrays.asList(new Object[]{20})), new ArrayList<Class>(Arrays.asList(new Class[]{int.class})), EnemyActionTest.class.getField("player"));
			methods.add(coinMethod);
			methods.add(magicMethod);

			EnemyAction c = new EnemyAction(methods, new ArrayList<>());

			c.invokeMethods();

			assertEquals(10, player.getCoins());
			assertEquals(20, player.getMagic());
		} catch(NoSuchMethodException | NoSuchFieldException e) {
			e.printStackTrace();
		}

	}
}