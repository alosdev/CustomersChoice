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

import de.alosdev.android.customerschoice.CustomersChoice;


/**
 * The Chained Logger can be used to chain several {@link Logger}s together.
 * It is normally done automatically by the {@link CustomersChoice#addLoggers(Logger...)}
 * @author hhosgel
 *
 */
public class ChainedLogger implements Logger {
  private final Logger[] loggers;

  public ChainedLogger(Logger... loggers) {
    if ((null == loggers) || (loggers.length < 1)) {
      throw new IllegalArgumentException("the logger array must contain at least one logger");
    }
    this.loggers = loggers;
  }

  @Override
  public void d(String tag, Object... args) {
    for (Logger log : loggers) {
      log.d(tag, args);
    }
  }

  @Override
  public void i(String tag, Object... args) {
    for (Logger log : loggers) {
      log.i(tag, args);
    }
  }

  @Override
  public void w(String tag, Object... args) {
    for (Logger log : loggers) {
      log.w(tag, args);
    }
  }

  @Override
  public void e(String tag, Object... args) {
    for (Logger log : loggers) {
      log.e(tag, args);
    }
  }

}
