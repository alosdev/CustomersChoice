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


/**
 * This implementation of {@link Logger} does nothing other than fulfilling the interface.
 * @author hhosgel
 *
 */
public class NoLogger implements Logger {
  @Override
  public void d(String tag, Object... args) {
  }

  @Override
  public void i(String tag, Object... args) {
  }

  @Override
  public void w(String tag, Object... args) {
  }

  @Override
  public void e(String tag, Object... args) {
  }
}
