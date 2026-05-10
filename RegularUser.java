import java.awt.Color;
public class RegularUser extends User {

    public static final Color CLR_ACCENT   = new Color( 25, 118, 210);
    
        public RegularUser(String u, String p, String e) { super(u, p, e); }
        @Override public String getRole()      { return "USER"; }
        @Override public Color  getRoleColor() { return CLR_ACCENT; }
    }