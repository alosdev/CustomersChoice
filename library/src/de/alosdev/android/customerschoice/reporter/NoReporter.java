package de.alosdev.android.customerschoice.reporter;

import de.alosdev.android.customerschoice.Variant;


/**
 * This implementation of {@link Reporter} does nothing else then fulfilling the interface.
 * @author Hasan Hosgel
 *
 */
public class NoReporter implements Reporter {
  @Override
  public void startVariant(Variant variant) {
    // do nothing
  }

  @Override
  public void reachesGoal(Variant variant) {
    // do nothing
  }
}
