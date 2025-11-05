package model.characters;

public class Hero extends GameCharacter {
    public Hero(int id, double x, double y, String skinPath, String name) {
        super(id, x, y, 70, 80, skinPath, name, 100);
    }
}