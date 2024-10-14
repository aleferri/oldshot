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

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Image;
import javax.microedition.midlet.*;

/**
 * @author Alessio
 */
public class OldShot extends MIDlet {

    private final static String ASSETS_DIR = "/it/aleferri/oldshot/resources";
    private final byte[] TEMP_BUFFER = new byte[4096];
    
    private GameLoop current;

    private Image loadImage(String path) throws IOException {
        InputStream input = getClass().getResourceAsStream(path);
        
        int count = input.read(TEMP_BUFFER);
        
        if (count >= 4096) {
            throw new RuntimeException("You are too confident of old phone capability, at most 4K sized images allowed");
        }
        
        if (count == 0) {
            throw new RuntimeException("Something went wrong");
        }
        
        return Image.createImage(TEMP_BUFFER, 0, count);
    }
    
    public void startApp() {
        Image cannon;
        try {
            cannon = this.loadImage(ASSETS_DIR + "/cannon.png");
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        }
        
        Image bullet;
        try {
            bullet = this.loadImage(ASSETS_DIR + "/bullet.png");
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        }
        
        Image boom;
        try {
            boom = this.loadImage(ASSETS_DIR + "/boom.png");
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        }
        
        Image alien;
        try {
            alien = this.loadImage(ASSETS_DIR + "/alien.png");
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        }
        
        long memory = Runtime.getRuntime().totalMemory();
        memory = memory / 1024;
        
        GameSurface gameSurface = new GameSurface(cannon, bullet, alien, boom);
        gameSurface.addMessage("Welcome, press any key!");
        gameSurface.addMessage("Memory: " + memory + " KB");
        Display.getDisplay(this).setCurrent(gameSurface);
        
        this.current = new GameLoop(gameSurface);
        Thread game = new Thread(current);
        game.start();
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }
}
