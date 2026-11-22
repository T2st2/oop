public class Main {

    public static void main(String[] args) {

        MemberManager manager = new MemberManager();

        // 회원 생성
        Member member1 =
                new Member("user01", "김철수", "010-1234-5678", "1234");

        Member member2 =
                new Member("user02", "이영희", "010-9876-5432", "5678");

        // 회원 등록
        manager.addMember(member1);
        manager.addMember(member2);

        System.out.println();

        // 회원 목록 확인
        System.out.println("===== 회원 목록 =====");
        manager.showAllMembers();

        // 로그인 테스트
        System.out.println("===== 로그인 =====");

        manager.login("user01", "1234");

        System.out.println();

        // 회원 정보 수정
        System.out.println("===== 회원 정보 수정 =====");

        member1.updateInfo(
                "김철수",
                "010-1111-2222",
                "9999"
        );

        member1.showInfo();

        System.out.println();

        // 회원 삭제
        System.out.println("===== 회원 삭제 =====");

        manager.deleteMember("user02");

        System.out.println();

        // 최종 회원 목록
        System.out.println("===== 최종 회원 목록 =====");

        manager.showAllMembers();
    }
}