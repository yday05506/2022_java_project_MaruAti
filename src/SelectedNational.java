import javax.swing.*;
import javax.tools.Tool;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SelectedNational extends JFrame {
    Image background;

    public SelectedNational() {
        super("나라 선택");
        setBounds(0,0,1980, 1080);    // 창 크기 설정
        setLayout(null);

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        background = toolkit.getImage("./images/background.jpg");   // 배경

        Container container = getContentPane();
        container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));    // 수평 정렬

        Dimension frameSize = getSize();
        Dimension windowSize = Toolkit.getDefaultToolkit().getScreenSize();

        // 한식 버튼
        Image imgKorea = new ImageIcon("./images/korea.png").getImage();
        Image koreaButton1 = imgKorea.getScaledInstance(270, 184, 0);
        ImageIcon koreaButton = new ImageIcon(koreaButton1);    // image -> imageIcon
        JButton btnKorea = new JButton(koreaButton);

//        btnKorea.setBounds(SCREEN_WIDTH/6, SCREEN_HEIGHT/3, koreaButton1.getWidth(btnKorea), koreaButton1.getHeight(btnKorea));
        btnKorea.setBorderPainted(false);
        btnKorea.setContentAreaFilled(false);
        btnKorea.setFocusPainted(false);
        btnKorea.setAlignmentX(CENTER_ALIGNMENT);   // 가운데 정렬

        // 버튼이 눌렸을 때 한식 윈도우 실행
        btnKorea.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Korean();   // 버튼 누르면 한식으로
                setVisible(false);  // 창 안 보이게 하기
            }
        });


        // 일식 버튼
        Image imgJapan = new ImageIcon("./images/japan.png").getImage();
        Image japanButton1 = imgJapan.getScaledInstance(270, 184, 0);
        ImageIcon japanButton = new ImageIcon(japanButton1);    // image -> imageIcon
        JButton btnJapan = new JButton(japanButton);

//        btnJapan.setBounds(SCREEN_WIDTH/4, SCREEN_HEIGHT/3, japanButton1.getWidth(btnJapan), japanButton1.getHeight(btnJapan));
        btnJapan.setBorderPainted(false);
        btnJapan.setContentAreaFilled(false);
        btnJapan.setFocusPainted(false);
        btnJapan.setAlignmentX(CENTER_ALIGNMENT);   // 가운데 정렬

        // 버튼이 눌렸을 때 일식 윈도우 실행
        btnJapan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Japan();   // 버튼 누르면 한식으로
                setVisible(false);  // 창 안 보이게 하기
            }
        });

        // 중식 버튼
        Image imgChina = new ImageIcon("./images/china.png").getImage();
        Image chinaButton1 = imgChina.getScaledInstance(270, 184, 0);
        ImageIcon chinaButton = new ImageIcon(chinaButton1);    // image -> imageIcon
        JButton btnChina = new JButton(chinaButton);

//        btnChina.setBounds(Button_X+400, SCREEN_HEIGHT/2, chinaButton1.getWidth(btnChina), chinaButton1.getHeight(btnChina));
        btnChina.setBorderPainted(false);
        btnChina.setContentAreaFilled(false);
        btnChina.setFocusPainted(false);
        btnChina.setAlignmentX(CENTER_ALIGNMENT);   // 가운데 정렬

        // 버튼이 눌렸을 때 중식 윈도우 실행
        btnChina.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Chinese();   // 버튼 누르면 중식으로
                setVisible(false);  // 창 안 보이게 하기
            }
        });

        container.add(Box.createHorizontalStrut(200));  // 가로 200만큼 빈 컴포넌트 삽입
        container.add(btnKorea);
        container.add(Box.createHorizontalStrut(300));  // 가로 300만큼 빈 컴포넌트 삽입
        container.add(btnJapan);
        container.add(Box.createHorizontalStrut(300));  // 가로 300만큼 빈 컴포넌트 삽입
        container.add(btnChina);

        // 화면 중앙에 띄우기
        setLocation((windowSize.width - frameSize.width) / 2, (windowSize.height - frameSize.height) / 2);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);    // x 누르면 종료
        setVisible(true);
        setBackground(Color.white);
    }

    public static void main(String[] args) {
        new SelectedNational();
    }
}
