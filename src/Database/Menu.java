package Database;

import java.sql.SQLException;
import java.util.Scanner;

public class Menu {
    SQL db;
    Scanner scan;
    public Menu(SQL db){
        this.db = db;
        scan = new Scanner(System.in);
    };

    public void menu(){
        boolean check = true;
        while(check) {
            System.out.println("=====Menu=====");
            System.out.println("Press [I] To Insert\nPress [D] To Delete\nPress [U] To Update\nPress [P] To Print all data\nPress [R] To Generate Reports\nType [Reset] To Reset All Tables\nPress [Q] To Quit");
            System.out.println("==============");
            String input = scan.next();
            switch (input) {
                case "I":
                case "i":
                    insertMenu();
                    break;
                case "D":
                case "d":
                    deleteMenu();
                    break;
                case "U":
                case "u":
                    updateMenu();
                    break;
                case "P":
                case "p":
                    printAll();
                    menu();
                    break;
                case "R":
                case "r":
                    reportMenu();
                    break;
                case "Reset":
                    db.resetTables();
                    menu();
                case "Q":
                case "q":
                    quit();
                    check = false;
                    return;
                default:
            }
        }
    }

    private void insertMenu(){
        boolean check = true;
        while(check) {
            System.out.println("=====Insert Menu=====");
            System.out.println("Press [1] To Equip a random primary weapon to specified character from Inventory");
            System.out.println("Press [2] To Equip an item to specified character from Inventory");
            System.out.println("Press [3] To add a new Armor perk to an Armor");
            System.out.println("Press [4] To add an item to a character's inventory");
            System.out.println("Press [5] To add an item to a character's storage");
            System.out.println("Press [6] To add a new account");
            System.out.println("Press [7] To add a new character");
            System.out.println("Press [8] To add a new AutoRifle");
            System.out.println("Press [9] To add a new ShotGun");
            System.out.println("Press [10] To add a new RocketLauncher");
            System.out.println("Press [11] To add a new Helmet");
            System.out.println("Press [12] To add a new Arm");
            System.out.println("Press [13] To add a new Chest");
            System.out.println("Press [14] To add a new Leg");
            System.out.println("Press [Q] To return to main menu");
            System.out.println("=====================");
            String input = scan.next();
            switch (input) {
                case "1":
                    equipRandom();
                    break;
                case "2":
                    equipItem();
                    break;
                case "3":
                    addArmorPerk();
                    break;
                case "4":
                    addItemToInventory();
                    break;
                case "5":
                    addItemToStorage();
                    break;
                case "6":
                    addNewAccount();
                    break;
                case "7":
                    addNewCharacter();
                    break;
                case "8":
                    addAutoRifle();
                    break;
                case "9":
                    addShotGun();
                    break;
                case "10":
                    addRocketLauncher();
                    break;
                case "11":
                    addHelmet();
                    break;
                case "12":
                    addArm();
                    break;
                case "13":
                    addChest();
                    break;
                case "14":
                    addLeg();
                    break;
                case "Q":
                case "q":
                    check = false;
                    return;
                default:
            }
        }
    }

    private void equipRandom(){
        //List the current characters available
        db.showTable(TableNames.Character);
        db.showTable(TableNames.Equip);
        System.out.println("Enter Character Name");
        String input = scan.next();
        try {
            db.equipRandomPrimaryFromInventory(input);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        db.showTable(TableNames.Equip);
    }

    private void equipItem(){
        //List the currentCharacter available
        db.showTable(TableNames.Character);
        db.showTable(TableNames.Stores);
        db.showTable(TableNames.Equip);
        System.out.println("Enter Character Name, Item number, and Item name");
        String characterName = scan.next();
        int num = scan.nextInt();
        String iName = scan.next();
        db.equipItemFromInventory(characterName, num, iName);
        db.showTable(TableNames.Equip);
    }

    private void addArmorPerk(){
        //List current armors available
        db.showTable(TableNames.Armor);
        System.out.println("Enter Armor name and new Perk name");
        String aName = scan.next();
        String pName = scan.next();
        db.addNewArmorPerk(aName, pName);
        db.showTable(TableNames.ArmorPerks);
    }

    private void addItemToInventory(){
        //List the current characters available
        db.showTable(TableNames.Character);
        db.showTable(TableNames.Item);
        System.out.println("Enter Character Name, number for Item, and Item Name");
        String cName = scan.next();
        int num = scan.nextInt();
        String iName = scan.next();
        db.addItemToInventory(cName, num, iName);
        db.showTable(TableNames.Stores);
    }

    private void addItemToStorage(){
        //List the current characters available
        db.showTable(TableNames.Character);
        db.showTable(TableNames.Item);
        System.out.println("Enter Character Name, number for Item, and Item Name");
        String cName = scan.next();
        int num = scan.nextInt();
        String iName = scan.next();
        db.addItemToStorage(cName, num, iName);
        db.showTable(TableNames.Stores);
    }

    private void addNewAccount(){
        System.out.println("Enter account name, account password, and email");
        String aName = scan.next();
        String pw = scan.next();
        String email = scan.next();
        db.addAccount(aName, pw, email);
        db.showTable(TableNames.Account);
    }

    private void addNewCharacter(){
        System.out.println("Existing Accounts: ");
        db.getAccountNames();
        System.out.println("Enter the account and the new character name");
        String aName = scan.next();
        String cName = scan.next();
        db.addCharacter(aName,cName);
        db.showTable(TableNames.Character);
    }

    private void addAutoRifle(){
        System.out.println("Enter a number, name of the auto rifle, damage, damage multiplier (X.XX), and mag size limit");
        int num = scan.nextInt();
        String name = scan.next();
        int damage = scan.nextInt();
        double multiplier;
        multiplier = scan.nextDouble();
        while (multiplier > 9 || multiplier <= 0){
            System.out.println("Damage multiplier have to be a double, smaller or equal to 9 and bigger than 0");
            multiplier = scan.nextDouble();
        }
        multiplier = Math.round(multiplier*100)/100;
        int magLimit = scan.nextInt();
        try {
            db.addAutoRifle(num, name, damage, multiplier, magLimit);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        db.showTable(TableNames.AutoRifle);
    }

    private void addShotGun(){
        System.out.println("Enter a number, name of the shotgun, damage, damage multiplier (X.XX), and palletSpread (int)");
        int num = scan.nextInt();
        String name = scan.next();
        int damage = scan.nextInt();
        double multiplier;
        multiplier = scan.nextDouble();
        while (multiplier > 9 || multiplier <= 0){
            System.out.println("Damage multiplier have to be a double, smaller or equal to 9 and bigger than 0");
            multiplier = scan.nextDouble();
        }
        multiplier = Math.round(multiplier*100)/100;
        int palletSpread = scan.nextInt();
        try {
            db.addShotGun(num, name, damage, multiplier, palletSpread);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        db.showTable(TableNames.Shotgun);
    }

    private void addRocketLauncher(){
        System.out.println("Enter a number, name of the rocket launcher, damage, damage multiplier (X.XX), and explosion Radius (int)");
        int num = scan.nextInt();
        String name = scan.next();
        int damage = scan.nextInt();
        double multiplier;
        multiplier = scan.nextDouble();
        while (multiplier > 9 || multiplier <= 0){
            System.out.println("Damage multiplier have to be a double, smaller or equal to 9 and bigger than 0");
            multiplier = scan.nextDouble();
        }
        multiplier = Math.round(multiplier*100)/100;
        int explosionRadius = scan.nextInt();
        try {
            db.addRocketLauncher(num, name, damage, multiplier, explosionRadius);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        db.showTable(TableNames.RocketLauncher);
    }

    private void addHelmet(){
        System.out.println("Enter a number, name of the helmet, resilience value (int), vision modifier (int)");
        int num = scan.nextInt();
        String name = scan.next();
        int resilience = scan.nextInt();
        int visionM = scan.nextInt();
        try {
            db.addHelmet(num, name, resilience,visionM);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        db.showTable(TableNames.Helmet);
    }

    private void addArm(){
        System.out.println("Enter a number, name of the arm, resilience value (int), attack modifier (int)");
        int num = scan.nextInt();
        String name = scan.next();
        int resilience = scan.nextInt();
        int attackM = scan.nextInt();
        try {
            db.addArm(num, name, resilience,attackM);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        db.showTable(TableNames.Arm);
    }

    private void addChest(){
        System.out.println("Enter a number, name of the chest, resilience value (int), health modifier (int)");
        int num = scan.nextInt();
        String name = scan.next();
        int resilience = scan.nextInt();
        int healthM = scan.nextInt();
        try {
            db.addChest(num, name, resilience,healthM);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        db.showTable(TableNames.Chest);
    }

    private void addLeg(){
        System.out.println("Enter a number, name of the leg, resilience value (int), speed modifier (int)");
        int num = scan.nextInt();
        String name = scan.next();
        int resilience = scan.nextInt();
        int speedM = scan.nextInt();
        try {
            db.addLeg(num, name, resilience,speedM);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        db.showTable(TableNames.Leg);
    }

    private void deleteMenu(){
        boolean check = true;
        while(check) {
            System.out.println("=====Delete Menu=====");
            System.out.println("Press [1] To delete a character");
            System.out.println("Press [2] To delete an item");
            System.out.println("Press [Q] To return to main menu");
            System.out.println("=====================");
            String input = scan.next();
            switch (input) {
                case "1":
                    delCharacter();
                    break;
                case "2":
                    delItem();
                    break;
                case "Q":
                case "q":
                    check = false;
                    break;
                default:
            }
        }
        menu();
    }

    private void delCharacter(){
        //List the current characters available
        db.showTable(TableNames.Character);
        System.out.println("Enter Character Name to be deleted");
        String cName = scan.next();
        db.deleteCharacter(cName);
        db.showTable(TableNames.Character);
    }

    private void delItem(){
        //List the current characters available
        db.showTable(TableNames.Item);
        System.out.println("Enter the number and item Name to be deleted");
        int num = scan.nextInt();
        String iName = scan.next();
        db.deleteItem(num ,iName);
        db.showTable(TableNames.Item);
    }

    private void updateMenu(){
        boolean check = true;
        while(check) {
            System.out.println("=====Update Menu=====");
            System.out.println("Press [1] To update the password of a specified account");
            System.out.println("Press [2] To update attack modifier for all Arms");
            System.out.println("Press [3] To update health modifier for a Chest");
            System.out.println("Press [4] To update speed modifier for Legs where it has more than a specified resilience");
            System.out.println("Press [5] To increase or decrease the damage for a weapon type");
            System.out.println("Press [6] To move item between vault and inventory");
            System.out.println("Press [Q] To return to main menu");
            System.out.println("=====================");
            String input = scan.next();
            switch (input) {
                case "1":
                    updatePW();
                    break;
                case "2":
                    updateAM();
                    break;
                case "3":
                    updateHM();
                    break;
                case "4":
                    updateSM();
                    break;
                case "5":
                    updateDamage();
                    break;
                case "6":
                    moveItemBetweenStorage();
                    break;
                case "Q":
                case "q":
                    check = false;
                    return;
            }
        }
    }

    private void updatePW(){
        //List current account available
        db.showTable(TableNames.Account);
        System.out.println("Enter the new password and the account name");
        String pw = scan.next();
        String aName = scan.next();
        db.updatePW(pw,aName);
        db.showTable(TableNames.Account);
    }

    private void updateAM(){
        System.out.println("Enter the new attack modifier for all Arms");
        int num = scan.nextInt();
        db.updateArmAttackModifier(num);
        db.showTable(TableNames.Arm);
    }

    private void updateHM(){
        //List current chests available
        db.showTable(TableNames.Chest);
        System.out.println("Enter the new health modifier and the chest armor name");
        int num = scan.nextInt();
        String iName = scan.next();
        db.updateChestHealthModifier(num, iName);
        db.showTable(TableNames.Chest);
    }

    private void updateSM(){
        System.out.println("Enter the new speed modifier and the resilience threshold");
        int speedM = scan.nextInt();
        int threshold = scan.nextInt();
        db.updateLegSpeedModifier(speedM, threshold);
        db.showTable(TableNames.Leg);
    }

    private void updateDamage(){
        boolean correctInput = false;
        String weaponType = "";
        while (!correctInput) {
            String newLineCatcher = scan.nextLine();
            System.out.println("Press [A] for auto rifle");
            System.out.println("Press [S] for shotgun");
            System.out.println("Press [R] for rocket launcher");
            String input = scan.nextLine();
            switch (input){
                case "A":
                case "a":
                    weaponType = TableNames.AutoRifle.name();
                    correctInput = true;
                    break;
                case "S":
                case "s":
                    weaponType = TableNames.Shotgun.name();
                    correctInput = true;
                    break;
                case "R":
                case "r":
                    weaponType = TableNames.RocketLauncher.name();
                    correctInput = true;
                    break;
                default:
                    correctInput = false;
            }
        }
        System.out.println("Enter the change in damage");
        int change = scan.nextInt();
        db.updateDamage(change, weaponType);
        db.showTable(TableNames.Weapon);
    }

    private void moveItemBetweenStorage(){
        db.showTable(TableNames.Stores);
        System.out.println("Enter the number and name of the item to swap between storages");
        int num = scan.nextInt();
        String name = scan.next();
        db.swapItemLocation(num, name);
        db.showTable(TableNames.Stores);
    }

    private void printAll(){
        for (TableNames table: TableNames.values()){
            db.showTable(table);
        }
    }

    private void reportMenu(){
        boolean check = true;
        while(check) {
            System.out.println("=====Report Menu=====");
            System.out.println("Press [1] To show existing accounts");
            System.out.println("Press [2] To show all primary weapons that is in a character's storage and the damage is above the given threshold");
            System.out.println("Press [3] To show a character's armor in all storages");
            System.out.println("Press [4] To show to sum of a character's armor resilience");
            System.out.println("Press [5] To list the character with more than an amount of certain items");
            System.out.println("Press [Q] To return to main menu");
            System.out.println("=====================");
            String input = scan.next();
            switch (input) {
                case "1":
                    showAccounts();
                    break;
                case "2":
                    showPrimary();
                    break;
                case "3":
                    showArmor();
                    break;
                case "4":
                    calcResilience();
                    break;
                case "5":
                    listCharacterWithItem();
                    break;
                case "Q":
                case "q":
                    check = false;
                    return;
                default:
            }
        }
    }

    private void showAccounts(){
        System.out.println("Accounts: ");
        db.getAccountNames();
    }

    private void showPrimary(){
        System.out.println("Enter the character's name and the damage threshold");
        String name = scan.next();
        int threshold = scan.nextInt();
        db.showPrimary(name, threshold);
    }

    private void showArmor(){
        System.out.println("Enter the character's name");
        String name = scan.next();
        db.showCharacterArmor(name);
    }

    private void calcResilience(){
        db.calcResilience();
    }

    private void listCharacterWithItem(){
        boolean check = true;
        while(check) {
            System.out.println("Decide which item type to check for: ");
            System.out.println("Press [1] for AutoRifle");
            System.out.println("Press [2] for ShotGun");
            System.out.println("Press [3] for RocketLauncher");
            System.out.println("Press [4] for Helmet");
            System.out.println("Press [5] for Arm");
            System.out.println("Press [6] for Chest");
            System.out.println("Press [7] for Leg");
            System.out.println("Press [8] for Weapon");
            System.out.println("Press [9] for Armor");
            int input = scan.nextInt();
            TableNames table = TableNames.Item;
            switch (input) {
                case 1:
                    table = TableNames.AutoRifle;
                    check = false;
                    break;
                case 2:
                    table = TableNames.Shotgun;
                    check = false;
                    break;
                case 3:
                    table = TableNames.RocketLauncher;
                    check = false;
                    break;
                case 4:
                    table = TableNames.Helmet;
                    check = false;
                    break;
                case 5:
                    table = TableNames.Arm;
                    check = false;
                    break;
                case 6:
                    table = TableNames.Chest;
                    check = false;
                    break;
                case 7:
                    table = TableNames.Leg;
                    check = false;
                    break;
                case 8:
                    table = TableNames.Weapon;
                    check = false;
                    break;
                case 9:
                    table = TableNames.Armor;
                    check = false;
            }
            System.out.println("Enter how much item to check for");
            int count = scan.nextInt();
            db.listCharacterWithWeapon(table,count);
        }
    }

    private void quit(){
        db.close();
    }

}
