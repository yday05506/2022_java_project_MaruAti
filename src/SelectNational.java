import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SelectNational extends JFrame {
    SelectNational() {
        JFrame nationalFrame = new JFrame();  // 프레임 생성

        JPanel nationalPanel = new JPanel();    // 패널 생성
        JButton KoreanBtn = new JButton(new ImageIcon("./images/korea.png"));  // 한국 선택
        JButton JapaneseBtn = new JButton(new ImageIcon("./images/japan.png"));  // 일본 선택
        JButton ChineseBtn = new JButton(new ImageIcon("./images/china.png"));  // 중국 선택

        nationalFrame.setVisible(true);   // 창 보이게 함
        nationalFrame.setSize(600, 600);
        // 창 전체화면
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        setUndecorated(true);
        gd.setFullScreenWindow(this);
        setLocationRelativeTo(null);    // 창 가운데
        setDefaultCloseOperation(EXIT_ON_CLOSE);    // 창 끄면 프로그램 종료

        // 패널 추가
        add(nationalPanel);
        // 패널 배경색
        nationalPanel.setBackground(Color.decode("#F5F5F5"));
        // 버튼 추가
        nationalPanel.add(KoreanBtn);
        nationalPanel.add(JapaneseBtn);
        nationalPanel.add(ChineseBtn);
        // 버튼 테두리 설정 해제
        KoreanBtn.setBorderPainted(false);
        JapaneseBtn.setBorderPainted(false);
        ChineseBtn.setBorderPainted(false);
        // 버튼 배경색
        KoreanBtn.setBackground(Color.decode("#F5F5F5"));
        JapaneseBtn.setBackground(Color.decode("#F5F5F5"));
        ChineseBtn.setBackground(Color.decode("#F5F5F5"));

        KoreanBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "한국 선택");
            }
        });

        JapaneseBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "일본 선택");
            }
        });

        ChineseBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "중국 선택");
            }
        });
    }


    public static void main(String[] args) {
        new SelectNational();
    }
}