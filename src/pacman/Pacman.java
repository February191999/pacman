package pacman;

import javax.swing.JFrame;

public class Pacman extends JFrame{ //Pacman class starts window with a model
	
	public Pacman() {
		add(new Model());
	}
	
	public static void main(String[] args) {
		Pacman pac = new Pacman();
		pac.setVisible(true);
		pac.setTitle("Pacman");
		pac.setSize(470,525); //Size of window
		pac.setDefaultCloseOperation(EXIT_ON_CLOSE); 
		pac.setLocationRelativeTo(null); //Window position is in the middle of screen
		
	}

}
