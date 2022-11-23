import javax.swing.*;
import javax.tools.Tool;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SelectedNational extends JFrame {
    public SelectedNational() {
        super("나라 선택");
        JPanel selectNationPanel = new JPanel();
        setSize(1980, 1080);    // 창 크기 설정

        ImageIcon imgKorea = new ImageIcon("./images/korea.png");
        ImageIcon imgJapan = new ImageIcon("./images/japan.png");
        ImageIcon imgChina = new ImageIcon("./images/china.png");

        setLayout(new GridLayout(3, 7, 0, 2));

        // 한식 버튼
        JButton btnKorea = new JButton(imgKorea);
        btnKorea.setBorderPainted(false);   // 버튼 외곽선 제거
        btnKorea.setContentAreaFilled(false);   // 버튼 내용 영역 채우지 않음
        btnKorea.setFocusPainted(false);    // 선택 되었을 때 생기는 테두리 제거

        // 일식 버튼
        JButton btnJapan = new JButton(imgJapan);
        btnJapan.setBorderPainted(false);   // 버튼 외곽선 제거
        btnJapan.setContentAreaFilled(false);   // 버튼 내용 영역 채우지 않음
        btnJapan.setFocusPainted(false);    // 선택 되었을 때 생기는 테두리 제거

        // 중식 버튼
        JButton btnChina = new JButton(imgChina);
        btnChina.setBorderPainted(false);   // 버튼 외곽선 제거
        btnChina.setContentAreaFilled(false);   // 버튼 내용 영역 채우지 않음
        btnChina.setFocusPainted(false);    // 선택 되었을 때 생기는 테두리 제거

        selectNationPanel.add(btnKorea);
        selectNationPanel.add(btnJapan);
        selectNationPanel.add(btnChina);
        add(selectNationPanel);

        Dimension frameSize = getSize();
        Dimension windowSize = Toolkit.getDefaultToolkit().getScreenSize();
        // 화면 중앙에 띄우기
        setLocation((windowSize.width - frameSize.width) / 2, (windowSize.height - frameSize.height) / 2);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);    // x 누르면 종료
        setVisible(true);
        setBackground(Color.white);

        // 버튼이 눌렸을 때 윈도우 실행
        btnKorea.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Korean();   // 버튼 누르면 한식으로
                setVisible(false);  // 창 안 보이게 하기
            }
        });

        btnJapan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Japan();   // 버튼 누르면 일식으로
                setVisible(false);  // 창 안 보이게 하기
            }
        });
    }

    public static void main(String[] args) {
        new SelectedNational();
    }
}
