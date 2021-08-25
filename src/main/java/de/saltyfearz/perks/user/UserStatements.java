package de.saltyfearz.perks.user;

public class UserStatements {

    public static final String USER_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `USER` (`Id` int NOT NULL AUTO_INCREMENT, `UUID` text NOT NULL, PRIMARY KEY(Id))";
    public static final String USER_SEARCH_SPECIFIC = "SELECT * FROM USER WHERE Id = ?";
    public static final String USER_CREATE = "REPLACE INTO `USER` SET `UUID` = ?";
}
