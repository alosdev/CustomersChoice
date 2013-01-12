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
 * The {@link VariantBuilder} is like the name defines a builder for the {@link Variant}, so you can freely configure
 * your variant and if you finished you can build the final {@link Variant}. The Builder implments a fluent interface,
 * so you can go through it like the append in {@link StringBuilder}.
 * @author Hasan Hosgel
 *
 */
public class VariantBuilder {
  private String name;
  private long startTime = 0;
  private long endTime = Long.MAX_VALUE;
  private int[] spreading = { 1 };

  public VariantBuilder(String name) {
    setName(name);
  }

  /**
   * sets the name
   * @param name
   */
  public VariantBuilder setName(String name) {
    if ((null == name) || (name.length() > 0)) {
      throw new IllegalArgumentException("The name must contains at least onw char");
    }
    this.name = name;
    return this;
  }

  /**
   * adds an startdate for the variant
   * @param startDate
   * @return
   */
  public VariantBuilder setStartDate(Date startDate) {
    startTime = startDate.getTime();
    return this;
  }

  /**
   * adds an starttime for the variant
   * @param starttime in milliseconds
   * @return
   */
  public VariantBuilder setStartTime(long startTime) {
    this.startTime = startTime;
    return this;
  }

  /**
   * adds the enddate for the variant
   * @param endDate
   * @return
   */
  public VariantBuilder setEndDate(Date endDate) {
    endTime = endDate.getTime();
    return this;
  }

  /**
   * adds the endtime for the variant
   * @param endTime in milliseconds
   * @return
   */
  public VariantBuilder setEndTime(long endTime) {
    this.endTime = endTime;
    return this;
  }

  /**
   * the the spreading/ variation of the variant
   * @param spreading
   * @return
   */
  public VariantBuilder setSpreading(int[] spreading) {
    this.spreading = spreading;
    return this;
  }

  /**
   * creates the {@link Variant} after configuring it completely.
   * @return
   */
  public Variant build() {
    return new Variant(name, startTime, endTime, spreading);
  }
}
