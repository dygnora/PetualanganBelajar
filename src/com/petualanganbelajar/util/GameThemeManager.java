package com.petualanganbelajar.util;

import java.awt.Color;

public class GameThemeManager {

    public static ModuleTheme getThemeForModule(int moduleId) {
        switch (moduleId) {
            case 1: // MODUL ANGKA (Green Theme)
                return new ModuleTheme(
                    new Color(46, 139, 87),          // Primary
                    new Color(200, 255, 200),        // Top BG
                    new Color(240, 255, 240),        // Bottom BG
                    new Color(34, 139, 34, 50),      // Accent
                    new Color[]{                     // Profile Rainbow
                        new Color(255, 215, 0), 
                        new Color(154, 205, 50), 
                        new Color(210, 180, 140)
                    },
                    new Color(255, 140, 0),          // Level Top
                    new Color(205, 92, 92),          // Level Bottom
                    new Color(139, 69, 19),          // Level Outline
                    Color.WHITE,                     // Level Text
                    new Color(255, 99, 71),          // Score Top
                    new Color(178, 34, 34)           // Score Bottom
                );

            case 2: // MODUL HURUF (Magenta/Pink Theme)
                return new ModuleTheme(
                    new Color(199, 21, 133),
                    new Color(255, 228, 238),
                    new Color(255, 250, 255),
                    new Color(255, 105, 180, 70),
                    new Color[]{
                        new Color(0, 139, 139),
                        new Color(32, 178, 170),
                        new Color(72, 209, 204)
                    },
                    new Color(50, 111, 168),
                    new Color(0, 128, 128),
                    new Color(0, 51, 51),
                    Color.WHITE,
                    new Color(102, 255, 0),
                    new Color(50, 168, 115)
                );

            case 3: // MODUL WARNA (Orange/Cyan Mix)
                return new ModuleTheme(
                    new Color(255, 112, 67),
                    new Color(255, 224, 178),
                    new Color(255, 243, 224),
                    new Color(255, 255, 255, 100),
                    new Color[]{Color.MAGENTA, new Color(255, 105, 180), Color.CYAN},
                    new Color(218, 112, 214),
                    new Color(153, 50, 204),
                    new Color(75, 0, 130),
                    Color.WHITE,
                    new Color(0, 255, 255),
                    new Color(0, 128, 128)
                );

            case 4: // MODUL BENTUK (Orange Theme)
                return new ModuleTheme(
                    new Color(255, 165, 0),
                    new Color(255, 250, 205),
                    new Color(255, 228, 181),
                    new Color(255, 140, 0, 50),
                    new Color[]{new Color(100, 149, 237), new Color(65, 105, 225)},
                    new Color(255, 69, 0),
                    new Color(139, 0, 0),
                    new Color(100, 0, 0),
                    Color.WHITE,
                    new Color(50, 205, 50),
                    new Color(0, 100, 0)
                );

            default: // Default (Blue Sky Theme)
                return new ModuleTheme(
                    new Color(70, 130, 180),
                    new Color(200, 230, 255),
                    Color.WHITE,
                    new Color(0, 0, 0, 20),
                    new Color[]{Color.LIGHT_GRAY, Color.GRAY},
                    Color.LIGHT_GRAY,
                    Color.GRAY,
                    Color.BLACK,
                    Color.WHITE,
                    Color.ORANGE,
                    Color.RED
                );
        }
    }
}