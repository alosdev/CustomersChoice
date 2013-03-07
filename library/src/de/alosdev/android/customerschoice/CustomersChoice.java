/*
# * Copyright 2012 Hasan Hosgel
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
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Build;
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
 * <p>CustomersChoice.getVariant(context, "Variant name");</p>
 *
 * <h3>adding a {@link Variant} by code with a spreading of 50:50 with
 * {@link CustomersChoice#addVariant(Variant)}</h3>
 * <p>CustomersChoice.addVariant(new
 * VariantBuilder("Variant name").setSpreading(new int[] { 50, 50 }).build());</p>
 *
 * <h3>JSON structure for configurations</h3>
 * <pre>
 * {
 *   "resetAll": true,
 *   "variants": [
 *     {
 *       "startTime": 51,
 *       "spreading": [ 1, 2 ],
 *       "name": "Variant1",
 *       "reset": true;
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
 * <p>CustomersChoice.configureByResource(context, R.string.resource);</p>
 * <p>With this you can add different configurations by locale, density and/or size.</p>
 *
 * <h4>adding several {@link Variant}s by a file one the SD Card {@link CustomersChoice#configureBySD(String)}</h4>
 * <p>CustomersChoice.configureBySD("FilepathWithFileName");</p>
 *
 * <h4>adding several {@link Variant}s by a network {@link CustomersChoice#configureByNetwork(Context, String)}</h4>
 * <p>CustomersChoice.configureByNetwork(context, "configurationURL");</p>
 *
 * <h3>adding loggers</h3>
 * <p>CustomersChoice.addLoggers(new AndroidLogger(), new CustomLogger());</p>
 *
 * <h3>adding reporters</h3>
 * <p>CustomersChoice.addReporters(new LogReporter(new AndroidLogger), new CustomReporter());</p>
 *
 * <h3>report of reached Goal</h3>
 * <p>CustomersChoice.reachesGoal("Variant name");</p>
 *
 * <h3>overwriting of the variant for a test scenario</h3>
 * <p>add the {@link BroadcastReceiver} configuration into the manifest:</p>
 * <h4>Attention</h4>
 * <p>Add also permissions to the broadcast receiver in the manifest, if you want to use it on a live application.</p>
 * <pre>
 * &lt;receiver android:name="de.alosdev.android.customerschoice.broadcast.OverwriteVariantBroadCastReceiver">
 *   &lt;intent-filter>
 *     &lt;action android:name="de.alosdev.android.customerschoice.demo.broadcast" />
 *   &lt;/intent-filter>
 * &lt;/receiver>
 * </pre>
 * <p>Write a broadcast Intent for overwriting the {@link Variant} in an {@link Activity}</p>
 * <pre>
 * Intent intent = new Intent("de.alosdev.android.customerschoice.demo.broadcast");
 * intent.putExtra(OverwriteVariantBroadCastReceiver.KEY_OVERWRITE_VARIANT, "Variant name");
 * intent.putExtra(OverwriteVariantBroadCastReceiver.KEY_FORCE_VARIANT, 2);
 * sendBroadcast(intent);
 * </pre>
 * <h3>setting {@link LifeTime} of {@link Variant}s</h3>
 * <p>You can change the {@link LifeTime} of the variant by {@link CustomersChoice#setLifeTime(Context, LifeTime)}.</p>
 * <li>{@link LifeTime#Session} - only persisted in memory</li>
 * <li>{@link LifeTime#Persistent} - persisted in preferences</li>
 * <br/><br/>
 * @author Hasan Hosgel
 *
 */
public final class CustomersChoice {
  private static final String FIELD_VARIANTS = "variants";
  private static final String FIELD_LAST_MODIFIED = "lastModified";
  private static final String FIELD_ETAG = "etag";
  private static final String KEY_SPREADING = "spreading";
  private static final String KEY_END_TIME = "endTime";
  private static final String KEY_START_TIME = "startTime";
  private static final String KEY_NAME = "name";
  private static final String KEY_VARIANTS = FIELD_VARIANTS;
  private static final String KEY_RESET_All = "resetAll";
  private static final String KEY_RESET = "reset";
  public static final String TAG = CustomersChoice.class.getSimpleName();
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
    Session, Persistent
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
   * @param context
   */
  public static int getVariant(Context context, final String name) {
    checkInstance();

    return instance.getInternalVariant(context, name);
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

  public static Logger getLogger() {
    checkInstance();
    return instance.log;
  }

  private int getInternalVariant(Context context, String name) {
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
        persistVariants(context);
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
      log.d(TAG, "reaches goal for ", name, " Variant: ", variant.currentVariant);
      report.reachesGoal(variant);
    }
  }

  public static void addVariant(final Variant variant) {
    addVariant(variant, true);
  }

  public static void addVariant(final Variant variant, boolean isNotReset) {
    checkInstance();

    Variant oldVariant = instance.variants.get(variant.name);
    if ((null != oldVariant) && isNotReset) {
      variant.currentVariant = oldVariant.currentVariant;
    }
    instance.variants.put(variant.name, variant);
    instance.log.d(TAG, "added variant: ", variant, " and isReset: ", !isNotReset);
  }

  private static void checkInstance() {
    if (null == instance) {
      instance = new CustomersChoice();

      // Work around pre-Froyo bugs in HTTP connection reuse.
      if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO) {
        System.setProperty("http.keepAlive", "false");
      }
    }
  }

  /**
   * sets the {@link LifeTime} of the Variants.<br/>
   * If the {@link LifeTime} it is {@link LifeTime#Session}, it will try to remove the saved Variants.<br/>
   * If it is {@link LifeTime#Persistent}, it will load the persisted Variants. You should call this after all
   * configuration is done
   * @param lifeTime
   * @param context
   */
  public static void setLifeTimeForVariants(Context context, LifeTime lifeTime) {
    checkInstance();
    instance.setLifeTime(context, lifeTime);
  }

  public void setLifeTime(Context context, LifeTime lifeTime) {
    final SharedPreferences preferences = getPreferences(context);
    final Set<String> foundVariants;
    switch (lifeTime) {
      case Session: {
        Editor editor = preferences.edit();
        foundVariants = preferences.getStringSet(getPreferencesKey(FIELD_VARIANTS, ""), null);
        if ((null != foundVariants) && !foundVariants.isEmpty()) {
          for (String variantName : foundVariants) {
            editor.remove(getPreferencesKey(FIELD_VARIANTS, variantName));
          }
          editor.remove(getPreferencesKey(FIELD_VARIANTS, ""));
          editor.commit();
        }
        log.d(TAG, "cleared persisted Variant");
        break;
      }

      case Persistent: {
        foundVariants = preferences.getStringSet(getPreferencesKey(FIELD_VARIANTS, ""), null);
        if ((null != foundVariants) && !foundVariants.isEmpty()) {
          for (String variantName : foundVariants) {
            forceVariant(context, variantName,
              preferences.getInt(getPreferencesKey(FIELD_VARIANTS, variantName), 0));
          }
        }
        log.d(TAG, "read persisted Variant");
        break;
      }

      default: {
        throw new IllegalArgumentException("Unknown LifeTime: " + lifeTime);
      }
    }
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

            addVariant(builder.build(), !variant.optBoolean(KEY_RESET, false));
          } else {
            log.w(TAG, "variant has not the required name: ", variant.toString());
          }
        }
      }
      if (json.optBoolean(KEY_RESET_All, false)) {
        log.d(TAG, "reset all Variants");
        for (Variant var : variants.values()) {
          var.currentVariant = 0;
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
   * @param context
   * @param fileAddress
   */
  public static void configureByNetwork(Context context, String fileAddress) {
    checkInstance();
    instance.internalConfigureByNetwork(context, fileAddress);
  }

  private void internalConfigureByNetwork(final Context context, String fileAddress) {
    new AsyncTask<String, Void, Void>() {
      @Override
      protected Void doInBackground(String... args) {
        String value = args[0];
        try {
          final SharedPreferences preferences = getPreferences(context);
          final URL url = new URL(value);
          log.d(TAG, "read from: ", value);

          final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
          conn.setReadTimeout(10000 /* milliseconds */);
          conn.setConnectTimeout(15000 /* milliseconds */);

          // set etag header if existing
          final String fieldEtag = preferences.getString(getPreferencesKey(value, FIELD_ETAG), null);
          if (null != fieldEtag) {
            conn.setRequestProperty("If-None-Match", fieldEtag);
          }

          // set modified since header if existing
          final long fieldLastModified = preferences.getLong(getPreferencesKey(value, FIELD_LAST_MODIFIED), 0);
          if (fieldLastModified > 0) {
            conn.setIfModifiedSince(fieldLastModified);
          }
          conn.connect();

          final int response = conn.getResponseCode();

          if (HttpStatus.SC_OK == response) {
            log.d(TAG, "found file");
            readFromInputStream(conn.getInputStream());

            // writing caching information into preferences
            final Editor editor = preferences.edit();
            editor.putString(getPreferencesKey(value, FIELD_ETAG), conn.getHeaderField("ETag"));
            editor.putLong(getPreferencesKey(value, FIELD_LAST_MODIFIED), conn.getHeaderFieldDate("Last-Modified", 0));
            editor.commit();
          } else if (HttpStatus.SC_NOT_MODIFIED == response) {
            log.i(TAG, "no updates, file not modified: ", value);
          } else {
            log.e(TAG, "cannot read from: ", value, " and get following response code:", response);
          }
        } catch (MalformedURLException e) {
          log.e(TAG, e, "the given URL is malformed: ", value);
        } catch (IOException e) {
          log.e(TAG, e, "Error during reading the file: ", value);
        }
        return null;
      }

    }.execute(fileAddress);
  }

  String getPreferencesKey(String value, String field) {
    final StringBuilder sb = new StringBuilder();
    sb.append(TAG).append('.').append(value).append('.').append(field);
    return sb.toString();
  }

  /**
   * forces a Variant to be a custom case.
   * @param context
   * @param variantName
   * @param forceVariant
   */

  public static void forceVariant(Context context, String variantName, int forceVariant) {
    checkInstance();

    instance.internalForceVariant(context, variantName, forceVariant);
  }

  private void internalForceVariant(Context context, String variantName, int forceVariant) {
    Variant variant = variants.get(variantName);
    if (null == variant) {
      log.w(TAG, "This Variant does not exists: ", variantName);
      return;
    }
    variant.currentVariant = forceVariant;
    persistVariants(context);
  }

  private void persistVariants(final Context context) {
    if (lifeTime == LifeTime.Persistent) {
      new AsyncTask<Void, Void, Void>() {
        @Override
        protected Void doInBackground(Void... params) {
          final Editor editor = getPreferences(context).edit();
          final HashSet<String> variantNames = new HashSet<String>();
          for (Variant variant : variants.values()) {
            variantNames.add(variant.name);
            editor.putInt(getPreferencesKey(FIELD_VARIANTS, variant.name), variant.currentVariant);
          }
          editor.putStringSet(getPreferencesKey(KEY_VARIANTS, ""), variantNames);
          editor.commit();
          log.d(TAG, "persisted Variant");
          return null;
        }
      }.execute();
    }
  }

  private SharedPreferences getPreferences(Context context) {
    return context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
  }
}
