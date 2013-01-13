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
package de.alosdev.android.customerschoice.reporter;

import de.alosdev.android.customerschoice.CustomersChoice;
import de.alosdev.android.customerschoice.Variant;


/**
 * The Chained Logger can be used to chain several {@link Logger}s together.
 * It is normally done automatically by the {@link CustomersChoice#addLoggers(Logger...)}
 * @author Hasan Hosgel
 *
 */
public class ChainedReporter implements Reporter {
  private final Reporter[] reporters;

  public ChainedReporter(Reporter... reporters) {
    if ((null == reporters) || (reporters.length < 1)) {
      throw new IllegalArgumentException("the reporters array must contain at least one reporter");
    }
    this.reporters = reporters;
  }

  @Override
  public void startVariant(Variant variant) {
    for (Reporter reporter : reporters) {
      reporter.startVariant(variant);
    }
  }

  @Override
  public void reachesGoal(Variant variant) {
    for (Reporter reporter : reporters) {
      reporter.reachesGoal(variant);
    }
  }
}
