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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Random;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import de.alosdev.android.customerschoice.logger.ChainedLogger;
import de.alosdev.android.customerschoice.logger.Logger;
import de.alosdev.android.customerschoice.logger.NoLogger;
import de.alosdev.android.customerschoice.reporter.ChainedReporter;
import de.alosdev.android.customerschoice.reporter.NoReporter;
import de.alosdev.android.customerschoice.reporter.Reporter;


/**
 * <h1>Customer's Choice</h1>
 * <p>
 * This library can be used to make simple usability tests on live Android
 * applications, so you can find the best choice, if you have two or more
 * different solutions. The Customer choose the best solution. For Example, if
 * it is better to have a red or blue purchase button.
 * </p>
 *
 * <h2>USAGE</h2>
 * <p>CustomersChoice.getVariant("Variant name");</p>
 *
 * <h3>adding a {@link Variant} by code with a spreading of 50:50 with
 * {@link CustomersChoice#addVariant(Variant)}</h3>
 * <p>CustomersChoice.addVariant(new
 * VariantBuilder("Variant name").setSpreading(new int[] { 50, 50 }).build());</p>
 *
 * <h3>JSON structure for configurations</h3>
 * <pre>
 * {
 *   "variants": [
 *     {
 *       "startTime": 51,
 *       "spreading": [ 1, 2 ],
 *       "name": "Variant1"
 *     },
 *     {
 *       "endTime": 53,
 *       "spreading": [ 3, 3 ],
 *       "name": "Variant2"
 *     }
 *   ]
 * }
 * </pre>
 *
 * <h4>adding several {@link Variant}s by a {@link String} resource with
 * {@link CustomersChoice#configureByResource(Context, int)}.</h4>
 * <p>CustomersChoice.configureByResource(this, R.string.resource);</p>
 * <p>With this you can add different configurations by locale, density and/or size.</p>
 *
 * <h4>adding several {@link Variant}s by a file one the SD Card {@link CustomersChoice#configureBySD(String)}</h4>
 * <p>CustomersChoice.configureBySD("FilepathWithFileName");</p>
 *
 * <h4>adding several {@link Variant}s by a network {@link CustomersChoice#configureByNetwork(String)}</h4>
 * <p>CustomersChoice.configureByNetwork("configurationURL");</p>
 *
 * <h3>adding a loggers</h3>
 * <p>CustomersChoice.addLoggers(new AndroidLogger(), new CustomLogger());</p>
 *
 * <h3>adding a reporters</h3>
 * <p>CustomersChoice.addReporters(new LogReporter(new AndroidLogger), new CustomReporter());</p>
 *
 * <h3>report of reached Goal</h3>
 * <p>CustomersChoice.reachesGoal("Variant name");</p>
 * <br/><br/>
 * @author Hasan Hosgel
 *
 */
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
  private Reporter report;

  /**
   * Definition of LifeTime of a {@link Variant}, whose default is
   * {@link #Session}, which cannot be changed in the moment
   *
   * @author Hasan Hosgel
   *
   */
  public enum LifeTime {
    Session, Peristent // Persistent not used in the moment
  }

  /**
   * used for initialization
   */
  public static void init() {
    checkInstance();
  }

  private CustomersChoice() {
    variants = new HashMap<String, Variant>();
    random = new Random(System.currentTimeMillis());
    log = new NoLogger();
    report = new NoReporter();
  }

  /**
   * returns the Variant, which is chosen by the internal logic, which can be
   * used for your cases(if,switch).
   */
  public static int getVariant(final String name) {
    checkInstance();

    return instance.getInternalVariant(name);
  }

  /**
   * sets the {@link Logger}s for the library. If none is set the default {@link Logger}
   * {@link NoLogger} is used.
   *
   * @param log
   *          if the parameter is NULL or empty, the {@link NoLogger} is used.
   */
  public static void addLoggers(Logger... loggers) {
    checkInstance();

    final Logger log;
    if ((null == loggers) || (loggers.length < 1)) {
      log = new NoLogger();
    } else if (loggers.length == 1) {
      log = loggers[0];
    } else {
      log = new ChainedLogger(loggers);
    }
    instance.log = log;
  }

  /**
   * sets the report s for the library. If none is set the default {@link Logger}
   * {@link NoLogger} is used.
   *
   * @param log
   *          if the parameter is NULL or empty, the {@link NoLogger} is used.
   */
  public static void addReporters(Reporter... reporters) {
    checkInstance();

    final Reporter report;
    if ((null == reporters) || (reporters.length < 1)) {
      report = new NoReporter();
    } else if (reporters.length == 1) {
      report = reporters[0];
    } else {
      report = new ChainedReporter(reporters);
    }
    instance.report = report;
  }

  private int getInternalVariant(String name) {
    int choosedVariant = 1;
    Variant variant = instance.variants.get(name);
    final long currentTime = System.currentTimeMillis();
    if ((null != variant) && (variant.start < currentTime) && (variant.end > currentTime)) {
      report.startVariant(variant);
      log.d(TAG, "choosed for ", name, " Variant: ", choosedVariant);
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
    return choosedVariant;
  }

  public static void reachesGoal(String name) {
    checkInstance();
    instance.internalReachesGoal(name);
  }

  private void internalReachesGoal(String name) {
    Variant variant = instance.variants.get(name);
    final long currentTime = System.currentTimeMillis();
    if ((null != variant) && (variant.start < currentTime) && (variant.end > currentTime)) {
      report.reachesGoal(variant);
    }

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

  /**
   * configures the library with the given String resource, which has to be
   * valid JSON.
   *
   * @param context
   * @param stringResourceId
   *          resource id of the {@link String} resource containing the JSON
   */
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
            builder = new VariantBuilder(variant.getString(KEY_NAME));
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

  /**
   * Configuring the {@link Variant} via a file on the SD Card. The file content
   * must be valid JSON.
   *
   * @param fileName
   *          the filename or relative file path to the configuration file on
   *          the SD Card.
   */
  public static void configureBySD(String fileName) {
    checkInstance();
    try {
      instance.internalConfigureBySD(fileName);
    } catch (IOException e) {
      instance.log.e(TAG, e, "error while reading file: ", fileName);
    }
  }

  private void internalConfigureBySD(String fileName) throws IOException {
    // checks if the external storage is mounted
    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
      final String filePath = TextUtils.concat(Environment.getExternalStorageDirectory().getAbsolutePath(), "/",
        fileName).toString();

      // loads the configuration file
      final File configurationFile = new File(filePath);

      // only loads the file if it's existing.
      if (configurationFile.exists()) {
        readFromInputStream(new FileInputStream(configurationFile));
      } else {
        log.w(TAG, "file does not exist on sd root:", fileName);
      }
    }
  }

  /**
   * loading the file content into a String and then parsing it.
   *
   * @param inputStream
   * @throws FileNotFoundException
   * @throws IOException
   */
  private void readFromInputStream(InputStream inputStream) throws FileNotFoundException, IOException {
    BufferedReader reader = null;
    InputStreamReader is = null;
    try {
      is = new InputStreamReader(inputStream);
      reader = new BufferedReader(is, 8192);

      String line = null;

      final StringBuilder result = new StringBuilder();
      while ((line = reader.readLine()) != null) {
        result.append(line);
      }

      parseStringVariants(result.toString());
    } finally {
      if (null != is) {
        is.close();
      }
      if (null != reader) {
        reader.close();
      }
    }
  }

  /**
   * Dont forget to add the permission
   * <code>&lt;uses-permission android:name="android.permission.INTERNET"/&gt</code>
   * in your AndroidManifest.xml<br/>
   * Any valid URL can be used for configuring the {@link Variant}s. The only
   * requirement is valid JSON.
   *
   * @param fileAddress
   */
  public static void configureByNetwork(String fileAddress) {
    checkInstance();
    instance.internalConfigureByNetwork(fileAddress);
  }

  private void internalConfigureByNetwork(String fileAddress) {
    new AsyncTask<String, Void, Void>() {
      @Override
      protected Void doInBackground(String... args) {
        try {
          URL url = new URL(args[0]);
          log.d(TAG, "read from: ", args[0]);

          HttpURLConnection conn = (HttpURLConnection) url.openConnection();
          conn.setReadTimeout(10000 /* milliseconds */);
          conn.setConnectTimeout(15000 /* milliseconds */);
          conn.connect();

          int response = conn.getResponseCode();
          if (HttpStatus.SC_OK == response) {
            log.d(TAG, "found file");
            readFromInputStream(conn.getInputStream());
          } else {
            log.e(TAG, "cannot read from: ", args[0], " and get following response code:", response);
          }
        } catch (MalformedURLException e) {
          log.e(TAG, e, "the given URL is malformed: ", args[0]);
        } catch (IOException e) {
          log.e(TAG, e, "Error during reading the file: ", args[0]);
        }
        return null;
      }
    }.execute(fileAddress);
  }

}
