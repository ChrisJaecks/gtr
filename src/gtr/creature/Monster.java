package gtr.creature;

import gtr.item.weapon.Weapon;
import gtr.item.weapon.Weapon.Range;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import jade.core.World;
import jade.util.Dice;
import jade.util.datatype.ColoredChar;
import jade.util.datatype.Direction;
import rogue.creature.Creature;
import rogue.creature.Player;

import java.awt.Color;

/**
 * Monster class creates a generic dumb monster
 * 
 * 
 * @author mxst
 */
public class Monster extends Creature {
	

	public final static ArrayList<?> monsterList = gtr.util.ReadFile
			.readYamlArrayList("res/monsters/monsters.yml");
	public final static HashMap<?,?> monsterWeaponList = gtr.util.ReadFile
			.readYamlHashMap("res/monsters/weapons.yml");
	
	private String name;
	private Movement movement;
	private Weapon weapon;
	private String dropRareness;
	private String dropType;
	
	/**
	 * finds (randomized) monster with matching face
	 * @param face
	 * @return a HashMap to generate a monster
	 */
	private HashMap<?,?> findMonsterByChar(char face){
		ArrayList<HashMap<?, ?>> possible_monsters = new ArrayList<HashMap<?, ?>>();
		for (int i=0; i<monsterList.size();i++) {
			if ( ((String) HashMap.class.cast(monsterList.get(i)).get("face")).charAt(0) == face ){
				// adds to possible monsters
				possible_monsters.add(HashMap.class.cast(monsterList.get(i)));
			}
		}
		Random randomGenerator = new Random();
		return possible_monsters.get(randomGenerator.nextInt(possible_monsters.size()));
	}
	
	public Monster(char face) {
		this(ColoredChar.create(face));
		// TODO Auto-generated constructor stub
	}
	
	public Monster() {
		this(ColoredChar.create('J', new Color(0,200,0)));
		// TODO Auto-generated constructor stub
	}
	
	// if its not a coloredChar
	public Monster(ColoredChar face) {
		super(face);
		HashMap<?,?> monster = findMonsterByChar(face.ch());
		
		name = (String) monster.get("name");
		movement = new Movement((HashMap<?, ?>) monster.get("movement"));
		
		
		Random randomGenerator = new Random();
		// gives one of the possible weapons to the mob
		ArrayList<?> possible_weapons = (ArrayList<?>) monster.get("weapons"); //liste mit waffenbezeichnungen
		weapon = new Weapon((HashMap<?,?>) monsterWeaponList
			.get((String) possible_weapons
					.get(randomGenerator.nextInt(possible_weapons.size()))
			)
		);
		// easy to access drop type
		dropType = weapon.getType();
		dropRareness = (String) monster.get("drops");
	}

	@Override
	
	//TODO Schadensberechnung
	public void act() {
		move(Dice.global.choose(Arrays.asList(Direction.values())));
		Direction dir = findPlayerInRange();
		if(dir != null){ //angreifbar
			attack(dir, weapon.getRange(), calcDamage());
		}
		
	}
	
	public Direction findPlayerInRange(){
		Range range = weapon.getRange();
		for(int i=range.getFrom();i<=range.getTo();i++){
			for(Direction dir: Direction.values()){
				try {
					if(world.getActorAt(Player.class, x()+dir.dx()*i,y()+dir.dy()*i) != null){
						return dir;
					}
				} catch(ArrayIndexOutOfBoundsException e){
					
				}
				
			}
		}
		return null;
		
	}

	public String getName() {
		return name;
	}

	public int getMove() {
		return movement.getMove(1);
	}
	
	//TODO rareness des drops einstellen
	public Weapon drop(){
		return new Weapon(dropType);
		
		//TODO not implemented in weapon.class
		// return new Weapon(dropType, dropRareness);
	}
	
	//TODO Damage muss noch errechnet werde
	public float calcDamage(){
		return 1.0F;
	}
}