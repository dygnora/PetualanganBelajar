package com.petualanganbelajar.ui.screen;

import com.petualanganbelajar.core.ScreenManager;
import com.petualanganbelajar.core.SoundPlayer;
import com.petualanganbelajar.model.LeaderboardEntry;
import com.petualanganbelajar.repository.LeaderboardRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.List;

public class LeaderboardScreen extends JPanel {

    private Image bgImage;
    private Image titleImage;
    private final LeaderboardRepository repo = new LeaderboardRepository();
    private JPanel listContainer;
    
    private JPanel headerPanel;
    private JPanel statsPanel;
    private JPanel contentWrapper;
    private JScrollPane scrollPane;
    private JPanel footerPanel;

    private StatCard totalCard;
    private StatCard avgCard;
    private StatCard topCard;
    private JLabel titleLabel;
    private GradientButton btnBack;

    private static final Color COL_BG_DARK = new Color(15, 23, 42); 
    private static final Color COL_BG_LIGHT = new Color(30, 41, 59); 
    
    private static final Color COL_ACCENT_BLUE = new Color(59, 130, 246);
    private static final Color COL_ACCENT_GOLD = new Color(251, 191, 36);
    private static final Color COL_ACCENT_SILVER = new Color(192, 192, 192);
    private static final Color COL_ACCENT_BRONZE = new Color(205, 127, 50);
    private static final Color COL_ACCENT_RED = new Color(220, 38, 38);

    private static final Color COL_TEXT_WHITE = new Color(248, 250, 252); 
    private static final Color COL_TEXT_GRAY = new Color(160, 174, 192);  

    private final float BASE_W = 1920f;
    private final float BASE_H = 1080f;
    private float scaleFactor = 1.0f;

    public LeaderboardScreen() {
        setLayout(new BorderLayout());
        loadAssets();
        initUI();
        
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                calculateScaleFactor();
                updateResponsiveLayout();
            }
        });
    }
    
    private void calculateScaleFactor() {
        if (getWidth() <= 0 || getHeight() <= 0) return;
        float sW = (float) getWidth() / BASE_W;
        float sH = (float) getHeight() / BASE_H;
        this.scaleFactor = Math.min(sW, sH);
        if (this.scaleFactor < 0.5f) this.scaleFactor = 0.5f;
    }

    private void updateResponsiveLayout() {
        if (headerPanel != null) {
            headerPanel.setPreferredSize(new Dimension((int)(800*scaleFactor), (int)(400*scaleFactor)));
            headerPanel.setBorder(new EmptyBorder((int)(-60*scaleFactor), 0, (int)(-80*scaleFactor), 0));
        }
        
        if (titleLabel != null) {
            if (titleImage != null) {
                Image scaled = titleImage.getScaledInstance((int)(700*scaleFactor), (int)(450*scaleFactor), Image.SCALE_SMOOTH);
                titleLabel.setIcon(new ImageIcon(scaled));
                titleLabel.setText("");
            } else {
                titleLabel.setFont(new Font("Segoe UI", Font.BOLD, (int)(72 * scaleFactor)));
            }
        }

        if (statsPanel != null) {
            statsPanel.setPreferredSize(new Dimension((int)(900*scaleFactor), (int)(180*scaleFactor))); 
            statsPanel.setBorder(new EmptyBorder((int)(30*scaleFactor), 0, (int)(20*scaleFactor), 0));
            ((FlowLayout)statsPanel.getLayout()).setHgap((int)(30*scaleFactor));
        }
        
        if (totalCard != null) totalCard.updateScale(scaleFactor);
        if (avgCard != null) avgCard.updateScale(scaleFactor);
        if (topCard != null) topCard.updateScale(scaleFactor);

        if (contentWrapper != null) {
            contentWrapper.setBorder(new EmptyBorder(0, (int)(20*scaleFactor), 0, (int)(20*scaleFactor)));
        }
        
        if (scrollPane != null) {
            scrollPane.setPreferredSize(new Dimension((int)(900*scaleFactor), (int)(450*scaleFactor)));
            scrollPane.getVerticalScrollBar().setUnitIncrement((int)(20*scaleFactor));
        }

        for (Component c : listContainer.getComponents()) {
            if (c instanceof ScoreCard) {
                ((ScoreCard)c).updateScale(scaleFactor);
            }
        }
        
        if (footerPanel != null) {
            footerPanel.setPreferredSize(new Dimension((int)(800*scaleFactor), (int)(120*scaleFactor)));
            footerPanel.setBorder(new EmptyBorder((int)(20*scaleFactor), 0, 0, 0));
        }
        if (btnBack != null) {
            btnBack.updateScale(scaleFactor);
        }

        revalidate();
        repaint();
    }

    @Override
    public void setVisible(boolean flag) {
        super.setVisible(flag);
        if (flag) {
            SwingUtilities.invokeLater(() -> {
                calculateScaleFactor();
                loadDataAsync(); 
            });
        }
    }

    private void loadDataAsync() {
        listContainer.removeAll();
        
        JLabel loading = new JLabel("Sedang memuat data...", SwingConstants.CENTER);
        loading.setFont(new Font("Segoe UI", Font.ITALIC, (int)(24*scaleFactor)));
        loading.setForeground(COL_TEXT_GRAY);
        loading.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        listContainer.add(Box.createVerticalStrut((int)(50*scaleFactor)));
        listContainer.add(loading);
        listContainer.revalidate();
        listContainer.repaint();

        new SwingWorker<List<LeaderboardEntry>, Void>() {
            @Override
            protected List<LeaderboardEntry> doInBackground() throws Exception {
                return repo.getTopScores();
            }

            @Override
            protected void done() {
                try {
                    updateUIWithData(get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void updateUIWithData(List<LeaderboardEntry> data) {
        listContainer.removeAll(); 

        if (data.isEmpty()) {
            updateStats(0, 0, 0);
            
            JLabel empty = new JLabel("Belum ada data juara", SwingConstants.CENTER);
            empty.setFont(new Font("Segoe UI", Font.BOLD, (int)(32 * scaleFactor)));
            empty.setForeground(COL_TEXT_GRAY);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            listContainer.add(Box.createVerticalStrut((int)(50 * scaleFactor)));
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
                
                // [UPDATE] MENGGUNAKAN FITUR LEVEL DARI DATABASE
                // Kita ambil e.getLevel() yang sudah tersedia di LeaderboardEntry
                ScoreCard card = new ScoreCard(rank, e.getPlayerName(), e.getScore(), e.getLevel(), avatar);
                
                card.updateScale(scaleFactor); 
                card.setAlignmentX(Component.CENTER_ALIGNMENT); 
                listContainer.add(card);
                
                listContainer.add(Box.createVerticalStrut((int)(15 * scaleFactor))); 
                rank++;
            }
        }
        
        listContainer.revalidate();
        listContainer.repaint();
    }

    private void loadAssets() {
        try {
            URL bgUrl = getClass().getResource("/images/bg_menu.png");
            if (bgUrl != null) bgImage = new ImageIcon(bgUrl).getImage();

            URL titleUrl = getClass().getResource("/images/title_papan_juara.png");
            if (titleUrl != null) titleImage = new ImageIcon(titleUrl).getImage();
        } catch (Exception ignored) {}
    }

    private void initUI() {
        add(createHeader(), BorderLayout.NORTH);

        contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setOpaque(false);

        contentWrapper.add(createStatsPanel(), BorderLayout.NORTH);

        JPanel listWrapper = new JPanel(new GridBagLayout());
        listWrapper.setOpaque(false);

        listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setOpaque(false);
        
        scrollPane = new JScrollPane(listContainer);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBar());
        
        listWrapper.add(scrollPane); 
        contentWrapper.add(listWrapper, BorderLayout.CENTER);
        
        add(contentWrapper, BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        titleLabel = new JLabel();
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        if (titleImage == null) {
            titleLabel.setText("PAPAN JUARA");
            titleLabel.setForeground(COL_TEXT_WHITE);
        }
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        return headerPanel;
    }

    private JPanel createStatsPanel() {
        statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        statsPanel.setOpaque(false);

        totalCard = new StatCard("Total Pemain", "0", "👥", COL_ACCENT_BLUE);
        avgCard = new StatCard("Rata-rata", "0 Poin", "⭐", COL_ACCENT_GOLD);
        topCard = new StatCard("Tertinggi", "0 Poin", "🔥", COL_ACCENT_RED);

        statsPanel.add(totalCard);
        statsPanel.add(avgCard);
        statsPanel.add(topCard);
        
        return statsPanel;
    }

    private JPanel createFooter() {
        footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setOpaque(false);
        
        btnBack = new GradientButton("KEMBALI", new Color(220, 38, 38), new Color(153, 27, 27));
        btnBack.addActionListener(e -> { 
            playSound("click"); 
            ScreenManager.getInstance().showScreen("MAIN_MENU"); 
        });

        footerPanel.add(btnBack);
        return footerPanel;
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
    // COMPONENTS (Inner Classes)
    // ============================================================

    class StatCard extends JPanel {
        private String value;
        private final String label, icon;
        private final Color accentColor;
        private float scale = 1.0f;

        StatCard(String label, String value, String icon, Color color) {
            this.label = label;
            this.value = value;
            this.icon = icon;
            this.accentColor = color;
            setOpaque(false);
        }
        
        void updateScale(float s) {
            this.scale = s;
            setPreferredSize(new Dimension((int)(250*s), (int)(90*s)));
            revalidate(); repaint();
        }

        void setValue(String value) {
            this.value = value;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int arc = (int)(20 * scale);

            g2.setColor(new Color(30, 41, 59, 220));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

            g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 120));
            g2.setStroke(new BasicStroke(1));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, arc, arc);

            g2.setColor(accentColor);
            g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, (int)(36*scale))); 
            g2.drawString(icon, (int)(20*scale), (int)(55*scale));

            g2.setFont(new Font("Segoe UI", Font.BOLD, (int)(26*scale))); 
            g2.setColor(COL_TEXT_WHITE);
            g2.drawString(value, (int)(75*scale), (int)(38*scale));

            g2.setFont(new Font("Segoe UI", Font.PLAIN, (int)(14*scale))); 
            g2.setColor(COL_TEXT_GRAY);
            g2.drawString(label, (int)(75*scale), (int)(62*scale));
        }
    }

    class ScoreCard extends JPanel {
        private final int rank;
        private final String name;
        private final int score;
        private final int level; // [FITUR LEVEL] Variable level
        private Image avatar;
        private boolean isHovered = false;
        private float scale = 1.0f; 

        // [FITUR LEVEL] Constructor diupdate menerima parameter level
        ScoreCard(int rank, String name, int score, int level, String avatarFile) {
            this.rank = rank; 
            this.name = name; 
            this.score = score;
            this.level = level; // Simpan level
            setOpaque(false);
            
            try {
                URL url = getClass().getResource("/images/" + avatarFile);
                if(url != null) avatar = new ImageIcon(url).getImage();
            } catch(Exception ignored) {}

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
            });
        }
        
        void updateScale(float s) {
            this.scale = s;
            setPreferredSize(new Dimension((int)(850*s), (int)(110*s))); 
            setMaximumSize(new Dimension((int)(850*s), (int)(110*s)));
            revalidate(); repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth(); 
            int h = getHeight();

            if(isHovered) g2.translate(0, -2);

            int arc = (int)(25 * scale);
            
            g2.setColor(new Color(30, 41, 59, 220));
            g2.fillRoundRect(0, 0, w, h, arc, arc);
            
            Color rankColor;
            if (rank == 1) rankColor = COL_ACCENT_GOLD;
            else if (rank == 2) rankColor = COL_ACCENT_SILVER;
            else if (rank == 3) rankColor = COL_ACCENT_BRONZE;
            else rankColor = COL_ACCENT_BLUE.darker();

            g2.setColor(rankColor);
            g2.setStroke(new BasicStroke(Math.max(1, (int)(3*scale)))); 
            g2.drawRoundRect(1, 1, w-2, h-2, arc, arc);

            // RANK NUMBER
            g2.setColor(rank <= 3 ? rankColor : COL_TEXT_WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, (int)(32*scale)));
            g2.drawString("#" + rank, (int)(30*scale), (int)(65*scale));

            // AVATAR
            if(avatar != null) {
                int avSize = (int)(75*scale);
                g2.drawImage(avatar, (int)(100*scale), (int)(18*scale), avSize, avSize, this);
            }
            
            // NAMA PLAYER
            g2.setColor(COL_TEXT_WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, (int)(28*scale)));
            g2.drawString(name, (int)(200*scale), (int)(50*scale));
            
            // [FITUR LEVEL] Tampilkan Level dari variable
            g2.setColor(COL_TEXT_GRAY);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, (int)(16*scale)));
            g2.drawString("Level " + level, (int)(200*scale), (int)(80*scale));

            // SCORE
            g2.setColor(COL_ACCENT_GOLD); 
            g2.setFont(new Font("Segoe UI", Font.BOLD, (int)(30*scale)));
            String s = score + " Poin";
            g2.drawString(s, w - g2.getFontMetrics().stringWidth(s) - (int)(40*scale), (int)(65*scale));

            if (rank == 1) {
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, (int)(30*scale)));
                g2.drawString("👑", (int)(10*scale), (int)(30*scale));
            }
        }
    }

    class GradientButton extends JButton {
        private final Color color1;
        private final Color color2;
        private boolean hover;
        private float scale = 1.0f; 

        GradientButton(String text, Color c1, Color c2) {
            super(text);
            this.color1 = c1;
            this.color2 = c2;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(Color.WHITE);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }
        
        void updateScale(float s) {
            this.scale = s;
            setFont(new Font("Segoe UI", Font.BOLD, (int)(22*s)));
            setPreferredSize(new Dimension((int)(240*s), (int)(65*s)));
            revalidate(); repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth();
            int h = getHeight();
            int arc = (int)(30 * scale);

            g2.setColor(new Color(0,0,0,60));
            g2.fillRoundRect((int)(3*scale), (int)(5*scale), w-(int)(6*scale), h-(int)(5*scale), arc, arc);

            GradientPaint gp = new GradientPaint(0, 0, hover ? color1.brighter() : color1, 
                                                 0, h, hover ? color2.brighter() : color2);
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, w, h-(int)(5*scale), arc, arc);

            FontMetrics fm = g2.getFontMetrics();
            int x = (w - fm.stringWidth(getText())) / 2;
            int y = ((h - (int)(5*scale) - fm.getHeight()) / 2) + fm.getAscent();
            
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