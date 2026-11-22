import java.util.ArrayList;

public class MemberManager {

    private ArrayList<Member> members = new ArrayList<>();

    // 회원 등록
    public void addMember(Member member) {
        members.add(member);
        System.out.println("회원 등록이 완료되었습니다.");
    }

    // 회원 조회
    public Member findMember(String memberId) {

        for (Member member : members) {
            if (member.getMemberId().equals(memberId)) {
                return member;
            }
        }

        return null;
    }

    // 회원 삭제
    public boolean deleteMember(String memberId) {

        Member member = findMember(memberId);

        if (member != null) {
            members.remove(member);
            System.out.println("회원이 삭제되었습니다.");
            return true;
        }

        System.out.println("해당 회원을 찾을 수 없습니다.");
        return false;
    }

    // 회원 목록 출력
    public void showAllMembers() {

        if (members.isEmpty()) {
            System.out.println("등록된 회원이 없습니다.");
            return;
        }

        for (Member member : members) {
            member.showInfo();
            System.out.println("--------------------");
        }
    }

    // 회원 로그인
    public boolean login(String memberId, String password) {

        Member member = findMember(memberId);

        if (member != null && member.login(password)) {
            System.out.println("로그인 성공!");
            return true;
        }

        System.out.println("아이디 또는 비밀번호가 잘못되었습니다.");
        return false;
    }
}