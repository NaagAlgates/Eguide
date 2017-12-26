package in.walkwithus.eguide.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import in.walkwithus.eguide.App;
import in.walkwithus.eguide.BuildConfig;

/**
 * Updated by bahwan on 12/25/17.
 * Project name: Eguide
 */


public class Preferences {

    private static final String DEFAULT_PREF_NAME = "walk_with_us-def-preference-app";
    private final SharedPreferences encryptedPrefs;
    private final SharedPreferences unencryptedPrefs;
    private SharedPreferences.Editor encryptedEditor;
    private SharedPreferences.Editor unencryptedEditor;

    public Preferences() {
        this(App.get(), DEFAULT_PREF_NAME);
    }

    public Preferences(String name) {
        this(App.get(), name);
    }

    public Preferences(Context context, String name) {
        if (context == null || TextUtils.isEmpty(name)) {
            encryptedPrefs = null;
            unencryptedPrefs = null;
        } else {
            encryptedPrefs = context.getSharedPreferences(name, Context.MODE_PRIVATE);
            unencryptedPrefs = BuildConfig.DEBUG ?
                    context.getSharedPreferences(name + "_unencrypted", Context.MODE_PRIVATE) : null;
        }
    }

    public String getString(String key, String defaultValue) {
        if (!isInitialized()) {
            return defaultValue;
        }
        String encrypted = encryptedPrefs.getString(Cryptography.encrypt(key), null);
        if (encrypted == null) {
            return defaultValue;
        }

        String value = Cryptography.decrypt(encrypted);
        if (unencryptedPrefs != null) {
            // Note: this block won't get executed in release build
            String unencryptedValue = unencryptedPrefs.getString(key, null);
            boolean testPassed = value.equals(unencryptedValue);
            if (!testPassed) {
                String msg = String.format("Retrieval failed for key '%s': value from encrypted file:'%s', value from unencrypted file: '%s'", key, value, unencryptedValue);
                Logger.e(Preferences.class.getSimpleName(), msg);
                throw new AssertionError(msg);
            }
        }
        return value;
    }

    public String getString(String key) {
        return getString(key, null);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        if (!isInitialized()) {
            return defaultValue;
        }
        String encrypted = encryptedPrefs.getString(Cryptography.encrypt(key), null);
        if (encrypted == null) {
            return defaultValue;
        }

        boolean value = Boolean.parseBoolean(Cryptography.decrypt(encrypted)); // Note that parseBoolean will NOT throw an exception
        if (unencryptedPrefs != null) {
            // Note: this block won't get executed in release build
            String unencryptedValue = unencryptedPrefs.getString(key, null);
            boolean testPassed = Boolean.toString(value).equals(unencryptedValue);
            if (!testPassed) {
                String msg = String.format("Retrieval failed for key '%s': value from encrypted file:'%s', value from unencrypted file: '%s'", key, Boolean.toString(value), unencryptedValue);
                Logger.e(Preferences.class.getSimpleName(), msg);
                throw new AssertionError(msg);
            }
        }
        return value;
    }

    public int getInt(String key, int defaultValue) {
        if (!isInitialized()) {
            return defaultValue;
        }
        String encrypted = encryptedPrefs.getString(Cryptography.encrypt(key), null);
        if (encrypted == null) {
            return defaultValue;
        }

        try {
            int value = Integer.parseInt(Cryptography.decrypt(encrypted));
            if (unencryptedPrefs != null) {
                // Note: this block won't get executed in release build
                String unencryptedValue = unencryptedPrefs.getString(key, null);
                boolean testPassed = Integer.toString(value).equals(unencryptedValue);
                if (!testPassed) {
                    String msg = String.format("Retrieval failed for key '%s': value from encrypted file:'%s', value from unencrypted file: '%s'", key, Integer.toString(value), unencryptedValue);
                    Logger.e(Preferences.class.getSimpleName(), msg);
                    throw new AssertionError(msg);
                }
            }
            return value;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public Preferences putString(String key, String value) {
        if (!isInitialized()) {
            return this;
        }

        putStringToEditors(key, value);
        return this;
    }

    public Preferences putBoolean(String key, boolean value) {
        if (!isInitialized()) {
            return this;
        }

        putStringToEditors(key, Boolean.toString(value));
        return this;
    }

    public Preferences putInt(String key, int value) {
        if (!isInitialized()) {
            return this;
        }

        putStringToEditors(key, Integer.toString(value));
        return this;
    }

    public Preferences remove(String key) {
        if (!isInitialized()) {
            return this;
        }

        removeFromEditors(key);
        return this;
    }

    public void apply() {
        if (!isInitialized()) {
            return;
        }
        apply(getEncryptedEditor());
        apply(getUnencryptedEditor());
    }

    public static void saveString(String key, String value) {
        new Preferences().putString(key, value).apply();
    }

    public static void saveBoolean(String key, boolean value) {
        new Preferences().putBoolean(key, value).apply();
    }

    private boolean isInitialized() {
        return encryptedPrefs != null;
    }

    private SharedPreferences.Editor getEncryptedEditor() {
        if (encryptedEditor == null && encryptedPrefs != null) {
            encryptedEditor = encryptedPrefs.edit();
        }
        return encryptedEditor;
    }

    private SharedPreferences.Editor getUnencryptedEditor() {
        if (unencryptedEditor == null && unencryptedPrefs != null) {
            unencryptedEditor = unencryptedPrefs.edit();
        }
        return unencryptedEditor;
    }

    private void putStringToEditors(String key, String value) {
        putStringToEditor(getEncryptedEditor(), Cryptography.encrypt(key), Cryptography.encrypt(value));
        putStringToEditor(getUnencryptedEditor(), key, value);
    }

    private void removeFromEditors(String key) {
        removeFromEditor(getEncryptedEditor(), Cryptography.encrypt(key));
        removeFromEditor(getUnencryptedEditor(), key);
    }

    private void putStringToEditor(SharedPreferences.Editor editor, String key, String value) {
        if (editor == null) {
            return;
        }
        editor.putString(key, value);
    }

    private void removeFromEditor(SharedPreferences.Editor editor, String key) {
        if (editor == null) {
            return;
        }
        editor.remove(key);
    }

    private void apply(SharedPreferences.Editor editor) {
        if (editor != null) {
            editor.apply();
        }
    }
}
