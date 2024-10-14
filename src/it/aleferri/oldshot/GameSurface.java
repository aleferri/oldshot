/*
 * Copyright 2024 Alessio.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.aleferri.oldshot;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 *
 * @author Alessio
 */
public class GameSurface extends Canvas {

    private static int calculateAliensFreshLine(int size) {
        int temp = 0;
        for (int i = 0; i < size - 1; i++) {
            temp = (temp << 1) | 1;
        }

        return temp;
    }

    private static int calculateAliensScanStart(int size) {
        int mask = 1;
        for (int i = 0; i < size - 2; i++) {
            mask = (mask << 1);
        }

        return mask;
    }

    private final Image cannon;
    private final Image bullet;
    private final Image alien;
    private final Image boom;
    private final ScreenProps props;
    private final short[] bullets;
    private final int[] aliens;
    private final int alienScanStart;
    private final int freshAliensLine;
    private final int messageOffsetX;
    private final int messageOffsetY;
    private final String[] messages;
    private AliensAnimation aliensAnimation;
    private Point lastBoom;
    private int boomDecay;
    
    private int playerPosition;
    

    public GameSurface(Image cannon, Image bullet, Image alien, Image boom) {
        int bulletLines = (this.getHeight() / 10) - 1;
        int aliensLines = (this.getHeight() / 20) - 1;

        this.props = new ScreenProps(this.getWidth(), this.getHeight());
        this.boom = boom;
        this.cannon = cannon;
        this.bullet = bullet;
        this.bullets = new short[bulletLines];
        this.alien = alien;
        this.aliens = new int[aliensLines];
        this.freshAliensLine = calculateAliensFreshLine(this.props.spritesPerLine);
        this.alienScanStart = calculateAliensScanStart(this.props.spritesPerLine);
        this.playerPosition = (this.getWidth() / 2) - 10;
        
        this.messageOffsetX = this.getWidth() / 2;
        this.messageOffsetY = this.getHeight() / 2;
        this.messages = new String[3];
        
        this.boomDecay = 0;
        this.lastBoom = null;

        short zero = 0;

        this.aliensAnimation = new AliensAnimation(zero, zero, zero, 0);

        for (int i = 0; i < this.bullets.length; i++) {
            this.bullets[i] = -1;
        }
    }

    protected void paint(Graphics g) {
        // Get the width and height of the screen in pixels
        int w = getWidth();
        int h = getHeight();

        // Set the current color to blue (hex RGB value ) and draw a filled
        // rectangle the size of the screen
        g.setColor(0x00D7FF);
        g.fillRect(0, 0, w, h);

        g.drawImage(this.cannon, this.playerPosition, this.props.cannotOffsetY, 0);

        // Set the current color to red, the font to the default font and 
        // draw a string to the center of the screen.
        g.setColor(0xFF0000);
        g.setFont(Font.getDefaultFont());

        for (int msgRow = 0; msgRow < this.messages.length; msgRow++) {
            String msg = this.messages[msgRow];

            if (msg != null && msg.length() != 0) {
                g.drawString(msg, this.messageOffsetX, this.messageOffsetY + (20 * msgRow),
                        Graphics.BASELINE | Graphics.HCENTER);
            }
        }

        int bulletHOffset = 0;
        for (int i = 0; i < this.bullets.length; i++) {
            int wOffset = this.bullets[i];

            if (wOffset < 0) {
                bulletHOffset += 10;
                continue; // Nothing to draw for this line
            }

            g.drawImage(this.bullet, wOffset, bulletHOffset, 0);

            bulletHOffset += 10;
        }

        int alienHOffset = this.aliensAnimation.gOffsetY;

        for (int k = 0; k < this.aliens.length; k++) {
            int line = this.aliens[k];

            int a2b0 = k * 2;
            int a2b1 = a2b0 + 1;

            if (line >= 0) {
                int wOffset = this.props.leftMostOffset + this.aliensAnimation.gOffsetX;
                int cursor = this.alienScanStart;

                for (int i = 0; i < this.props.spritesPerLine; i++) {
                    if ((cursor & line) > 0) {
                        // Alien there, is there a bullet too?
                        if (this.bullets[a2b1] >= wOffset && this.bullets[a2b1] <= (wOffset + 20)) {
                            // Hit
                            line = line ^ cursor; // Remove the alien
                            this.aliens[k] = line;
                            this.bullets[a2b1] = -1; // But also remove the bullet
                            
                            // Then register the BOOM!
                            this.lastBoom = new Point((short)wOffset, (short)alienHOffset);
                            this.boomDecay = 2;
                        } else {
                            g.drawImage(this.alien, wOffset, alienHOffset, 0);
                        }
                    }

                    cursor = cursor >> 1;
                    wOffset += 20;
                }
            }

            alienHOffset += 20;
        }
        
        if (this.lastBoom != null) {
            g.drawImage(this.boom, this.lastBoom.x, this.lastBoom.y, 0);
        }
    }

    public void slideBullets() {
        int last = this.bullets.length - 1;
        for (int i = 0; i < last; i++) {
            this.bullets[i] = this.bullets[i + 1];
        }
        this.bullets[last] = -1;
        
        if (this.boomDecay > 0) {
            this.boomDecay--;
        } else {
            this.lastBoom = null;
        }

        this.repaint();
    }

    public void slideAliens() {
        int last = this.aliens.length - 1;
        for (int i = last; i > 0; i--) {
            this.aliens[i] = this.aliens[i - 1];
        }
        this.aliens[0] = this.freshAliensLine >> 1;

        this.repaint();
    }

    public void keyPressed(int keyCode) {
        super.keyPressed(keyCode);

        int gameCommand = this.getGameAction(keyCode);

        switch (gameCommand) {
            case LEFT:
                if (this.playerPosition >= 14) {
                    this.playerPosition -= 10;
                }
                break;
            case RIGHT:
                if (this.playerPosition <= (this.getWidth() - 30)) {
                    this.playerPosition += 10;
                }
                break;
            case UP:
                short wOffset = (short) (this.playerPosition + 5);
                int bIndex = (this.props.cannotOffsetY - 10) / 10;
                this.bullets[bIndex] = wOffset;
                break;
            default:
                break;
        }

        String keyName = this.getKeyName(keyCode);

        this.resetMessages();
        this.addMessage("Key: " + keyName);

        this.repaint();
    }

    public void setAliensAnimation(AliensAnimation aAnimation) {
        this.aliensAnimation = aAnimation;
    }

    public void setPlayerPosition(int playerPosition) {
        this.playerPosition = playerPosition;
    }
    
    public void resetMessages() {
        for (int i = 0; i < this.messages.length; i++) {
            this.messages[i] = null;
        }
    }
    
    public void addMessage(String message) {
        for (int i = 0; i < this.messages.length; i++) {
            if (this.messages[i] == null || this.messages[i].length() == 0) {
                this.messages[i] = message;
                return;
            }
        }
    }
}
