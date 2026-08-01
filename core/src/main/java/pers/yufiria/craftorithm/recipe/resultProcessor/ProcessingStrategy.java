package pers.yufiria.craftorithm.recipe.resultProcessor;

public enum ProcessingStrategy {

    COPY_FROM_SOURCE,
    ADD,
    MERGE_SOURCE,
    REMOVE;

    public static ProcessingStrategy fromString(String str) {
        return switch (str.toLowerCase()) {
            case "copy_from_source" -> COPY_FROM_SOURCE;
            case "add" -> ADD;
            case "merge_source" -> MERGE_SOURCE;
            case "remove" -> REMOVE;
            default -> throw new IllegalArgumentException("Unknown processing strategy: " + str);
        };
    }

}
