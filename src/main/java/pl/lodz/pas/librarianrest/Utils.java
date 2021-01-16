package pl.lodz.pas.librarianrest;

import java.util.regex.Pattern;

public class Utils {
    public static boolean isValidUuid(String str) {

        if (str == null) return false;

        var regex = Pattern.compile("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}");

        return regex.matcher(str).matches();
    }
}
