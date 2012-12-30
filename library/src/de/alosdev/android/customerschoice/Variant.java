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

public class Variant {
  public final String name;
  public final long start;
  public final long end;
  public final int[] spreading;
  public int currentVariant;

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
    sb.append(')');
    return sb.toString();
  }
}
