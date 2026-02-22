package com.zinier.capacity_planner.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Standalone CLI utility to generate BCrypt password hashes for SQL seed scripts.
 *
 * Usage:
 *   java -cp build/libs/capacity-planner-*.jar \
 *        com.zinier.capacity_planner.util.BcryptPasswordGenerator "YourPassword"
 */
public class BcryptPasswordGenerator {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: BcryptPasswordGenerator <password>");
            System.out.println("Example: BcryptPasswordGenerator \"MySecurePass123!\"");
            System.exit(1);
        }

        String rawPassword = args[0];
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode(rawPassword);

        System.out.println("Password : " + rawPassword);
        System.out.println("BCrypt   : " + hash);
        System.out.println();
        System.out.println("SQL snippet:");
        System.out.println("  INSERT INTO app_user (username, password, full_name, user_role, is_active)");
        System.out.println("  VALUES ('admin', '" + hash + "', 'System Admin', 'SUPER_ADMIN', TRUE);");
    }
}
