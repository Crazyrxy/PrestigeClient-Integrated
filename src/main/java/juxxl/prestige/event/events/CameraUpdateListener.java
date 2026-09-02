package juxxl.prestige.event.events;

import java.util.ArrayList;
import juxxl.prestige.event.CancellableEvent;
import juxxl.prestige.event.Listener;

public interface CameraUpdateListener extends Listener {
   void onCameraUpdate(CameraUpdateListener.CameraUpdateEvent var1);

   class CameraUpdateEvent extends CancellableEvent<CameraUpdateListener> {
      public double x;
      public double y;
      public double z;

      public CameraUpdateEvent(double x, double y, double z) {
         this.x = x;
         this.y = y;
         this.z = z;
      }

      public double getX() {
         return this.x;
      }

      public double getY() {
         return this.y;
      }

      public double getZ() {
         return this.z;
      }

      public void setX(double x) {
         this.x = x;
      }

      public void setY(double y) {
         this.y = y;
      }

      public void setZ(double z) {
         this.z = z;
      }

      @Override
      public void fire(ArrayList<CameraUpdateListener> listeners) {
         listeners.forEach(l -> l.onCameraUpdate(this));
      }

      @Override
      public Class<CameraUpdateListener> getListenerType() {
         return CameraUpdateListener.class;
      }
   }
}
