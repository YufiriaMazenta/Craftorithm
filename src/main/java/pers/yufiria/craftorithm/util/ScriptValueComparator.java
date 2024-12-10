package pers.yufiria.craftorithm.util;

public class ScriptValueComparator {

    public static boolean compare(int var1, int var2, String type) {
        type = type.replaceAll("\\s", "");
        return switch (type) {
            case ">" -> var1 > var2;
            case "<" -> var1 < var2;
            case ">=" -> var1 >= var2;
            case "<=" -> var1 <= var2;
            case "=", "==" -> var1 == var2;
            case "!=" -> var1 != var2;
            default -> throw new IllegalArgumentException(type + " comparison method does not exist");
        };
    }

    public static boolean compare(double var1, double var2, String type) {
        type = type.replaceAll("\\s", "");
        return switch (type) {
            case ">" -> var1 > var2;
            case "<" -> var1 < var2;
            case ">=" -> var1 >= var2;
            case "<=" -> var1 <= var2;
            case "=", "==" -> var1 == var2;
            case "!=" -> var1 != var2;
            default -> throw new IllegalArgumentException(type + " comparison method does not exist");
        };
    }

    public static boolean compare(long var1, long var2, String type) {
        type = type.replaceAll("\\s", "");
        return switch (type) {
            case ">" -> var1 > var2;
            case "<" -> var1 < var2;
            case ">=" -> var1 >= var2;
            case "<=" -> var1 <= var2;
            case "=", "==" -> var1 == var2;
            case "!=" -> var1 != var2;
            default -> throw new IllegalArgumentException(type + " comparison method does not exist");
        };
    }

    public static boolean compare(String var1, String var2, String type) {
        Number var1Num, var2Num;
        boolean result;
        try {
            if (var1.contains(".")) {
                var1Num = Double.parseDouble(var1);
                var2Num = Double.parseDouble(var2);
                result = ScriptValueComparator.compare((Double) var1Num, (Double) var2Num, type);
            } else {
                var1Num = Integer.parseInt(var1);
                var2Num = Integer.parseInt(var2);
                result = ScriptValueComparator.compare((Integer) var1Num, (Integer) var2Num, type);
            }
            return result;
        } catch (NumberFormatException e) {
            return switch (type) {
                case ">" -> var1.length() > var2.length();
                case "<" -> var1.length() < var2.length();
                case ">=" -> var1.length() >= var2.length();
                case "<=" -> var1.length() <= var2.length();
                case "=", "==" -> var1.equals(var2);
                case "!=" -> !var1.equals(var2);
                case "has" -> var1.contains(var2);
                case "in" -> var2.contains(var1);
                default -> throw new IllegalArgumentException(type + " comparison method does not exist");
            };
        }
    }

}
