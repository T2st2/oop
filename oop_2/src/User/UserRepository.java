package User;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Properties;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class UserRepository {
    private static final Path FILE = Path.of("members.properties");
    private final ArrayList<User> users = new ArrayList<>();

    public UserRepository() {
        loadUsers();
    }

    public boolean addUser(User user) {
        for (User u : users) {
            if (u.getId().equals(user.getId())) {
                return false;
            }
        }

        // 비밀번호 원문 대신 솔트와 해시를 보관합니다.
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        String passwordHash = Base64.getEncoder().encodeToString(salt)
                + ":" + hash(user.getPassword(), salt);
        users.add(new Member(user.getId(), passwordHash, user.getName()));
        saveUsers();
        return true;
    }

    public User findUser(String id, String password) {
        for (User user : users) {
            if (user.getId().equals(id)) {
                String[] parts = user.getPassword().split(":", 2);
                byte[] salt = Base64.getDecoder().decode(parts[0]);
                String candidate = hash(password, salt);
                if (MessageDigest.isEqual(
                        Base64.getDecoder().decode(parts[1]),
                        Base64.getDecoder().decode(candidate))) {
                    return user;
                }
            }
        }
        return null;
    }

    private void loadUsers() {
        if (Files.notExists(FILE)) {
            return;
        }
        Properties data = new Properties();
        try (var reader = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {
            data.load(reader);
            for (String id : data.stringPropertyNames()) {
                String[] fields = data.getProperty(id).split("\t", 2);
                users.add(new Member(id, fields[0], fields[1]));
            }
        } catch (IOException e) {
            throw new UncheckedIOException("회원 정보를 불러오지 못했습니다.", e);
        }
    }

    private void saveUsers() {
        Properties data = new Properties();
        for (User user : users) {
            data.setProperty(user.getId(), user.getPassword() + "\t" + user.getName());
        }
        try (var writer = Files.newBufferedWriter(FILE, StandardCharsets.UTF_8)) {
            data.store(writer, "Library members");
        } catch (IOException e) {
            throw new UncheckedIOException("회원 정보를 저장하지 못했습니다.", e);
        }
    }

    private String hash(String password, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 600_000, 256);
        try {
            byte[] result = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(result);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("비밀번호 처리에 실패했습니다.", e);
        } finally {
            spec.clearPassword();
        }
    }
}
