package com.seanjohnson.textfighter.display;

import com.seanjohnson.textfighter.Player;
import com.seanjohnson.textfighter.TextFighter;
import com.seanjohnson.textfighter.location.Choice;
import com.seanjohnson.textfighter.location.Location;
import org.junit.Test;
import org.w3c.dom.Text;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class GraphicalInterfaceTest {

	public static Player player = new Player();
	public static Location testLocation;

	@Test
	public void autoCompleteWithOnePossible() {
		//Setup our location
		ArrayList<Choice> choices = new ArrayList<>();
		choices.add(new Choice("testChoice1", "", "", new ArrayList<>(), new ArrayList<>(), ""));
		testLocation = new Location("location", new ArrayList<>(), choices, new ArrayList<>(), new ArrayList<>());
		TextFighter.locations.add(testLocation);
		player.setLocation("location");
		TextFighter.player = player;

		//Test Cases
		assertEquals("testChoice1 ", GraphicalInterface.autoComplete("test"));
		assertNotEquals("testChoice1 ", GraphicalInterface.autoComplete("tespdaf"));

		//Teardown
		testLocation = null;
		TextFighter.player = null;
		TextFighter.locations = new ArrayList<>();
	}

	@Test
	public void autoCompleteWithMultiplePossible() {
		//Setup our location
		ArrayList<Choice> choices = new ArrayList<>();
		choices.add(new Choice("testChoice1", "", "", new ArrayList<>(), new ArrayList<>(), ""));
		choices.add(new Choice("testChoice2", "", "", new ArrayList<>(), new ArrayList<>(), ""));
		testLocation = new Location("location", new ArrayList<>(), choices, new ArrayList<>(), new ArrayList<>());
		TextFighter.locations.add(testLocation);
		player.setLocation("location");
		TextFighter.player = player;

		//Test cases
		assertEquals("testChoice", GraphicalInterface.autoComplete("test"));
		assertNotEquals("testChoice1 ", GraphicalInterface.autoComplete("tespdaf"));

		//Teardown
		testLocation = null;
		TextFighter.player = null;
		TextFighter.locations = new ArrayList<>();
	}

	@Test
	public void autoCompleteWithNoPossible() {
		//Setup our location
		ArrayList<Choice> choices = new ArrayList<>();
		choices.add(new Choice("testChoice1", "", "", new ArrayList<>(), new ArrayList<>(), ""));
		choices.add(new Choice("testChoice2", "", "", new ArrayList<>(), new ArrayList<>(), ""));
		testLocation = new Location("location", new ArrayList<>(), choices, new ArrayList<>(), new ArrayList<>());
		TextFighter.locations.add(testLocation);
		player.setLocation("location");
		TextFighter.player = player;

		//Test Cases
		assertEquals("tesasdfas", GraphicalInterface.autoComplete("tesasdfas"));
		assertNotEquals("testasdf ", GraphicalInterface.autoComplete("testasdf"));
		assertNotEquals("testChoice1 ", GraphicalInterface.autoComplete("testasdf"));

		//Teardown
		testLocation = null;
		TextFighter.player = null;
		TextFighter.locations = new ArrayList<>();
	}


}