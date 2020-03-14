import java.applet.Applet;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Label;
import java.awt.MediaTracker;
import java.awt.Panel;
import java.awt.Point;
import java.awt.TextField;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @Name    TextLCDApplet
 * @Version 0.2
 * @Autor   Tim Rinkens
 * 
 * Java Applet GUI for testing josx.platform.rcx.TextLCD
 */

public class TextLCDApplet extends Applet {

	private static Frame f = new Frame("TextLCD");
	private static final	Font textfieldFont = new Font("SansSerif", Font.PLAIN, 12);
	private static Font smallFont = new Font("SansSerif", Font.PLAIN, 10);
	private static Font bigFont = new Font("SansSerif", Font.PLAIN, 16);
	private static Font bigBoldFont = new Font("SansSerif", Font.PLAIN, 18);
	private static final String title = "RCX-Download / RCX-Direct-Mode";
	private static Label[] titleLabel = new Label[4];
	private static RCXSegment segment;
	private static TextField tf_TextLCD = new TextField("JAVA", 5);
	private static Image rcx_image;
	private static TextLCDThread textLCDThread;

	public void init() {
		//URL url = getClass().getResource(getParameter("img"));
		//System.out.println(url);
		//imageThread = new ImageThread(this);
		//imageThread.start();
		setLayout(new CardLayout());
		setBackground(Color.white);
		loadImage();
		int i = bigBoldFont.getSize();
		// Pruefen, ob Fontgroesse zu gross fuer Rahmen ist:
		while (getFontMetrics(bigBoldFont).stringWidth(title)> getPreferredSize().width-5)
			bigBoldFont = new Font("SansSerif", Font.PLAIN, --i);
		//System.out.println(bigBoldFont.getSize());
		smallFont = new Font("SansSerif", Font.PLAIN,
			(int)Math.round(bigBoldFont.getSize()*0.6));
		bigFont = new Font("SansSerif", Font.PLAIN,
			(int)Math.round(bigBoldFont.getSize()*0.88));

		Panel titlePanel = new Panel(new GridLayout(4, 1));

		titleLabel[0] =
			new Label(title, Label.CENTER);
		titleLabel[0].setFont(bigBoldFont);
		titleLabel[1] =
			new Label("(Copyright \u00a9 Tim Rinkens)", Label.CENTER);
		titleLabel[1].setFont(smallFont);
		titleLabel[2] = new Label("for leJOS", Label.CENTER);
		titleLabel[2].setFont(bigFont);
		titleLabel[3] =
			new Label(
				"(leJOS is copyright (c) 2000 Jose Solorzano)",
				Label.CENTER);
		titleLabel[3].setFont(smallFont);

		for (int k = 0; k < titleLabel.length; k++) {
			titlePanel.add(titleLabel[k]);
			titleLabel[k].addMouseListener(new TextLCDMouseListener(this));
			titleLabel[k].setVisible(false);
		}
		titlePanel.setBackground(Color.white);

		add("Title", titlePanel);
		add("LCD", createLCD());

		segment.addMouseListener(new TextLCDMouseListener(this));
		textLCDThread = new TextLCDThread(this);
		textLCDThread.start();
	}

	public Dimension getPreferredSize() {
		return new Dimension(300, 110);
	}

	public Dimension getMinimumSize() {
		return new Dimension(240, 100);
	}

	private Panel createLCD() {

		Panel p = new Panel();

		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc;
		p.setLayout(gbl);
		p.setBackground(Color.white);
		//LCD Display
		segment = new RCXSegment(rcx_image);
		//("img/lcd.jpg"); //(rcx_image); //(this);
		gbc = makegbc(0, 0, 3, 1);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbl.setConstraints(segment, gbc);
		p.add(segment);

		//Label TextField
		Label lb_TextLCD = new Label("TextLCD.print");
		lb_TextLCD.setFont(textfieldFont);
		gbc = makegbc(0, 1, 1, 1);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbl.setConstraints(lb_TextLCD, gbc);
		p.add(lb_TextLCD);

		//TextField
		gbc = makegbc(1, 1, 1, 1);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		tf_TextLCD.setFont(textfieldFont);
		gbl.setConstraints(tf_TextLCD, gbc);
		p.add(tf_TextLCD);

		//Label2 TextField
		Label lb_TextLCD2 = new Label("   ", Label.LEFT);
		lb_TextLCD2.setFont(textfieldFont);
		gbc = makegbc(2, 1, 1, 1);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbl.setConstraints(lb_TextLCD2, gbc);
		p.add(lb_TextLCD2);

		segment.setText(tf_TextLCD.getText());

		tf_TextLCD.addTextListener(new java.awt.event.TextListener() {
			public void textValueChanged(java.awt.event.TextEvent event) {
				TextField tf = (TextField) event.getSource();
				//System.out.println("textValueChanged: "+tf.getText());
				if (tf.getText().length() > 5) {
					tf.setText(tf.getText().substring(1, 6));
					tf.setCaretPosition(5);
				} else
					segment.setText(tf.getText());
			}
		});

		return p;
	}

	private GridBagConstraints makegbc(int x, int y, int width, int height) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = width;
		gbc.gridheight = height;
		gbc.insets = new Insets(1, 1, 1, 1);
		return gbc;
	}

	public void flip(String label) {
		((CardLayout) this.getLayout()).show(this, label);
	}

	private void loadImage() {

		try {
			showStatus("Loading LCD-Image ...");
			rcx_image = getImage(getDocumentBase(), getParameter("img"));
			//rcx_image = getImage(getClass().getResource(getParameter("img")));
			//rcx_image = getToolkit().getImage(getParameter("img"));

			MediaTracker mt = new MediaTracker(this);

			mt.addImage(rcx_image, 0);
			try {
				//Warten, bis das Image vollständig geladen ist,
				mt.waitForAll();
			} catch (InterruptedException e) {
				//nothing
				showStatus("LCD-Image loaded.");
			}
			if (mt.isErrorAny()) {
				showStatus("Can't find image '" + getParameter("img") + "'.");
				Thread.sleep(2000);
			}
		} catch (Exception e) {
			showStatus("Can't find image '" + getParameter("img") + "'.");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException ex) {
			}
		}
	}
	
	public RCXSegment getSegment() {
		return segment;
	}
	
	public Label[] getTitleLabel() {
		return titleLabel;
	}
	
	public TextField getLCDTextField() {
		return tf_TextLCD;
	}

	public void destroy() {
		//System.out.println(" -----------  END  -----------");
		segment.removeMouseListener(new TextLCDMouseListener(this));
		for (int k = 0; k < titleLabel.length; k++) {
			titleLabel[k].removeMouseListener(new TextLCDMouseListener(this));
		}
	}
	/*
	public void main(String className, String args[]) {

		Applet a;
		Dimension appletSize;

		try {
			// create an instance of your applet class
			a = (Applet) Class.forName(className).newInstance();
		} catch (ClassNotFoundException e) {
			return;
		} catch (InstantiationException e) {
			return;
		} catch (IllegalAccessException e) {
			return;
		}

		TextLCDApplet TextLCDTest = new TextLCDApplet();

		TextLCDTest.init();

		f.add("Center", TextLCDTest);

		appletSize = a.getSize();
		f.pack();
		//f.resize(appletSize);  
		f.setSize(appletSize.width, appletSize.height);
		//f.setSize(300, 100);
		//f.resize(400, 400);

		f.show();
	}
	*/
}

class TextLCDMouseListener extends MouseAdapter {
	private TextLCDApplet owner;

	public TextLCDMouseListener(TextLCDApplet owner) {
		this.owner = owner;
	}

	public void mouseClicked(MouseEvent mouse_event) {

		if (mouse_event.getSource().equals(owner.getSegment())) {
			int x = mouse_event.getX();
			int y = mouse_event.getY();
			//System.out.println("Segment " + x + "," + y);
			if ((x > 11) && (x < 33) && (y > 43) && (y < 56))
				owner.flip("Title");
		} else
			owner.flip("LCD");

		mouse_event.consume();
	}
}

class TextLCDThread extends Thread {

	private TextLCDApplet owner;
	private RCXSegment segment;
	private Label[] animLabel = new Label[4];
	private double[] endPosY = new double[4];
	private double[] deltaY = new double[4];
	private Dimension dim;

	public TextLCDThread(TextLCDApplet owner) {
		this.owner = owner;
		this.segment = owner.getSegment();
		this.animLabel = owner.getTitleLabel();
		//System.out.println("owner.getPreferredSize().getHeight()= "+
		//                    owner.getPreferredSize().getHeight());
		dim = owner.getPreferredSize();
	}

	public void run() {
		try {

			//Thread.sleep(2000);
			while (!owner.isValid()) {
				sleep(30);
				yield();
			}
			int i, k;
			for (i = 0; i < animLabel.length; i++) {
				endPosY[i] = animLabel[i].getLocation().y;
				deltaY[i] = dim.height - endPosY[i];
			}

			for (i = 0; i < animLabel.length; i++) {
				k = (int) dim.height;
				animLabel[i].setLocation(new Point(0, k));
				animLabel[i].setVisible(true);
				int absGray;

				while (k > endPosY[i] + 2) {
					sleep((int) (40));
					k -= 3;
					absGray =
						java.lang.Math.abs(
							(int) (255.0
								* (1.0 - (dim.height - k) / deltaY[i])));
					animLabel[i].setLocation(new Point(0, k));
					animLabel[i].setForeground(
						new Color(absGray, absGray, absGray));
					animLabel[i].validate();
					yield();
				}
				animLabel[i].setLocation(new Point(0, (int) endPosY[i]));
				animLabel[i].setForeground(new Color(0, 0, 0));
			}

			//while (!owner.imageLoaded) { Thread.sleep(60); Thread.yield();}
			sleep(1000);
			//segment.setVisible(false);
			
			String[] segText = { "JAVA", "for", "LEGO", "=", "LEJOS" };
			owner.getLCDTextField().setText(segText[0]);
			owner.flip("LCD");

			for (k = -segment.getPreferredSize().height; k <= 1; k++) {
				segment.moveTo(0, k);
				//segment.validate();
				sleep((int) 40);
				yield();
			}
			for (i = 1; i < segText.length; i++) {
				sleep((int) 1000);
				owner.getLCDTextField().setText(segText[i]);
				yield();
			}
		} catch (InterruptedException e) {
			//break;
		}
	}
}
/*
class TitleLabel extends Label {
	private String text;
	private Color fontColor;
	public TitleLabel(String pText, int pAlignment) {
		//super(pText, pAlignment);
		text = pText;
		fontColor = Color.white;	 
	}

	public void paint(Graphics g) {
		//super.paint(g);
		Dimension dim = getSize();
		drawShadow(g, this.getFont(), text,
			(int)Math.floor(
			(dim.width-g.getFontMetrics(getFont()).stringWidth(text)) / 2.0),
			g.getFontMetrics(getFont()).getAscent() +
			g.getFontMetrics(getFont()).getDescent());
	}
	public void drawShadow(Graphics pG, Font pFont, String pText,
			int posX, int posY) {
		pG.setColor(Color.lightGray);
		pG.drawString(pText, posX + 1, posY + 1);
		pG.setColor(fontColor);
		pG.drawString(pText, posX, posY);
	}
	public void setForeground(Color pColor) {
		fontColor = pColor;
		repaint();
	}
} */

/*
class ImageThread extends Thread {

     TextLCDApplet owner;
     Image rcx_image;
    
     public ImageThread(TextLCDApplet owner) {
       this.owner = owner;
       //this.rcx_image = owner.rcx_image;
     }

     public void run()
     {
      //rcx_image = new java.net.URL(owner.getDocumentBase(), owner.getParameter("img")); 

       try {
          owner.showStatus("Loading LCD-Image ...");

          rcx_image = owner.getImage(owner.getDocumentBase(), owner.getParameter("img"));
          //rcx_image = getToolkit().getImage(getParameter("img"));

          MediaTracker mt = new MediaTracker(owner);

          mt.addImage(rcx_image, 0);
          try {
             //Warten, bis das Image vollständig geladen ist,
             mt.waitForAll();
          } catch (InterruptedException e) {
             //nothing
             owner.showStatus("LCD-Image loaded.");
          }
          if (mt.isErrorAny()) { 
              owner.showStatus("Can't find image '"+owner.getParameter("img")+"'."); 
              sleep(2000); 
          }
       } catch (Exception e) {
          owner.showStatus("Can't find image '"+owner.getParameter("img")+"'."); 
          try { sleep(2000); } catch (InterruptedException ex) {  }
       } finally {
          owner.imageLoaded = true;
       }

     }
} */