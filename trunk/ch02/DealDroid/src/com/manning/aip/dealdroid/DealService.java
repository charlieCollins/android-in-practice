package com.manning.aip.dealdroid;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.manning.aip.dealdroid.model.Item;

import java.util.ArrayList;
import java.util.List;

// Use IntentService which will queue each call to startService(Intent) through onHandleIntent and then shutdown
//
// NOTE that this implementation intentionally doesn't use PowerManager/WakeLock or deal with power issues
// (if the device is asleep, AlarmManager wakes up for BroadcastReceiver onReceive, but then might sleep again)
// (can use PowerManager and obtain WakeLock here, but STILL might not work, there is a gap)
// (this can be mitigated but for this example this complication is not needed)
// (it's not critical if user doesn't see new deals until phone is awake and notification is sent, both)
public class DealService extends IntentService {

   private DealDroidApp app;

   public DealService() {
      super("Deal Service");
   }

   @Override
   public void onStart(Intent intent, int startId) {
      super.onStart(intent, startId);
   }

   @Override
   public void onHandleIntent(Intent intent) {
      Log.i(Constants.LOG_TAG, "DealService invoked, checking for new deals (will notify if present)");
      this.app = (DealDroidApp) getApplication();
      app.sectionList = app.parser.parse();
      List<Item> newDealsList = this.checkForNewDeals();
      if (!newDealsList.isEmpty()) {
         this.sendNotification(this, newDealsList);
      }

      // uncomment to force notification, new deals or not
      /*
      count++;
      if (count == 1) {
         SystemClock.sleep(5000);
         this.sendNotification(this, 1);
      }
      */
   }

   private List<Item> checkForNewDeals() {
      List<Item> newDealsList = new ArrayList<Item>();
      if (!app.sectionList.isEmpty()) {
         // "deals" are only for Daily Deals section, which is first, at index 0
         ArrayList<Item> items = app.sectionList.get(0).items;
         if (!items.isEmpty()) {
            for (Item item : items) {
               if (!app.currentDeals.contains(item)) {
                  Log.d(Constants.LOG_TAG, "New deal found: " + item.title);
                  newDealsList.add(item);
               }
            }
            // reset the currentDeals (which are used to know if present deals are different, or not)
            app.currentDeals.clear();
            app.currentDeals.addAll(items);
         }
      }
      return newDealsList;
   }

   // TODO notifications seem to stack up rather than replace existing with same pending intent?
   private void sendNotification(final Context context, final List<Item> newDealsList) {
      Intent notificationIntent = new Intent(context, DealList.class);
      PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

      NotificationManager notificationMgr =
               (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
      Notification notification =
               new Notification(android.R.drawable.star_on, getString(R.string.deal_service_ticker), System
                        .currentTimeMillis());
      notification.flags |= Notification.FLAG_AUTO_CANCEL;
      notification.setLatestEventInfo(context, getResources().getQuantityString(R.plurals.deal_service_new_deal,
               newDealsList.size(), newDealsList.size()), getNewDealsString(newDealsList), contentIntent);
      notificationMgr.notify(0, notification);
   }

   private String getNewDealsString(final List<Item> newDealsList) {
      StringBuilder sb = new StringBuilder();
      sb.append("\n");
      for (Item item : newDealsList) {
         sb.append(item.title + "\n");
      }
      return sb.toString();
   }
}