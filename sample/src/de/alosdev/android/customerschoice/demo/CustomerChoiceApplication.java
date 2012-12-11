package de.alosdev.android.customerschoice.demo;

import android.app.Application;
import de.alosdev.android.customerschoice.CustomersChoice;
import de.alosdev.android.customerschoice.VariantBuilder;
import de.alosdev.android.customerschoice.logger.AndroidLogger;


public class CustomerChoiceApplication extends Application {
  @Override
  public void onCreate() {
    super.onCreate();
    CustomersChoice.addVariant(new VariantBuilder().setName("Variant1").setSpreading(new int[] { 50, 50 }).build());
    CustomersChoice.addVariant(new VariantBuilder().setName("Variant2").setSpreading(new int[] { 5, 50 }).build());
    CustomersChoice.setLogger(new AndroidLogger());
  }
}
