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

/**
 *
 * @author Alessio
 */
public class GameLoop implements Runnable {
    
    private final static int COUNTDOWN = 24;
    
    private final GameSurface surface;
    private int frameCount;
    private boolean running;

    public GameLoop(GameSurface surface) {
        this.running = false;
        this.frameCount = COUNTDOWN;
        this.surface = surface;
    }
    
    public void run() {
        this.running = true;
        
        this.surface.slideAliens();

        while (this.running) {
            try {
                Thread.sleep(100); // every frame, 10 fps = 100 msec

                this.surface.slideBullets();
                this.frameCount -= 1;
                
                if (this.frameCount <= 0) {
                    this.frameCount = COUNTDOWN;
                    this.surface.slideAliens();
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } catch (RuntimeException re) {
                re.printStackTrace();
            }
        }
    }
    
}
