package core;

import java.awt.event.*;

public class KeyHandler implements KeyListener {
    public boolean upPressed, downPressed, leftPressed, rightPressed;
    public boolean attackPressed, shieldPressed, dashPressed, firePressed;
    public boolean interactPressed;
    public boolean inventoryPressed, questPressed, pausePressed;
    public boolean enterPressed;

    // One-shot flags (set once, consumed by game logic)
    public boolean attackJustPressed;
    public boolean fireJustPressed;
    public boolean interactJustPressed;
    public boolean inventoryJustPressed;
    public boolean questJustPressed;
    public boolean pauseJustPressed;
    public boolean enterJustPressed;

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        switch (code) {
            case KeyEvent.VK_W, KeyEvent.VK_UP    -> upPressed = true;
            case KeyEvent.VK_S, KeyEvent.VK_DOWN  -> downPressed = true;
            case KeyEvent.VK_A, KeyEvent.VK_LEFT  -> leftPressed = true;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> rightPressed = true;
            case KeyEvent.VK_Z                    -> { if (!attackPressed) attackJustPressed = true; attackPressed = true; }
            case KeyEvent.VK_X                    -> shieldPressed = true;
            case KeyEvent.VK_V                    -> { if (!firePressed) fireJustPressed = true; firePressed = true; }
            case KeyEvent.VK_SHIFT                -> { if (!dashPressed) dashPressed = true; } // moved dash to SHIFT 
            case KeyEvent.VK_E                    -> { if (!interactPressed) interactJustPressed = true; interactPressed = true; }
            case KeyEvent.VK_I                    -> { if (!inventoryPressed) inventoryJustPressed = true; inventoryPressed = true; }
            case KeyEvent.VK_Q                    -> { if (!questPressed) questJustPressed = true; questPressed = true; }
            case KeyEvent.VK_ESCAPE               -> { if (!pausePressed) pauseJustPressed = true; pausePressed = true; }
            case KeyEvent.VK_ENTER                -> { if (!enterPressed) enterJustPressed = true; enterPressed = true; }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        switch (code) {
            case KeyEvent.VK_W, KeyEvent.VK_UP    -> upPressed = false;
            case KeyEvent.VK_S, KeyEvent.VK_DOWN  -> downPressed = false;
            case KeyEvent.VK_A, KeyEvent.VK_LEFT  -> leftPressed = false;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> rightPressed = false;
            case KeyEvent.VK_Z                    -> attackPressed = false;
            case KeyEvent.VK_X                    -> shieldPressed = false;
            case KeyEvent.VK_V                    -> firePressed = false;
            case KeyEvent.VK_SHIFT                -> dashPressed = false;
            case KeyEvent.VK_E                    -> interactPressed = false;
            case KeyEvent.VK_I                    -> inventoryPressed = false;
            case KeyEvent.VK_Q                    -> questPressed = false;
            case KeyEvent.VK_ESCAPE               -> pausePressed = false;
            case KeyEvent.VK_ENTER                -> enterPressed = false;
        }
    }

    public void clearJustPressed() {
        attackJustPressed = false;
        interactJustPressed = false;
        inventoryJustPressed = false;
        questJustPressed = false;
        pauseJustPressed = false;
        enterJustPressed = false;
    }
}
