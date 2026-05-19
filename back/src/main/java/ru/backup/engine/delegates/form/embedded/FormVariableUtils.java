package ru.backup.engine.delegates.form.embedded;

public class FormVariableUtils {
  private static String LIST_PREFIX = "lst_";

  public static String setVariableNameAsList(String name) {
    return LIST_PREFIX + name;
  }

  public static boolean isVariableAsList(String name) {
    return name.startsWith(LIST_PREFIX);
  }

  public static String getOriginalListVariableName(String name) {
    return name.replace(LIST_PREFIX, "");
  }
}
