package se233.chapter1.model.character;

import se233.chapter1.model.DamageType;

public class BattleMageCharacter extends BasedCharacter {
    public BattleMageCharacter(String name, String imgPath,int basedDef,int basedRes) {
        this.name = name;
this.type=DamageType.mage;
        this.imgpath = imgPath;
        this.fullHp = 40;
        this.hp = 40;
        this.basedPow = 40;
        this.power = 40;
        this.basedDef = basedDef; // set a default defense
        this.defense = 10;
        this.resistance = basedRes;

    }
}
