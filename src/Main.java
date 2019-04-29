import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class Main {

	public static class Comp extends JComponent {

		private static final long serialVersionUID = 1L;
		public boolean gameOver; // gameOver check
		public JTextField text;
		public JFrame frame;
		public Font bigFont = new Font("Arial", Font.BOLD, 30);
		public Font smallFont = new Font("Arial", Font.BOLD, 14);
		public String word = ""; // word submitted to guess
		public int maxTries = 9;
		public int tries = maxTries; // lives
		public String cWord = " "; // current guessed word
		public boolean startOver = true;
		String usedLetters = "1234567890"; // words used to guess

		public Comp(JTextField text, JFrame frame) throws InterruptedException {
			this.text = text;
			this.frame = frame;
		}

		public void PressEnter() {

			String enteredText = fixWord(text.getText());
			if (this.word == null || word == "" || startOver) { // has yet to put in a word
				startOver = false;
				word = enteredText; //this.fixWord(enteredText);
				if (word == null)
					return;
				else if (word == " " || word == "")
					return;
				System.out.println("Added word to find: " + word);
				text.setText("");
				tries = maxTries;
				cWord = "";
				for (int i = 0; i < word.length(); i++) {
					if (word.charAt(i) == '-')
						cWord += "-";
					else
						cWord += " ";
				}
			} else {
				
				if (word.contains(enteredText)) { // if the entered text matches part of the word
					String newcWord = "";
					char[] letters = word.toCharArray();
					boolean addedLetter = false;
					for (int i = 0; i < letters.length; i++) {
						addedLetter = false; //
						if (word.charAt(i) == cWord.charAt(i)) {
							newcWord += letters[i];
							addedLetter = true;
						} else {
							for (char c : enteredText.toCharArray()) {
								if (word.charAt(i) == c) {
									newcWord += letters[i];
									addedLetter = true;
									break;
								}
							}
						}
						if (!addedLetter) { // letter was not added to newCword
							// IF THIS IS TOUCHED, PROGRAM DIES.
							newcWord += " ";
						}
					}
					cWord = newcWord;
					if (cWord.equals(word)) {
						this.repaint();
						win();
						return;
					}
				} else {
					if (enteredText.length() > 1) // if try was a word
						tries--;
					else if (!usedLetters.contains(enteredText)) { // obvious
						tries--;
						usedLetters += enteredText;
					}
					System.out.println("Tries left: " + tries);
					if (tries <= 0) {
						cWord = word;
						this.repaint();
						dead();
						return;
					}

				}
			}
			this.repaint();
			text.setText("");
		}

		public void win() {
			System.out.println("You have won the game.");
			tries = maxTries + 1;
			startOver = true;
			text.setText("");
		}

		public void dead() {
			tries = 0;
			System.out.println("You have lost the game.");
			startOver = true;
			text.setText("");
		}

		public String fixWord(String str) {
			String word = "";
			char before = ' ';
			for (char c : str.toCharArray()) {
				if (Character.isLetter(c)) {
					c = Character.toLowerCase(c);
					word += "" + c;
					before = c;
				} else if (Character.isWhitespace(c)||c=='-') {
					if (!(Character.isWhitespace(before)||before=='-')) {
						word += "-";
						before = c;
					}
				}
			}
			return word;
		}

		public void refreshHangingMan(int x, int y, Graphics2D g) { // painting hanged poor guy
			// if (this.parts == null) {
			double headRadius = 80.0;
			double torsoHeight = headRadius * 1.2;
			Ellipse2D head = new Ellipse2D.Double(x, y, headRadius / 2, headRadius / 2);
			Line2D torso = new Line2D.Double(head.getCenterX(), head.getCenterY() + headRadius / 4, head.getCenterX(),
					head.getCenterY() + headRadius / 4 + torsoHeight);
			Line2D lArm = new Line2D.Double(torso.getX1(), torso.getY1() + torsoHeight / 6,
					torso.getX1() - torsoHeight / 4, torso.getY1() + torsoHeight * (1.2));
			Line2D rArm = new Line2D.Double(torso.getX1(), torso.getY1() + torsoHeight / 6,
					torso.getX1() + torsoHeight / 4, torso.getY1() + torsoHeight * (1.2));
			Line2D lLeg = new Line2D.Double(torso.getX1(), torso.getY2(), torso.getX1() - torsoHeight / 4,
					torso.getY2() + torsoHeight);
			Line2D rLeg = new Line2D.Double(torso.getX1(), torso.getY2(), torso.getX1() + torsoHeight / 4,
					torso.getY2() + torsoHeight);
			Rectangle2D base = new Rectangle2D.Double(x - 50, y - 50 + (int) (100 + torsoHeight * 2.2), 250, 20);
			Rectangle2D pole = new Rectangle2D.Double(x + 200 - 10, y - 50, 10, (int) (100 + torsoHeight * 2.2));
			Line2D weld1 = new Line2D.Double(x + 200 * 3 / 4, y - 70 + 20, x + 200 - 10, y - 70 + 20 + 50);
			Line2D weld2 = new Line2D.Double(x + 200 * 3 / 4 - 20, y - 70 + 20, x + 200 - 10, y - 70 + 20 + 50 + 20);
			Rectangle2D top = new Rectangle2D.Double(x, y - 70, 200, 20);
			Line2D rope = new Line2D.Double((int) head.getCenterX(), y - 50, (int) head.getCenterX(),
					(int) (head.getCenterY() - headRadius / 4));
			Shape[] part = { base, pole, weld2, weld1, top, rope, head, torso, lArm, rArm, lLeg, rLeg };
			for (int i = 0; i < part.length * (maxTries - Math.min(tries, maxTries)) / maxTries; i++) {
				g.draw(part[i]);
			}
		}

		@Override
		protected void paintComponent(Graphics g) { // painting the screen
			super.paintComponent(g);
			this.refreshHangingMan(this.getWidth() / 2, this.getHeight() / 2 - 150, (Graphics2D) g);
			if (startOver) {
				String str = "Write a word to play.";
				g.drawString(str, this.getWidth() / 2 - g.getFontMetrics().stringWidth(str) / 2,
						this.getHeight() - text.getHeight() - g.getFontMetrics().getHeight() - 10);
			}
			if (tries <= 0)
				g.setColor(Color.RED);
			else if (tries > maxTries)
				g.setColor(Color.BLUE);
			else
				g.setColor(Color.BLACK);
			text.setBounds(this.getWidth() / 2 - text.getWidth() / 2, this.getHeight() - text.getHeight() - 10,
					text.getWidth(), text.getHeight());
			if (g.getFont() != bigFont)
				g.setFont(bigFont);
			g.drawString("Tries left: " + tries, this.getWidth() / 2 - 200, this.getHeight() / 2 - 150);
			if (word != null || word != "") {
				int spacing = this.getWidth() / 2 - 30 * word.length() / 2;
				for (int i = 0; i < word.length(); i++) {
					char c = cWord.charAt(Math.min(i, cWord.length() - 1));
					g.drawString("" + c, spacing, this.getHeight() - text.getHeight() - 50 - 10);
					int width = 30;
					g.drawLine(spacing - 10, this.getHeight() - text.getHeight() - 50 - 10, spacing + width - 10,
							this.getHeight() - text.getHeight() - 50 - 10);
					spacing += width + 10;
				}
			}

		}

	}

	public static void main(String[] args) throws InterruptedException {
		int frameWidth = 800, frameHeight = 600, textBoxWidth = 200, textBoxHeight = 50;
		JTextField textBox1 = new JTextField(100);
		JFrame frame = new JFrame();
		JComponent comp = new Comp(textBox1, frame);
		textBox1.setBounds(frameWidth / 2 - textBoxWidth / 2, frameHeight - textBoxHeight * 2 - 50, textBoxWidth,
				textBoxHeight);
		textBox1.addActionListener(new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				((Comp) comp).PressEnter();
			}
		});
		frame.setTitle("Hangman Game");
		frame.setSize(frameWidth, frameHeight);
		frame.add(textBox1);
		frame.add(comp);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

}