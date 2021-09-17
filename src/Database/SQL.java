package Database;

import java.sql.*;

public class SQL {
    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    public String url;

    // error constants
    public static final int ER_DUP_ENTRY = 1062;
    public static final int ER_DUP_ENTRY_WITH_KEY_NAME = 1586;

    public SQL(String url) throws Exception{
        try{
            this.connect = DriverManager.getConnection(url);
            setUpDB();
        } catch (Exception e) {throw e;}
    }

    private void setUpDB(){
        try{
            statement = connect.createStatement();
            statement.executeUpdate("PRAGMA foreign_keys = ON;");
        }catch(SQLException e){
            System.err.println(e.getMessage());
        }
    }
    // deletes
    public void deleteCharacter(String cName){
        String sql = "DELETE FROM Character WHERE CharacterName = ?";
        try{
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setString(1, cName);
            // update
            preparedStatement.executeUpdate();
        }catch(SQLException e){
            System.out.println("DeleteCharacter"+e.getMessage());
        }
    }

    public void deleteItem(int num, String iName){
        String sql = "DELETE FROM Item WHERE Num = ? AND Name = ?";
        try{
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setInt(1,num);
            preparedStatement.setString(2, iName);
            // update
            preparedStatement.executeUpdate();
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    // inserts
    public void equipRandomPrimaryFromInventory(String userName) throws SQLException {
        String sql = "INSERT INTO Equip (CharacterName, ItemNum, ItemName) "+
                " SELECT S.CharacterName AS CharacterName, S.ItemNum AS ItemNum, S.ItemName AS ItemName "+
                " FROM Stores S WHERE EXISTS "+
                " (SELECT * FROM PrimaryWeapon P "+
                " WHERE S.ItemNum = P.ItemNum AND S.ItemName = P.ItemName AND S.Label = 'Inventory') " +
                " AND S.CharacterName = ? "+
                " ORDER BY RANDOM() "+
                " LIMIT 1";
        try{
            PreparedStatement statement = connect.prepareStatement(sql);
            statement.setString(1, userName);
            // update
            statement.executeUpdate();
        }catch(SQLException e) {
            switch (e.getErrorCode())
            {
                case ER_DUP_ENTRY:
                case ER_DUP_ENTRY_WITH_KEY_NAME:
                    System.out.printf("Duplicate key error: %s\n", e.getMessage());
                    break;
                default:
                    throw e;
            }
        }catch (Exception e)
        {
            throw e;
        }
    }

    public void equipItemFromInventory(String userName, int num, String itemName){
        String sql = "INSERT INTO Equip (CharacterName, ItemNum, ItemName) "+
                " SELECT S.CharacterName AS CharacterName, S.ItemNum AS ItemNum, S.ItemName AS ItemName "+
                " FROM Stores S WHERE EXISTS "+
                " (SELECT * FROM Item I "+
                " WHERE S.ItemNum = I.Num AND S.ItemName = I.Name AND S.Label = 'Inventory') " +
                " AND S.CharacterName = ? " +
                " AND S.ItemNum = ? " +
                " AND S.ItemName = ?";
        try{
            PreparedStatement statement = connect.prepareStatement(sql);
            statement.setString(1, userName);
            statement.setInt(2, num);
            statement.setString(3, itemName);
            // update
            statement.executeUpdate();
        }catch(SQLException e) {
            switch (e.getErrorCode())
            {
                case ER_DUP_ENTRY:
                case ER_DUP_ENTRY_WITH_KEY_NAME:
                    System.out.printf("Duplicate key error: %s\n", e.getMessage());
                    break;
                default:
                    System.err.println(e.getMessage());
            }
        }catch (Exception e)
        {
            throw e;
        }
    }

    public void addNewArmorPerk(String armorName, String armorPerk){
        String sql = "INSERT INTO ArmorPerks(ANum, AName, ArmorPerk) " +
                " SELECT Num, Name, \'" + armorPerk +"\'" +
                " FROM Item " +
                " WHERE Name = \'" + armorName + "\';";
        try{
            statement = connect.createStatement();
            statement.executeUpdate(sql);
        }catch(SQLException e){
            System.err.println(e.getMessage());
        }
    }

    public void addItemToInventory(String characterName, int num, String itemName){
        String sql = "INSERT INTO Stores (CharacterName, Label, ItemNum, ItemName)"+
                "Values (?,'Inventory',?,?)";
        try{
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setString(1, characterName);
            preparedStatement.setInt(2, num);
            preparedStatement.setString(3, itemName);

            preparedStatement.executeUpdate();
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public void addItemToStorage(String characterName, int num, String itemName){
        String sql = "INSERT INTO Stores (CharacterName, Label, ItemNum, ItemName)"+
                "Values (?,'Vault',?,?)";
        try{
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setString(1, characterName);
            preparedStatement.setInt(2, num);
            preparedStatement.setString(3, itemName);

            preparedStatement.executeUpdate();
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public void addAccount(String accountName, String pw, String email){
        //Add the n
        String sql = "INSERT INTO Account(AccountName, Password, Email) " +
                " Values(?, ?, ?)";
        try{
            PreparedStatement statement = connect.prepareStatement(sql);
            statement.setString(1, accountName);
            statement.setString(2, pw);
            statement.setString(3, email);

            statement.executeUpdate();
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public void addCharacter(String accountName, String characterName){
        //Add the new character
        String sql = "INSERT INTO Character(CharacterName) " +
                "SELECT ? WHERE EXISTS (" +
                "SELECT * FROM Account " +
                "WHERE AccountName = ?);";
        try{
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setString(1, characterName);
            preparedStatement.setString(2, accountName);
            preparedStatement.executeUpdate();
        }catch (SQLException e)
        {
            switch (e.getErrorCode())
            {
                case ER_DUP_ENTRY:
                case ER_DUP_ENTRY_WITH_KEY_NAME:
                    System.out.printf("Duplicate key error: %s\n", e.getMessage());
                    break;
                default:
                    System.err.println("Character Insert"+e.getMessage());
            }
        }
        catch (Exception e)
        {
            throw e;
        }
        //Make sure the character belongs to an account
        sql = "INSERT INTO Owns (AccountName, CharacterName) " +
                "SELECT ?, ? WHERE EXISTS (" +
                "SELECT * FROM Account " +
                "WHERE AccountName = ?);";
        try{
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setString(1, accountName);
            preparedStatement.setString(2, characterName);
            preparedStatement.setString(3, accountName);
            preparedStatement.executeUpdate();
        }catch (SQLException e)
        {
            switch (e.getErrorCode())
            {
                case ER_DUP_ENTRY:
                case ER_DUP_ENTRY_WITH_KEY_NAME:
                    System.out.printf("Duplicate key error: %s\n", e.getMessage());
                    break;
                default:
                    System.err.println("Owns Insert"+e.getMessage());
            }
        }
        catch (Exception e)
        {
            throw e;
        }

        //Create the Storage for character
        sql = "INSERT INTO Storage (CharacterName, Label) " +
                "SELECT ?,'Vault' WHERE EXISTS (" +
                "SELECT * FROM Account " +
                "WHERE AccountName = ?);";
        try{
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setString(1, characterName);
            preparedStatement.setString(2, accountName);
            preparedStatement.executeUpdate();
        }catch (SQLException e)
        {
            switch (e.getErrorCode())
            {
                case ER_DUP_ENTRY:
                case ER_DUP_ENTRY_WITH_KEY_NAME:
                    System.out.printf("Duplicate key error: %s\n", e.getMessage());
                    break;
                default:
                    System.err.println("Storage Insert"+e.getMessage());
            }
        }
        catch (Exception e)
        {
            throw e;
        }

        //Create the Storage for character
        sql = "INSERT INTO Storage (CharacterName, Label) " +
                "SELECT ?, 'Inventory' WHERE EXISTS (" +
                "SELECT * FROM Account " +
                "WHERE AccountName = ?);";
        try{
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setString(1, characterName);
            preparedStatement.setString(2, accountName);
            preparedStatement.executeUpdate();
        }catch (SQLException e)
        {
            switch (e.getErrorCode())
            {
                case ER_DUP_ENTRY:
                case ER_DUP_ENTRY_WITH_KEY_NAME:
                    System.out.printf("Duplicate key error: %s\n", e.getMessage());
                    break;
                default:
                    System.err.println("Storage Insert"+e.getMessage());
            }
        }
        catch (Exception e)
        {
            throw e;
        }

        //Create Inventory for character
        sql = "INSERT INTO Inventory (CharacterName, Label) " +
                "SELECT ?, 'Inventory' WHERE EXISTS (" +
                "SELECT * FROM Account " +
                "WHERE AccountName = ?);";
        try{
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setString(1, characterName);
            preparedStatement.setString(2, accountName);
            preparedStatement.executeUpdate();
        }catch (SQLException e)
        {
            switch (e.getErrorCode())
            {
                case ER_DUP_ENTRY:
                case ER_DUP_ENTRY_WITH_KEY_NAME:
                    System.out.printf("Duplicate key error: %s\n", e.getMessage());
                    break;
                default:
                    System.err.println("Inventory Insert"+e.getMessage());
            }
        }
        catch (Exception e)
        {
            throw e;
        }

        //Add Storage and Inventory to the Have table
        sql = "INSERT INTO Have (CharacterName, Label)" +
                "SELECT ?,'Vault' WHERE EXISTS (" +
                "SELECT * FROM Account " +
                "WHERE AccountName = ?);";
        try{
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setString(1, characterName);
            preparedStatement.setString(2, accountName);
            preparedStatement.executeUpdate();
        }catch (SQLException e)
        {
            switch (e.getErrorCode())
            {
                case ER_DUP_ENTRY:
                case ER_DUP_ENTRY_WITH_KEY_NAME:
                    System.out.printf("Duplicate key error: %s\n", e.getMessage());
                    break;
                default:
                    System.err.println("Have Insert"+e.getMessage());
            }
        }
        catch (Exception e)
        {
            throw e;
        }

        sql = "INSERT INTO Have (CharacterName, Label)" +
                "SELECT ?,'Inventory' WHERE EXISTS (" +
                "SELECT * FROM Account " +
                "WHERE AccountName = ?);";
        try{
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setString(1, characterName);
            preparedStatement.setString(2, accountName);
            preparedStatement.executeUpdate();
        }catch (SQLException e)
        {
            switch (e.getErrorCode())
            {
                case ER_DUP_ENTRY:
                case ER_DUP_ENTRY_WITH_KEY_NAME:
                    System.out.printf("Duplicate key error: %s\n", e.getMessage());
                    break;
                default:
                    System.err.println("Have Insert"+e.getMessage());
            }
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    public void addAutoRifle(int num, String name, int damage, double primaryDmgMultiplier, int magSizeLimit) throws SQLException {
        String sql = "INSERT INTO Item(Num, Name) Values(?, ?);";
        try{
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setInt(1, num);
            preparedStatement.setString(2, name);

            preparedStatement.executeUpdate();
        }catch(SQLException e){
            switch (e.getErrorCode())
            {
                case ER_DUP_ENTRY:
                case ER_DUP_ENTRY_WITH_KEY_NAME:
                    System.out.printf("Duplicate key error: %s\n", e.getMessage());
                    break;
                default:
                    throw e;
            }
        }catch (Exception e){throw e;}

        sql = " INSERT INTO Weapon(ItemNum, ItemName, Damage) Values(?, ?, ?);";
        try{
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setInt(1, num);
            preparedStatement.setString(2, name);
            preparedStatement.setInt(3, damage);

            preparedStatement.executeUpdate();
        }catch(SQLException e){
            switch (e.getErrorCode())
            {
                case ER_DUP_ENTRY:
                case ER_DUP_ENTRY_WITH_KEY_NAME:
                    System.out.printf("Duplicate key error: %s\n", e.getMessage());
                    break;
                default:
                    throw e;
            }
        }catch (Exception e){throw e;}

        sql = " INSERT INTO PrimaryWeapon(ItemNum, ItemName, PrimaryDmgMultiplier) Values(?, ?, ?);";
        try{
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setInt(1, num);
            preparedStatement.setString(2, name);
            preparedStatement.setDouble(3, primaryDmgMultiplier);

            preparedStatement.executeUpdate();
        }catch(SQLException e){
            switch (e.getErrorCode())
            {
                case ER_DUP_ENTRY:
                case ER_DUP_ENTRY_WITH_KEY_NAME:
                    System.out.printf("Duplicate key error: %s\n", e.getMessage());
                    break;
                default:
                    throw e;
            }
        }catch (Exception e){throw e;}

        sql = " INSERT INTO AutoRifle(ItemNum, ItemName, MagSizeLimit) Values(?, ?, ?);";
        try{
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setInt(1, num);
            preparedStatement.setString(2, name);
            preparedStatement.setInt(3, magSizeLimit);

            preparedStatement.executeUpdate();
        }catch(SQLException e){
            switch (e.getErrorCode())
            {
                case ER_DUP_ENTRY:
                case ER_DUP_ENTRY_WITH_KEY_NAME:
                    System.out.printf("Duplicate key error: %s\n", e.getMessage());
                    break;
                default:
                    throw e;
            }
        }catch (Exception e){throw e;}
    }

    public void addShotGun(int num, String name, int damage, double secondaryDmgMultiplier, int palletSpread) throws SQLException {
        String sql = "INSERT INTO Item(Num, Name) Values(?, ?);";
        try{
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setInt(1, num);
            preparedStatement.setString(2, name);

            preparedStatement.executeUpdate();
        }catch(SQLException e){
            switch (e.getErrorCode())
            {
                case ER_DUP_ENTRY:
                case ER_DUP_ENTRY_WITH_KEY_NAME:
                    System.out.printf("Duplicate key error: %s\n", e.getMessage());
                    break;
                default:
                    throw e;
            }
        }catch (Exception e){throw e;}

        sql = " INSERT INTO Weapon(ItemNum, ItemName, Damage) Values(?, ?, ?);";
        try{
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setInt(1, num);
            preparedStatement.setString(2, name);
            preparedStatement.setInt(3, damage);

            preparedStatement.executeUpdate();
        }catch(SQLException e){
            switch (e.getErrorCode())
            {
                case ER_DUP_ENTRY:
                case ER_DUP_ENTRY_WITH_KEY_NAME:
                    System.out.printf("Duplicate key error: %s\n", e.getMessage());
                    break;
                default:
                    throw e;
            }
        }catch (Exception e){throw e;}

        sql = " INSERT INTO Secondary(ItemNum, ItemName, SecondaryDmgMultiplier) Values(?, ?, ?);";
        try{
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setInt(1, num);
            preparedStatement.setString(2, name);
            preparedStatement.setDouble(3, secondaryDmgMultiplier);

            preparedStatement.executeUpdate();
        }catch(SQLException e){
            switch (e.getErrorCode())
            {
                case ER_DUP_ENTRY:
                case ER_DUP_ENTRY_WITH_KEY_NAME:
                    System.out.printf("Duplicate key error: %s\n", e.getMessage());
                    break;
                default:
                    throw e;
            }
        }catch (Exception e){throw e;}

        sql = " INSERT INTO ShotGun(ItemNum, ItemName, PalletSpread) Values(?, ?, ?);";
        try{
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setInt(1, num);
            preparedStatement.setString(2, name);
            preparedStatement.setInt(3, palletSpread);

            preparedStatement.executeUpdate();
        }catch(SQLException e){
            switch (e.getErrorCode())
            {
                case ER_DUP_ENTRY:
                case ER_DUP_ENTRY_WITH_KEY_NAME:
                    System.out.printf("Duplicate key error: %s\n", e.getMessage());
                    break;
                default:
                    throw e;
            }
        }catch (Exception e){throw e;}
    }

    public void addRocketLauncher(int num, String name, int damage, double heavyDmgMultiplier, int explosionRadius) throws SQLException {
        String sql = "INSERT INTO Item(Num, Name) Values(?, ?);";
        try{
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setInt(1, num);
            preparedStatement.setString(2, name);

            preparedStatement.executeUpdate();
        }catch(SQLException e){
            switch (e.getErrorCode())
            {
                case ER_DUP_ENTRY:
                case ER_DUP_ENTRY_WITH_KEY_NAME:
                    System.out.printf("Duplicate key error: %s\n", e.getMessage());
                    break;
                default:
                    throw e;
            }
        }catch (Exception e){throw e;}

        sql = " INSERT INTO Weapon(ItemNum, ItemName, Damage) Values(?, ?, ?);";
        try{
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setInt(1, num);
            preparedStatement.setString(2, name);
            preparedStatement.setInt(3, damage);

            preparedStatement.executeUpdate();
        }catch(SQLException e){
            switch (e.getErrorCode())
            {
                case ER_DUP_ENTRY:
                case ER_DUP_ENTRY_WITH_KEY_NAME:
                    System.out.printf("Duplicate key error: %s\n", e.getMessage());
                    break;
                default:
                    throw e;
            }
        }catch (Exception e){throw e;}

        sql = " INSERT INTO Heavy(ItemNum, ItemName, HeavyDmgMultiplier) Values(?, ?, ?);";
        try{
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setInt(1, num);
            preparedStatement.setString(2, name);
            preparedStatement.setDouble(3, heavyDmgMultiplier);

            preparedStatement.executeUpdate();
        }catch(SQLException e){
            switch (e.getErrorCode())
            {
                case ER_DUP_ENTRY:
                case ER_DUP_ENTRY_WITH_KEY_NAME:
                    System.out.printf("Duplicate key error: %s\n", e.getMessage());
                    break;
                default:
                    throw e;
            }
        }catch (Exception e){throw e;}

        sql = " INSERT INTO RocketLauncher(ItemNum, ItemName, ExplosionRadius) Values(?, ?, ?);";
        try{
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setInt(1, num);
            preparedStatement.setString(2, name);
            preparedStatement.setInt(3, explosionRadius);

            preparedStatement.executeUpdate();
        }catch(SQLException e){
            switch (e.getErrorCode())
            {
                case ER_DUP_ENTRY:
                case ER_DUP_ENTRY_WITH_KEY_NAME:
                    System.out.printf("Duplicate key error: %s\n", e.getMessage());
                    break;
                default:
                    throw e;
            }
        }catch (Exception e){throw e;}
    }

    public void addHelmet(int num, String name, int resilience, int visionModifier) throws SQLException {
        String sql = "INSERT INTO Item(Num, Name) Values(?, ?);";
        try{
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setInt(1, num);
            preparedStatement.setString(2, name);

            preparedStatement.executeUpdate();
        }catch(SQLException e){
            switch (e.getErrorCode())
            {
                case ER_DUP_ENTRY:
                case ER_DUP_ENTRY_WITH_KEY_NAME:
                    System.out.printf("Duplicate key error: %s\n", e.getMessage());
                    break;
                default:
                    throw e;
            }
        }catch (Exception e){throw e;}

        sql = " INSERT INTO Armor(ItemNum, ItemName, Resilience) Values(?, ?, ?);";
        try{
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setInt(1, num);
            preparedStatement.setString(2, name);
            preparedStatement.setInt(3, resilience);

            preparedStatement.executeUpdate();
        }catch(SQLException e){
            switch (e.getErrorCode())
            {
                case ER_DUP_ENTRY:
                case ER_DUP_ENTRY_WITH_KEY_NAME:
                    System.out.printf("Duplicate key error: %s\n", e.getMessage());
                    break;
                default:
                    throw e;
            }
        }catch (Exception e){throw e;}

        sql = " INSERT INTO Helmet(ItemNum, ItemName, VisionModifier) Values(?, ?, ?);";
        try{
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setInt(1, num);
            preparedStatement.setString(2, name);
            preparedStatement.setDouble(3, visionModifier);

            preparedStatement.executeUpdate();
        }catch(SQLException e){
            switch (e.getErrorCode())
            {
                case ER_DUP_ENTRY:
                case ER_DUP_ENTRY_WITH_KEY_NAME:
                    System.out.printf("Duplicate key error: %s\n", e.getMessage());
                    break;
                default:
                    throw e;
            }
        }catch (Exception e){throw e;}
    }

    public void addArm(int num, String name, int resilience, int attackModifier) throws SQLException {
        String sql = "INSERT INTO Item(Num, Name) Values(?, ?);";
        try{
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setInt(1, num);
            preparedStatement.setString(2, name);

            preparedStatement.executeUpdate();
        }catch(SQLException e){
            switch (e.getErrorCode())
            {
                case ER_DUP_ENTRY:
                case ER_DUP_ENTRY_WITH_KEY_NAME:
                    System.out.printf("Duplicate key error: %s\n", e.getMessage());
                    break;
                default:
                    throw e;
            }
        }catch (Exception e){throw e;}

        sql = " INSERT INTO Armor(ItemNum, ItemName, Resilience) Values(?, ?, ?);";
        try{
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setInt(1, num);
            preparedStatement.setString(2, name);
            preparedStatement.setInt(3, resilience);

            preparedStatement.executeUpdate();
        }catch(SQLException e){
            switch (e.getErrorCode())
            {
                case ER_DUP_ENTRY:
                case ER_DUP_ENTRY_WITH_KEY_NAME:
                    System.out.printf("Duplicate key error: %s\n", e.getMessage());
                    break;
                default:
                    throw e;
            }
        }catch (Exception e){throw e;}

        sql = " INSERT INTO Arm(ItemNum, ItemName, AttackModifier) Values(?, ?, ?);";
        try{
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setInt(1, num);
            preparedStatement.setString(2, name);
            preparedStatement.setDouble(3, attackModifier);

            preparedStatement.executeUpdate();
        }catch(SQLException e){
            switch (e.getErrorCode())
            {
                case ER_DUP_ENTRY:
                case ER_DUP_ENTRY_WITH_KEY_NAME:
                    System.out.printf("Duplicate key error: %s\n", e.getMessage());
                    break;
                default:
                    throw e;
            }
        }catch (Exception e){throw e;}
    }

    public void addChest(int num, String name, int resilience, int healthModifier) throws SQLException {
        String sql = "INSERT INTO Item (Num, Name) Values(?, ?);";
        try{
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setInt(1, num);
            preparedStatement.setString(2, name);

            preparedStatement.executeUpdate();
        }catch(SQLException e){
            switch (e.getErrorCode())
            {
                case ER_DUP_ENTRY:
                case ER_DUP_ENTRY_WITH_KEY_NAME:
                    System.out.printf("Duplicate key error: %s\n", e.getMessage());
                    break;
                default:
                    throw e;
            }
        }catch (Exception e){throw e;}

        sql = " INSERT INTO Armor (ItemNum, ItemName, Resilience) Values(?, ?, ?);";
        try{
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setInt(1, num);
            preparedStatement.setString(2, name);
            preparedStatement.setInt(3, resilience);

            preparedStatement.executeUpdate();
        }catch(SQLException e){
            switch (e.getErrorCode())
            {
                case ER_DUP_ENTRY:
                case ER_DUP_ENTRY_WITH_KEY_NAME:
                    System.out.printf("Duplicate key error: %s\n", e.getMessage());
                    break;
                default:
                    throw e;
            }
        }catch (Exception e){throw e;}

        sql = " INSERT INTO Chest (ItemNum, ItemName, HealthModifier) Values(?, ?, ?);";
        try{
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setInt(1, num);
            preparedStatement.setString(2, name);
            preparedStatement.setDouble(3, healthModifier);

            preparedStatement.executeUpdate();
        }catch(SQLException e){
            switch (e.getErrorCode())
            {
                case ER_DUP_ENTRY:
                case ER_DUP_ENTRY_WITH_KEY_NAME:
                    System.out.printf("Duplicate key error: %s\n", e.getMessage());
                    break;
                default:
                    throw e;
            }
        }catch (Exception e){throw e;}
    }

    public void addLeg(int num, String name, int resilience, int speedModifier) throws SQLException {
        String sql = "INSERT INTO Item(Num, Name) Values(?, ?);";
        try{
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setInt(1, num);
            preparedStatement.setString(2, name);

            preparedStatement.executeUpdate();
        }catch(SQLException e){
            switch (e.getErrorCode())
            {
                case ER_DUP_ENTRY:
                case ER_DUP_ENTRY_WITH_KEY_NAME:
                    System.out.printf("Duplicate key error: %s\n", e.getMessage());
                    break;
                default:
                    throw e;
            }
        }catch (Exception e){throw e;}

        sql = " INSERT INTO Armor(ItemNum, ItemName, Resilience) Values(?, ?, ?);";
        try{
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setInt(1, num);
            preparedStatement.setString(2, name);
            preparedStatement.setInt(3, resilience);

            preparedStatement.executeUpdate();
        }catch(SQLException e){
            switch (e.getErrorCode())
            {
                case ER_DUP_ENTRY:
                case ER_DUP_ENTRY_WITH_KEY_NAME:
                    System.out.printf("Duplicate key error: %s\n", e.getMessage());
                    break;
                default:
                    throw e;
            }
        }catch (Exception e){throw e;}

        sql = " INSERT INTO Leg (ItemNum, ItemName, SpeedModifier) Values(?, ?, ?);";
        try{
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setInt(1, num);
            preparedStatement.setString(2, name);
            preparedStatement.setDouble(3, speedModifier);

            preparedStatement.executeUpdate();
        }catch(SQLException e){
            switch (e.getErrorCode())
            {
                case ER_DUP_ENTRY:
                case ER_DUP_ENTRY_WITH_KEY_NAME:
                    System.out.printf("Duplicate key error: %s\n", e.getMessage());
                    break;
                default:
                    throw e;
            }
        }catch (Exception e){throw e;}
    }

    // updates
    public void updatePW(String newPW, String accountName){
        String sql = "UPDATE Account "+
                " SET Password = ? "+
                " WHERE AccountName = ?";
        try{
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setString(1, newPW);
            preparedStatement.setString(2, accountName);
            // update
            preparedStatement.executeUpdate();
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public void updateArmAttackModifier(int newVal){
        String sql = "UPDATE Arm SET AttackModifier = ?";
        try{
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setInt(1,newVal);

            preparedStatement.executeUpdate();
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public void updateChestHealthModifier(int newVal, String itemName){
        String sql = "UPDATE Chest SET HealthModifier = ? "+
                " WHERE ItemName = ?";
        try{
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setInt(1,newVal);
            preparedStatement.setString(2,itemName);

            preparedStatement.executeUpdate();
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public void updateLegSpeedModifier(int newVal, int threshHold){
        String sql = "UPDATE Leg " +
                "SET SpeedModifier = ?" +
                "WHERE EXISTS (" +
                "SELECT *" +
                "FROM Armor A " +
                "WHERE " +
                "Leg.ItemNum = A.ItemNum " +
                "AND Leg.ItemName = A.ItemName " +
                "AND A.Resilience > ?);";
        try{
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setInt(1,newVal);
            preparedStatement.setInt(2,threshHold);

            preparedStatement.executeUpdate();
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public void updateDamage(int addDamage, String weaponType){
        String sql = "UPDATE Weapon SET Damage = Damage + "+addDamage+
                " WHERE EXISTS "+
                " (SELECT * FROM "+weaponType+" A"+
                " WHERE Weapon.ItemNum = A.ItemNum AND Weapon.ItemName = A.ItemName)";
        try{
            statement = connect.createStatement();
            statement.executeUpdate(sql);
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public void swapItemLocation(int itemNum, String itemName){
        String sql = "UPDATE Stores " +
                "SET Label = CASE " +
                "WHEN Label = 'Vault' THEN 'Inventory' " +
                "ELSE 'Vault' " +
                "END " +
                "WHERE ItemNum = ? " +
                "AND ItemName = ?;";
        try{
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setInt(1,itemNum);
            preparedStatement.setString(2,itemName);

            preparedStatement.executeUpdate();
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    // generate reports
    public void getAccountNames(){
        String sql = "SELECT AccountName FROM Account;";
        try{
            statement = connect.createStatement();
            resultSet = statement.executeQuery(sql);
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            //Iterate through the data in the result set and display it
            while(resultSet.next()){
                //Print the row
                for(int i = 1; i <= columnsNumber; i++)
                    System.out.println(resultSet.getString(i) + " ");
                System.out.println();
            }

        }catch(SQLException e){
            System.err.println(e.getMessage());
        }
    }

    public void showPrimary(String characterName, int damageValue){
        String sql = "SELECT P.ItemNum, P.ItemName " +
                " FROM PrimaryWeapon P " +
                " INNER JOIN Stores S ON P.ItemNum = S.ItemNum AND P.ItemName = S.ItemName " +
                " INNER JOIN Weapon W ON P.ItemNum = W.ItemNum AND P.ItemName = W.ItemName " +
                " WHERE W.Damage > ? AND S.CharacterName = ?;";
        try{
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setInt(1, damageValue);
            preparedStatement.setString(2, characterName);
            resultSet = preparedStatement.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            System.out.println("Primary Weapons in " + characterName + "'s Storage\nwhich damage > " + damageValue + ":");
            while(resultSet.next()){
                //Print the row
                for(int i = 1; i <= columnsNumber; i++)
                    System.out.print(resultSet.getString(i) + " ");
                System.out.println();
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public void showCharacterArmor(String characterName){
        String sql = "SELECT A.ItemNum, A.ItemName " +
                "FROM Armor A " +
                "WHERE EXISTS ( " +
                "SELECT * " +
                "FROM Stores S " +
                "WHERE S.ItemNum = A.ItemNum " +
                "AND S.ItemName = A.ItemName " +
                "AND S.CharacterName = ?);";
        try{
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setString(1, characterName);
            resultSet = preparedStatement.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            System.out.println(characterName + "'s Armor: ");
            while(resultSet.next()){
                //Print the row
                for(int i = 1; i <= columnsNumber; i++)
                    System.out.print(resultSet.getString(i) + " ");
                System.out.println();
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public void calcResilience(){
        String sql = "SELECT E.CharacterName, SUM(A.Resilience) " +
                "FROM Equip E, Armor A " +
                "WHERE E.ItemNum = A.ItemNum AND E.ItemName = A.ItemName " +
                "GROUP BY CharacterName";
        try{
            statement = connect.createStatement();
            resultSet = statement.executeQuery(sql);
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            System.out.println("Resilience: ");
            while(resultSet.next()){
                //Print the row
                for(int i = 1; i <= columnsNumber; i++)
                    System.out.print(resultSet.getString(i) + " ");
                System.out.println();
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public void listCharacterWithWeapon( TableNames table, int count){
        String sql = "SELECT CharacterName, COUNT(CharacterName) " +
                "FROM Stores S " +
                "WHERE EXISTS ( " +
                "SELECT * " +
                "FROM "+ table.name() +" R " +
                "WHERE S.ItemName = R.ItemName " +
                ") " +
                "GROUP BY CharacterName " +
                "HAVING COUNT (CharacterName) > " + count +";";
        try{
            statement = connect.createStatement();
            resultSet = statement.executeQuery(sql);
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            System.out.println("Characters with more than " + count + " " + table.name() + "[Name][Count]: ");
            while(resultSet.next()){
                //Print the row
                for(int i = 1; i <= columnsNumber; i++)
                    System.out.print(resultSet.getString(i) + " ");
                System.out.println();
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    // Display table
    public void showTable(TableNames tableName){
        String sql = "SELECT * FROM " + tableName.name() + ";";
        try{
            statement = connect.createStatement();
            resultSet = statement.executeQuery(sql);
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            System.out.println("===" + tableName.name() + "===");
            //Iterate through the data in the result set and display it
            while(resultSet.next()){
                //Print the row
                for(int i = 1; i <= columnsNumber; i++)
                    System.out.print(resultSet.getString(i) + " ");
                System.out.println();
            }
            System.out.println("=================\n");
        }catch(SQLException e){
            System.err.println(e.getMessage());
        }
    }

    // Create the tables if not exists
    public void createTables() throws SQLException{
        System.out.println("Creating tables if table does not exists in given database...");

        String sql;
        try {
            statement = connect.createStatement();

            sql = "CREATE TABLE IF NOT EXISTS Account " +
                    "(AccountName VARCHAR(20), "+
                    " Password VARCHAR(10) NOT NULL, "+
                    " Email VARCHAR(30), "+
                    " PRIMARY KEY(AccountName))";
            statement.executeUpdate(sql);
            //System.out.println("Created table Account in given database...");

            sql = "CREATE TABLE IF NOT EXISTS Character "+
                    "(CharacterName VARCHAR(20), "+
                    " PRIMARY KEY(CharacterName))";
            statement.executeUpdate(sql);
            //System.out.println("Created table Character in given database...");

            sql = "CREATE TABLE IF NOT EXISTS Owns"+
                    "(AccountName VARCHAR(20), "+
                    " CharacterName VARCHAR(20), "+
                    " FOREIGN KEY (AccountName) REFERENCES Account (AccountName) ON DELETE CASCADE, "+
                    " FOREIGN KEY (CharacterName) REFERENCES Character(CharacterName) ON DELETE CASCADE)";
            statement.executeUpdate(sql);
            //System.out.println("Created table Owns in given database...");

            sql = "CREATE TABLE IF NOT EXISTS Equip "+
                    "(CharacterName VARCHAR(20), "+
                    " ItemNum INT, "+
                    " ItemName VARCHAR(20), "+
                    " FOREIGN KEY (CharacterName) REFERENCES Character (CharacterName) ON DELETE CASCADE, "+
                    " FOREIGN KEY (ItemNum, ItemName) REFERENCES Item (Num, Name) ON DELETE CASCADE)";
            statement.executeUpdate(sql);
            //System.out.println("Created table Equip in given database...");

            sql = "CREATE TABLE IF NOT EXISTS Storage "+
                    "(CharacterName VARCHAR(20), "+
                    " Label VARCHAR(10) NOT NULL, "+
                    " FOREIGN KEY (CharacterName) REFERENCES Character (CharacterName) ON DELETE CASCADE, "+
                    " PRIMARY KEY (CharacterName, Label))";
            statement.executeUpdate(sql);
            //System.out.println("Created table Storage in given database...");

            sql = "CREATE TABLE IF NOT EXISTS Have "+
                    "(CharacterName VARCHAR(20), "+
                    " Label VARCHAR(10), "+
                    " FOREIGN KEY (CharacterName, Label) REFERENCES Storage (CharacterName, Label) ON DELETE CASCADE);";
            statement.executeUpdate(sql);
            //System.out.println("Created table Have in given database...");

            sql = "CREATE TABLE IF NOT EXISTS Stores "+
                    "(CharacterName VARCHAR(20), "+
                    " Label VARCHAR(10), "+
                    " ItemNum INT, "+
                    " ItemName VARCHAR(20), "+
                    " FOREIGN KEY (CharacterName, Label) REFERENCES Storage (CharacterName, Label) ON DELETE CASCADE, "+
                    " FOREIGN KEY (ItemNum, ItemName) REFERENCES Item (Num, Name) ON DELETE CASCADE)";
            statement.executeUpdate(sql);
            //System.out.println("Created table Stores in given database...");

            sql = "CREATE TABLE IF NOT EXISTS Inventory "+
                    "(CharacterName VARCHAR(20), "+
                    " Label VARCHAR(10) NOT NULL, "+
                    " size INTEGER default (63) NOT NULL, "+
                    " FOREIGN KEY (CharacterName, Label) REFERENCES Storage (CharacterName, Label) ON DELETE CASCADE, "+
                    " PRIMARY KEY (CharacterName, Label), " +
                    " CHECK(size > 0))";

            statement.executeUpdate(sql);
            //System.out.println("Created table Inventory in given database...");

            sql = "CREATE TABLE IF NOT EXISTS Item "+
                    "(Num INT NOT NULL, "+
                    " Name VARCHAR(20) NOT NULL, "+
                    " PRIMARY KEY(Num, Name))";
            statement.executeUpdate(sql);
            //System.out.println("Created table Item in given database...");

            sql = "CREATE TABLE IF NOT EXISTS Armor( "+
                    "ItemNum INT, "+
                    "ItemName VARCHAR(20), "+
                    "Resilience INT, "+
                    "FOREIGN KEY (ItemNum, ItemName) REFERENCES Item (Num, Name) ON DELETE CASCADE);";
            statement.executeUpdate(sql);
            //System.out.println("Created table Armor in given database...");

            sql = "CREATE TABLE IF NOT EXISTS ArmorPerks "+
                    "(ANum INT, "+
                    " AName VARCHAR(20), "+
                    " ArmorPerk VARCHAR(20), "+
                    " FOREIGN KEY (ANum, AName) REFERENCES Item (Num, Name) ON DELETE CASCADE)";
            statement.executeUpdate(sql);
            //System.out.println("Created table ArmorPerks in given database...");

            sql = "CREATE TABLE IF NOT EXISTS Helmet "+
                    "(ItemNum INT, "+
                    " ItemName VARCHAR(20), "+
                    " VisionModifier INT, "+
                    " FOREIGN KEY (ItemNum, ItemName) REFERENCES Item (Num, Name) ON DELETE CASCADE);";
            statement.executeUpdate(sql);
            //System.out.println("Created table Helmet in given database...");

            sql = "CREATE TABLE IF NOT EXISTS Arm( " +
                    "ItemNum INT, " +
                    "ItemName VARCHAR(20), " +
                    "AttackModifier INT, " +
                    "FOREIGN KEY (ItemNum, ItemName) REFERENCES Item (Num, Name) ON DELETE CASCADE);";


            statement.executeUpdate(sql);
            //System.out.println("Created table Arm in given database...");

            sql = "CREATE TABLE IF NOT EXISTS Chest "+
                    "(ItemNum INT, "+
                    " ItemName VARCHAR(20), "+
                    " HealthModifier INT, "+
                    " CONSTRAINT 'FKConstraint' FOREIGN KEY (ItemNum, ItemName) REFERENCES Item (Num, Name) ON DELETE CASCADE);";
            statement.executeUpdate(sql);
            //System.out.println("Created table Chest in given database...");

            sql = "CREATE TABLE IF NOT EXISTS Leg "+
                    "(ItemNum INT, "+
                    " ItemName VARCHAR(20), "+
                    " SpeedModifier INT, "+
                    " CONSTRAINT 'FKConstraint' FOREIGN KEY (ItemNum, ItemName) REFERENCES Item (Num, Name) ON DELETE CASCADE);";
            statement.executeUpdate(sql);
            //System.out.println("Created table Leg in given database...");

            sql = "CREATE TABLE IF NOT EXISTS Weapon "+
                    " (ItemNum INT, "+
                    " ItemName VARCHAR(20), "+
                    " Damage INT, "+
                    " CONSTRAINT 'FKConstraint' FOREIGN KEY (ItemNum, ItemName) REFERENCES Item (Num, Name) ON DELETE CASCADE);";
            statement.executeUpdate(sql);
            //System.out.println("Created table Weapon in given database...");

            sql = "CREATE TABLE IF NOT EXISTS WeaponPerks "+
                    "(WNum INT, "+
                    " WName VARCHAR(20), "+
                    " WeaponPerk VARCHAR(20), "+
                    " CONSTRAINT 'FKConstraint' FOREIGN KEY (WNum, WName) REFERENCES Item (Num, Name) ON DELETE CASCADE)";
            statement.executeUpdate(sql);
            //System.out.println("Created table WeaponPerks in given database...");

            sql = "CREATE TABLE IF NOT EXISTS PrimaryWeapon "+
                    "(ItemNum INT, "+
                    " ItemName VARCHAR(20), "+
                    " PrimaryDmgMultiplier NUMERIC(3,2), "+
                    " CONSTRAINT 'FKConstraint' FOREIGN KEY (ItemNum, ItemName) REFERENCES Item (Num, Name) ON DELETE CASCADE);";
            statement.executeUpdate(sql);
            //System.out.println("Created table Primary in given database...");

            sql = "CREATE TABLE IF NOT EXISTS Secondary "+
                    "(ItemNum INT, "+
                    " ItemName VARCHAR(20), "+
                    " SecondaryDmgMultiplier INT, "+
                    " CONSTRAINT 'FKConstraint' FOREIGN KEY (ItemNum, ItemName) REFERENCES Item (Num, Name) ON DELETE CASCADE);";
            statement.executeUpdate(sql);
            //System.out.println("Created table Secondary in given database...");

            sql = "CREATE TABLE IF NOT EXISTS Heavy "+
                    "(ItemNum INT, "+
                    " ItemName VARCHAR(20), "+
                    " HeavyDmgMultiplier INT, "+
                    " CONSTRAINT 'FKConstraint' FOREIGN KEY (ItemNum, ItemName) REFERENCES Item (Num, Name) ON DELETE CASCADE);";
            statement.executeUpdate(sql);
            //System.out.println("Created table Heavy in given database...");

            sql = "CREATE TABLE IF NOT EXISTS AutoRifle "+
                    "(ItemNum INT, "+
                    " ItemName VARCHAR(20), "+
                    " MagSizeLimit INTEGER NOT NULL, "+
                    " CONSTRAINT 'FKConstraint' FOREIGN KEY (ItemNum, ItemName) REFERENCES Item (Num, Name) ON DELETE CASCADE, "+
                    " CHECK (MagSizeLimit > 0))";
            statement.executeUpdate(sql);
            //System.out.println("Created table AutoRifle in given database...");

            sql = "CREATE TABLE IF NOT EXISTS Shotgun "+
                    "(ItemNum INT, "+
                    " ItemName VARCHAR(20), "+
                    " PalletSpread INT, "+
                    " CONSTRAINT 'FKConstraint' FOREIGN KEY (ItemNum, ItemName) REFERENCES Item (Num, Name) ON DELETE CASCADE)";
            statement.executeUpdate(sql);
            //System.out.println("Created table Shotgun in given database...");

            sql = "CREATE TABLE IF NOT EXISTS RocketLauncher "+
                    "(ItemNum INT, "+
                    " ItemName VARCHAR(20), "+
                    " ExplosionRadius INT, "+
                    " CONSTRAINT 'FKConstraint' FOREIGN KEY (ItemNum, ItemName) REFERENCES Item (Num, Name) ON DELETE CASCADE)";
            statement.executeUpdate(sql);
            //System.out.println("Created table RocketLauncher in given database...");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void resetTables(){
        for(TableNames table: TableNames.values()){
            dropTable(table);
        }
        try {
            createTables();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void dropTable(TableNames table){
        String sql = "DROP TABLE " + table.name() + ";";
        try{
            statement = connect.createStatement();
            statement.executeUpdate(sql);
        }catch(SQLException e){
            System.err.println(e.getMessage());
        }
    }

    public void close(){
        try{
            if (resultSet != null)
                resultSet.close();
            if (statement != null)
                statement.close();
            if (connect != null)
                connect.close();
        } catch (Exception e){
            System.err.println(e.getMessage());
        }
    }

}