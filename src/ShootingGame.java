import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ShootingGame extends JFrame implements Runnable {
    // 이미지 파일 불러오는 툴킷
    Toolkit imageTool = Toolkit.getDefaultToolkit();
    Image character = imageTool.getImage("./images/mouse.png");

    // 이미지 버퍼
    Image buffImg;
    Graphics buffG;

    // 플레이어 위치값
    int xpos = 100;
    int ypos = 100;

    public ShootingGame() {
        // 프레임 설정
        setTitle("식재료 얻기"); // 프레임 제목
        setSize(860,480);   // 프레임 크기 설정
        setResizable(false);    // 프레임의 크기 변경 못하게 설정
        setVisible(true);   // 프레임 보이기
        setDefaultCloseOperation(EXIT_ON_CLOSE);    // x버튼 누르면 종료

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        ypos -= 5;
                        break;
                    case KeyEvent.VK_DOWN:
                        ypos += 5;
                        break;
                    case KeyEvent.VK_LEFT:
                        xpos -= 5;
                        break;
                    case KeyEvent.VK_RIGHT:
                        xpos += 5;
                        break;
                }
            }
        });
    }

    public void paint(Graphics g) {
        buffImg = createImage(getWidth(), getHeight());  // 버퍼링용 이미지
        buffG = buffImg.getGraphics();  // 붓?
        update(g);
    }

    public void update(Graphics g) {
        buffG.clearRect(0,0,860,480);   // 백지화
        buffG.drawImage(character, xpos, ypos, this);   // 캐릭터 그리기
        g.drawImage(buffImg,0,0,this);  // 화면 g에 버퍼(buffG)에 그려진 이미지 옮김
        repaint();
    }

    @Override
    public void run() {
        try {
            while(true) {
                repaint();
                Thread.sleep(15);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ShootingGame shootingGame = new ShootingGame();
        new Thread(shootingGame).start();
    }
}