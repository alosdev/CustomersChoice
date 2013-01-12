/*
 * Copyright 2012 Hasan Hosgel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.alosdev.android.customerschoice.logger;

import android.util.Log;


/**
 * This class is an implementation of the {@link Logger}, which using the default Android {@link Log}.
 * @author Hasan Hosgel
 *
 */
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
