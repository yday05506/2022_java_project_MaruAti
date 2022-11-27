import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameDescription extends JFrame {

    public GameDescription() {
        super("GAME DESCRIPTION");
        setBounds(0,0,1980,1080);
        setLayout(null);

        BackgroundPanel backPanel = new BackgroundPanel();
        setContentPane(backPanel);

        Container container = getContentPane();
        container.setLayout(new GridLayout(3, 1));

        Dimension frameSize = getSize();
        Dimension windowSize = Toolkit.getDefaultToolkit().getScreenSize();

        // 홈 버튼
        Image imgHome = new ImageIcon("./images/home_btn.png").getImage();
        Image homeButton1 = imgHome.getScaledInstance(500,500,0);
        ImageIcon homeButton = new ImageIcon(homeButton1);  // image -> imageIcon
        JButton btnHome = new JButton(homeButton);

        btnHome.setBorderPainted(false);
        btnHome.setContentAreaFilled(false);
        btnHome.setFocusPainted(false);
        btnHome.setAlignmentX(CENTER_ALIGNMENT);    // 가운데 정렬

        btnHome.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SelectedNational(); // 버튼 누르면 나라 선택으로
                setVisible(false);  // 창 안 보이게 하기

            }
        });


        container.add(Box.createHorizontalStrut(650));
        container.add(Box.createVerticalStrut(10));
        container.add(btnHome);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);    // x 누르면 종료
        setVisible(true);
        setBackground(Color.gray);
    }

    class BackgroundPanel extends JPanel {
        ImageIcon background = new ImageIcon("./images/background_description.jpg");    // 배경

        public void paintComponent(Graphics g) {
            g.drawImage(background.getImage(),0,0,getWidth(), getHeight(), this);
            setOpaque(false);
            super.paintComponent(g);
        }
    }


    public static void main(String[] args) {
        new GameOver();
    }
}
