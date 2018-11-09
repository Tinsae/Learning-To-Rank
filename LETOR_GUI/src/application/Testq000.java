package application;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFrame;

public class Testq000 extends JFrame implements MouseListener {

	 private JButton b;
	 int xBounds = 1;
	 int yBounds = 1;
	public Testq000(String title)
	    {
	        super(title);
	        setSize(640, 520);
	        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    	setLayout(null);
	    	b = new JButton();
	    	b.addMouseListener(this);
	    	b.setBounds(xBounds,yBounds, 40, 40);
	    	this.add(b);
	    }
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		xBounds+=5;
		yBounds+=5;
    	b.setBounds(xBounds,yBounds, 40, 40);
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
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

	public static void main(String [] args)
	{
	Testq000 win = new Testq000("some window");
    win.setVisible(true);
		
	}
	 
}
