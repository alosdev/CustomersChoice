package de.alosdev.android.customerschoice.logger;

import android.util.Log;


public class AndroidLogger implements Logger {
  @Override
  public void d(String tag, Object... args) {
    println(Log.DEBUG, tag, args);
  }

  @Override
  public void i(String tag, Object... args) {
    println(Log.INFO, tag, args);
  }

  @Override
  public void w(String tag, Object... args) {
    println(Log.WARN, tag, args);
  }

  @Override
  public void e(String tag, Object... args) {
    println(Log.ERROR, tag, args);
  }

  private void println(int level, String tag, Object... args) {
    Throwable e = null;
    StringBuilder sb = new StringBuilder();
    if ((null != args) && (args.length > 0)) {
      for (Object arg : args) {
        if (arg instanceof Throwable) {
          e = (Throwable) arg;
        } else {
          sb.append(arg);
        }
      }

      if (null == e) {
        Log.println(level, tag, sb.toString());
      } else {
        Log.println(level, tag, sb.toString() + Log.getStackTraceString(e));
      }

    }
  }
}
