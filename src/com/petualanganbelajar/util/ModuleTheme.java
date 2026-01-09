package com.petualanganbelajar.util;

import java.awt.Color;

public class ModuleTheme {
    public Color primary, bgTop, bgBottom, accent;
    public Color[] profileColors;
    public Color levelTop, levelBottom, levelOutline, levelText;
    public Color scoreTop, scoreBottom;

    public ModuleTheme(Color primary, Color bgTop, Color bgBottom, Color accent,
                       Color[] profileColors,
                       Color lvlTop, Color lvlBottom, Color lvlOut, Color lvlText,
                       Color scrTop, Color scrBottom) {
        this.primary = primary; 
        this.bgTop = bgTop; 
        this.bgBottom = bgBottom; 
        this.accent = accent;
        this.profileColors = profileColors;
        this.levelTop = lvlTop; 
        this.levelBottom = lvlBottom; 
        this.levelOutline = lvlOut; 
        this.levelText = lvlText;
        this.scoreTop = scrTop; 
        this.scoreBottom = scrBottom;
    }
}