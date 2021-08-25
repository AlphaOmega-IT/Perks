package de.saltyfearz.perks.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Constants {

    public static String PREFIX = "§e§lCitybuild §f» ";
    public static String EMPTY = "";
    public static String NO_PERKS = "§cDu hast leider noch keine Perks aus den Kisten gezogen. Gehe in den Shop um dir Kisten zu kaufen. §ehttps://iostein.net/Kisten/";

    public static String INFORMATION_CB_LINE_ONE = "§aPerks bieten dir verschiedene Vorteile in Citybuild.";
    public static String INFORMATION_CB_LINE_TWO = "§aSie können dir in unterschiedlichen Situationen das Spielgeschehen vereinfachen.";
    public static String INFORMATION_CB_LINE_THREE = "§aDu kannst insgesamt 3 Perks gleichzeitig aktivieren.";

    public static String PAGE_1_BUTTON = "§fSeite 1";
    public static String PAGE_2_BUTTON = "§fSeite 2";

    public static String INV_TITLE = "§b§lPerks";

    public static Integer PAGE_1 = 1;
    public static Integer PAGE_2 = 2;

    public static String DEACTIVATE = "§7Deaktiviert";
    public static String ACTIVATE = "§aAktiviert";
    public static String NOT_UNLOCKED = "§cNicht freigeschaltet";

    public static Boolean DEACTIVATED = false;
    public static Boolean ACTIVATED = true;

    public static String NOT_UNLOCKED_PERK_YET = "§cDu hast das Perk (?) noch nicht freigeschaltet.";

    public static String PAGE_1_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjUzNDc0MjNlZTU1ZGFhNzkyMzY2OGZjYTg1ODE5ODVmZjUzODlhNDU0MzUzMjFlZmFkNTM3YWYyM2QifX19";
    public static String PAGE_2_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGVmMzU2YWQyYWE3YjE2NzhhZWNiODgyOTBlNWZhNWEzNDI3ZTVlNDU2ZmY0MmZiNTE1NjkwYzY3NTE3YjgifX19";

    public static List<String> loreInformation = new ArrayList<>(Arrays.asList(PREFIX + INFORMATION_CB_LINE_ONE, INFORMATION_CB_LINE_TWO, INFORMATION_CB_LINE_THREE));

}
