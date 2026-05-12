import java.awt.Color;
public class AdminUser extends User {
      public AdminUser(String var1, String var2, String var3) {
         super(var1, var2, var3);
      }

      public String getRole() {
         return "ADMIN";
      }

      public Color getRoleColor() {
         return IntelligentTravelPlanner.CLR_DANGER;
      }
   }