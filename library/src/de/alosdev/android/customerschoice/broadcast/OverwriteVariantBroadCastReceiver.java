package de.alosdev.android.customerschoice.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import de.alosdev.android.customerschoice.CustomersChoice;
import de.alosdev.android.customerschoice.logger.Logger;


public class OverwriteVariantBroadCastReceiver extends BroadcastReceiver {
  public static final String KEY_OVERWRITE_VARIANT = "OverWriteVariant";
  public static final String KEY_FORCE_VARIANT = "ForceVariant";

  @Override
  public void onReceive(Context context, Intent intent) {
    final String variantName = intent.getStringExtra(getResultData());
    final int forceVariant = intent.getIntExtra(KEY_FORCE_VARIANT, 0);

    Logger log = CustomersChoice.getLogger();
    if ((null == variantName) || (variantName.length() < 1)) {
      log.w(CustomersChoice.TAG, "the sended Broadcast didn't contains a valid variant name.");
      return;
    } else if (forceVariant < 0) {
      log.w(CustomersChoice.TAG, "the sended Broadcast must contain a forceVariant which is larger than -1.");
      return;
    }

    CustomersChoice.forceVariant(variantName, forceVariant);
  }

}
