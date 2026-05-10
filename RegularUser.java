import java.awt.Color;

// RegularUser class extends User and represents a normal application user
public class RegularUser extends User {

    // color used to represent regular user role in UI
    public static final Color CLR_ACCENT   = new Color( 25, 118, 210);
    
    // constructor passing values to parent User class
    public RegularUser(String u, String p, String e) { 
        super(u, p, e); 
    }

    // returns role type of this user
    @Override 
    public String getRole() { 
        return "USER"; 
    }

    // returns color associated with regular user role
    @Override 
    public Color getRoleColor() { 
        return CLR_ACCENT; 
    }
}