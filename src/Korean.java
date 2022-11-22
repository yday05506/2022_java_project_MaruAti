import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

public class Korean extends JFrame {
    GamePanel panel;
    GameThread gThread;

    static int score;   // 점수

   public Korean() {
        setTitle("식재료 얻기");
        setDefaultCloseOperation(EXIT_ON_CLOSE);    // x버튼 누르면 종료
        setBounds(300,300,1980,1080); // 위치, 크기 정하기
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
                        panel.dx = -8;
                        break;
                    case KeyEvent.VK_RIGHT:
                        panel.dx = 8;
                        break;
                    case KeyEvent.VK_UP:
                        panel.dy = -8;
                        break;
                    case KeyEvent.VK_DOWN:
                        panel.dy = 8;
                        break;
                }   // 방향키 4개 구분
            }
        });
    }   // 생성자

    class GamePanel extends JPanel { // 게임 화면 그려낼 Panel
        Image imgBack, imgPlayer, imgKimchi, imgHam, imgFire;
        int width, height;  // 패널 사이즈 가지고 오기
        int x, y, w, h; // xy:플레이어의 중심 좌표, wh:이미지 절반폭
        int dx = 0, dy = 0; // 플레이어 이미지의 이동 속도, 방향
        int hp = 3; // 플레이어 체력

        // 김치 객체 참조 변수, 여러 마리일 수 있으므로 ArrayList(유동적 배열) 활용
        ArrayList<Kimchi> kimchi = new ArrayList<>();
        // 햄 객체 참조 변수, 여러 마리일 수 있으므로 ArrayList(유동적 배열) 활용
        ArrayList<Ham> ham = new ArrayList<>();
        // 불 객체 참죠 변수, 여러 마리일 수 있으므로 ArrayList(유동적 배열) 활용
        ArrayList<Fire> fires = new ArrayList<>();

        public GamePanel() {
            // GUI 관련 프로그램의 편의를 위해 만들어진 도구 상자(Toolkit) 객체
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            imgBack = toolkit.getImage("./images/background.jpg");  // 배경
            imgPlayer = toolkit.getImage("./images/mouse_01.png");  // 캐릭터
            imgKimchi = toolkit.getImage("./images/kimchi.png");    // 김치
            imgHam = toolkit.getImage("./images/ham.png");  // 햄
            imgFire = toolkit.getImage("./images/fire_02.png"); // 불
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
            for(int i = 0; i < kimchi.size(); i++) {
                Kimchi k = kimchi.get(i);
                g.drawImage(k.img, k.x-k.w, k.y-k.h, this);
            }
            for(int i = 0; i < ham.size(); i++) {
                Ham ha = ham.get(i);
                g.drawImage(ha.img, ha.x-ha.w, ha.y-ha.h, this);
            }
            for(int i = 0; i < fires.size(); i++) {
                Fire f = fires.get(i);
                g.drawImage(f.img, f.x-f.w, f.y-f.h, this);
            }
            g.drawImage(imgPlayer, x-w, y-h, this); // 캐릭터
            g.setFont(new Font(null, Font.BOLD, 20));   // 점수 표시
            g.drawString("SCORE : " + score, 10, 30);
            g.setFont(new Font(null, Font.BOLD, 20));   // 체력 표시
            g.drawString("HP : " + hp, 10, 70);
        }

        void move() {   // 플레이어 움직이기
            // 김치 움직이기
            // 중간에 배열의 개수 변경될 여지가 있다면
            // 맨 마지막 요소부터 거꾸로 0번 요소까지 역으로 처리
            for(int i = kimchi.size()-1; i >= 0; i--) {
                Kimchi k = kimchi.get(i);
                k.move();
                if(k.isDead == true)    // ArrayList에서 제거
                    kimchi.remove(i);
            }
            // 햄 움직이기
            for(int i = ham.size()-1; i >= 0; i--) {
                Ham ha = ham.get(i);
                ha.move();
                if(ha.isDead == true)    // ArrayList에서 제거
                    ham.remove(i);
            }
            for(int i = fires.size()-1; i >= 0; i--) {
                Fire f = fires.get(i);
                f.move();
                if(f.isDead == true)
                    fires.remove(i);
            }
            x += dx;
            y += dy;
            // 플레이어 좌표가 화면 밖으로 나가지 않게
            if(x < w) x = w;
            if(x > width - w) x = width - w;
            if(y < h) y = h;
            if(y > height - h) y = height - h;
        }

        void makeKimchi()   {   // 김치 생성 메소드
            if(width == 0 || height == 0) return;

            Random rnd = new Random();  // 50번에 한 번 꼴로 만들기
            int n = rnd.nextInt(25);
            if(n == 0) kimchi.add(new Kimchi(imgKimchi, width, height));
        }

        void makeHam() {    // 햄 생성 메소드
            if(width == 0 || height == 0) return;

            Random rnd = new Random();
            int n = rnd.nextInt(25);
            if(n == 0) ham.add(new Ham(imgHam, width, height));
        }

        void makeFire() {   // 불 생성 메소드
            if(width == 0 || height == 0) return;

            Random rnd = new Random();  // 50번에 한 번 꼴로 만들기
            int n = rnd.nextInt(40);
            if(n == 0) fires.add(new Fire(imgFire, width, height));
        }

        // 충돌 체크 작업 계산 메소드
        void KimchiCheckCollision() { // 플레이어와 버블의 충돌
            for(Kimchi k : kimchi) {
                int left = k.x - k.w;
                int right = k.x + k.w;
                int top = k.y - k.h;
                int bottom = k.y + k.h;

                if(x > left && x < right && y > top && y < bottom) {
                    k.isDead = true;    // 충돌
                    score += 5;
                }
            }
        }

        void HamCheckCollision() { // 플레이어와 버블의 충돌
            for(Ham ha : ham) {
                int left = ha.x - ha.w;
                int right = ha.x + ha.w;
                int top = ha.y - ha.h;
                int bottom = ha.y + ha.h;

                if(x > left && x < right && y > top && y < bottom) {
                    ha.isDead = true;    // 충돌
                    score += 10;
                }
            }
        }

        void FireCheckCollision() {
            for(Fire f : fires) {
                int left = f.x - f.w;
                int right = f.x + f.w;
                int top = f.y - f.h;
                int bottom = f.y + f.h;

                if(x > left && x < right && y > top && y < bottom) {
                    f.isDead = true;    // 충돌
                    score -= 20;
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
                panel.makeKimchi();
                panel.makeHam();
                panel.makeFire();
                panel.move();
                panel.KimchiCheckCollision(); // 충돌 체크 기능 호출
                panel.HamCheckCollision();
                panel.FireCheckCollision();
                try {   // 너무 빨리 돌아서 천천히 돌도록
                    sleep(25);
                    repaint();
                } catch (InterruptedException e) {}
            }
        }
    }

    public static void main(String[] args) {
        new Korean();
    }

}
