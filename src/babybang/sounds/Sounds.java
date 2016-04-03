package babybang.sounds;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javafx.scene.media.AudioClip;
import javafx.scene.media.MediaException;

/**
 * Utility class for sounds.
 */
public class Sounds {

	private static final String BABYBANG_SOUNDS_CONFIG = "babybang.sounds.config";

	private static final String DEFAULT_SOUNDS_CONFIG = "sounds/config/babysmash.sounds";

	private static final String NO_SOUNDS = "no.sounds";

	private static final List<AudioClip> AUDIO_CLIPS = new ArrayList<>();

	private static final ReadWriteLock LOCK = new ReentrantReadWriteLock();

	static {
		LOCK.writeLock().lock();
		try {
			if (AUDIO_CLIPS.isEmpty()) {
				init();
			}
		} finally {
			LOCK.writeLock().unlock();
		}
	}

	private Sounds() {
		super();
	}

	/**
	 * Initialize the audio clips from the config file specified by the System
	 * property {@value #BABYBANG_SOUNDS_CONFIG}, or will use the default
	 * {@value #DEFAULT_SOUNDS_CONFIG} if the property is not configured or the
	 * file cannot be found. To disable sounds entirely, set the property to
	 * {@value #NO_SOUNDS}.
	 *
	 * This method will be run automatically when the class is loaded.
	 */
	public static void init() {
		String configFile = System.getProperty(BABYBANG_SOUNDS_CONFIG, DEFAULT_SOUNDS_CONFIG);
		if (configFile.equals(NO_SOUNDS)) {
			LOCK.writeLock().lock();
			try {
				AUDIO_CLIPS.clear();
				return;
			} finally {
				LOCK.writeLock().unlock();
			}
		}
		InputStream configIs = null;
		try {
			configIs = ClassLoader.getSystemResourceAsStream(configFile);
			if (configIs == null) {
				configIs = ClassLoader.getSystemResourceAsStream(DEFAULT_SOUNDS_CONFIG);
				if (configIs == null) {
					System.err.println("Cannot find the default audio config file. Audio clips not loaded.");
					return;
				}
			}

			BufferedReader bis = new BufferedReader(new InputStreamReader(configIs));
			String line;
			URL url;
			List<AudioClip> newClips = new ArrayList<>();
			while ((line = bis.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty() || line.startsWith("#")) {
					continue;
				}
				url = ClassLoader.getSystemResource(line);
				if (url != null) {
					try {
						AudioClip ac = new AudioClip(url.toString());
						newClips.add(ac);
					} catch (MediaException e) {
						System.err.println("Cannot load " + line);
						e.printStackTrace();
					}
				} else {
					System.err.println("Cannot find " + line);
				}
			}
			if (!newClips.isEmpty()) {
				LOCK.writeLock().lock();
				try {
					AUDIO_CLIPS.clear();
					AUDIO_CLIPS.addAll(newClips);
				} finally {
					LOCK.writeLock().unlock();
				}
			}
		} catch (IOException e) {
			System.err.println("Audio may not work due to the following error.");
			e.printStackTrace();
		} finally {
			if (configIs != null) {
				try {
					configIs.close();
				} catch (IOException e) {
					// Nope
				}
			}
		}
	}

	/**
	 * Get a random AudioClip.
	 *
	 * @param rand
	 *            The Random to use. If null, a new Random with the default seed
	 *            will be used.
	 * @return A random AudioClip. Null will be returned if no audio clips are
	 *         loaded.
	 */
	public static AudioClip getRandomClip(Random rand) {
		if (rand == null) {
			rand = new Random();
		}
		LOCK.readLock().lock();
		try {
			if (AUDIO_CLIPS.isEmpty()) {
				return null;
			}

			return AUDIO_CLIPS.get(rand.nextInt(AUDIO_CLIPS.size()));
		} finally {
			LOCK.readLock().unlock();
		}
	}

}
