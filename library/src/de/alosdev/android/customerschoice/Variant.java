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

import java.util.Date;


/**
 * The {@link Variant} is the domain object for defining a possible variant. It contains the name/ key of the variant,
 * the start and end date and also the spreading for the different variants. The Variant itself is just an integer, so
 * you can decide on your own, how you want use it. The configuration of a variant is more or less final.
 * @author Hasan Hosgel
 *
 */
public class Variant {
  public final String name;
  public final long start;
  public final long end;
  public final int[] spreading;

  /**
   * contains the current configuration in the application.
   */
  public int currentVariant;

  /**
   * The Constructor needs the name/key of the variant, the start and end date and the spreading for the variants.
   * @param name
   * @param start
   * @param end
   * @param spreading
   */
  public Variant(String name, Date start, Date end, int[] spreading) {
    this(name, start.getTime(), end.getTime(), spreading);
  }

  /**
   * The Constructor needs the name/key of the variant, the start and end date in current time milliseconds and the spreading for the variants.
   * @param name a {@link String} with at least a length of 1.
   * @param start
   * @param end
   * @param spreading
   */
  public Variant(String name, long start, long end, int[] spreading) {
    if ((null == name) || (name.trim().length() == 0)) {
      throw new IllegalArgumentException("the name is a requiered parameter");
    }
    this.name = name;
    this.start = start;
    this.end = end;
    if ((null == spreading) || (spreading.length < 1)) {
      throw new IllegalArgumentException("The spreading must be at least one element");
    }
    this.spreading = spreading;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("Variant(name:").append(name).append("; startTime:").append(start);
    sb.append("; endTime:").append(end).append("; spreading:");
    for (int val : spreading) {
      sb.append(val).append(',');
    }
    sb.append("; current Variant: ").append(currentVariant).append(')');
    return sb.toString();
  }
}
