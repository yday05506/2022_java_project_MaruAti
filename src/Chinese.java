import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class Chinese extends JFrame {
    GamePanel panel;
    GameThread gThread;

    static int score;   // 점수

    public Chinese() {
        setTitle("식재료 얻기");
        setDefaultCloseOperation(EXIT_ON_CLOSE);    // x버튼 누르면 종료
        setBounds(0,0,1980,1080); // 위치, 크기 정하기
        setResizable(false);

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
                        panel.dx = -12;
                        break;
                    case KeyEvent.VK_RIGHT:
                        panel.dx = 12;
                        break;
                    case KeyEvent.VK_UP:
                        panel.dy = -12;
                        break;
                    case KeyEvent.VK_DOWN:
                        panel.dy = 12;
                        break;
                }   // 방향키 4개 구분
            }
        });
    }   // 생성자

    class GamePanel extends JPanel { // 게임 화면 그려낼 Panel
        ImageIcon imgBack = new ImageIcon("./images/background.jpg");  // 배경
        Image imgPlayer, imgBokchoy, imgHam, imgMushroom, imgBubble;
        int width, height;  // 패널 사이즈 가지고 오기
        int x, y, w, h; // xy:플레이어의 중심 좌표, wh:이미지 절반폭
        int dx = 0, dy = 0; // 플레이어 이미지의 이동 속도, 방향
        int hp = 3; // 플레이어 체력

        // 청경채 객체 참조 변수, 여러 마리일 수 있으므로 ArrayList(유동적 배열) 활용
        ArrayList<Food> bokChoys = new ArrayList<>();
        // 햄 객체 참조 변수, 여러 마리일 수 있으므로 ArrayList(유동적 배열) 활용
        ArrayList<Food> ham = new ArrayList<>();
        // 버섯 객체 참조 변수, 여러 마리일 수 있으므로 ArrayList(유동적 배열) 활용
        ArrayList<Food> mushrooms = new ArrayList<>();
        // 버블 객체 참죠 변수, 여러 마리일 수 있으므로 ArrayList(유동적 배열) 활용
        ArrayList<Food> bubbles = new ArrayList<>();

        public GamePanel() {
            // GUI 관련 프로그램의 편의를 위해 만들어진 도구 상자(Toolkit) 객체
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            imgPlayer = toolkit.getImage("./images/mouse.png");  // 캐릭터
            imgBokchoy = toolkit.getImage("./images/bok_choy.png");    // 청경채
            imgHam = toolkit.getImage("./images/ham.png");  // 햄
            imgMushroom = toolkit.getImage("./images/mushroom.png");  // 버섯
            imgBubble = toolkit.getImage("./images/bubble.png"); // 버블
        }

        // 보여질 내용물 작업을 수행하는 메소드 : 자동 실행 (콜백 메소드)
        protected void paintComponent(Graphics g) {
            // 화면에 보여질 작업
            if(width == 0 || height == 0) {
                width = getWidth();
                height = getHeight();
                // 리사이징
//                imgBack = imgBack.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                imgPlayer = imgPlayer.getScaledInstance(128, 128, Image.SCALE_SMOOTH);
                // 플레이어 좌표 계산
                x = width / 2;
                y = height - 100;
                w = 64;
                h = 64;
            }

            // 이곳에 화가 객체가 있음 → 그림 그리는 작업은 무조건 여기서
            g.drawImage(imgBack.getImage(),0,0,getWidth(), getHeight(), this);  // 배경 그리기
            for(int i = 0; i < bokChoys.size(); i++) {
                Food bc = bokChoys.get(i);
                g.drawImage(bc.img, bc.x-bc.w, bc.y-bc.h, this);
            }
            for(int i = 0; i < ham.size(); i++) {
                Food ha = ham.get(i);
                g.drawImage(ha.img, ha.x-ha.w, ha.y-ha.h, this);
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
            g.setFont(new Font(null, Font.BOLD, 25));   // 점수 표시
            g.drawString("SCORE : " + score, 10, 30);
            g.setFont(new Font(null, Font.BOLD, 25));   // 체력 표시
            g.drawString("HP : " + hp, 10, 70);
            setOpaque(false);
            super.paintComponent(g);
        }

        void move() {   // 플레이어 움직이기
            // 청경채 움직이기
            // 중간에 배열의 개수 변경될 여지가 있다면
            // 맨 마지막 요소부터 거꾸로 0번 요소까지 역으로 처리
            for(int i = bokChoys.size()-1; i >= 0; i--) {
                Food bc = bokChoys.get(i);
                bc.move();
                if(bc.isDead == true)    // ArrayList에서 제거
                    bokChoys.remove(i);
            }
            // 햄 움직이기
            for(int i = ham.size()-1; i >= 0; i--) {
                Food ha = ham.get(i);
                ha.move();
                if(ha.isDead == true)    // ArrayList에서 제거
                    ham.remove(i);
            }
            // 버섯 움직이기
            for(int i = mushrooms.size()-1; i >= 0; i--) {
                Food m = mushrooms.get(i);
                m.move();
                if(m.isDead == true)    // ArrayList에서 제거
                    mushrooms.remove(i);
            }
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

        void makeBokchoy()   {   // 청경채 생성 메소드
            if(width == 0 || height == 0) return;

            Random rnd = new Random();  // 50번에 한 번 꼴로 만들기
            int n = rnd.nextInt(25);
            if(n == 0) bokChoys.add(new Food(imgBokchoy, width, height));
        }

        void makeHam() {    // 햄 생성 메소드
            if(width == 0 || height == 0) return;

            Random rnd = new Random();
            int n = rnd.nextInt(25);
            if(n == 0) ham.add(new Food(imgHam, width, height));
        }

        void makeMushroom()   {   // 버섯 생성 메소드
            if(width == 0 || height == 0) return;

            Random rnd = new Random();  // 50번에 한 번 꼴로 만들기
            int n = rnd.nextInt(25);
            if(n == 0) mushrooms.add(new Food(imgMushroom, width, height));
        }

        void makeBubble() {   // 버블 생성 메소드
            if(width == 0 || height == 0) return;

            Random rnd = new Random();  // 50번에 한 번 꼴로 만들기
            int n = rnd.nextInt(80);
            if(n == 0) bubbles.add(new Food(imgBubble, width, height));
        }

        // 충돌 체크 작업 계산 메소드
        void BokchoyCheckCollision() { // 플레이어와 청경채의 충돌
            for(Food bc : bokChoys) {
                int left = bc.x - bc.w;
                int right = bc.x + bc.w;
                int top = bc.y - bc.h;
                int bottom = bc.y + bc.h;

                if(x > left && x < right && y > top && y < bottom) {
                    bc.isDead = true;    // 충돌
                    score += 500;
                }
            }
        }

        void HamCheckCollision() { // 플레이어와 햄의 충돌
            for(Food ha : ham) {
                int left = ha.x - ha.w;
                int right = ha.x + ha.w;
                int top = ha.y - ha.h;
                int bottom = ha.y + ha.h;

                if(x > left && x < right && y > top && y < bottom) {
                    ha.isDead = true;    // 충돌
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
                panel.makeBokchoy();
                panel.makeHam();
                panel.makeMushroom();
                panel.makeBubble();

                panel.move();

                // 충돌 체크 기능 호출
                panel.BokchoyCheckCollision();
                panel.HamCheckCollision();
                panel.MushroomCheckCollision();
                panel.BubbleCheckCollision();
                if(panel.hp == 0) {
                    new GameOver();
                    setVisible(false);
                    break;
                }
                try {   // 너무 빨리 돌아서 천천히 돌도록
                    sleep(10);
                    repaint();
                } catch (InterruptedException e) {}
            }

        }
    }

    public static void playMusic() {
        File bgm;
        AudioInputStream stream;
        AudioFormat format;
        DataLine.Info info;

        bgm = new File("./audios/startDescStory.wav");
        Clip clip;

        try {
            stream = AudioSystem.getAudioInputStream(bgm);
            format = stream.getFormat();
            info = new DataLine.Info(Clip.class, format);
            clip = (Clip) AudioSystem.getLine(info);
            clip.open(stream);
            clip.start();
        } catch (Exception e) {
            System.out.println("err" + e);
        }

    }
    public static void main(String[] args) {
        playMusic();
        new Korean();
    }
}
