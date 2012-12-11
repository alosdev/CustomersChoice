package de.alosdev.android.customerschoice.logger;

public interface Logger {
  void d(String tag, Object... args);

  void i(String tag, Object... args);

  void w(String tag, Object... args);

  void e(String tag, Object... args);

}
