package core;

import java.awt.event.*;

public class KeyHandler implements KeyListener {
    public boolean upPressed, downPressed, leftPressed, rightPressed;
    public boolean attackPressed, shieldPressed, dashPressed, firePressed;
    public boolean interactPressed;
    public boolean inventoryPressed, questPressed, pausePressed;
    public boolean enterPressed;
    public boolean savePressed, newGamePressed;
    public boolean yesPressed, noPressed;

    // One-shot flags (set once, consumed by game logic)
    public boolean attackJustPressed;
    public boolean fireJustPressed;
    public boolean interactJustPressed;
    public boolean inventoryJustPressed;
    public boolean questJustPressed;
    public boolean pauseJustPressed;
    public boolean enterJustPressed;
    public boolean saveJustPressed;
    public boolean newGameJustPressed;
    public boolean yesJustPressed;
    public boolean noJustPressed;

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
            case KeyEvent.VK_SPACE:
            case KeyEvent.VK_V:      if (!firePressed) fireJustPressed = true; firePressed = true; break;
            case KeyEvent.VK_SHIFT:  if (!dashPressed) dashPressed = true; break;
            case KeyEvent.VK_E:      if (!interactPressed) interactJustPressed = true; interactPressed = true; break;
            case KeyEvent.VK_I:      if (!inventoryPressed) inventoryJustPressed = true; inventoryPressed = true; break;
            case KeyEvent.VK_Q:      if (!questPressed) questJustPressed = true; questPressed = true; break;
            case KeyEvent.VK_ESCAPE: if (!pausePressed) pauseJustPressed = true; pausePressed = true; break;
            case KeyEvent.VK_ENTER:  if (!enterPressed) enterJustPressed = true; enterPressed = true; break;
            case KeyEvent.VK_F5:     if (!savePressed) saveJustPressed = true; savePressed = true; break;
            case KeyEvent.VK_R:      if (!newGamePressed) newGameJustPressed = true; newGamePressed = true; break;
            case KeyEvent.VK_Y:      if (!yesPressed) yesJustPressed = true; yesPressed = true; break;
            case KeyEvent.VK_N:      if (!noPressed) noJustPressed = true; noPressed = true; break;
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
            case KeyEvent.VK_SPACE:
            case KeyEvent.VK_V:      firePressed = false; break;
            case KeyEvent.VK_SHIFT:  dashPressed = false; break;
            case KeyEvent.VK_E:      interactPressed = false; break;
            case KeyEvent.VK_I:      inventoryPressed = false; break;
            case KeyEvent.VK_Q:      questPressed = false; break;
            case KeyEvent.VK_ESCAPE: pausePressed = false; break;
            case KeyEvent.VK_ENTER:  enterPressed = false; break;
            case KeyEvent.VK_F5:     savePressed = false; break;
            case KeyEvent.VK_R:      newGamePressed = false; break;
            case KeyEvent.VK_Y:      yesPressed = false; break;
            case KeyEvent.VK_N:      noPressed = false; break;
        }
    }

    public void clearJustPressed() {
        attackJustPressed = false;
        interactJustPressed = false;
        inventoryJustPressed = false;
        questJustPressed = false;
        pauseJustPressed = false;
        enterJustPressed = false;
        saveJustPressed = false;
        newGameJustPressed = false;
        yesJustPressed = false;
        noJustPressed = false;
    }
}
