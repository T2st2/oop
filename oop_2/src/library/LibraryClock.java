package library;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// 시연용 가상 날짜를 관리합니다. 실제 컴퓨터의 날짜는 변경하지 않습니다.
public final class LibraryClock {
    private final Path file = Path.of("clock.properties").toAbsolutePath();
    private long offsetDays;

    public LibraryClock() {
        if (Files.notExists(file)) {
            return;
        }
        try {
            offsetDays = Long.parseLong(Files.readString(file, StandardCharsets.UTF_8).strip());
            if (offsetDays < 0) {
                throw new IllegalArgumentException("날짜 이동 값은 0 이상이어야 합니다.");
            }
            today(); // 저장된 값으로 날짜를 만들 수 있는지 검증합니다.
        } catch (IOException | RuntimeException e) {
            throw new IllegalStateException("가상 날짜 파일을 확인하세요: " + file, e);
        }
    }

    public LocalDate today() {
        return LocalDate.now().plusDays(offsetDays);
    }

    public void advance(ChronoUnit unit) throws IOException {
        if (unit != ChronoUnit.WEEKS && unit != ChronoUnit.MONTHS && unit != ChronoUnit.YEARS) {
            throw new IllegalArgumentException("지원하지 않는 시간 이동 단위입니다.");
        }
        LocalDate realToday = LocalDate.now();
        LocalDate next = realToday.plusDays(offsetDays).plus(1, unit);
        long newOffset = ChronoUnit.DAYS.between(realToday, next);
        save(newOffset);
        offsetDays = newOffset;
    }

    private void save(long newOffset) throws IOException {
        Path temp = Files.createTempFile(file.getParent(), "clock-", ".tmp");
        try {
            Files.writeString(temp, Long.toString(newOffset), StandardCharsets.UTF_8);
            try {
                Files.move(temp, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temp);
        }
    }
}
