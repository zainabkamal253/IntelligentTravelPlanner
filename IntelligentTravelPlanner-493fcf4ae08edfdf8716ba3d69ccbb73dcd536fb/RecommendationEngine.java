import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
   public class RecommendationEngine implements Recommendable {
      RecommendationEngine() {
      }

      public List<TouristPlace> recommend(String var1, String var2) {
         List var3 = TouristDB.getInstance().placesFor(var1, var2);
         Collections.shuffle(var3, new Random());
         return var3;
      }

      public Itinerary generateItinerary(Trip var1) {
         Itinerary var2 = new Itinerary();
         String var3 = var1.getDestination().getName();
         String var4 = var1.getInterest() == null ? "general" : var1.getInterest().toLowerCase();
         int var5 = var1.getDays();
         ArrayList var6 = new ArrayList(TouristDB.getInstance().placesFor(var3, var4));
         ArrayList var7 = new ArrayList(TouristDB.getInstance().placesFor(var3, ""));
         var7.removeAll(var6);
         Random var8 = new Random();
         Collections.shuffle(var6, var8);
         Collections.shuffle(var7, var8);
         var2.setSummary(this.buildSummary(var3, var4, var5, var6.size()));
         int var9 = 0;
         int var10 = 0;

         for(int var11 = 1; var11 <= var5; ++var11) {
            DayPlan var12 = new DayPlan(var11);
            var12.setTheme(this.themeForDay(var11, var5, var4));
            if (var11 == 1) {
               var12.addActivity(new Activity("Morning", "10:00 AM", "\ud83d\udeec", "Arrival in " + var3, "Check in to your accommodation, settle in and freshen up after your journey."));
               var12.addActivity(new Activity("Late Morning", "11:30 AM", "☕", "Welcome breakfast", "Start light at a recommended local café and plan the day ahead."));
            } else {
               var12.addActivity(this.morningOpener(var11, var3, var4, var8));
            }

            TouristPlace var13 = this.nextPlace(var6, var7, var9, var10);
            if (var13 != null) {
               if (var6.contains(var13)) {
                  ++var9;
               } else {
                  ++var10;
               }

               var12.addActivity(new Activity("Morning Visit", "12:00 PM", var13.getEmoji(), var13.getName(), var13.getDescription().isEmpty() ? "Spend time exploring this " + var13.getCategory().toLowerCase() + " spot in " + var3 + "." : var13.getDescription()));
            }

            var12.addActivity(this.lunchSlot(var3, var4, var13, var8));
            TouristPlace var14 = null;
            int var15 = 0;

            while(var15++ < 3) {
               TouristPlace var16 = this.nextPlace(var6, var7, var9, var10);
               if (var16 == null) {
                  break;
               }

               if (!var16.equals(var13)) {
                  var14 = var16;
                  if (var6.contains(var16)) {
                     ++var9;
                  } else {
                     ++var10;
                  }
                  break;
               }

               if (var6.contains(var16)) {
                  ++var9;
               } else {
                  ++var10;
               }
            }

            if (var14 != null) {
               var12.addActivity(new Activity("Afternoon", "3:00 PM", var14.getEmoji(), var14.getName(), var14.getDescription().isEmpty() ? "Continue your " + var4 + " day with this stop." : var14.getDescription()));
            } else {
               var12.addActivity(this.fillerAfternoon(var3, var4, var11, var8));
            }

            Activity var17 = this.interestFlourish(var4, var3, var11, var5, var8);
            if (var17 != null) {
               var12.addActivity(var17);
            }

            if (var11 == var5 && var5 > 1) {
               var12.addActivity(new Activity("Evening", "6:30 PM", "\ud83d\udecd", "Souvenir shopping", "Pick up gifts, sweets or handicrafts unique to " + var3 + "."));
               var12.addActivity(new Activity("Night", "9:00 PM", "\ud83d\udeeb", "Departure preparations", "Pack up, settle accounts and prepare for the journey home."));
            } else {
               var12.addActivity(this.eveningSlot(var3, var4, var11, var8));
            }

            var2.addDay(var12);
         }

         return var2;
      }

      private TouristPlace nextPlace(List<TouristPlace> var1, List<TouristPlace> var2, int var3, int var4) {
         if (var3 < var1.size()) {
            return (TouristPlace)var1.get(var3);
         } else {
            return var4 < var2.size() ? (TouristPlace)var2.get(var4) : null;
         }
      }

      private String themeForDay(int var1, int var2, String var3) {
         if (var1 == 1) {
            return "Arrival & First Impressions";
         } else if (var1 == var2) {
            return "Final Day & Farewell";
         } else {
            switch (var3) {
               case "adventure":
                  String[] var6 = new String[]{"Heights & Adrenaline", "Trails & Trekking", "Outdoor Action", "Wild Exploration"};
                  return var6[(var1 - 2) % var6.length];
               case "food":
                  String[] var7 = new String[]{"Street Food Trail", "Heritage Cuisine", "Hidden Eateries", "Sweet & Savoury"};
                  return var7[(var1 - 2) % var7.length];
               case "historical":
                  String[] var8 = new String[]{"Mughal Marvels", "Forts & Palaces", "Sacred Sites", "Heritage Walk"};
                  return var8[(var1 - 2) % var8.length];
               case "nature":
                  String[] var9 = new String[]{"Lakes & Greenery", "Mountain Views", "Gardens & Open Air", "Wildlife & Wonder"};
                  return var9[(var1 - 2) % var9.length];
               case "shopping":
                  String[] var10 = new String[]{"Bazaars & Bargains", "Modern Malls", "Crafts & Curios", "Local Treasures"};
                  return var10[(var1 - 2) % var10.length];
               case "cultural":
                  String[] var11 = new String[]{"Old Quarters", "Music & Arts", "Local Life", "Festivals & Folklore"};
                  return var11[(var1 - 2) % var11.length];
               default:
                  String[] var12 = new String[]{"Discovery", "Local Highlights", "Hidden Gems", "Free Exploration"};
                  return var12[(var1 - 2) % var12.length];
            }
         }
      }

      private Activity morningOpener(int var1, String var2, String var3, Random var4) {
         String[] var5;
         switch (var3) {
            case "adventure" -> var5 = new String[]{"Light stretching & energy breakfast — fuel up for the day's activities", "Quick gear check and protein-rich breakfast at the hotel", "Sunrise walk near the hotel followed by a light meal"};
            case "food" -> var5 = new String[]{"Traditional desi nashta — paratha, halwa puri or chana", "Local breakfast café with fresh-brewed chai", "Try a regional breakfast specialty unique to " + var2};
            case "nature" -> var5 = new String[]{"Early-morning fresh air and a quiet outdoor breakfast", "Sunrise viewing followed by tea at a scenic spot", "Birdwatching walk with a packed light breakfast"};
            default -> var5 = new String[]{"Relaxed breakfast and final review of today's plan", "Local café breakfast — observe the morning rhythm of " + var2, "Hotel breakfast with a chance to catch up on travel notes"};
         }

         String var6 = var5[var4.nextInt(var5.length)];
         return new Activity("Morning", "8:30 AM", "☕", "Breakfast & day prep", var6);
      }

      private Activity lunchSlot(String var1, String var2, TouristPlace var3, Random var4) {
         String var5 = var3 == null ? "central " + var1 : "near " + var3.getName();
         String[] var6;
         switch (var2) {
            case "food" -> var6 = new String[]{"Hunt for an iconic dish — locals are the best guides.", "Try a heritage restaurant — authentic recipes passed down generations.", "Eat where the rickshaw drivers eat — usually the cheapest and tastiest."};
            case "adventure" -> var6 = new String[]{"Refuel with a high-energy meal — you'll need it.", "Quick, hearty lunch to keep the day's momentum.", "Hydrate well and pack snacks for the afternoon."};
            default -> var6 = new String[]{"Pick a spot " + var5 + " — local food beats chain restaurants.", "Lunch at a recommended eatery " + var5 + ".", "Try a regional specialty — ask the waiter what's popular."};
         }

         return new Activity("Lunch", "1:30 PM", "\ud83c\udf72", "Lunch " + var5, var6[var4.nextInt(var6.length)]);
      }

      private Activity fillerAfternoon(String var1, String var2, int var3, Random var4) {
         String[] var5;
         switch (var2) {
            case "adventure" -> var5 = new String[]{"Try a local outdoor activity — paragliding, zip-lining, or quad biking if available.", "Hire a guide for an off-grid trail walk near " + var1 + ".", "Rent a bicycle and explore the outskirts of " + var1 + "."};
            case "food" -> var5 = new String[]{"Self-guided street food crawl — try at least three new dishes.", "Visit a local sweet shop and a tea house in the same afternoon.", "Cooking class or visit to a spice market in " + var1 + "."};
            case "shopping" -> var5 = new String[]{"Bargain hunting at a smaller bazaar away from the tourist trail.", "Visit artisan workshops where you can see crafts being made.", "Spend the afternoon at the largest mall in " + var1 + "."};
            case "nature" -> var5 = new String[]{"Quiet picnic at a nearby park or scenic open space.", "Sunset walk along the most scenic stretch in " + var1 + ".", "Boat ride or visit to a nearby viewpoint."};
            default -> var5 = new String[]{"Free exploration — wander a neighbourhood you haven't seen yet.", "Coffee at a heritage café and people-watching.", "Visit a local art gallery or cultural centre."};
         }

         return new Activity("Afternoon", "3:00 PM", "✨", "Open exploration", var5[var4.nextInt(var5.length)]);
      }

      private Activity interestFlourish(String var1, String var2, int var3, int var4, Random var5) {
         if (var3 != 1 && var3 != var4) {
            switch (var1) {
               case "adventure" -> {
                  return new Activity("Late Afternoon", "5:00 PM", "\ud83e\ude82", "Adventure twist of the day", (new String[]{"Try a local thrill activity — depends on city: paragliding, rafting, ziplining.", "Sunset hike to a viewpoint nobody on Instagram has tagged yet.", "Find an underrated trail and ask locals what's around it."})[var5.nextInt(3)]);
               }
               case "food" -> {
                  return new Activity("Late Afternoon", "5:00 PM", "\ud83e\udd58", "Tea & snacks ritual", "Find a local tea house — high tea here is a social institution, not a meal.");
               }
               case "historical" -> {
                  return new Activity("Late Afternoon", "5:00 PM", "\ud83d\udcdc", "Heritage walking tour", "Take a guided heritage walk — context turns ruins into stories.");
               }
               case "nature" -> {
                  return new Activity("Late Afternoon", "5:00 PM", "\ud83c\udf05", "Golden hour viewing", "Position yourself at a viewpoint — sunsets are the best free entertainment.");
               }
               case "shopping" -> {
                  return new Activity("Late Afternoon", "5:00 PM", "\ud83c\udfa8", "Handicraft hunting", "Look for items you can't get back home — woodwork, embroidery, regional crafts.");
               }
               case "cultural" -> {
                  return new Activity("Late Afternoon", "5:00 PM", "\ud83c\udfad", "Cultural performance", "Check for qawwali, theatre or local music shows happening tonight.");
               }
               default -> {
                  return null;
               }
            }
         } else {
            return null;
         }
      }

      private Activity eveningSlot(String var1, String var2, int var3, Random var4) {
         String[] var5;
         switch (var2) {
            case "food" -> var5 = new String[]{"Late dinner — try the dish " + var1 + " is famous for.", "Food street walk — desserts and BBQ are the headline acts at night.", "Rooftop restaurant with a view of the city skyline."};
            case "adventure" -> var5 = new String[]{"Recovery dinner — protein-rich, big portions, early to bed.", "Casual dinner with the day's photos shared among the group.", "Light dinner and gear-prep for tomorrow."};
            default -> var5 = new String[]{"Dinner at a recommended local restaurant.", "Casual evening stroll followed by dinner at a popular spot.", "Try the most-recommended restaurant in " + var1 + " tonight."};
         }

         return new Activity("Evening", "8:00 PM", "\ud83c\udf19", "Dinner & wind down", var5[var4.nextInt(var5.length)]);
      }

      private String buildSummary(String var1, String var2, int var3, int var4) {
         StringBuilder var5 = new StringBuilder();
         var5.append(var3).append("-day ").append(var2).append(" trip to ");
         var5.append(var1).append(", featuring ");
         if (var4 >= var3 * 2) {
            var5.append("a deeply local mix of");
         } else if (var4 >= var3) {
            var5.append("a curated selection of");
         } else {
            var5.append("the best of");
         }

         var5.append(" ").append(var2).append(" attractions, food, and exploration.");
         return var5.toString();
      }
   }