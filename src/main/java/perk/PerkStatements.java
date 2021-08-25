package perk;

public class PerkStatements {

    public static final String PERK_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `PERK` (`Id` int NOT NULL AUTO_INCREMENT PRIMARY KEY, `PerkName` text NOT NULL)";
    public static final String PERK_CREATE = "INSERT IGNORE INTO `PERK` SET (`PerkName`) = ?";
    public static final String PERK_SEARCH_SPECIFIC = "SELECT * FROM `PERK` WHERE `PerkName` = ?";

    public static final String PERK_USER_TABLE = "CREATE TABLE IF NOT EXISTS PERK_USER (Id int NOT NULL AUTO_INCREMENT PRIMARY KEY, PerkId int NOT NULL, FOREIGN KEY (PerkId) REFERENCES PERK (Id), UserId int NOT NULL, FOREIGN KEY (UserId) REFERENCES USER (Id), Activated boolean DEFAULT FALSE)";
    public static final String PERKS_SEARCH_BY_USER = "SELECT p.PerkName, pu.Activated FROM PERK_USER pu INNER JOIN perk p ON p.Id = pu.PerkId WHERE pu.UserId = (SELECT user.Id FROM user WHERE user.UUID = ?)";

    public static final String PERKS_UPDATE_ACTIVATED = "UPDATE PERK_USER SET Activated = ? WHERE PerkId = ? AND UserId = ?";
}
