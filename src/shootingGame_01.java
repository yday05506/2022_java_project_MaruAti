import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class shootingGame_01 {
    public static void main(String[] args) {
        game_Frame fms = new game_Frame();
    }
}

class game_Frame extends JFrame implements KeyListener, Runnable {
    int f_width;
    int f_height;

    int x, y;
    int[] cx = {0, 0, 0};   // 배경 스크롤 속도 제어용 변수
    int bx = 0; // 전체 배경 스크롤 용 변수

    boolean KeyUp = false;
    boolean KeyDown = false;
    boolean KeyLeft = false;
    boolean KeyRight = false;
    boolean KeySpace = false;

    int cnt;

    int player_speed;   // 캐릭터 움직이는 속도를 조절할 변수
    int egg_speed;  // 달걀 날아가는 속도 조절 변수
    int fire_speed; // 달걀 연사 속도 조절 변수
    int food_speed;    // 음식(적) 이동 속도 설정
    int player_status = 0;  // 유저 캐릭터 상태 체크 변수 0:평상 시, 1:달걀 발사, 2:충돌
    int game_score; // 게임 점수 계산
    int player_hp;  // 체력

    Thread th;
    Toolkit tk = Toolkit.getDefaultToolkit();

    Image[] player_img; // 애니메이션 표현을 위해 이미지 배열로 받음
    Image BackGround_img;   // 배경화면 이미지
    Image[] shift_img;  // 처치용 이미지 배열

    Image Egg_img;
    Image Food_img;
    ArrayList Egg_List = new ArrayList();
    ArrayList Food_List = new ArrayList();
    ArrayList Shift_List = new ArrayList(); // 다수의 처치 이벤트를 처리하기 위한 배열

    Image buffImage;
    Graphics buffg;

    Egg egg;
    Food food;

    Shift shift;    // 처치 이펙트용 클래스 접근 키

    game_Frame() {
        init();
        start();

        setTitle("식재료 얻기");
        setSize(f_width, f_height);
        Dimension screen = tk.getScreenSize();

        int f_xpos = (int)(screen.getWidth() / 2 - f_width / 2);
        int f_ypos = (int)(screen.getHeight() / 2 - f_height / 2);

        setLocation(f_xpos, f_ypos);
        setResizable(false);
        setVisible(true);
    }

    public void init() {
        x = 100;
        y = 100;
        f_width = 1200;
        f_height = 600;

        // 이미지 만드는 방식을 ImageIcon으로 변경
        Egg_img = new ImageIcon("fried_egg.png").getImage();
        Food_img = new ImageIcon("bubble.png").getImage();

        // 플레이어 애니메이션 표현을 위해 파일 이름을 넘버마다 나눠 배열로 담음
        player_img = new Image[2];
        for(int i = 0; i < player_img.length; ++i) {
            player_img[i] = new ImageIcon("mouse_0" + i + ".png").getImage();
        }

        // 전체 배경화면 이미지 받기
        //BackGround_img = new ImageIcon("background.png").getImage();

        // 처치 애니메이션 표현을 위해 파일 이름을 넘버마다 나눠 배열로 담음
        // 모든 이미지는 Swing의 ImageIcon으로 받아 이미지 넓이, 높이, 값을 바로 얻을 수 있게 함
        shift_img = new Image[3];
        for(int i = 0; i < shift_img.length; ++i) {
            shift_img[i] = new ImageIcon("shift_0" + i + ".png").getImage();
        }

        game_score = 0; // 게임 스코어 초기화
        player_hp = 3;  // 최초 플레이어 체력
        player_speed = 5;   // 캐릭터 움직이는 속도 설정
        egg_speed = 11; // 달걀 움직임 속도 설정
        fire_speed = 15;    // 연사 속도 설정
        food_speed = 7; // 적이 날아오는 속도 설정
    }

    public void start() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addKeyListener(this);

        th = new Thread(this);
        th.start();
    }

    public void run() {
        try {
            while(true) {
                KeyProcess();
                FoodProcess();
                EggProcess();

                ShiftProcess(); // 음식 처리 메소드 실행

                repaint();

                Thread.sleep(20);
                cnt++;
            }
        } catch (Exception e) {}
    }

    public void EggProcess() {
        if(KeySpace) {
            player_status = 1;  // 달걀 발사 → 플레이어 캐릭터 상태를 1로 변경
            if((cnt % fire_speed) == 0) {   // 플레이어 달걀 연사 속도 조절
                egg = new Egg(x +150, y + 30, egg_speed);    // 미사일 이동 속도 값 추가 받기
                Egg_List.add(egg);
            }
        }

        for(int i = 0; i < Egg_List.size(); ++i) {
            egg = (Egg) Egg_List.get(i);
            egg.move();
            if(egg.x > f_width - 20) {
                Egg_List.remove(i);
            }

            for(int j = 0; j < Food_List.size(); ++j) {
                food = (Food) Food_List.get(j);

                if(Crash(egg.x, egg.y, food.x, food.y, Egg_img, Food_img)) {
                    // 달걀 좌표 및 이미지 파일, 음식 좌표 및 이미지 파일을 받아
                    // 충돌 판정 메소드로 넘기고, true, false 값을 리턴 받아
                    // true면 아래 실행
                    Egg_List.remove(i);
                    Food_List.remove(j);

                    game_score += 10;   // 게임 점수 +10점

                    // 적이 위치해 있는 곳의 중심 좌표 x, y 값과
                    // 처치 설정을 받은 값(0 또는 1)을 받음
                    // 처치 설정 값 - 0:폭발, 1:단순 처치
                    shift = new Shift(food.x + Food_img.getWidth(null) / 2, food.y + Food_img.getHeight(null) / 2, 0, 0);
                    Shift_List.add(shift);  // 충돌 판정으로 사라진 적의 위치에 이펙트 추가
                }
            }
        }
    }

    public void FoodProcess() {
        for(int i = 0; i < Food_List.size(); ++i) {
            food = (Food) (Food_List.get(i));
            food.move();
            if(food.x < -200) {
                Food_List.remove(i);
            }

            if(Crash(x, y, food.x, food.y, player_img[0], Food_img)) {
                // 플레이어와 적의 충돌을 판정하여 boolean값을 리턴 받아
                // true면 아래 실행
                player_hp--;    // 플레이어 체력 -1
                Food_List.remove(i);    // 음식 제거
                game_score += 10;   // 제거된 음식으로 게임 스코어 10 증가

                // 음식이 위치해 있는 곳의 중심 좌표 x, y 값과
                // 처치 설정을 받은 값(0 또는 1)을 받음
                // 처치 설정 값 - 0:폭발, 1:단순 처치
                shift = new Shift(food.x, Food_img.getWidth(null) / 2, food.y + Food_img.getHeight(null)/ 2, 0);
                Shift_List.add(shift);  // 제거된 음식 위치에 처치 이펙트 추가
                // 적이 위치해 있는 곳의 중심 좌표 x, y 값과
                // 처치 설정을 받은 값(0 또는 1)을 받음
                // 처치 설정 값 - 0:폭발, 1:단순 처치
                shift = new Shift(x, y, 1, 0);
                Shift_List.add(shift);  // 충돌 시 캐릭터 위치에 처치용 이펙트 추가
            }
        }

        if(cnt % 200 == 0) {
            // 적 움직임 속도를 추가로 받아 적 생성
            food = new Food(f_width + 100, 100, food_speed);
            Food_List.add(food);
            food = new Food(f_width + 100, 200, food_speed);
            Food_List.add(food);
            food = new Food(f_width + 100, 300, food_speed);
            Food_List.add(food);
            food = new Food(f_width + 100, 400, food_speed);
            Food_List.add(food);
            food = new Food(f_width + 100, 500, food_speed);
            Food_List.add(food);
        }
    }

    // 처치 이펙트 처리용 메소드
    public void ShiftProcess() {
        for(int i = 0; i < Shift_List.size(); ++i) {
            shift = (Shift) Shift_List.get(i);
            // 이펙트 애니메이션을 나타내기 위해 이펙트 처리 추가가 발생하면 호출
            shift.effect();
        }
    }

    public boolean Crash(int x1, int y1, int x2, int y2, Image img1, Image img2) {
        // 기존 충돌 판정 소스 변경
        // 이미지 변수를 바로 받아 해당 이미지의 넓이, 높이 값을 바로 계산

        boolean check = false;

        // 이미지 넓이, 높이 값을 바로 받아 계산
        if(Math.abs((x1 + img1.getWidth(null) / 2) - (x2+img2.getWidth(null) / 2)) < (img2.getWidth(null) / 2 + img1.getWidth(null) / 2) && Math.abs((y1 + img1.getHeight(null) / 2) - (y2 + img2.getHeight(null) / 2)) < (img2.getHeight(null) / 2 + img1.getHeight(null) / 2)) {
            check = true;   // 위 값이 true면 check에 true 전달
        } else {check = false;}
        return check;   // check의 값을 메소드에 리턴
    }

    public void paint(Graphics g) {
        buffImage = createImage(f_width, f_height);
        buffg = buffImage.getGraphics();

        update(g);
    }

    public void update(Graphics g) {
        // Draw_Background();  // 배경 이미지 그리기 메소드 실행
        Draw_Player();  // 캐릭터 그리는 메소드 이름

        Draw_Food();
        Draw_Egg();

        Draw_Shift();   // 처치 이벤트 그리기 메소드 실행
        Draw_StatusText();  // 상태 표시 텍스트 그리는 메소드 실행

        g.drawImage(buffImage, 0, 0, this);
    }

    // 배경 이미지 그리는 메소드
//    public Draw_BackGround() {
//        // 화면 지우기 명령 여기서 실행
//        buffg.clearRect(0,0,f_width,f_height);
//
//        // 기본 값이 0인 bx가 -3500보다 크면 실행
//        if(bx > -3500) {
//            // bx를 0에서 -1만큼 계속 줄이므로 배경 이미지의 x 좌표는
//            // 계속 좌측으로 이동
//            // 전체 배경은 천천히 좌측으로 움직이게 됨
//            buffg.drawImage(BackGround_img, bx, 0, this);
//            bx -= 1;
//        } else {bx = 0;}
////        for(int i = 0; i < cx.length; ++i) {
////            if(cx[i] < 1400) {
////                cx[i] += 5 + i * 3;
////            } else {cx[i] = 0;}
////        }
//    }

    public void Draw_Player() {
        switch (player_status) {
            case 0: // 평상 시
                if((cnt / 5 % 2) == 0) {
                    buffg.drawImage(player_img[0], x, y, this);
                } else {buffg.drawImage(player_img[1],x,y,this);}
                break;
            case 1: // 달걀 발사
                if((cnt / 5 % 2) == 0) {
                    buffg.drawImage(player_img[0],x,y,this);
                } else {buffg.drawImage(player_img[1],x,y,this);}
                player_status = 0;  // 미사일 쏘기가 끝나면 플레이어 상태를 0으로 돌림
                break;
            case 2: // 충돌
                break;
        }
    }

    public void Draw_Egg() {
        for(int i = 0; i < Egg_List.size(); ++i) {
            egg = (Egg) (Egg_List.get(i));
            buffg.drawImage(Egg_img, egg.x, egg.y, this);
        }
    }

    public void Draw_Food() {
        for(int i = 0; i < Egg_List.size(); ++i) {
            egg = (Egg) (Egg_List.get(i));
            buffg.drawImage(Egg_img, egg.x, egg.y, this);
        }
    }

    // 폭발 이펙트 그리는 부분
    public void Draw_Shift() {
        for(int i = 0; i < Shift_List.size(); ++i) {
            // 처치 이펙트의 존재 유무를 체크하여 리스트를 받음
            shift = (Shift) Shift_List.get(i);

            // 설정 값이 0이면 처치용 이미지 그리기
            if(shift.damage == 0) {
                if(shift.shift_cnt < 7) {
                    buffg.drawImage(shift_img[0],shift.x-shift_img[0].getWidth(null) / 2, shift.y - shift_img[0].getHeight(null)/ 2, this);
                } else if(shift.shift_cnt < 14) {
                    buffg.drawImage(shift_img[1], shift.x-shift_img[1].getWidth(null)/2, shift.y-shift_img[1].getHeight(null)/2, this);
                } else if(shift.shift_cnt < 21) {
                    buffg.drawImage(shift_img[2], shift.x - shift_img[2].getWidth(null)/2, shift.y-shift_img[2].getHeight(null)/2, this);
                } else if(shift.shift_cnt > 21) {
                    Shift_List.remove(i);
                    // 폭발은 따로 카운터를 계산하여 이미지를 순차적으로 그림
                    shift.shift_cnt = 0;
                }
            } else {    // 설정값이 1이면 단순 피격용 이미지
                if(shift.shift_cnt < 7) {
                    buffg.drawImage(shift_img[0], shift.x + 120, shift.y + 15, this);
                } else if(shift.shift_cnt < 14) {
                    buffg.drawImage(shift_img[1], shift.x + 60, shift.y + 5, this);
                } else if(shift.shift_cnt < 21) {
                    buffg.drawImage(shift_img[0], shift.x + 5, shift.y + 10, this);
                } else if(shift.shift_cnt > 21) {
                    Shift_List.remove(i);
                    shift.shift_cnt = 0;
                }
            }
        }
    }

    public void Draw_StatusText() { // 상태 체크용 텍스트
        // 폰트 설정 : 기본 폰트, 굵게, 사이즈 20
        buffg.setFont(new Font("Default", Font.BOLD, 20));

        // 좌표 x:1000, y:70에 스코어 표시
        buffg.drawString("SCORE : " + game_score, 1000, 70);

        // 좌표 x:1000, y:90에 플레이어 체력 표시
        buffg.drawString("HP : " + player_hp, 1000, 90);
    }

    public void KeyProcess() {
        if(KeyUp == true) {
            if(y > 20) y -= 5;  // 캐릭터가 보여지는 화면 위로 못 넘어가게
            player_status = 0;  // 이동키가 눌려지면 플레이어 상태를 0으로 돌림
        }

        if(KeyDown == true) {
            // 캐릭터가 보여지는 화면 아래로 넘어가지 못하게
            if(y + player_img[0].getHeight(null) < f_height) y += 5;
            player_status = 0;  // 이동키가 눌려지면 플레이어 상태 0으로 돌림
        }

        if(KeyLeft == true) {
            if(x > 0) x -= 5;   // 캐릭터가 보여지는 화면 왼쪽으로 못 넘어감
            player_status = 0;  // 이동키가 눌려지면 플레이어 상태 0으로 돌림
        }

        if(KeyRight == true) {
            // 캐릭터가 보여지는 화면 오른쪽으로 못 넘어가게 함
            if(x + player_img[0].getWidth(null) < f_width) x += 5;
            player_status = 0;  // 이동키가 눌려지면 플레이어 상태 0으로 돌림
        }
    }

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                KeyUp = true;
                break;
            case KeyEvent.VK_DOWN:
                KeyDown = true;
                break;
            case KeyEvent.VK_LEFT:
                KeyLeft = true;
                break;
            case KeyEvent.VK_RIGHT:
                KeyRight = true;
                break;

            case KeyEvent.VK_SPACE:
                KeySpace = true;
                break;
        }
    }

    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                KeyUp = false;
                break;
            case KeyEvent.VK_DOWN:
                KeyDown = false;
                break;
            case KeyEvent.VK_LEFT:
                KeyLeft = false;
                break;
            case KeyEvent.VK_RIGHT:
                KeyRight = false;
                break;

            case KeyEvent.VK_SPACE:
                KeySpace = false;
                break;
        }
    }

    public void keyTyped(KeyEvent e) {}

    class Egg {
        int x;
        int y;
        int speed;  // 달걀 스피드 변수 추가

        Egg(int x, int y, int speed) {
            this.x = x;
            this.y = y;
            this.speed = speed; // 객체 생성 시 속도 값을 추가로 받음
        }

        public void move() {
            x += speed; // 달걀 스피드 속도만큼 이동
        }
    }

    class Food {
        int x;
        int y;
        int speed;  // 적 이동 변수 추가

        Food(int x, int y, int speed) {
            this.x = x;
            this.y = y;
            this.speed = speed; // 객체 생성 시 속도 값을 추가로 받음
        }

        public void move() {
            x -= speed; // 적 이동 속도만큼 이동
        }
    }

    class Shift {
        // 여러 개의 폭발 이미지를 그리기 위해 클래스를 추가하여 객체 관리
        int x;  // 이미지 그릴 x 좌표
        int y;  // 이미지 그릴 y 좌표
        int shift_cnt;  // 이미지를 순차적으로 그리기 위한 카운터
        int damage; // 이미지 종류를 구분하기 위한 변수값

        Shift(int x, int y, int damage, int i) {
            this.x = x;
            this.y = y;
            this.damage = damage;
            shift_cnt = 0;
        }

        public void effect() {
            shift_cnt++;    // 해당 메소드 호출 시 카운터를 +1 시킴
        }
    }
}