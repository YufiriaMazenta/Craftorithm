package top.oasismc.oasisrecipe.script.util;

public class ValueUtil {

    public static boolean compare(int var1, int var2, String type) {
        type = type.replaceAll("\\s", "");
        switch (type) {
            case ">":
                return var1 > var2;
            case "<":
                return var1 < var2;
            case ">=":
                return var1 >= var2;
            case "<=":
                return var1 <= var2;
            case "=":
                return var1 == var2;
            case "!=":
                return var1 != var2;
            default:
                throw new IllegalArgumentException(type + " comparison method does not exist");
        }
    }

    public static boolean compare(double var1, double var2, String type) {
        type = type.replaceAll("\\s", "");
        switch (type) {
            case ">":
                return var1 > var2;
            case "<":
                return var1 < var2;
            case ">=":
                return var1 >= var2;
            case "<=":
                return var1 <= var2;
            case "=":
                return var1 == var2;
            case "!=":
                return var1 != var2;
            default:
                throw new IllegalArgumentException(type + " comparison method does not exist");
        }
    }

    public static boolean compare(long var1, long var2, String type) {
        type = type.replaceAll("\\s", "");
        switch (type) {
            case ">":
                return var1 > var2;
            case "<":
                return var1 < var2;
            case ">=":
                return var1 >= var2;
            case "<=":
                return var1 <= var2;
            case "=":
                return var1 == var2;
            case "!=":
                return var1 != var2;
            default:
                throw new IllegalArgumentException(type + " comparison method does not exist");
        }
    }

}
