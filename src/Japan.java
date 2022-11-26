import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

public class Japan extends JFrame {
    GamePanel panel;
    GameThread gThread;

    static int score;   // 점수

    public Japan() {
        setTitle("식재료 얻기");
        setDefaultCloseOperation(EXIT_ON_CLOSE);    // x버튼 누르면 종료
        setBounds(0,0,1980,1080); // 위치, 크기 정하기
        setResizable(false);
        setBackground(Color.gray);

        panel = new GamePanel();
        add(panel, BorderLayout.CENTER);

        setVisible(true);   // 창 보이게 하기

        // 게임 진행시키는 스레드 객체 생성 및 시작
        gThread = new GameThread();
        gThread.start();    // run() 메소드 자동 실행

        // 키보드 입력에 반응하는 keyListener를 프레임에 등록
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            public void keyReleased(KeyEvent e) {
                // 눌러진 키가 무엇인지
                int keyCode = e.getKeyCode();
                switch (keyCode) {
                    case KeyEvent.VK_LEFT:
                        panel.dx = 0;
                        break;
                    case KeyEvent.VK_RIGHT:
                        panel.dx = 0;
                        break;
                    case KeyEvent.VK_UP:
                        panel.dy = 0;
                        break;
                    case KeyEvent.VK_DOWN:
                        panel.dy = 0;
                        break;
                }   // 방향키 4개 구분
            }

            public void keyPressed(KeyEvent e) {
                // 눌러진 키가 무엇인지
                int keyCode = e.getKeyCode();
                switch (keyCode) {
                    case KeyEvent.VK_LEFT:
                        panel.dx = -10;
                        break;
                    case KeyEvent.VK_RIGHT:
                        panel.dx = 10;
                        break;
                    case KeyEvent.VK_UP:
                        panel.dy = -10;
                        break;
                    case KeyEvent.VK_DOWN:
                        panel.dy = 10;
                        break;
                }   // 방향키 4개 구분
            }
        });
    }   // 생성자

    class GamePanel extends JPanel { // 게임 화면 그려낼 Panel
        ImageIcon imgBack = new ImageIcon("./images/background.jpg");  // 배경
        Image imgPlayer, imgSushi, imgTakoyaki, imgDango, imgBubble;
        int width, height;  // 패널 사이즈 가지고 오기
        int x, y, w, h; // xy:플레이어의 중심 좌표, wh:이미지 절반폭
        int dx = 0, dy = 0; // 플레이어 이미지의 이동 속도, 방향
        int hp = 3; // 플레이어 체력

        // 초밥 객체 참조 변수, 여러 마리일 수 있으므로 ArrayList(유동적 배열) 활용
        ArrayList<Food> sushi = new ArrayList<>();
        // 타코야끼 객체 참조 변수, 여러 마리일 수 있으므로 ArrayList(유동적 배열) 활용
        ArrayList<Food> takoyakies = new ArrayList<>();
        // 당고 객체 참조 변수, 여러 마리일 수 있으므로 ArrayList(유동적 배열) 활용
        ArrayList<Food> dangos = new ArrayList<>();
        // 버블 객체 참죠 변수, 여러 마리일 수 있으므로 ArrayList(유동적 배열) 활용
        ArrayList<Food> bubbles = new ArrayList<>();

        public GamePanel() {
            // GUI 관련 프로그램의 편의를 위해 만들어진 도구 상자(Toolkit) 객체
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            imgPlayer = toolkit.getImage("./images/mouse.png");  // 캐릭터
            imgSushi = toolkit.getImage("./images/sushi.png");    // 초밥
            imgTakoyaki = toolkit.getImage("./images/takoyaki.png");  // 타코야끼
            imgDango = toolkit.getImage("./images/dango.png");  // 당고
            imgBubble = toolkit.getImage("./images/bubble.png"); // 불
        }

        // 보여질 내용물 작업을 수행하는 메소드 : 자동 실행 (콜백 메소드)
        protected void paintComponent(Graphics g) {
            // 화면에 보여질 작업
            if(width == 0 || height == 0) {
                width = getWidth();
                height = getHeight();
                // 리사이징
                imgPlayer = imgPlayer.getScaledInstance(128, 128, Image.SCALE_SMOOTH);
                // 플레이어 좌표 계산
                x = width / 2;
                y = height - 100;
                w = 64;
                h = 64;
            }

            // 이곳에 화가 객체가 있음 → 그림 그리는 작업은 무조건 여기서
            g.drawImage(imgBack.getImage(),0,0,getWidth(), getHeight(), this);  // 배경 그리기
            for(int i = 0; i < sushi.size(); i++) {
                Food s = sushi.get(i);
                g.drawImage(s.img, s.x-s.w, s.y-s.h, this);
            }
            for(int i = 0; i < takoyakies.size(); i++) {
                Food t = takoyakies.get(i);
                g.drawImage(t.img, t.x-t.w, t.y-t.h, this);
            }
            for(int i = 0; i < dangos.size(); i++) {
                Food d = dangos.get(i);
                g.drawImage(d.img, d.x-d.w, d.y-d.h, this);
            }
            for(int i = 0; i < bubbles.size(); i++) {
                Food b = bubbles.get(i);
                g.drawImage(b.img, b.x-b.w, b.y-b.h, this);
            }
            g.drawImage(imgPlayer, x-w, y-h, this); // 캐릭터
            g.setFont(new Font(null, Font.BOLD, 20));   // 점수 표시
            g.drawString("SCORE : " + score, 10, 30);
            g.setFont(new Font(null, Font.BOLD, 20));   // 체력 표시
            g.drawString("HP : " + hp, 10, 70);
            setOpaque(false);
            super.paintComponent(g);
        }

        void move() {   // 플레이어 움직이기
            // 초밥 움직이기
            // 중간에 배열의 개수 변경될 여지가 있다면
            // 맨 마지막 요소부터 거꾸로 0번 요소까지 역으로 처리
            for(int i = sushi.size()-1; i >= 0; i--) {
                Food s = sushi.get(i);
                s.move();
                if(s.isDead == true)    // ArrayList에서 제거
                    sushi.remove(i);
            }
            // 타코야끼 움직이기
            for(int i = takoyakies.size()-1; i >= 0; i--) {
                Food t = takoyakies.get(i);
                t.move();
                if(t.isDead == true)    // ArrayList에서 제거
                    takoyakies.remove(i);
            }
            // 당고 움직이기
            for(int i = dangos.size()-1; i >= 0; i--) {
                Food d = dangos.get(i);
                d.move();
                if(d.isDead == true)    // ArrayList에서 제거
                    dangos.remove(i);
            }
            // 버블 움직이기
            for(int i = bubbles.size()-1; i >= 0; i--) {
                Food b = bubbles.get(i);
                b.move();
                if(b.isDead == true)
                    bubbles.remove(i);
            }
            x += dx;
            y += dy;
            // 플레이어 좌표가 화면 밖으로 나가지 않게
            if(x < w) x = w;
            if(x > width - w) x = width - w;
            if(y < h) y = h;
            if(y > height - h) y = height - h;
        }

        void makeSushi()   {   // 초밥 생성 메소드
            if(width == 0 || height == 0) return;

            Random rnd = new Random();
            int n = rnd.nextInt(25);
            if(n == 0) sushi.add(new Food(imgSushi, width, height));
        }

        void makeTakoyaki() {    // 타코야끼 생성 메소드
            if(width == 0 || height == 0) return;

            Random rnd = new Random();
            int n = rnd.nextInt(25);
            if(n == 0) takoyakies.add(new Food(imgTakoyaki, width, height));
        }

        void makeDango()   {   // 당고 생성 메소드
            if(width == 0 || height == 0) return;

            Random rnd = new Random();
            int n = rnd.nextInt(25);
            if(n == 0) dangos.add(new Food(imgDango, width, height));
        }

        void makeBubble() {   // 버블 생성 메소드
            if(width == 0 || height == 0) return;

            Random rnd = new Random();  // 50번에 한 번 꼴로 만들기
            int n = rnd.nextInt(40);
            if(n == 0) bubbles.add(new Food(imgBubble, width, height));
        }

        // 충돌 체크 작업 계산 메소드
        void SushiCheckCollision() { // 플레이어와 초밥의 충돌
            for(Food s : sushi) {
                int left = s.x - s.w;
                int right = s.x + s.w;
                int top = s.y - s.h;
                int bottom = s.y + s.h;

                if(x > left && x < right && y > top && y < bottom) {
                    s.isDead = true;    // 충돌
                    score += 500;
                }
            }
        }

        void TakoyakiCheckCollision() { // 플레이어와 타코야끼의 충돌
            for(Food t : takoyakies) {
                int left = t.x - t.w;
                int right = t.x + t.w;
                int top = t.y - t.h;
                int bottom = t.y + t.h;

                if(x > left && x < right && y > top && y < bottom) {
                    t.isDead = true;    // 충돌
                    score += 1000;
                }
            }
        }

        void DangoCheckCollision() { // 플레이어와 당고의 충돌
            for(Food d : dangos) {
                int left = d.x - d.w;
                int right = d.x + d.w;
                int top = d.y - d.h;
                int bottom = d.y + d.h;

                if(x > left && x < right && y > top && y < bottom) {
                    d.isDead = true;    // 충돌
                    score += 3000;
                }
            }
        }

        void BubbleCheckCollision() {
            for(Food b : bubbles) {
                int left = b.x - b.w;
                int right = b.x + b.w;
                int top = b.y - b.h;
                int bottom = b.y + b.h;

                if(x > left && x < right && y > top && y < bottom) {
                    b.isDead = true;    // 충돌
                    hp -= 1;
                }
            }
        }
    }

    class GameThread extends Thread {
        @Override
        public void run() {
            while(true) {
                // 적군 객체 만들어내는 기능 메소드 호출
                panel.makeSushi();
                panel.makeTakoyaki();
                panel.makeDango();
                panel.makeBubble();

                panel.move();

                // 충돌 체크 기능 호출
                panel.SushiCheckCollision();
                panel.TakoyakiCheckCollision();
                panel.DangoCheckCollision();
                panel.BubbleCheckCollision();
                if(panel.hp == 0) {
                    new GameOver();
                    setVisible(false);
                    break;
                }
                try {   // 너무 빨리 돌아서 천천히 돌도록
                    sleep(18);
                    repaint();
                } catch (InterruptedException e) {}
            }

        }
    }

    public static void main(String[] args) {
        new Japan();
    }

}
