package pers.yufiria.craftorithm.config;

import crypticlib.lang.LangHandler;
import crypticlib.lang.entry.StringLangEntry;

@LangHandler(langFileFolder = "lang")
public class Lang {

    public static final StringLangEntry pluginHookSuccess = new StringLangEntry("plugin-hook.success", "&8[&3Craftorithm&8] Plugin <plugin> hooked");
    public static final StringLangEntry pluginHookFailed = new StringLangEntry("plugin-hook.failed", "&8[&3Craftorithm&8] Plugin <plugin> hook failed");
    public static final StringLangEntry updateNewVersionFound = new StringLangEntry("update.new-version-found", "&8[&3Craftorithm&8] &aDetected a new version <new_version>!");
    public static final StringLangEntry updateFindUpdatesFailed = new StringLangEntry("update.find-update-failed", "&8[&3Craftorithm&8] &cFailed to find Craftorithm updates!");

}
