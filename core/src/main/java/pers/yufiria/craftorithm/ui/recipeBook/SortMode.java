package pers.yufiria.craftorithm.ui.recipeBook;

public enum SortMode {

    NAME_ASC,
    NAME_DESC,
    TIME_ASC,
    TIME_DESC;

    public SortMode next() {
        return switch (this) {
            case NAME_ASC -> NAME_DESC;
            case NAME_DESC -> TIME_ASC;
            case TIME_ASC -> TIME_DESC;
            case TIME_DESC -> NAME_ASC;
        };
    }

}
