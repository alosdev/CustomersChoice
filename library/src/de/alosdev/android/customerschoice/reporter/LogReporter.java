package de.alosdev.android.customerschoice.reporter;

import de.alosdev.android.customerschoice.Variant;
import de.alosdev.android.customerschoice.logger.Logger;


/**
 * This {@link Reporter} logs the reporting to the given reporter
 * @author Hasan Hosgel
 *
 */
public class LogReporter implements Reporter {
  private static final String TAG = "LogReporter";
  private final Logger logger;

  public LogReporter(Logger logger) {
    if (null == logger) {
      throw new IllegalArgumentException("the Logger must be not NULL");
    }
    this.logger = logger;
  }

  @Override
  public void startVariant(Variant variant) {
    logger.i(TAG, "start variant (", variant.name, ") with follogin case: ", variant.currentVariant);
  }

  @Override
  public void reachesGoal(Variant variant) {
    logger.i(TAG, "reached goal for variant (", variant.name, ") with following case: ", variant.currentVariant);
  }
}
