package InventoryTrackingSytem;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

public class InventoryTrackingSystem extends Canvas implements Runnable {

	private static final long serialVersionUID = 5103159351344430204L;

	public static final int WIDTH = 1200, HEIGHT = WIDTH / 16 * 9;

	private Thread thread;
	private boolean running = false;
	private Handler handler;
	public static int currentFPS = 0;
	Menu mainMenu;
	HelpMenu helpMenu;

	public enum STATE {
		Menu,
		Help;
	}

	public static STATE itsSTATE = STATE.Menu;

	public InventoryTrackingSystem() {
		new Window(WIDTH, HEIGHT, "Inventory Tracking System", this);
		handler = new Handler();		
		mainMenu = new Menu(this, handler);
		this.addMouseListener(mainMenu);
		this.addKeyListener(new KeyInput(handler));


	}

	public synchronized void start() {
		thread = new Thread(this);
		thread.start();
		running = true;
	}

	public synchronized void stop() {
		try {
			thread.join();
			running = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() { 
		this.requestFocus();
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		int frames = 0;
		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while (delta >= 1) {
				tick();
				delta--;
			}
			if (running)
				render();
			frames++;

			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				System.out.println("FPS: " + frames);
				currentFPS = frames;
				frames = 0;
			}
		}
		stop();

	}

	private void tick() {
		handler.tick();
		if (itsSTATE == STATE.Menu) {
			mainMenu.tick();
		}
	}

	private void render() { //this reduces the amount of frames (makes it easier on the computing side)
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(2);
			return;
		}
		Graphics g = bs.getDrawGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, WIDTH, HEIGHT);

		handler.render(g);

		if (itsSTATE == STATE.Menu  || itsSTATE == STATE.Help) {
			handler.clearElements();
			mainMenu.render(g);
		}
		
		g.dispose();
		bs.show();
	}

	public static void main(String[] args) {
		new InventoryTrackingSystem();

	}

}
