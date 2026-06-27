package pers.yufiria.craftorithm.ui.recipeBook;

import crypticlib.lang.entry.StringLangEntry;
import pers.yufiria.craftorithm.config.Languages;

public enum SortMode {

    NAME_ASC(Languages.MENU_RECIPE_BOOK_SORT_MODE_NAME_ASC),
    NAME_DESC(Languages.MENU_RECIPE_BOOK_SORT_MODE_NAME_DESC),
    TIME_ASC(Languages.MENU_RECIPE_BOOK_SORT_MODE_TIME_ASC),
    TIME_DESC(Languages.MENU_RECIPE_BOOK_SORT_MODE_TIME_DESC);

    private final StringLangEntry nameLang;

    SortMode(StringLangEntry nameLang) {
        this.nameLang = nameLang;
    }

    public StringLangEntry nameLang() {
        return nameLang;
    }

    public SortMode next() {
        return switch (this) {
            case NAME_ASC -> NAME_DESC;
            case NAME_DESC -> TIME_ASC;
            case TIME_ASC -> TIME_DESC;
            case TIME_DESC -> NAME_ASC;
        };
    }

}
