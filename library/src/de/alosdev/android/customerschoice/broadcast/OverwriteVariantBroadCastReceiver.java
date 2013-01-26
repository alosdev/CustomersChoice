package de.alosdev.android.customerschoice.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import de.alosdev.android.customerschoice.CustomersChoice;
import de.alosdev.android.customerschoice.logger.Logger;


/**
 * This receiver can override the current variant, so you can define a a test case for your scenario.
 * <h1>Attention</h1>
 * Add also permissions to the broadcast receiver in the manifest, if you want to use it on a live application.
 * @author hhosgel
 */
public class OverwriteVariantBroadCastReceiver extends BroadcastReceiver {
  public static final String KEY_OVERWRITE_VARIANT = "OverWriteVariant";
  public static final String KEY_FORCE_VARIANT = "ForceVariant";

  @Override
  public void onReceive(Context context, Intent intent) {
    final String variantName = intent.getStringExtra(KEY_OVERWRITE_VARIANT);
    final int forceVariant = intent.getIntExtra(KEY_FORCE_VARIANT, 0);

    Logger log = CustomersChoice.getLogger();
    if ((null == variantName) || (variantName.length() < 1)) {
      log.w(CustomersChoice.TAG, "the sended Broadcast didn't contains a valid variant name: ", variantName);
      return;
    } else if (forceVariant < 0) {
      log.w(CustomersChoice.TAG, "the sended Broadcast must contain a forceVariant which is larger than -1.");
      return;
    }
    log.e(CustomersChoice.TAG, "set variant with ", variantName, " force value: ", forceVariant);

    CustomersChoice.forceVariant(variantName, forceVariant);
  }

}
