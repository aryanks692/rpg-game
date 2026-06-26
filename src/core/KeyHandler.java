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
            case KeyEvent.VK_W: case KeyEvent.VK_UP:    upPressed = true; break;
            case KeyEvent.VK_S: case KeyEvent.VK_DOWN:  downPressed = true; break;
            case KeyEvent.VK_A: case KeyEvent.VK_LEFT:  leftPressed = true; break;
            case KeyEvent.VK_D: case KeyEvent.VK_RIGHT: rightPressed = true; break;
            case KeyEvent.VK_Z:      if (!attackPressed) attackJustPressed = true; attackPressed = true; break;
            case KeyEvent.VK_X:      shieldPressed = true; break;
            case KeyEvent.VK_V:      if (!firePressed) fireJustPressed = true; firePressed = true; break;
            case KeyEvent.VK_SHIFT:  if (!dashPressed) dashPressed = true; break;
            case KeyEvent.VK_E:      if (!interactPressed) interactJustPressed = true; interactPressed = true; break;
            case KeyEvent.VK_I:      if (!inventoryPressed) inventoryJustPressed = true; inventoryPressed = true; break;
            case KeyEvent.VK_Q:      if (!questPressed) questJustPressed = true; questPressed = true; break;
            case KeyEvent.VK_ESCAPE: if (!pausePressed) pauseJustPressed = true; pausePressed = true; break;
            case KeyEvent.VK_ENTER:  if (!enterPressed) enterJustPressed = true; enterPressed = true; break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        switch (code) {
            case KeyEvent.VK_W: case KeyEvent.VK_UP:    upPressed = false; break;
            case KeyEvent.VK_S: case KeyEvent.VK_DOWN:  downPressed = false; break;
            case KeyEvent.VK_A: case KeyEvent.VK_LEFT:  leftPressed = false; break;
            case KeyEvent.VK_D: case KeyEvent.VK_RIGHT: rightPressed = false; break;
            case KeyEvent.VK_Z:      attackPressed = false; break;
            case KeyEvent.VK_X:      shieldPressed = false; break;
            case KeyEvent.VK_V:      firePressed = false; break;
            case KeyEvent.VK_SHIFT:  dashPressed = false; break;
            case KeyEvent.VK_E:      interactPressed = false; break;
            case KeyEvent.VK_I:      inventoryPressed = false; break;
            case KeyEvent.VK_Q:      questPressed = false; break;
            case KeyEvent.VK_ESCAPE: pausePressed = false; break;
            case KeyEvent.VK_ENTER:  enterPressed = false; break;
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
