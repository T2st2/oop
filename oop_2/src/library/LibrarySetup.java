package library;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class LibrarySetup {
    private static final Integer LOAN_PERIOD_DAYS = 7; // TODO: 현재 대출 기간은 7일로 설정되었습니다.
    private static final BigDecimal DAILY_LATE_RATE = new BigDecimal("0.0007"); // TODO: 현재 일일 연체료 계산율은 0.07% 입니다.

    private static List<Book> registerBooks() {
        List<Book> books = new ArrayList<>();

        // 소설
        books.add(new Book("채식주의자", "소설", "창비", 17_000)); // 한강
        books.add(new Book("가공범", "소설", "북다", 22_000)); // 히가시노 게이고
        books.add(new Book("모순", "소설", "쓰다", 13_000)); // 양귀자
        books.add(new Book("단 한 번의 삶", "소설", "복복서가", 16_800)); // 김영하
        books.add(new Book("노르웨이의 숲", "소설", "민음사", 17_000)); // 무라카미 하루키

        // 컴퓨터
        books.add(new Book("혼자 공부하는 파이썬", "컴퓨터", "한빛미디어", 22_000)); // 윤인성
        books.add(new Book("Do it! 점프 투 파이썬", "컴퓨터", "이지스퍼블리싱", 22_000)); // 박응용
        books.add(new Book("모던 자바스크립트", "컴퓨터", "위키북스", 45_000)); // 이웅모
        books.add(new Book("이펙티브 자바", "컴퓨터", "인사이트", 36_000)); // 조슈아 블로크
        books.add(new Book("혼자 공부하는 C 언어", "컴퓨터", "한빛미디어", 26_000)); // 서현우

        // 경제
        books.add(new Book("주식투자 무작정 따라하기", "경제", "길벗", 25_000)); // 윤재수
        books.add(new Book("돈의 심리학", "경제", "인플루엔셜", 24_800)); // 모건 하우절
        books.add(new Book("투자 고수들의 매매일지, 적자생존 2027", "경제", "21세기북스", 26_000)); // 박세익, 조재완
        books.add(new Book("박곰희 연금 부자 수업", "경제", "인플루엔셜", 21_000)); // 박곰희
        books.add(new Book("돈, 뜨겁게 사랑하고 차갑게 다루어라", "경제", "미래의창", 15_000)); // 앙드레 코스톨라니

        // 과학
        books.add(new Book("서늘한 대화", "과학", "어크로스", 22_000)); // 정재승
        books.add(new Book("코스모스", "과학", "사이언스북스", 22_000)); // 칼 세이건
        books.add(new Book("이기적 유전자", "과학", "을유문화사", 22_000)); // 리처드 도킨스
        books.add(new Book("세상이 그대를 속일지라도", "과학", "동아시아", 18_000)); // 김상욱
        books.add(new Book("물고기는 존재하지 않는다", "과학", "곰출판", 17_000)); // 룰루 밀러

        // 자기계발
        books.add(new Book("아주 작은 습관의 힘", "자기계발", "비즈니스북스", 18_000)); // 제임스 클리어
        books.add(new Book("세이노의 가르침", "자기계발", "데이원", 7_200)); // 세이노(SayNo)
        books.add(new Book("방을 치웠을 뿐인데 운이 트이기 시작했다", "자기계발", "쌤앤파커스", 18_000)); // 마스다 미츠히로
        books.add(new Book("원씽", "자기계발", "비즈니스북스", 16_800)); // 게리 켈러, 제이 파파산
        books.add(new Book("행동하지 않으면 인생은 바뀌지 않는다", "자기계발", "현대지성", 16_900)); // 브라이언 트레이시

        return books;
    }

    public static LibraryService createLibrary() {
        return new LibraryService(registerBooks(), LOAN_PERIOD_DAYS, DAILY_LATE_RATE);
    }
}
