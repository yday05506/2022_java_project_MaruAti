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
        Image imgBack, imgPlayer, imgSushi, imgEgg, imgMushroom, imgBubble;
        int width, height;  // 패널 사이즈 가지고 오기
        int x, y, w, h; // xy:플레이어의 중심 좌표, wh:이미지 절반폭
        int dx = 0, dy = 0; // 플레이어 이미지의 이동 속도, 방향
        int hp = 3; // 플레이어 체력

        // 초밥 객체 참조 변수, 여러 마리일 수 있으므로 ArrayList(유동적 배열) 활용
        ArrayList<Food> sushi = new ArrayList<>();
        // 달걀 객체 참조 변수, 여러 마리일 수 있으므로 ArrayList(유동적 배열) 활용
        ArrayList<Food> eggs = new ArrayList<>();
        // 버섯 객체 참조 변수, 여러 마리일 수 있으므로 ArrayList(유동적 배열) 활용
        ArrayList<Food> mushrooms = new ArrayList<>();
        // 버블 객체 참죠 변수, 여러 마리일 수 있으므로 ArrayList(유동적 배열) 활용
        ArrayList<Food> bubbles = new ArrayList<>();

        public GamePanel() {
            // GUI 관련 프로그램의 편의를 위해 만들어진 도구 상자(Toolkit) 객체
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            imgBack = toolkit.getImage("./images/background.jpg");  // 배경
            imgPlayer = toolkit.getImage("./images/mouse.png");  // 캐릭터
            imgSushi = toolkit.getImage("./images/sushi.png");    // 초밥
            imgEgg = toolkit.getImage("./images/fried_egg.png");  // 달걀
            imgMushroom = toolkit.getImage("./images/mushroom.png");  // 버섯
            imgBubble = toolkit.getImage("./images/bubble.png"); // 불
        }

        // 보여질 내용물 작업을 수행하는 메소드 : 자동 실행 (콜백 메소드)
        protected void paintComponent(Graphics g) {
            // 화면에 보여질 작업
            if(width == 0 || height == 0) {
                width = getWidth();
                height = getHeight();
                // 리사이징
                imgBack = imgBack.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                imgPlayer = imgPlayer.getScaledInstance(128, 128, Image.SCALE_SMOOTH);
                // 플레이어 좌표 계산
                x = width / 2;
                y = height - 100;
                w = 64;
                h = 64;
            }

            // 이곳에 화가 객체가 있음 → 그림 그리는 작업은 무조건 여기서
            g.drawImage(imgBack,0,0,this);  // 배경 그리기
            for(int i = 0; i < sushi.size(); i++) {
                Food s = sushi.get(i);
                g.drawImage(s.img, s.x-s.w, s.y-s.h, this);
            }
            for(int i = 0; i < eggs.size(); i++) {
                Food e = eggs.get(i);
                g.drawImage(e.img, e.x-e.w, e.y-e.h, this);
            }
            for(int i = 0; i < mushrooms.size(); i++) {
                Food m = mushrooms.get(i);
                g.drawImage(m.img, m.x-m.w, m.y-m.h, this);
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
            // 달걀 움직이기
            for(int i = eggs.size()-1; i >= 0; i--) {
                Food e = eggs.get(i);
                e.move();
                if(e.isDead == true)    // ArrayList에서 제거
                    eggs.remove(i);
            }
            // 버섯 움직이기
            for(int i = mushrooms.size()-1; i >= 0; i--) {
                Food m = mushrooms.get(i);
                m.move();
                if(m.isDead == true)    // ArrayList에서 제거
                    mushrooms.remove(i);
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

        void makeEgg() {    // 달걀 생성 메소드
            if(width == 0 || height == 0) return;

            Random rnd = new Random();
            int n = rnd.nextInt(25);
            if(n == 0) eggs.add(new Food(imgEgg, width, height));
        }

        void makeMushroom()   {   // 버섯 생성 메소드
            if(width == 0 || height == 0) return;

            Random rnd = new Random();
            int n = rnd.nextInt(25);
            if(n == 0) mushrooms.add(new Food(imgMushroom, width, height));
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

        void EggCheckCollision() { // 플레이어와 달걀의 충돌
            for(Food e : eggs) {
                int left = e.x - e.w;
                int right = e.x + e.w;
                int top = e.y - e.h;
                int bottom = e.y + e.h;

                if(x > left && x < right && y > top && y < bottom) {
                    e.isDead = true;    // 충돌
                    score += 1000;
                }
            }
        }

        void MushroomCheckCollision() { // 플레이어와 버섯의 충돌
            for(Food m : mushrooms) {
                int left = m.x - m.w;
                int right = m.x + m.w;
                int top = m.y - m.h;
                int bottom = m.y + m.h;

                if(x > left && x < right && y > top && y < bottom) {
                    m.isDead = true;    // 충돌
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
                    score -= 10000;
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
                panel.makeEgg();
                panel.makeBubble();
                panel.makeMushroom();

                panel.move();

                // 충돌 체크 기능 호출
                panel.SushiCheckCollision();
                panel.EggCheckCollision();
                panel.MushroomCheckCollision();
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
