import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameStory extends JFrame {

    public GameStory() {
        super("STORY");
        setBounds(0,0,1980,1080);
        setLayout(null);

        BackgroundPanel backPanel = new BackgroundPanel();
        setContentPane(backPanel);

        Container container = getContentPane();
        container.setLayout(new GridLayout(3, 3));

        Dimension frameSize = getSize();
        Dimension windowSize = Toolkit.getDefaultToolkit().getScreenSize();

        // 홈 버튼
        Image imgHome = new ImageIcon("./images/home_btn.png").getImage();
        Image homeButton1 = imgHome.getScaledInstance(500,500,0);
        ImageIcon homeButton = new ImageIcon(homeButton1);  // image -> imageIcon
        JButton btnHome = new JButton(homeButton);

        btnHome.setBorderPainted(false);    // 버튼 외곽선 제거
        btnHome.setContentAreaFilled(false);    // 버튼 내용 영역 채우기 안 함
        btnHome.setFocusPainted(false); // 선택했을 때 생기는 테두리 사용 안 함
        btnHome.setAlignmentX(CENTER_ALIGNMENT);    // 가운데 정렬

        btnHome.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SelectedNational(); // 버튼 누르면 나라 선택으로
                setVisible(false);  // 창 안 보이게 하기

            }
        });


        container.add(Box.createHorizontalStrut(-650));
        container.add(Box.createVerticalStrut(50));
        container.add(btnHome);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);    // x 누르면 종료
        setVisible(true);
        setBackground(Color.gray);
    }

    class BackgroundPanel extends JPanel {
        ImageIcon background = new ImageIcon("./images/background_story.jpg");    // 배경

        public void paintComponent(Graphics g) {
            g.drawImage(background.getImage(),10,0,getWidth(), getHeight(), this);
            setOpaque(false);
            super.paintComponent(g);
        }
    }


    public static void main(String[] args) {
        new GameStory();
    }
}
