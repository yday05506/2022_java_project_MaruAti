import java.awt.*;
import java.util.Random;

public class Bubble {
    Image img;  // 이미지 참조 변수
    int x, y;   // 이미지 중심 좌표
    int w, h;   // 이미지 절반 폭, 절반 높이
    int dy; // 불 변화량
    int width, height;  // 화면(panel)의 사이즈

    boolean isDead = false; // 본인 객체가 죽었는지

    public Bubble(Image imgBubble, int width, int height) {
        this.width = width;
        this.height = height;

        // 멤버 변수 값 초기화
        img = imgBubble.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        // 이미지 절반 넓이
        w = 50;
        h = 50;

        Random rnd = new Random();
        x = rnd.nextInt(width-w*2) + w; // w~width-w
        y = -h;

        dy =+ rnd.nextInt(15) + 1;  // 떨어지는 속도 랜덤
    }

    void move() {   // Fire의 움직이는 기능 메소드
        y += dy;
        // 만약 화면 밖으로 나가면 객체 없애기
        if(y > height + h)  // ArrayList에서 제거
            isDead = true;
    }
}
