package de.alosdev.android.customerschoice.reporter;

import de.alosdev.android.customerschoice.CustomersChoice;
import de.alosdev.android.customerschoice.Variant;
import de.alosdev.android.customerschoice.logger.Logger;


/**
 * This interface allows to use pluggable {@link Reporter}. The default implementation is the {@link NoReporter}.
 * If you want to use the {@link Logger}, you can configure the {@link LogReporter} in the
 * {@link CustomersChoice#addReporters(Reporter...)}.
 *
 * @author Hasan Hosgel
 *
 */
public interface Reporter {
  void startVariant(Variant variant);

  void reachesGoal(Variant variant);
}
