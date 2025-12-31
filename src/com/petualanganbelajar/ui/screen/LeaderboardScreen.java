package com.petualanganbelajar.ui.screen;

import com.petualanganbelajar.core.ScreenManager;
import com.petualanganbelajar.core.SoundPlayer;
import com.petualanganbelajar.model.LeaderboardEntry;
import com.petualanganbelajar.repository.LeaderboardRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;
import java.util.List;

public class LeaderboardScreen extends JPanel {

    // --- ASSETS ---
    private Image bgImage;
    private Image titleImage;
    private final LeaderboardRepository repo = new LeaderboardRepository();
    private JPanel listContainer;

    // --- STATS COMPONENTS ---
    private StatCard totalCard;
    private StatCard avgCard;
    private StatCard topCard;

    // --- THEME CONSTANTS ---
    private static final Color COL_BG_DARK = new Color(15, 23, 42); 
    private static final Color COL_BG_LIGHT = new Color(30, 41, 59); 
    
    // Accent Colors
    private static final Color COL_ACCENT_BLUE = new Color(59, 130, 246);
    private static final Color COL_ACCENT_GOLD = new Color(251, 191, 36);
    private static final Color COL_ACCENT_SILVER = new Color(192, 192, 192);
    private static final Color COL_ACCENT_BRONZE = new Color(205, 127, 50);
    private static final Color COL_ACCENT_RED = new Color(220, 38, 38);

    // Text Colors
    private static final Color COL_TEXT_WHITE = new Color(248, 250, 252); 
    private static final Color COL_TEXT_GRAY = new Color(160, 174, 192);  

    public LeaderboardScreen() {
        setLayout(new BorderLayout());
        loadAssets();
        initUI();
    }

    @Override
    public void setVisible(boolean flag) {
        super.setVisible(flag);
        if (flag) {
            loadDataAsync(); // UBAH: Panggil versi Async
        }
    }

    // --- LOGIC BARU: SWING WORKER (ANTI-LAG) ---
    private void loadDataAsync() {
        // 1. Tampilkan status loading sementara
        listContainer.removeAll();
        
        JLabel loading = new JLabel("Sedang memuat data...", SwingConstants.CENTER);
        loading.setFont(new Font("Segoe UI", Font.ITALIC, 18));
        loading.setForeground(COL_TEXT_GRAY);
        loading.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        listContainer.add(Box.createVerticalStrut(50));
        listContainer.add(loading);
        listContainer.revalidate();
        listContainer.repaint();

        // 2. Jalankan Worker di Background
        new SwingWorker<List<LeaderboardEntry>, Void>() {
            @Override
            protected List<LeaderboardEntry> doInBackground() throws Exception {
                // Proses berat (Database) terjadi di sini
                return repo.getTopScores();
            }

            @Override
            protected void done() {
                try {
                    // Update UI setelah data siap
                    updateUIWithData(get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    // Method ini isinya sama persis dengan refreshData() lama Anda
    private void updateUIWithData(List<LeaderboardEntry> data) {
        listContainer.removeAll(); // Hapus loading text

        if (data.isEmpty()) {
            updateStats(0, 0, 0);
            
            JLabel empty = new JLabel("Belum ada data juara", SwingConstants.CENTER);
            empty.setFont(new Font("Segoe UI", Font.BOLD, 24));
            empty.setForeground(COL_TEXT_GRAY);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            listContainer.add(Box.createVerticalStrut(50));
            listContainer.add(empty);
        } else {
            int total = data.size();
            int maxScore = data.stream().mapToInt(LeaderboardEntry::getScore).max().orElse(0);
            int totalScore = data.stream().mapToInt(LeaderboardEntry::getScore).sum();
            int avgScore = total > 0 ? totalScore / total : 0;

            updateStats(total, avgScore, maxScore);

            int rank = 1;
            for (LeaderboardEntry e : data) {
                String avatar = (e.getAvatar() != null) ? e.getAvatar() : "avatar_" + ((rank % 2) + 1) + ".png";
                
                ScoreCard card = new ScoreCard(rank, e.getPlayerName(), e.getScore(), avatar);
                card.setAlignmentX(Component.CENTER_ALIGNMENT); 
                listContainer.add(card);
                listContainer.add(Box.createVerticalStrut(10));
                rank++;
            }
        }
        
        listContainer.revalidate();
        listContainer.repaint();
    }

    private void loadAssets() {
        try {
            // UBAH KE GETRESOURCE AGAR SUPPORT JAR (Sesuai Audit)
            URL bgUrl = getClass().getResource("/images/bg_menu.png");
            if (bgUrl != null) bgImage = new ImageIcon(bgUrl).getImage();

            URL titleUrl = getClass().getResource("/images/title_papan_juara.png");
            if (titleUrl != null) titleImage = new ImageIcon(titleUrl).getImage();
        } catch (Exception ignored) {}
    }

    private void initUI() {
        // --- 1. HEADER (NORTH) ---
        add(createHeader(), BorderLayout.NORTH);

        // --- 2. CENTER CONTENT (WRAPPER) ---
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setOpaque(false);
        contentWrapper.setBorder(new EmptyBorder(0, 20, 0, 20)); 

        // A. STATS PANEL (NORTH of Center)
        contentWrapper.add(createStatsPanel(), BorderLayout.NORTH);

        // B. LIST CONTAINER (CENTER of Center)
        JPanel listWrapper = new JPanel(new GridBagLayout());
        listWrapper.setOpaque(false);

        listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setOpaque(false);
        
        JScrollPane scroll = new JScrollPane(listContainer);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.getVerticalScrollBar().setUI(new ModernScrollBar());
        
        scroll.setPreferredSize(new Dimension(650, 320)); 

        listWrapper.add(scroll); 
        contentWrapper.add(listWrapper, BorderLayout.CENTER);
        
        add(contentWrapper, BorderLayout.CENTER);

        // --- 3. FOOTER (SOUTH) ---
        add(createFooter(), BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(800, 200));
        header.setBorder(new EmptyBorder(-80, 0, -100, 0));

        JLabel titleLabel = new JLabel();
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        if (titleImage != null) {
            Image scaled = titleImage.getScaledInstance(400, 240, Image.SCALE_SMOOTH);
            titleLabel.setIcon(new ImageIcon(scaled));
        } else {
            titleLabel.setText("PAPAN JUARA");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
            titleLabel.setForeground(COL_TEXT_WHITE);
        }
        header.add(titleLabel, BorderLayout.CENTER);
        return header;
    }

    private JPanel createStatsPanel() {
        JPanel stats = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        stats.setOpaque(false);
        stats.setPreferredSize(new Dimension(800, 80));
        stats.setBorder(new EmptyBorder(0, 0, 10, 0));

        totalCard = new StatCard("Total Pemain", "0", "👥", COL_ACCENT_BLUE);
        avgCard = new StatCard("Rata-rata", "0 Poin", "⭐", COL_ACCENT_GOLD);
        topCard = new StatCard("Tertinggi", "0 Poin", "🔥", COL_ACCENT_RED);

        stats.add(totalCard);
        stats.add(avgCard);
        stats.add(topCard);
        
        return stats;
    }

    private JPanel createFooter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(800, 80));
        p.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        GradientButton btnBack = new GradientButton("KEMBALI", new Color(220, 38, 38), new Color(153, 27, 27));
        btnBack.addActionListener(e -> { 
            playSound("click"); 
            ScreenManager.getInstance().showScreen("MAIN_MENU"); 
        });

        p.add(btnBack);
        return p;
    }

    private void updateStats(int total, int avg, int max) {
        if (totalCard != null) totalCard.setValue(String.valueOf(total));
        if (avgCard != null) avgCard.setValue(avg + " Poin");
        if (topCard != null) topCard.setValue(max + " Poin");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        if (bgImage != null) {
            g2.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g2.setPaint(new GradientPaint(0, 0, COL_BG_DARK, 0, getHeight(), COL_BG_LIGHT));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        g2.setColor(new Color(0, 0, 0, 140)); 
        g2.fillRect(0, 0, getWidth(), getHeight());
    }

    private void playSound(String name) {
        try { SoundPlayer.getInstance().playSFX(name + ".wav"); } catch (Exception ignored) {}
    }

    // ============================================================
    // COMPONENTS (Inner Classes - Tidak Diubah)
    // ============================================================

    class StatCard extends JPanel {
        private String value;
        private final String label, icon;
        private final Color accentColor;

        StatCard(String label, String value, String icon, Color color) {
            this.label = label;
            this.value = value;
            this.icon = icon;
            this.accentColor = color;
            setOpaque(false);
            setPreferredSize(new Dimension(210, 75));
        }

        void setValue(String value) {
            this.value = value;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(30, 41, 59, 220));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

            g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 120));
            g2.setStroke(new BasicStroke(1));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);

            g2.setColor(accentColor);
            g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28)); 
            g2.drawString(icon, 15, 48);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
            g2.setColor(COL_TEXT_WHITE);
            g2.drawString(value, 60, 32);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.setColor(COL_TEXT_GRAY);
            g2.drawString(label, 60, 52);
        }
    }

    class ScoreCard extends JPanel {
        private final int rank;
        private final String name;
        private final int score;
        private Image avatar;
        private boolean isHovered = false;

        ScoreCard(int rank, String name, int score, String avatarFile) {
            this.rank = rank; this.name = name; this.score = score;
            setOpaque(false);
            setPreferredSize(new Dimension(600, 85)); 
            setMaximumSize(new Dimension(600, 85));
            
            try {
                // FIXED: Resource Loading
                URL url = getClass().getResource("/images/" + avatarFile);
                if(url != null) avatar = new ImageIcon(url).getImage();
            } catch(Exception ignored) {}

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth(); 
            int h = getHeight();

            if(isHovered) g2.translate(0, -2);

            g2.setColor(new Color(30, 41, 59, 220));
            g2.fillRoundRect(0, 0, w, h, 20, 20);
            
            Color rankColor;
            if (rank == 1) rankColor = COL_ACCENT_GOLD;
            else if (rank == 2) rankColor = COL_ACCENT_SILVER;
            else if (rank == 3) rankColor = COL_ACCENT_BRONZE;
            else rankColor = COL_ACCENT_BLUE.darker();

            g2.setColor(rankColor);
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(1, 1, w-2, h-2, 20, 20);

            g2.setColor(rank <= 3 ? rankColor : COL_TEXT_WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 26));
            g2.drawString("#" + rank, 25, 50);

            if(avatar != null) g2.drawImage(avatar, 85, 12, 60, 60, this);
            
            g2.setColor(COL_TEXT_WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
            g2.drawString(name, 165, 38);
            
            g2.setColor(COL_TEXT_GRAY);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            g2.drawString("Level " + (score/100), 165, 60);

            g2.setColor(COL_ACCENT_GOLD); 
            g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
            String s = score + " Poin";
            g2.drawString(s, w - g2.getFontMetrics().stringWidth(s) - 30, 50);

            if (rank == 1) {
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
                g2.drawString("👑", 10, 25);
            }
        }
    }

    class GradientButton extends JButton {
        private final Color color1;
        private final Color color2;
        private boolean hover;

        GradientButton(String text, Color c1, Color c2) {
            super(text);
            this.color1 = c1;
            this.color2 = c2;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 18));
            setPreferredSize(new Dimension(200, 55));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth();
            int h = getHeight();

            g2.setColor(new Color(0,0,0,60));
            g2.fillRoundRect(3, 5, w-6, h-5, 25, 25);

            GradientPaint gp = new GradientPaint(0, 0, hover ? color1.brighter() : color1, 
                                                 0, h, hover ? color2.brighter() : color2);
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, w, h-5, 25, 25);

            FontMetrics fm = g2.getFontMetrics();
            int x = (w - fm.stringWidth(getText())) / 2;
            int y = ((h - 5 - fm.getHeight()) / 2) + fm.getAscent();
            
            g2.setColor(new Color(0,0,0,40));
            g2.drawString(getText(), x+1, y+1);
            
            g2.setColor(Color.WHITE);
            g2.drawString(getText(), x, y);
        }
    }
    
    class ModernScrollBar extends BasicScrollBarUI {
        @Override protected void configureScrollBarColors() { thumbColor = new Color(255,255,255,50); trackColor = new Color(0,0,0,0); }
        @Override protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
        @Override protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
        @Override protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 10, 10);
        }
        private JButton createZeroButton() { JButton j=new JButton(); j.setPreferredSize(new Dimension(0,0)); return j; }
    }
}