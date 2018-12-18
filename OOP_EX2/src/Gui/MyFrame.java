package Gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import Algorithms.ShortestPathAlgo;
import Coords.Convert_pixel_gps;
import Coords.Range;
import File_format.Game2CSV;
import GIS.Fruit;
import GIS.Game;
import GIS.Pacman;
import Geom.Pixel;
import Geom.Point3D;

public class MyFrame extends JFrame implements MouseListener {
	private BufferedImage myImage;
	private Game game;
	private boolean isPacman;
	private boolean isFruit;
	private Map map;

	private boolean isPath; // added might need to delete later
	private ShortestPathAlgo algo; // same

	public MyFrame() {
		this.isPacman = false;
		this.isFruit = false;
		this.game = new Game();
		this.map = new Map();
		this.myImage = map.getMyImage();
		this.isPath = false;

		initGUI();
		this.addMouseListener(this);
	}

	private void initGUI() {
		/* Create the menu bar. */
		MenuBar menuBar = new MenuBar();
		Menu fileMenu = new Menu("File");
		MenuItem loadFile = new MenuItem("Load");
		MenuItem saveFile = new MenuItem("Save");
		MenuItem clearFile = new MenuItem("Clear");
		MenuItem exitFile = new MenuItem("Exit");

		fileMenu.add(loadFile);
		fileMenu.add(saveFile);
		fileMenu.add(clearFile);
		fileMenu.add(exitFile);

		menuBar.add(fileMenu);

		Menu inputMenu = new Menu("Input");
		MenuItem pacmanInput = new MenuItem("Pacman");
		MenuItem fruitInput = new MenuItem("Fruit");

		inputMenu.add(pacmanInput);
		inputMenu.add(fruitInput);
		
		menuBar.add(inputMenu);
		
		Menu simulationMenu = new Menu("Simulation");
		MenuItem startSimulation = new MenuItem("start");
		//MenuItem fastStartSimulation = new MenuItem("fast start");
		
		simulationMenu.add(startSimulation);
		//simulationMenu.add(fastStartSimulation);
		
		menuBar.add(simulationMenu);

		setMenuBar(menuBar);
		/* End to create the menu bar. */

		/*
		 * Add action to load File button
		 * https://stackoverflow.com/questions/15703214/save-file-open-file-dialog-box-
		 * using-swing-netbeans-gui-editor
		 */
		loadFile.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				/* Cannot draw fruits and pacman */
				setPacman(false);
				setFruit(false);

				ChooseButtonLoadFile(arg0);
			}
		});

		/*
		 * Add action to save File button
		 * https://stackoverflow.com/questions/15703214/save-file-open-file-dialog-box-
		 * using-swing-netbeans-gui-editor
		 */
		saveFile.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				setPacman(false);
				setFruit(false);
				ChooseButtonSaveFile(arg0);
			}
		});

		/* Add action to clear File button */
		clearFile.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				Game game = getGame();
				game.getFruits().clear();
				game.getPacmans().clear();
				setPacman(false);
				setFruit(false);
				repaint();
			}
		});

		/* Add action to exit File button */
		exitFile.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});

		/* Add action to pacman input button */
		pacmanInput.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				setPacman(true);
				setFruit(false);
			}
		});

		/* Add action to fruit input button */
		fruitInput.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				setFruit(true);
				setPacman(false);
			}
		});
		
		/* Add action to start simulation button */
		startSimulation.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				setFruit(false);
				setPacman(false);
				startAlgo(arg0);
			}
		});

	}

	/* Load */
	private void ChooseButtonLoadFile(ActionEvent e) {
		/* Open load file chooser */
		JFileChooser openFile = new JFileChooser();
		int returnValue = openFile.showOpenDialog(null);

		/* If the file selected */
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = openFile.getSelectedFile();// the file that selected.
			String filePath = selectedFile.getPath();// the path to the file.

			/* If the file that selected is csv file */
			if (filePath.contains(".csv")) {
				/* Clear the game board */
				this.game.getFruits().clear();
				this.game.getPacmans().clear();

				/* Load the new game to the game board */
				this.game = new Game(filePath);
				repaint();
			}
		}

	}
	
	/* save Button */
	/*https://stackoverflow.com/questions/10471396/appending-the-file-type-to-a-file-in-java-using-jfilechooser
	 * https://stackoverflow.com/questions/13905298/how-to-save-a-txt-file-using-jfilechooser */
	private void ChooseButtonSaveFile(ActionEvent e) {

		/* Open save file chooser */
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int result = chooser.showSaveDialog(this);

		/* If the file path selected. */
		if (result == chooser.APPROVE_OPTION) {
			File f = chooser.getSelectedFile();
			String filePath = f.getAbsolutePath();
			/* Check if the file name end with ".csv"  */
			if(!filePath.endsWith(".csv")) {
			    f = new File(filePath + ".csv");
			    Game2CSV creatGameCSV = new Game2CSV(this.game, f);
			}else {
				Game2CSV creatGameCSV = new Game2CSV(this.game, f);
			}

		}

	}
	
	private void startAlgo(ActionEvent e) {
		ShortestPathAlgo algo = new ShortestPathAlgo(this.game); 
		this.algo = algo;
		this.isPath = true;   // may be delete
		
		System.out.println("final distance: " + this.algo.getPath().getDistance());
		System.out.println("final time: " + this.algo.getPath().finalTime());
		
		
	
		repaint();
	}

	public void paint(Graphics g) {
		g.drawImage(this.myImage, -9, -9, this.getWidth(), this.getHeight(), this);

		this.map.setHeight(this.getHeight());
		this.map.setWidth(this.getWidth());
		/* Draw pacmans */
		Iterator<Pacman> pacIt = this.game.getPacmans().iterator();
		Convert_pixel_gps convert = new Convert_pixel_gps(this.map);
		while (pacIt.hasNext()) {
			Pacman pac = pacIt.next();
			Pixel pixel = new Pixel(0, 0);
			pixel = convert.convertGPStoPixel(pac.getGps());
			int r = 30;
			int x = pixel.getX() - (r / 2);
			int y = pixel.getY() - (r / 2);
			g.setColor(Color.yellow);
			g.fillOval(x, y, r, r);
		}

		/* Draw fruits */
		Iterator<Fruit> fruitIt = this.game.getFruits().iterator();
		while (fruitIt.hasNext()) {
			Fruit fruit = fruitIt.next();
			Pixel pixel = new Pixel(0, 0);
			pixel = convert.convertGPStoPixel(fruit.getGps());
			int r = 15;
			int x = pixel.getX() - (r / 2);
			int y = pixel.getY() - (r / 2);
			g.setColor(Color.red);
			g.fillOval(x, y, r, r);
		}

		/* Draw lines */
		if (isPath) {
			Pixel a = new Pixel(0, 0);
			Pixel b = new Pixel(0, 0);
			Point3D first = new Point3D(0, 0, 0);
			Point3D second = new Point3D(0, 0, 0);

			for (int i = 0; i < (algo.getPath().getPath().size()) - 1; i++) { // if path.size =1 then bug

				if (i == 0) {
					first = ((Pacman) (algo.getPath().getPath().get(0))).getGps();
					second = ((Fruit) (algo.getPath().getPath().get(1))).getGps();
				} else {
					first = ((Fruit) (algo.getPath().getPath().get(i))).getGps();
					second = ((Fruit) (algo.getPath().getPath().get(i + 1))).getGps();
				}

				a = convert.convertGPStoPixel(first);
				b = convert.convertGPStoPixel(second);

				g.setColor(Color.GREEN);
				g.drawLine(a.getX(), a.getY(), b.getX(), b.getY());
			}
		}
	}

	public BufferedImage getMyImage() {
		return myImage;
	}

	public Game getGame() {
		return this.game;
	}

	public boolean isPacman() {
		return isPacman;
	}

	public boolean isFruit() {
		return isFruit;
	}

	public void setFruit(boolean isFruit) {
		this.isFruit = isFruit;
	}

	public void setPacman(boolean isPacman) {
		this.isPacman = isPacman;
	}

	public void mouseClicked(MouseEvent e) {
		/* If want to add pacmans */
		if (isPacman) {
			int x = e.getX();
			int y = e.getY();
			Pixel pixel = new Pixel(x, y);
			Convert_pixel_gps convert = new Convert_pixel_gps(this.map);
			Point3D gps = new Point3D(convert.convertPixeltoGPS(pixel));

			Pacman pac = new Pacman(gps.x(), gps.y(), this.game.getPacmans().size());
			this.game.getPacmans().add(pac);
		}

		if (isFruit) {
			int x = e.getX();
			int y = e.getY();
			Pixel pixel = new Pixel(x, y);
			Convert_pixel_gps convert = new Convert_pixel_gps(this.map);
			Point3D gps = new Point3D(convert.convertPixeltoGPS(pixel));

			Fruit fruit = new Fruit(gps.x(), gps.y(), this.game.getFruits().size());
			this.game.getFruits().add(fruit);
		}

		repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
