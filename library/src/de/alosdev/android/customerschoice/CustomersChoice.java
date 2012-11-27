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

package de.alosdev.android.customerschoice;


public final class CustomersChoice {
  private static CustomersChoice instance;

  private CustomersChoice() {
  }

  public static int getVariant(final String key) {
    checkInstance();
    return 0;
  }

  private static void checkInstance() {
    if (null == instance) {
      instance = new CustomersChoice();
    }
  }
}
