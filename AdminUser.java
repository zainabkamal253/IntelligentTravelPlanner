import java.awt.Color;
//AdminUser class represents an admin account in the system.
//It extends the base User class and adds admin-specific role info.
public class AdminUser extends User {

      // constructor passes values to parent User class
      public AdminUser(String var1, String var2, String var3) {
         super(var1, var2, var3);
      }

      // returns the role of this user
      public String getRole() {
         return "ADMIN";
      }

      // returns a color associated with admin role (used in UI)
      public Color getRoleColor() {
         return IntelligentTravelPlanner.CLR_DANGER;
      }
}