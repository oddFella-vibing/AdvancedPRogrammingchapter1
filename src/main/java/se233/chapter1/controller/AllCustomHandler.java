package se233.chapter1.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.StackPane;
import se233.chapter1.Launcher;
import se233.chapter1.model.character.BasedCharacter;
import se233.chapter1.model.item.Armor;
import se233.chapter1.model.item.BasedEquipment;
import se233.chapter1.model.item.Weapon;

import java.util.ArrayList;

public class AllCustomHandler {
    public static class GenCharacterHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            Launcher.setEquippedArmor(null);
            Launcher.setEquippedWeapon(null);
            Launcher.setAllEquipments(GenItemList.setUpItemList());
            Launcher.setMainCharacter(GenCharacter.setUpCharacter());
            Launcher.refreshPane();
        }

    }

    public static void onDragDetected(MouseEvent event, BasedEquipment equipment, ImageView imgView) {
        Dragboard db = imgView.startDragAndDrop(TransferMode.ANY);
        db.setDragView(imgView.getImage());
        ClipboardContent content = new ClipboardContent();
        content.put(equipment.DATA_FORMAT, equipment);
        db.setContent(content);
        event.consume();
    }

    public static void onDragOver(DragEvent event, String type) {
        Dragboard dragboard = event.getDragboard();

        BasedEquipment retrievedEquipment = (BasedEquipment)dragboard.getContent(
                BasedEquipment.DATA_FORMAT);
        if (dragboard.hasContent(BasedEquipment.DATA_FORMAT) && retrievedEquipment.
                getClass().getSimpleName().equals(type))
            event.acceptTransferModes(TransferMode.MOVE);
    }
    public static void onDragDropped(DragEvent event, Label lbl, StackPane imgGroup) {
        boolean dragCompleted = false;
        Dragboard dragboard = event.getDragboard();
        ArrayList<BasedEquipment> allEquipments=Launcher.getAllEquipments();
        if (dragboard.hasContent(BasedEquipment.DATA_FORMAT)) {
            BasedEquipment retrievedEquipment = (BasedEquipment) dragboard.getContent(
                    BasedEquipment.DATA_FORMAT);
            BasedCharacter character=Launcher.getMainCharacter();

            boolean isBattlemage = character.getClass().getSimpleName().equals("BattleMageCharacter")
                    || character.getClass().getSimpleName().equals("Battlemage");

            if (retrievedEquipment.getClass().getSimpleName().equals("Weapon")) {
                Weapon weapon = (Weapon) retrievedEquipment;

                // FIX: A Battlemage can equip ANY weapon, everyone else must match the DamageType string
                if (isBattlemage || weapon.getDamageType().toString().equals(character.getType().toString())) {
                    if (Launcher.getEquippedWeapon() != null) {
                        allEquipments.add(Launcher.getEquippedWeapon());
                    }
                    Launcher.setEquippedWeapon(weapon);
                    character.equipWeapon(weapon);
                } else {
                    // FIX: If the weapon type matches neither rule, reset it back to the available inventory list
                    allEquipments.add(retrievedEquipment);
                }
            } else {
                // FIX: Using proper string comparison, verify they are NOT a Battlemage before equipping Armor
                if (!isBattlemage) {
                    if (Launcher.getEquippedArmor() != null) {
                        allEquipments.add(Launcher.getEquippedArmor());
                    }
                    Launcher.setEquippedArmor((Armor) retrievedEquipment);
                    character.equipArmor((Armor) retrievedEquipment);
                } else {
                    // FIX: If a Battlemage tries to wear Armor, reject it and return it back to the available inventory list
                    allEquipments.add(retrievedEquipment);
                }
            }
            Launcher.setMainCharacter(character);
            Launcher.setAllEquipments(allEquipments);
            Launcher.refreshPane();
            ImageView imgView = new ImageView();
            if (imgGroup.getChildren().size() != 1) {
                imgGroup.getChildren().remove(1);
                Launcher.refreshPane();
            }
            lbl.setText(retrievedEquipment.getClass().getSimpleName() + ":\n" +
                    retrievedEquipment.getName());
            imgView.setImage(new Image(Launcher.class.getResource(retrievedEquipment.
                    getImagepath()).toString()));
            imgGroup.getChildren().add(imgView);
            dragCompleted = true;
        }
        event.setDropCompleted(true);
    }
    public static void onEquipDone(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        ArrayList<BasedEquipment> allEquipments = Launcher.getAllEquipments();
        BasedEquipment retrievedEquipment = (BasedEquipment) dragboard.getContent(BasedEquipment.DATA_FORMAT);

        int pos = -1;
        for (int i = 0; i < allEquipments.size(); i++) {
            if (allEquipments.get(i).getName().equals(retrievedEquipment.getName())) {
                pos = i;
            }
        }

        if (pos != -1) {
            allEquipments.remove(pos);
        }

        Launcher.setAllEquipments(allEquipments);
        Launcher.refreshPane();
    }
}
