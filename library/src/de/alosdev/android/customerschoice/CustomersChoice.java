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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import de.alosdev.android.customerschoice.logger.Logger;
import de.alosdev.android.customerschoice.logger.NoLogger;


public final class CustomersChoice {
  private static final String KEY_SPREADING = "spreading";
  private static final String KEY_END_TIME = "endTime";
  private static final String KEY_START_TIME = "startTime";
  private static final String KEY_NAME = "name";
  private static final String KEY_VARIANTS = "variants";
  private static final String TAG = CustomersChoice.class.getSimpleName();
  private static CustomersChoice instance;
  private LifeTime lifeTime = LifeTime.Session;
  private HashMap<String, Variant> variants;
  private Random random;
  private Logger log;

  public enum LifeTime {
    Session, Peristent //Persistent not used in the moment
  }

  public static void init() {
    checkInstance();
  }

  private CustomersChoice() {
    variants = new HashMap<String, Variant>();
    random = new Random(System.currentTimeMillis());
    log = new NoLogger();
  }

  public static int getVariant(final String name) {
    checkInstance();

    return instance.getInternalVariant(name);
  }

  public static void setLogger(Logger log) {
    checkInstance();
    if (null == log) {
      log = new NoLogger();
    }
    instance.log = log;
  }

  private int getInternalVariant(String name) {
    int choosedVariant = 1;
    Variant variant = instance.variants.get(name);
    final long currentTime = System.currentTimeMillis();
    if ((null != variant) && (variant.start < currentTime) && (variant.end > currentTime)) {
      if (variant.currentVariant < 1) {
        int complete = 0;
        for (int spreadingItem : variant.spreading) {
          complete += spreadingItem;
        }

        int nextInt = random.nextInt(complete);
        for (int i = 1; i <= variant.spreading.length; i++) {
          nextInt -= variant.spreading[i - 1];
          if (nextInt < 1) {
            variant.currentVariant = i;
            break;
          }
        }
      }
      choosedVariant = variant.currentVariant;
    }
    log.d(TAG, "choosed for ", name, " Variant: ", choosedVariant);
    return choosedVariant;
  }

  public static void addVariant(final Variant variant) {
    checkInstance();
    instance.variants.put(variant.name, variant);
    instance.log.d(TAG, "added variant: ", variant);
  }

  private static void checkInstance() {
    if (null == instance) {
      instance = new CustomersChoice();
    }
  }

  public void setLifeTimeForVariants(LifeTime lifeTime) {
    this.lifeTime = lifeTime;
  }

  public LifeTime getLifeTime() {
    return lifeTime;
  }

  public static void configureByResource(Context context, int stringResourceId) {
    checkInstance();
    instance.configure(context, stringResourceId);
  }

  private void configure(Context context, int stringResourceId) {
    String jsonString = context.getString(stringResourceId);
    parseStringVariants(jsonString);
  }

  private void parseStringVariants(String jsonString) {
    try {
      JSONObject json = new JSONObject(jsonString);
      JSONArray array = json.getJSONArray(KEY_VARIANTS);
      final int arrayLength = array.length();
      if (arrayLength > 0) {
        JSONObject variant;
        VariantBuilder builder;
        for (int i = 0; i < arrayLength; i++) {
          variant = array.getJSONObject(i);
          if (variant.has(KEY_NAME)) {
            builder = new VariantBuilder();
            builder.setName(variant.getString(KEY_NAME));
            builder.setStartTime(variant.optLong(KEY_START_TIME, 0));
            builder.setEndTime(variant.optLong(KEY_END_TIME, Long.MAX_VALUE));
            if (variant.has(KEY_SPREADING)) {
              JSONArray spreading = variant.getJSONArray(KEY_SPREADING);
              final int length = spreading.length();
              final int[] spread = new int[length];
              for (int j = 0; j < length; j++) {
                spread[j] = spreading.getInt(j);
              }
              builder.setSpreading(spread);
            }
            addVariant(builder.build());
          } else {
            log.w(TAG, "variant has not the required name: ", variant.toString());
          }
        }
      }
    } catch (JSONException e) {
      log.e(TAG, e, "cannot read string resource");
    }
  }

  public static void configureBySD(String fileName) {
    checkInstance();
    try {
      instance.internalConfigureBySD(fileName);
    } catch (IOException e) {
      instance.log.e(TAG, e, "error while reading file: ", fileName);
    }
  }

  private void internalConfigureBySD(String fileName) throws IOException {
    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
      final String filePath = TextUtils.concat(Environment.getExternalStorageDirectory().getAbsolutePath(),
        "/", fileName).toString();
      final File configurationFile = new File(filePath);
      if (configurationFile.exists()) {
        BufferedReader reader = null;
        try {
          reader = new BufferedReader(new InputStreamReader(new FileInputStream(configurationFile)), 8192);

          String line = null;

          final StringBuilder result = new StringBuilder();
          while ((line = reader.readLine()) != null) {
            result.append(line);
          }

          parseStringVariants(result.toString());
        } finally {
          if (null != reader) {
            reader.close();
          }
        }
      } else {
        log.i(TAG, "file does not exist on sd root:", fileName);
      }
    }
  }

}
