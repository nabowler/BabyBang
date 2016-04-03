package babybang;

import java.util.Random;

import babybang.sounds.Sounds;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * BabyBang entry point.
 */
public class BabyBangApp extends Application {

	/** Exit Key-Combination. */
	private static final String EXIT_COMBO = "Alt+Shift+Q";

	/** One Random to rule them all. */
	private Random rand = new Random();

	/** Label to show the last key pressed. */
	private Label label;

	/** The currently playing, or last played, AudioClip. */
	private AudioClip currentClip;

	public BabyBangApp() {
		super();
		initLabel();
	}

	@Override
	public void start(Stage primaryStage) {
		initStage(primaryStage);
		primaryStage.show();
	}

	/**
	 * Initializes the stage to fullscreen, always on top, changes the
	 * fullScreen exit key combination, and sets up the scene.
	 */
	private void initStage(Stage stage) {
		stage.setTitle("Baby Bang - " + EXIT_COMBO + " to close.");
		stage.setAlwaysOnTop(true);
		stage.setFullScreen(true);
		stage.setMaximized(true);
		// Scene KeyEvent handler uses this combo to close BabyBang.
		stage.setFullScreenExitHint(EXIT_COMBO);
		stage.setFullScreenExitKeyCombination(KeyCombination.valueOf(EXIT_COMBO));
		initScene(stage);
	}

	/** Sets up the scene. */
	private void initScene(Stage primaryStage) {
		StackPane layout = new StackPane();
		Scene scene = new Scene(layout, primaryStage.getWidth(), primaryStage.getHeight());
		layout.getChildren().add(label);

		scene.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
			if (e.isAltDown() && e.isShiftDown() && e.getCode().equals(KeyCode.Q)) {
				primaryStage.setFullScreen(false);
				primaryStage.close();
				e.consume();
				return;
			}
			if (this.currentClip == null || !this.currentClip.isPlaying()) {
				currentClip = Sounds.getRandomClip(rand);
				if (currentClip != null) {
					currentClip.play();
				}
			}
			String text = e.getText();
			if (text == null || text.trim().isEmpty()) {
				text = "BABY!";
			}
			this.label.setTextFill(Paint.valueOf(nextColorString(this.rand)));
			this.label.setText(text);
			e.consume();
		});
		primaryStage.setScene(scene);
	}

	/** Initialize the label. */
	private void initLabel() {
		this.label = new Label("BABY!");
		this.label.setAlignment(Pos.BASELINE_CENTER);
		this.label.setFont(Font.font(300));
		this.label.setTextFill(Paint.valueOf("blue"));
	}

	/**
	 * Generate a random Color String.
	 *
	 * @param rand
	 *            The random to use. If null a new Random will be used.
	 * @return The generated Color String, formatted as 6 hex-digits.
	 */
	private static String nextColorString(Random rand) {
		if (rand == null) {
			rand = new Random();
		}

		StringBuilder colorSb = new StringBuilder(6).append(lpad(Integer.toHexString(rand.nextInt(256)), 2, '0'))
				.append(lpad(Integer.toHexString(rand.nextInt(256)), 2, '0'))
				.append(lpad(Integer.toHexString(rand.nextInt(256)), 2, '0'));
		return colorSb.toString();
	}

	/**
	 * Left pad a String.
	 *
	 * @param str
	 *            The string to pad.
	 * @param len
	 *            The String length after padding.
	 * @param c
	 *            The pad character
	 * @return The left padding String. If str.length >= len, str is returned
	 *         unmodified.
	 */
	private static String lpad(String str, int len, char c) {
		if (str == null) {
			str = "";
		}
		if (str.length() >= len) {
			return str;
		}
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len - str.length(); i++) {
			sb.append(c);
		}
		return sb.append(str).toString();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
