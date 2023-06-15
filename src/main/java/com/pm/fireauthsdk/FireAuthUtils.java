package com.pm.fireauthsdk;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class FireAuthUtils {

    private static FirebaseAuth firebaseAuth;

    public FireAuthUtils(FirebaseAuth firebaseAuth) {
        FireAuthUtils.firebaseAuth = firebaseAuth;
    }

    public static void addRoles(String userId, List<String> roles) throws FirebaseAuthException {
        Map<String, Object> customClaims = firebaseAuth.getUser(userId).getCustomClaims();
        List<String> roleList;
        try {
            roleList = (List<String>) customClaims.get("role");
        } catch (Exception e) {
            log.error("Firebase roles not in list format");
            return;
        }
        for (String newRole : roles) {
            if (!roleList.contains(newRole)) {
                roleList.add(newRole);
            }
        }
        customClaims.put("role", roleList);
        firebaseAuth.setCustomUserClaims(userId, customClaims);
    }
}
