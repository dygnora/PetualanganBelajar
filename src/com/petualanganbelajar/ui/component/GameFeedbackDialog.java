package com.petualanganbelajar.ui.component;

import com.petualanganbelajar.util.UIHelper;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class GameFeedbackDialog extends JDialog {

    public static final int TYPE_ERROR = 0;
    public static final int TYPE_SUCCESS = 1;

    // Warna Tema
    private final Color BG_CREAM = new Color(255, 253, 235); // Krem lembut
    private final Color COLOR_SUCCESS = new Color(76, 175, 80); // Hijau
    private final Color COLOR_ERROR = new Color(229, 57, 53);   // Merah
    
    private final Font FONT_TITLE = new Font("Comic Sans MS", Font.BOLD, 28);
    private final Font FONT_MSG = new Font("Comic Sans MS", Font.PLAIN, 20);

    public GameFeedbackDialog(Window owner, String title, String message, int type) {
        super(owner, ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0)); // Transparan mutlak untuk area luar

        // Panel utama dengan bentuk balon/kartu
        JPanel contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int arc = 50; 

                // 1. Bayangan Lembut (Drop Shadow)
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(8, 8, w - 16, h - 16, arc, arc);

                // 2. Background Panel
                g2.setColor(BG_CREAM);
                g2.fillRoundRect(4, 4, w - 16, h - 16, arc, arc);

                // 3. Border Tebal Berwarna
                g2.setColor(type == TYPE_SUCCESS ? COLOR_SUCCESS : COLOR_ERROR);
                g2.setStroke(new BasicStroke(5f)); // Ketebalan border 5px
                g2.drawRoundRect(4, 4, w - 16, h - 16, arc, arc);
                
                g2.dispose();
            }
        };
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        // Padding diperbesar agar konten tidak mepet
        contentPanel.setBorder(new EmptyBorder(30, 40, 30, 40)); 
        contentPanel.setOpaque(false);

        // --- 1. ICON (CUSTOM DRAWN untuk X, IMAGE untuk Bintang) ---
        JPanel iconPanel = createIconPanel(type);
        iconPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- 2. TITLE (Judul) ---
        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER); // Center Explicit
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(type == TYPE_SUCCESS ? COLOR_SUCCESS.darker() : COLOR_ERROR.darker());
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER); // Paksa Tengah

        // --- 3. MESSAGE (Pesan) ---
        // [FIX] Tambahkan SwingConstants.CENTER agar teks benar-benar di tengah
        JLabel lblMsg = new JLabel("<html><center>" + message + "</center></html>", SwingConstants.CENTER);
        lblMsg.setFont(FONT_MSG);
        lblMsg.setForeground(new Color(80, 80, 80)); // Abu tua
        lblMsg.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblMsg.setHorizontalAlignment(SwingConstants.CENTER); // Paksa Tengah Horizontal

        // --- 4. BUTTON (Tombol Modern) ---
        Color btnColor = type == TYPE_SUCCESS ? COLOR_SUCCESS : new Color(255, 179, 0); // Kuning Emas
        String btnText = type == TYPE_SUCCESS ? "OK, LANJUT!" : "COBA LAGI";
        
        DialogButton btnOk = new DialogButton(btnText, btnColor);
        btnOk.addActionListener(e -> dispose());

        // --- Menyusun Komponen ---
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(iconPanel);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(lblTitle);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(lblMsg);
        contentPanel.add(Box.createVerticalStrut(25));
        contentPanel.add(btnOk);
        contentPanel.add(Box.createVerticalStrut(10));

        setContentPane(contentPanel);
        pack(); 
        
        // Memastikan ukuran minimal
        int minW = 420;
        int minH = 320;
        if (getWidth() < minW) setSize(minW, getHeight());
        if (getHeight() < minH) setSize(getWidth(), minH);
        
        setLocationRelativeTo(owner);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 50, 50));
    }

    // --- Helper: Membuat Icon Panel (MENGGAMBAR X MANUAL) ---
    private JPanel createIconPanel(int type) {
        return new JPanel() {
            {
                setPreferredSize(new Dimension(100, 100)); // Ukuran area icon
                setMaximumSize(new Dimension(100, 100));
                setOpaque(false);
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int cx = getWidth() / 2;
                int cy = getHeight() / 2;

                if (type == TYPE_SUCCESS) {
                    // Coba Load Gambar Bintang Emas
                    ImageIcon icon = UIHelper.loadIcon("star_bright.png", 90, 90);
                    if (icon != null) {
                        icon.paintIcon(this, g2, (getWidth()-90)/2, (getHeight()-90)/2);
                    } else {
                        // Fallback jika gambar hilang
                        g2.setColor(new Color(255, 215, 0)); 
                        g2.fillOval(cx-30, cy-30, 60, 60);
                    }
                } else {
                    // [FIX] GAMBAR TANDA SILANG (X) MANUAL
                    // Ini tidak menggunakan Font, jadi tidak akan terpotong
                    g2.setColor(COLOR_ERROR);
                    g2.setStroke(new BasicStroke(15f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)); // Garis sangat tebal & bulat
                    
                    int r = 25; // Radius silang dari titik tengah
                    g2.drawLine(cx - r, cy - r, cx + r, cy + r); // Garis \
                    g2.drawLine(cx + r, cy - r, cx - r, cy + r); // Garis /
                }
            }
        };
    }

    // --- Inner Class: Tombol Dialog ---
    private class DialogButton extends JButton {
        private Color baseColor;
        private boolean hover = false;

        public DialogButton(String text, Color color) {
            super(text);
            this.baseColor = color;
            setFont(new Font("Comic Sans MS", Font.BOLD, 20));
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setAlignmentX(Component.CENTER_ALIGNMENT);
            setPreferredSize(new Dimension(200, 55)); 
            setMaximumSize(new Dimension(200, 55));

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(); int h = getHeight();

            int offset = getModel().isPressed() ? 3 : 0;

            g2.setColor(new Color(0, 0, 0, 30));
            g2.fillRoundRect(2, 5, w - 4, h - 5, 40, 40);

            g2.setColor(hover ? baseColor.brighter() : baseColor);
            g2.fillRoundRect(0, offset, w, h - offset - 3, 40, 40);

            g2.setColor(Color.WHITE);
            FontMetrics fm = g2.getFontMetrics();
            int x = (w - fm.stringWidth(getText())) / 2;
            int y = (h - offset - 3 - fm.getHeight()) / 2 + fm.getAscent() + offset;
            g2.drawString(getText(), x, y);

            g2.dispose();
        }
    }
}