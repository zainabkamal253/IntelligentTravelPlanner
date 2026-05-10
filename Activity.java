import java.io.Serializable;

public class Activity implements Serializable {
      private static final long serialVersionUID = 1L;

      // basic details of an activity in itinerary
      public final String slot;
      public final String time;
      public final String emoji;
      public final String title;
      public final String description;

      // constructor to initialize activity data
      public Activity(String var1, String var2, String var3, String var4, String var5) {
         this.slot = var1;
         this.time = var2;
         this.emoji = var3;
         this.title = var4;
         this.description = var5 == null ? "" : var5;
      }
}